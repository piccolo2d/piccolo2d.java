package edu.umd.cs.piccolo.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

public class PObjectOutputStreamTest extends TestCase {
    private PObjectOutputStream outStream;
    private ByteArrayOutputStream outByteStream;

    public void setUp() throws IOException {
        outByteStream = new ByteArrayOutputStream();
        outStream = new PObjectOutputStream(outByteStream);
    }

    public void testToByteArrayThrowsExceptionOnNull() throws IOException {
        try {
            PObjectOutputStream.toByteArray(null);
        }
        catch (final NullPointerException e) {
            // expected
        }
    }

    public void testToByteArrayOnEmptyStreamWorks() throws IOException {
        outStream.flush();
        final byte[] outputBytes = outByteStream.toByteArray();
        assertNotNull(outputBytes);
        assertTrue("Header not output", outputBytes.length > 0);
    }

    public void testWriteConditionalObjectAcceptsNull() throws IOException {

    }
}
