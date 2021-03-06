package me.palazzini.bleexample.data.repository

import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.palazzini.bleexample.domain.repository.ReportRepository
import java.io.File
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class TextReportRepository : ReportRepository {

    private var file: File? = null
    private var outputStreamWriter: OutputStreamWriter? = null

    private fun createAndOpenFile() {
        val downloadsDirectory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        val parent = File(downloadsDirectory.absolutePath, "ILLUSIO")
        file = File(parent, "report.txt")

        parent.mkdirs()
        file?.createNewFile()

        outputStreamWriter = OutputStreamWriter(file?.outputStream())
    }

    override suspend fun add(text: String) {
        withContext(Dispatchers.IO) {
            if (file == null || outputStreamWriter == null) {
                createAndOpenFile()
            }

            outputStreamWriter?.appendLine(text)
        }
    }

    override suspend fun flush() {
        withContext(Dispatchers.IO) {
            outputStreamWriter?.flush()
        }
    }

    override suspend fun getAll(): List<String> = withContext(Dispatchers.IO) {
        if (file == null || file?.inputStream() == null) {
            createAndOpenFile()
        }

        val stream = InputStreamReader(file?.inputStream())
        stream.readLines()
    }
}