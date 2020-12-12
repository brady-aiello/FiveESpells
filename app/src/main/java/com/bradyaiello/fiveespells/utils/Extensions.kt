package com.bradyaiello.fiveespells.utils

import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LifecycleOwnerAmbient
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

@Composable
fun <R, T : R> LiveData<T>.observeAsStateWithPolicy(
    initial: R,
    snapshotMutationPolicy: SnapshotMutationPolicy<R> =
        structuralEqualityPolicy()
): State<R> {
    val lifecycleOwner = LifecycleOwnerAmbient.current
    val state = remember { mutableStateOf(initial, snapshotMutationPolicy) }
    onCommit(this, lifecycleOwner) {
        val observer = Observer<T> { state.value = it }
        observe(lifecycleOwner, observer)
        onDispose { removeObserver(observer) }
    }
    return state
}