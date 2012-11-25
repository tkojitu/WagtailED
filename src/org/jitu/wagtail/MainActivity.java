package org.jitu.wagtail;

import java.io.File;
import java.io.IOException;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements DialogInterface.OnClickListener {
    private final static int ACTIVITY_FILE_CHOOSER = 1;
    private final static int ACTIVITY_FILE_SAVER   = 2;

    private EditorControl control = new EditorControl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_file:
            showFileMenu();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void showFileMenu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(R.array.file_menu_items, this);
        builder.create().show();
    }

    public void onClick(DialogInterface dialog, int which) {
        Resources r = getResources();
        String[] items = r.getStringArray(R.array.file_menu_items);
        String item = items[which];
        if (r.getString(R.string.menu_item_new).equals(item)) {
            onNew();
        } else if (r.getString(R.string.menu_item_open).equals(item)) {
            onOpen();
        } else if (r.getString(R.string.menu_item_save).equals(item)) {
            onSave();
        } else if (r.getString(R.string.menu_item_save_as).equals(item)) {
            onSaveAs();
        }
    }

    private void onNew() {
        EditText et = (EditText)findViewById(R.id.edit);
        et.setText("");
        control.newFile();
    }

    private void onOpen() {
        Intent intent = new Intent(this, FileChooser.class);
        String root = Environment.getExternalStorageDirectory().getPath();
        intent.putExtra(FileChooser.ARG_PATH, root);
        startActivityForResult(intent, ACTIVITY_FILE_CHOOSER);
    }

    private void onSave() {
        File file = control.getCurrentFile();
        if (file == null) {
            onSaveAs();
            return;
        }
        saveFile(file);
    }

    private void onSaveAs() {
        Intent intent = new Intent(this, FileSaver.class);
        String path = control.getAbsolutePath();
        intent.putExtra(FileSaver.ARG_PATH, path);
        startActivityForResult(intent, ACTIVITY_FILE_SAVER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode < 0) {
            return;
        }
        switch (requestCode) {
        case ACTIVITY_FILE_CHOOSER:
            onFileChooserResult(data);
            break;
        case ACTIVITY_FILE_SAVER:
            onFileSaverResult(data);
            break;
        }
    }

    private void onFileChooserResult(Intent data) {
        String path = data.getStringExtra(FileChooser.RESULT_PATH);
        if (path.isEmpty()) {
            return;
        }
        openFile(new File(path));
    }

    private void openFile(File file) {
        try {
            String text = control.readFile(file);
            if (text == null) {
                return;
            }
            EditText et = (EditText)findViewById(R.id.edit);
            et.setText(text);
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void onFileSaverResult(Intent data) {
        String path = data.getStringExtra(FileChooser.RESULT_PATH);
        if (path.isEmpty()) {
            return;
        }
        saveFile(new File(path));
    }

    private void saveFile(File file) {
        try {
            EditText et = (EditText)findViewById(R.id.edit);
            String text = et.getText().toString();
            control.saveFile(file, text);
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        File file = control.getCurrentFile();
        if (file == null) {
            return;
        }
        saveFile(file);
    }
}
