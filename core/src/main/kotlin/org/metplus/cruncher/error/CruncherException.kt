package org.metplus.cruncher.error

open class CruncherException(text: String) : Exception(text)
class DocumentParseException(text: String?) : CruncherException("Exception while parsing a file: $text")
