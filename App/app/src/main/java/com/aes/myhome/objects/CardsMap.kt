package com.aes.myhome.objects

import kotlinx.serialization.Serializable

@Serializable
data class CardsMap(var number: Int, var resourceId: Int, var title: String)