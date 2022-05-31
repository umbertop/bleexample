package me.palazzini.bleexample.data.repository

import android.content.Context
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.palazzini.bleexample.domain.repository.ReportRepository
import timber.log.Timber
import java.io.File
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class TextReportRepository(
    private val context: Context
) : ReportRepository {

    private var file: File? = null

    init {
        val downloadsDirectory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        val parent = File(downloadsDirectory.absolutePath, "ILLUSIO")
        file = File(parent, "report.txt")

        parent.mkdirs()
        file?.createNewFile()
    }

    override suspend fun add(text: String) = withContext(Dispatchers.IO) {
        if (file == null || file?.outputStream() == null) {
            Timber.w("add: Cannot open log file")
            return@withContext
        }

        val stream = OutputStreamWriter(file?.outputStream())
        stream.appendLine(text)
        stream.close()
    }

    override suspend fun getAll(): List<String> = withContext(Dispatchers.IO) {
        if (file == null || file?.inputStream() == null) {
            Timber.w("add: Cannot open log file")
            return@withContext emptyList()
        }

        val stream = InputStreamReader(file?.inputStream())
        stream.readLines()
    }
}