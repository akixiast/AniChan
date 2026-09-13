package com.aki.anichan.ui.base

interface ViewHolderContract<T> {
    fun bind(item: T, index: Int)
}