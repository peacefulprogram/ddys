package com.jing.ddys.repository

sealed class Resource<in T> {
    object Loading : Resource<Any>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String, val exception: Exception? = null) : Resource<Any>()
}
