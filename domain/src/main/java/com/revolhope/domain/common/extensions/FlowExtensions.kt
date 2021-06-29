package com.revolhope.domain.common.extensions

import com.revolhope.domain.common.exceptions.InnerException
import com.revolhope.domain.common.model.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

// -------------------------------------------------------------------------------------------------
// FlowCollector
// -------------------------------------------------------------------------------------------------

suspend fun <T> FlowCollector<State<T>>.emitSuccess(value: T) {
    this.emit(State.Success(value))
}

suspend fun <T> FlowCollector<State<T>>.emitLoading() {
    this.emit(State.Loading)
}

suspend fun <T> FlowCollector<State<T>>.emitError(
    message: String? = null,
    messageRes: Int? = null,
    errorCode: String? = null,
    throwable: Throwable? = null
) {
    this.emit(
        State.Error(
            message = message,
            messageRes = messageRes,
            errorCode = errorCode,
            throwable = throwable
        )
    )
}

// -------------------------------------------------------------------------------------------------
// Flow
// -------------------------------------------------------------------------------------------------

// Convert to flow ---------------------------------------------------------------------------------
fun <T> T.asFlow(): Flow<T> = flow { emit(this@asFlow) }

suspend inline fun <T> (suspend () -> Flow<T>).asStateFlow(
    isLoadingEnabled: Boolean = true,
    crossinline catch: (t: Throwable) -> State.Error,
    noinline onMapTransformation: ((T) -> State<T>)? = null
): Flow<State<T>> =
    mapToState(isLoadingEnabled, this, onMapTransformation).catch { cause -> emit(catch(cause)) }

suspend inline fun <T> Flow<State<T>>.firstNonLoading(): State<T>? =
    this.firstOrNull { it !is State.Loading }

suspend inline fun <T> mapToState(
    emitLoading: Boolean,
    crossinline action: suspend () -> Flow<T>,
    noinline onMapTransformation: ((T) -> State<T>)? = null
): Flow<State<T>> =
    action.invoke()
        .map { onMapTransformation?.invoke(it) ?: State.Success(it) as State<T> }
        .onStart { if (emitLoading) emit(State.Loading) }

suspend inline fun <T> T.asStateFlow(
    isLoadingEnabled: Boolean = true,
    crossinline catch: (t: Throwable) -> State.Error
): Flow<State<T>> =
    suspend { this@asStateFlow.asFlow() }.asStateFlow(isLoadingEnabled, catch)

// Map results -------------------------------------------------------------------------------------
inline fun <T, R> Flow<T?>.mapIfNotNull(crossinline transform: suspend (value: T) -> R): Flow<R?> =
    transform { value ->
        return@transform emit(value?.let { transform(it) })
    }

suspend inline fun <T> Flow<State<T>>.collectOnMain(crossinline action: suspend (value: State<T>) -> Unit) {
    this.collect { state ->
        if (coroutineScope { coroutineContext } != Dispatchers.Main) {
            withContext(Dispatchers.Main) {
                action.invoke(state)
            }
        } else {
            action.invoke(state)
        }
    }
}

// -------------------------------------------------------------------------------------------------
// TODO: Experimental
// -------------------------------------------------------------------------------------------------

inline val <T> T.onFirstOrNullEmittedValueDefaultPredicate: Boolean
    get() =
        (this is State<*> && this !is State.Loading) || this is Boolean && this || this !is Boolean

suspend inline fun Flow<Boolean>.onFirstSuccessEmitted(block: () -> Unit): Flow<Boolean> =
    this.onFirstOrNullEmittedValue(
        predicate = { this },
        block = { block() }
    )

suspend inline fun <T> Flow<T>.onFirstOrNullEmittedValue(
    crossinline predicate: T.() -> Boolean = { onFirstOrNullEmittedValueDefaultPredicate },
    block: T.() -> Unit
): Flow<T> =
    this.also { flow -> flow.firstOrNull { predicate(it) }?.let { data -> block.invoke(data) } }

@Suppress("UNCHECKED_CAST")
suspend inline fun <reified R> Flow<Boolean>.onSuccessMapToOrThrow(crossinline block: suspend () -> R): Flow<R> =
    this.map {
        when {
            it -> block.invoke()
            R::class == Boolean::class -> false as R
            else -> throw InnerException.FlowFalseState() //TODO: Change exception!
        }
    }

