package org.jitu.wagtail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.CharArrayBuffer;

import android.os.Environment;

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
        br.close();
        currentFile = file;
        return buf.toString();
    }

    public String getAbsolutePath() {
        if (currentFile == null) {
            return Environment.getExternalStorageDirectory().getAbsolutePath() +
                    File.separator + "Untitled.txt";
        }
        return currentFile.getAbsolutePath();
    }

    public void saveFile(File file, String text) throws IOException {
        FileWriter wf = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(wf);
        bw.write(text, 0, text.length());
        bw.close();
    }
}
