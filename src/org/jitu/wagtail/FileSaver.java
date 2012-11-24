package org.jitu.wagtail;

import java.io.File;

import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

public class FileSaver extends FileChooser {
    protected void setupContentView() {
        setContentView(R.layout.file_saver);
        setFileEdit();
    }

    protected void setupRoot() {
        File tmp = getArgFile().getParentFile();
        if (tmp.exists() && tmp.isDirectory()) {
            root = currentDir = tmp;
        } else {
            root = currentDir = Environment.getExternalStorageDirectory();
        }
    }

    private void setFileEdit() {
        EditText et = (EditText)findViewById(R.id.file_edit);
        String filename = getArgFile().getName();
        et.setText(filename);
        et.setSelection(et.getText().length());
    }

    protected ListView findFileList() {
        return (ListView)findViewById(R.id.file_list);
    }

    protected FileArrayAdapter newFileArrayAdapter() {
        return FileArrayAdapter.newInstance(FileSaver.this, R.layout.file_chooser_list,
                currentDir);
    }

    protected void onFileClick(File file) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
             return cancelToHome();
        default:
            return true;
        }
    }

    private boolean cancelToHome() {
        backToParent(-2, "");
        return true;
    }

    public void onOk(View view) {
        String path = getSavedPath();
        backToParent(0, path);
    }

    public void onCancel(View view) {
        cancel();
    }

    private String getSavedPath() {
        EditText et = (EditText)findViewById(R.id.file_edit);
        String text = et.getText().toString();
        return currentDir + File.separator + text;
    }
}
