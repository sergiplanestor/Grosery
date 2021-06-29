package com.revolhope.domain

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.Test

class FlowPlaygroundTest {

    private val dummyDataSource1: List<Int> = listOf(1, 2, 3, 4, 5)
    private val dummyDataSource2: List<Int> = listOf(6, 7, 8, 9, 10)
    private val dummyDataSource3: List<String> = listOf("a", "b", "c", "d", "e")

    private val <T> T.emitAsFlowDelayed: Flow<T>
        get() = flow {
            delay(500)
            emit(this@emitAsFlowDelayed)
        }

    private suspend fun <T> List<T>.emitDelayed(collector: FlowCollector<T>, millis: Long = 500) {
        this.forEach {
            delay(millis)
            collector.emit(it)
        }
    }

    private val <T> T.emitAsFlow: Flow<T>
        get() = flow { emit(this@emitAsFlow) }

    private suspend fun <T> T.emitAsStateFlow(initial: T? = null): StateFlow<T> =
        if (initial != null) {
            this.emitAsFlow.stateIn(GlobalScope, SharingStarted.Eagerly, initial)
        } else {
            this.emitAsFlow.stateIn(GlobalScope)
        }

    @Test
    fun test_1() {
        flow {
            println("Flow start")
            dummyDataSource1.emitDelayed(this, 100)
        }

        runBlocking {
            println("Whateveeeeeer")
            // Launch a concurrent coroutine to check if the main thread is blocked
            launch {
                for (k in 1..3) {
                    println("I'm not blocked $k")
                    delay(100)
                }
            }
            // Collect the flow
            flow { dummyDataSource1.emitDelayed(this, 100) }.collect { value ->
                println(value)
            }
        }
    }
}