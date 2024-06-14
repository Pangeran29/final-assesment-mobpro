package org.d3if3030.mobpro1_compose.model

data class Response<T>(
    val statusCode: Int,
    val data: T,
    val message: String?
)
