package org.d3if3030.mobpro1_compose.model

data class LogDay(
    val id: Int,
    val upload_by: String,
    val file_location: String,
    val description: String,
    val createdAt: String,
    val updatedAt: String
)
