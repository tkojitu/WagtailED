package org.jitu.wagtail;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import org.apache.http.util.CharArrayBuffer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class FileControl {
    private static final String CURRENT_URI = "CURRENT_URI";

    private MainActivity activity;
    private Uri currentUri;
    private Exception error;

    public FileControl(MainActivity activity) {
        this.activity = activity;
    }

    public String readUri(Uri uri) {
        if (uri == null) {
            return "";
        }
        InputStream is = null;
        try {
            is = getInputStreamFromUri(uri);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            int nread;
            char[] chunk = new char[8192];
            CharArrayBuffer buf = new CharArrayBuffer(8192);
            while ((nread = br.read(chunk, 0, chunk.length)) != -1) {
                buf.append(chunk, 0, nread);
            }
            currentUri = uri;
            return buf.toString();
        } catch (IOException e) {
            error = e;
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private InputStream getInputStreamFromUri(Uri uri) throws IOException {
        if ("file".equals(uri.getScheme())) {
            return new FileInputStream(uri.getPath());
        }
        ContentResolver cr = activity.getContentResolver();
        return cr.openInputStream(uri);
    }

    public Uri getCurrentUri() {
        return currentUri;
    }

    public String getCurrentFileName() {
        if (currentUri == null) {
            return "";
        }
        String str = currentUri.getPath();
        int index = str.lastIndexOf('/');
        return str.substring(index + 1);
    }

    public String getHomePath() {
        File storage = Environment.getExternalStorageDirectory();
        File home = new File(storage, "notes");
        if (home.exists()) {
            return home.getAbsolutePath();
        } else {
            return storage.getAbsolutePath();
        }
    }

    public boolean saveUri(Uri uri, String text) {
        if (uri == null) {
            return true;
        }
        BufferedWriter writer = null;
        try {
            ContentResolver cr = activity.getContentResolver();
            OutputStream os = cr.openOutputStream(uri);
            OutputStreamWriter osw = new OutputStreamWriter(os);
            writer = new BufferedWriter(osw);
            writer.write(text);
            currentUri = uri;
            return true;
        } catch (IOException e) {
            error = e;
            return false;
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void newFile() {
        currentUri = null;
    }

    public String getErrorMessage() {
        if (error == null) {
            return "";
        }
        return error.getMessage();
    }

    public void onSaveInstanceState(Bundle outState) {
        if (currentUri == null) {
            return;
        }
        outState.putSerializable(CURRENT_URI, currentUri.toString());
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        String str = (String) savedInstanceState.getSerializable(CURRENT_URI);
        if (str != null) {
            currentUri = Uri.parse(str);
        }
    }
}
