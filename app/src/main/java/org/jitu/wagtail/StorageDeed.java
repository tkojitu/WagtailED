package org.jitu.wagtail;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;

public class StorageDeed implements View.OnClickListener {
    public static final String OI_EXTRA_BUTTON_TEXT = "org.openintents.extra.BUTTON_TEXT";
    public static final String OI_EXTRA_TITLE = "org.openintents.extra.TITLE";
    public static final String OI_ACTION_PICK_DIRECTORY = "org.openintents.action.PICK_DIRECTORY";
    public static final String OI_ACTION_PICK_FILE = "org.openintents.action.PICK_FILE";

    public static final int REQUEST_OI_ACTION_PICK_FILE = 11;
    public static final int REQUEST_OI_ACTION_PICK_DIRECTORY = 12;

    private MainActivity activity;
    private FileControl fileControl;
    private File pickedDirectory;

    public StorageDeed(MainActivity activity) {
        this.activity = activity;
        fileControl = new FileControl(activity);
        pickedDirectory = new File(fileControl.getHomePath());
    }

    public boolean saveUri(Uri uri, String text) {
        return fileControl.saveUri(uri, text);
    }

    public void onSaveInstanceState(Bundle outState) {
        fileControl.onSaveInstanceState(outState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        fileControl.onRestoreInstanceState(savedInstanceState);
        setAppTitle();
    }

    public void showFileMenu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setItems(R.array.file_menu_items,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onClickFile(which);
                    }
                });
        builder.create().show();
    }

    public void onClickFile(int which) {
        Resources r = activity.getResources();
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
        fileControl.newFile();
        activity.clearEditText();
        setAppTitle();
    }

    private void onOpen() {
        String home = fileControl.getHomePath();
        Intent intent = new Intent(OI_ACTION_PICK_FILE);
        intent.setData(Uri.parse("file://" + home));
        intent.putExtra(OI_EXTRA_TITLE, activity.getString(R.string.oi_open_title));
        intent.putExtra(OI_EXTRA_BUTTON_TEXT, activity.getString(R.string.oi_open_button));
        try {
            activity.startActivityForResult(intent, REQUEST_OI_ACTION_PICK_FILE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, R.string.oi_no_filemanager_installed, Toast.LENGTH_SHORT).show();
        }
    }

    public void onSave() {
        Uri uri = fileControl.getCurrentUri();
        if (uri == null) {
            onSaveAs();
            return;
        }
        saveUri(uri);
    }

    private void onSaveAs() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
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
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.file_save_dialog, null);
        ImageButton button = (ImageButton) view.findViewById(R.id.file_manager);
        button.setOnClickListener(this);
        builder.setView(view);
        builder.create().show();
    }

    private void onFileSaveDialogOk(String filename) {
        File file = new File(pickedDirectory, filename);
        Uri uri = Uri.fromFile(file);
        saveUri(uri);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.file_manager) {
            onClickOpenFileManager();
        }
    }

    private void onClickOpenFileManager() {
        String home = fileControl.getHomePath();
        Intent intent = new Intent(OI_ACTION_PICK_DIRECTORY);
        intent.setData(Uri.parse("file://" + home));
        intent.putExtra(OI_EXTRA_TITLE, activity.getString(R.string.oi_pick_directory_title));
        intent.putExtra(OI_EXTRA_BUTTON_TEXT, activity.getString(R.string.oi_pick_directory_button));
        try {
            activity.startActivityForResult(intent, REQUEST_OI_ACTION_PICK_DIRECTORY);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, R.string.oi_no_filemanager_installed, Toast.LENGTH_SHORT).show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK || data == null) {
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
        File file = new File(path);
        Uri uri = Uri.fromFile(file);
        openUri(uri);
    }

    public void openUri(Uri uri) {
        String text = fileControl.readUri(uri);
        if (text == null) {
            String msg = fileControl.getErrorMessage();
            Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
            return;
        }
        activity.setEditText(text);
        setAppTitle();
    }

    private void saveUri(Uri uri) {
        String text = activity.getEditText();
        if (!saveUri(uri, text)) {
            String msg = fileControl.getErrorMessage();
            Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
        }
        setAppTitle();
    }

    public void saveCurrentFile() {
        saveUri(fileControl.getCurrentUri());
    }

    private void setAppTitle() {
        String title = fileControl.getCurrentFileName();
        if (title.isEmpty()) {
            activity.setTitle(R.string.app_name);
        } else {
            activity.setTitle(title);
        }
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
}
