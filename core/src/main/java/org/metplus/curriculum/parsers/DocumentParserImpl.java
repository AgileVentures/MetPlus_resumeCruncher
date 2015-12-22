package org.metplus.curriculum.parsers;


import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.metplus.curriculum.exceptions.DocumentParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Class used to parse documents from the database
 */
public class DocumentParserImpl {
    private static final Logger LOG = LoggerFactory.getLogger(DocumentParserImpl.class);

    ByteArrayInputStream stream;
    String document;

    /**
     * Class constructor
     * @param stream Stream from the database
     */
    public DocumentParserImpl(ByteArrayOutputStream stream) {
        this.stream = new ByteArrayInputStream(stream.toByteArray());
        document = null;
    }

    /**
     * Parse the document
     * @throws DocumentParseException When something went wrong while parsing the document
     */
    public void parse() throws DocumentParseException {
        AutoDetectParser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        try {
            parser.parse(stream, handler, metadata);
            document = handler.toString();
        } catch (SAXException | TikaException | IOException e) {
            throw new DocumentParseException(e.getMessage());
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                LOG.warn("Unable to close the stream: " + e.getMessage());
            }
        }
    }

    /**
     * Retrieve the document after parsing
     * @return Parsed document
     * @throws DocumentParseException When something went wrong while parsing the document
     */
    public String getDocument() throws DocumentParseException {
        if(document == null)
            parse();
        return document;
    }
}