@Suppress("UNCHECKED_CAST")
suspend inline fun <reified T, reified R> Flow<State<T>>.onSuccessMapTo(crossinline block: suspend () -> State<R>): Flow<State<R>> =
    this.map {
        val bothAreBooleans = T::class == Boolean::class && R::class == Boolean::class
        val toResultType: (State<T>) -> State<R> = { state ->
            when {
                state !is State.Error && bothAreBooleans -> {
                    state as State.Success<Boolean> as State<R>
                }
                state !is State.Error && R::class != Boolean::class -> {
                    InnerException.FlowFalseState().asStateError()
                }
                else -> {
                    state as State.Error
                }
            }
        }
        when (it) {
            is State.Error -> toResultType(it)
            State.Loading -> State.Loading
            is State.Success -> if (it.data as? Boolean != false) block.invoke() else toResultType(
                it
            )
        }
    }


// ------ TEST
// Not checked!
@Suppress("UNCHECKED_CAST")
suspend inline fun <reified T, R : Any> Flow<State<T>>.then(
    crossinline then: suspend () -> Flow<State<R>>,
    crossinline thenPredicate: State<T>.() -> Boolean = { this is State.Success && data != null },
    crossinline onPredicateFailed: State<T>.() -> State<R>? = { null }
): Flow<State<R>> =
    MutableStateFlow<State<R>?>(null).apply {
        this@then.collect { originalState ->
            when (originalState) {
                is State.Success -> {
                    value = if (originalState.thenPredicate()) {
                        then.invoke().firstNonLoading()
                        // then.invoke().collect { value = it }
                    } else {
                        originalState.onPredicateFailed()
                    }
                }
                is State.Error -> {
                    value = originalState
                }
                State.Loading -> {
                    value = State.Loading
                }
            }
        }
    }.filterNotNull()

@Suppress("UNCHECKED_CAST")
suspend inline fun <reified T, R : Any> Flow<State<T>>.then2(
    noinline then: suspend () -> Flow<State<R>>,
    crossinline predicate: State<T>.() -> Boolean = { this is State.Success && data != null },
    crossinline onPredicateFailed: State<T>.() -> State<R>? = { null }
): Flow<State<R>> {
    return coroutineScope {
        this@then2.map { originalState ->
            when (originalState) {
                is State.Success -> {
                    if (originalState.predicate()) {
                        then.invoke().firstNonLoading()
                        // then.invoke().collect { value = it }
                    } else {
                        originalState.onPredicateFailed()
                    } ?: throw RuntimeException("Ooops...")
                }
                is State.Error -> {
                    originalState
                }
                State.Loading -> {
                    State.Loading
                }
            }
        }.stateIn(this, SharingStarted.Eagerly, State.Loading)
    }

    /*val originalFlow = this@then2
    withContext(Dispatchers.Main) {
        originalFlow.collect { originalState ->
            when (originalState) {
                is State.Success -> {
                    value = if (originalState.predicate()) {
                        then.invoke().firstOrNull { it !is State.Loading }
                        // then.invoke().collect { value = it }
                    } else {
                        originalState.onPredicateFailed()
                    }
                }
                is State.Error -> {
                    value = originalState
                }
                State.Loading -> {
                    value = State.Loading
                }
            }
        }
    }

    MutableStateFlow<State<R>?>(null).apply {

    }.filterNotNull()*/
}

// ......

@Suppress("UNCHECKED_CAST")
suspend inline fun <T> Flow<State<T>>.onSuccessThen(
    vararg nestedActions: suspend State.Success<T>.() -> Flow<State<T>>
): Flow<State<T>> =
    this.map {
        if (it is State.Success) {
            thenExecutor(
                state = it,
                nestedActions = nestedActions
            )
        } else {
            it
        }
    }

@Suppress("UNCHECKED_CAST")
suspend fun <T> Flow<State<T>>.thenExecutor(
    vararg nestedActions: suspend State.Success<T>.() -> Flow<State<T>>,
    state: State.Success<T>,
    index: Int = 0
): State<T> {
    return nestedActions[index].invoke(state).firstOrNull { blockState ->
        blockState !is State.Loading
    }?.let {
        if (it is State.Success) {
            if (index < nestedActions.lastIndex) {
                thenExecutor(
                    state = it as? State.Success<T> ?: throw RuntimeException(),
                    index = index + 1,
                    nestedActions = nestedActions
                )
            } else {
                it
            }
        } else {
            it
        }
    } ?: throw RuntimeException()
}
