package com.aki.anichan.ui.base

interface ViewModelContract<T> {
    fun loadData(param: T)
}