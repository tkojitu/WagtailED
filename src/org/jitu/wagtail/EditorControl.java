package org.jitu.wagtail;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.CharArrayBuffer;

public class EditorControl {
    private File currentFile;

    public String readFile(File file) throws IOException {
        URL url = file.toURI().toURL();
        URLConnection conn = url.openConnection();
        InputStream is = conn.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        int nread;
        char[] chunk = new char[8192];
        CharArrayBuffer buf = new CharArrayBuffer(8192);
        while ((nread = br.read(chunk, 0, chunk.length)) != -1) {
            buf.append(chunk, 0, nread);
        }
        currentFile = file;
        return buf.toString();
    }
}
