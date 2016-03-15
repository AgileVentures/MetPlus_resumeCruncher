package org.metplus.curriculum.parsers;

import com.google.common.io.ByteStreams;
import org.apache.tika.Tika;
import org.apache.tomcat.jni.File;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Created by Joao Pereira on 19/09/2015.
 */
public class DocumentParserImplTest {



    @Test
    public void testParsePDFSimpleLine() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        stream.write(ByteStreams.toByteArray(getClass().getClassLoader().getResourceAsStream("single_line.pdf")));
        DocumentParserImpl a = new DocumentParserImpl(stream);
        assertTrue(a.getDocument().contains("My string"));
    }


    @Test
    public void testParsePDFWithStyle() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        stream.write(ByteStreams.toByteArray(getClass().getClassLoader().getResourceAsStream("line_with_bold.pdf")));
        DocumentParserImpl a = new DocumentParserImpl(stream);
        System.out.println(a.getDocument());
        assertTrue(a.getDocument().contains("My String bamm"));
    }


    @Test
    public void testParseWordSimpleLine() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        stream.write(ByteStreams.toByteArray(getClass().getClassLoader().getResourceAsStream("single_line.docx")));
        DocumentParserImpl a = new DocumentParserImpl(stream);

        assertTrue(a.getDocument().contains("My simple line"));
    }


    @Test
    public void testParseWordWithStyle() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        stream.write(ByteStreams.toByteArray(getClass().getClassLoader().getResourceAsStream("lines_with_format.docx")));
        DocumentParserImpl a = new DocumentParserImpl(stream);
        assertTrue(a.getDocument().contains("My simple line\n" +
                "Bamm\n" +
                "Italico"));
    }

    @Test
    public void testGetDocumentMultipleCalls() throws Exception {
        ByteArrayOutputStream str = new ByteArrayOutputStream();
        str.write("My String".getBytes());
        DocumentParserImpl printStream = spy(new DocumentParserImpl(str));

        printStream.getDocument();
        printStream.getDocument();
        printStream.getDocument();
        verify(printStream, times(1)).parse();
        assertEquals("My String\n", printStream.getDocument());
    }

    @Test
    public void testGetDocumentTestHTML() throws Exception {
        ByteArrayOutputStream str = new ByteArrayOutputStream();
        str.write("<html><body>My String</body></html>".getBytes());
        DocumentParserImpl printStream = spy(new DocumentParserImpl(str));
        assertEquals("My String", printStream.getDocument());
    }
}