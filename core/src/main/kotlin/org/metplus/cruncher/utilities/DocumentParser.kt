package org.metplus.cruncher.utilities

import org.apache.tika.exception.TikaException
import org.apache.tika.metadata.Metadata
import org.apache.tika.parser.AutoDetectParser
import org.apache.tika.sax.BodyContentHandler
import org.metplus.cruncher.error.DocumentParseException
import org.slf4j.LoggerFactory
import org.xml.sax.SAXException
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

class DocumentParserImpl(stream: ByteArrayOutputStream) {

    private var stream: ByteArrayInputStream = ByteArrayInputStream(stream.toByteArray())
    private var document: String? = null

    @Throws(DocumentParseException::class)
    fun parse() {
        val parser = AutoDetectParser()
        val handler = BodyContentHandler()
        val metadata = Metadata()
        try {
            parser.parse(stream, handler, metadata)
            document = handler.toString()
        } catch (e: SAXException) {
            throw DocumentParseException(e.message)
        } catch (e: TikaException) {
            throw DocumentParseException(e.message)
        } catch (e: IOException) {
            throw DocumentParseException(e.message)
        } finally {
            try {
                stream.close()
            } catch (e: IOException) {
                LOG.warn("Unable to close the stream: " + e.message)
            }

        }
    }

    @Throws(DocumentParseException::class)
    fun getDocument(): String? {
        if (document == null)
            parse()
        return document
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(DocumentParserImpl::class.java)
    }
}
