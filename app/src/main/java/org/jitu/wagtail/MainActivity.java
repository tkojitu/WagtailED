package org.jitu.wagtail;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends Activity {
    public static final String OI_EXTRA_BUTTON_TEXT = "org.openintents.extra.BUTTON_TEXT";
    public static final String OI_EXTRA_TITLE = "org.openintents.extra.TITLE";
    public static final String OI_ACTION_PICK_DIRECTORY = "org.openintents.action.PICK_DIRECTORY";
    public static final String OI_ACTION_PICK_FILE = "org.openintents.action.PICK_FILE";

    private static final int REQUEST_OI_ACTION_PICK_FILE = 11;
    private static final int REQUEST_OI_ACTION_PICK_DIRECTORY = 12;

    private FileControl fileControl = new FileControl();
    private EditControl editControl = new EditControl();

    private File pickedDirectory = new File(fileControl.getHomePath());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editControl.addTextWatchers(getEdit());
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
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
        case R.id.menu_edit:
            showEditMenu();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void showFileMenu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(R.array.file_menu_items,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onClickFile(dialog, which);
                    }
                });
        builder.create().show();
    }

    private void showEditMenu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(R.array.edit_menu_items,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onClickEdit(dialog, which);
                    }
                });
        builder.create().show();
    }

    public void onClickFile(DialogInterface dialog, int which) {
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

    public void onClickEdit(DialogInterface dialog, int which) {
        Resources r = getResources();
        String[] items = r.getStringArray(R.array.edit_menu_items);
        String item = items[which];
        if (r.getString(R.string.menu_item_cut).equals(item)) {
            editControl.cut(this, getEdit());
        } else if (r.getString(R.string.menu_item_copy).equals(item)) {
            editControl.copy(this, getEdit());
        } else if (r.getString(R.string.menu_item_paste).equals(item)) {
            editControl.paste(this, getEdit());
        } else if (r.getString(R.string.menu_item_undo).equals(item)) {
            editControl.undo(getEdit());
        } else if (r.getString(R.string.menu_item_redo).equals(item)) {
            editControl.redo(getEdit());
        }
    }

    private void onNew() {
        editControl.clear(getEdit());
        fileControl.newFile();
        setTitle();
    }

    private void setTitle() {
        String title = fileControl.getCurrentFileName();
        if (title.isEmpty()) {
            setTitle(R.string.app_name);
        } else {
            setTitle(title);
        }
    }

    private EditText getEdit() {
        return (EditText) findViewById(R.id.edit);
    }

    private void onOpen() {
        String home = fileControl.getHomePath();
        Intent intent = new Intent(OI_ACTION_PICK_FILE);    
        intent.setData(Uri.parse("file://" + home));
        intent.putExtra(OI_EXTRA_TITLE, getString(R.string.oi_open_title));
        intent.putExtra(OI_EXTRA_BUTTON_TEXT, getString(R.string.oi_open_button));
        try {
            startActivityForResult(intent, REQUEST_OI_ACTION_PICK_FILE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.oi_no_filemanager_installed, Toast.LENGTH_SHORT).show();
        }
    }

    private void onSave() {
        File file = fileControl.getCurrentFile();
        if (file == null) {
            onSaveAs();
            return;
        }
        saveFile(file);
    }

    private void onSaveAs() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                EditText edit = (EditText) ((AlertDialog) dialog).findViewById(R.id.filename);
                String filename = edit.getText().toString();
                onFileSaveDialogOk(filename);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        LayoutInflater inflater = getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.file_save_dialog, null));
        builder.create().show();
    }

    public void onClickOpenFileManager(View view) {
        String home = fileControl.getHomePath();
        Intent intent = new Intent(OI_ACTION_PICK_DIRECTORY);
        intent.setData(Uri.parse("file://" + home));
        intent.putExtra(OI_EXTRA_TITLE, getString(R.string.oi_pick_directory_title));
        intent.putExtra(OI_EXTRA_BUTTON_TEXT, getString(R.string.oi_pick_directory_button));
        try {
            startActivityForResult(intent, REQUEST_OI_ACTION_PICK_DIRECTORY);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.oi_no_filemanager_installed, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        switch (requestCode) {
        case REQUEST_OI_ACTION_PICK_FILE:
            onOiActionPickFile(data);
            break;
        case REQUEST_OI_ACTION_PICK_DIRECTORY:
            onOiActionPickDirectory(data);
            break;
        }
    }

    private void onOiActionPickFile(Intent data) {
        String path = data.getDataString();
        if (path == null || path.isEmpty()) {
            return;
        }
        if (path.startsWith("file://")) {
            path = path.substring(7);
        }
        openFile(new File(path));
    }

    private void openFile(File file) {
        String text = fileControl.readFile(file);
        if (text == null) {
            String msg = fileControl.getErrorMessage();
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            return;
        }
        editControl.setText(getEdit(), text);
        setTitle();
    }

    private void onOiActionPickDirectory(Intent data) {
        String dir = data.getDataString();
        if (dir == null || dir.isEmpty()) {
            return;
        }
        if (dir.startsWith("file://")) {
            dir = dir.substring(7);
        }
        pickedDirectory = new File(dir);
    }

    private void onFileSaveDialogOk(String filename) {
        File file = new File(pickedDirectory, filename);
        saveFile(file);
    }

    private void saveFile(File file) {
        String text = editControl.getText(getEdit());
        if (!fileControl.saveFile(file, text)) {
            String msg = fileControl.getErrorMessage();
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }
        setTitle();
    }

    @Override
    protected void onPause() {
        super.onPause();
        File file = fileControl.getCurrentFile();
        if (file == null) {
            return;
        }
        saveFile(file);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            onVolumeDown();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void onVolumeDown() {
        onSave();
        showSavedFileName();
    }

    private void showSavedFileName() {
        File file = fileControl.getCurrentFile();
        if (file == null) {
            return;
        }
        String msg = "Save " + file.getName();
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        fileControl.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        fileControl.onRestoreInstanceState(savedInstanceState);
        setTitle();
        super.onRestoreInstanceState(savedInstanceState);
    }
}
