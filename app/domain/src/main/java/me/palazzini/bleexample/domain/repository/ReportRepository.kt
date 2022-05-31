package me.palazzini.bleexample.domain.repository

interface ReportRepository {
    suspend fun add(text: String)
    suspend fun getAll(): List<String>
}