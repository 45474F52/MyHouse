package com.aes.myhome.objects

import kotlinx.serialization.Serializable

@Serializable
class Product (val name: String, val description: String) {
    override fun toString(): String {
        return name
    }
}