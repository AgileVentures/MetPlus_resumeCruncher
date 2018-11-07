package org.metplus.cruncher.resume

import java.io.InputStream

class FileInputStreamFake(
        private val fileContent: String
) : InputStream() {

    private var readUntil: Int = 0

    override fun read(): Int {
        if (readUntil == fileContent.length)
            return -1
        return fileContent[readUntil++].toInt()
    }
}