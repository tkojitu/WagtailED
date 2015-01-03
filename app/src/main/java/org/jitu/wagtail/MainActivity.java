package org.jitu.wagtail;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.gesture.GestureOverlayView;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements View.OnClickListener {
    private EditControl editControl = new EditControl();
    private MenuFileMan menuFileMan = new MenuFileMan(this);
    private GestureControl gestureControl = new GestureControl(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editControl.addTextWatchers(getEdit());
        setupGestures();
        setupArrows();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        handleIntent();
    }

    private void setupGestures() {
        gestureControl.onCreate();
        getGestureView().addOnGesturePerformedListener(gestureControl);
        hideGestureView();
    }

    private GestureOverlayView getGestureView() {
        return (GestureOverlayView) findViewById(R.id.gestureView);
    }

    private void handleIntent() {
        Intent intent = getIntent();
        String action = intent.getAction();
        if (Intent.ACTION_EDIT.equals(action) || Intent.ACTION_VIEW.equals(action)) {
            menuFileMan.openUri(intent.getData());
        }
    }

    private void setupArrows() {
        findViewById(R.id.button_down).setOnClickListener(this);
        findViewById(R.id.button_left).setOnClickListener(this);
        findViewById(R.id.button_right).setOnClickListener(this);
        findViewById(R.id.button_up).setOnClickListener(this);
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
            menuFileMan.showFileMenu();
            return true;
        case R.id.menu_edit:
            showEditMenu();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void showEditMenu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(R.array.edit_menu_items,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onClickEdit(which);
                    }
                });
        builder.create().show();
    }

    public void onClickEdit(int which) {
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

    private EditText getEdit() {
        return (EditText) findViewById(R.id.edit);
    }

    public String getEditText() {
        return editControl.getText(getEdit());
    }

    public void setEditText(String text) {
        editControl.setText(getEdit(), text);
    }

    public void clearEditText() {
        editControl.clear(getEdit());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        menuFileMan.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        menuFileMan.saveCurrentFile();
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            onVolumeDown();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void onVolumeDown() {
        menuFileMan.onSave();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        menuFileMan.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        menuFileMan.onRestoreInstanceState(savedInstanceState);
        super.onRestoreInstanceState(savedInstanceState);
    }

    public void onGestureClicked(View view) {
        ToggleButton button = (ToggleButton) view;
        if (button.isChecked()) {
            int color = getResources().getColor(R.color.deepskyblue);
            button.setTextColor(color);
            showGestureView();
        } else {
            int color = getResources().getColor(R.color.black);
            button.setTextColor(color);
            hideGestureView();
        }
    }

    private void showGestureView() {
        getGestureView().setVisibility(View.VISIBLE);
    }

    private void hideGestureView() {
        getGestureView().setVisibility(View.INVISIBLE);
    }

    public void moveCursorHome() {
        editControl.moveCursorHome(getEdit());
        getScroller().fullScroll(View.FOCUS_UP);
    }

    public void moveCursorEnd() {
        editControl.moveCursorEnd(getEdit());
        getScroller().fullScroll(View.FOCUS_DOWN);
    }

    private ScrollView getScroller() {
        return (ScrollView) findViewById(R.id.scroller);
    }

    public void tabify() {
        editControl.tabify(getEdit());
    }

    public void untabify() {
        editControl.untabify(getEdit());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.button_down:
            editControl.handleArrowDown(getEdit());
            break;
        case R.id.button_left:
            editControl.handleArrowLeft(getEdit());
            break;
        case R.id.button_right:
            editControl.handleArrowRight(getEdit());
            break;
        case R.id.button_up:
            editControl.handleArrowUp(getEdit());
            break;
        }
    }
}
