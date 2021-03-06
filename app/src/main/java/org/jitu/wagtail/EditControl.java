package org.jitu.wagtail;

import android.content.Context;
import android.text.Selection;
import android.util.Log;
import android.widget.EditText;

public class EditControl {
    private EditHistorian historian = new EditHistorian();
    private ClipboardControl clipper = ClipboardControl.newInstance();
    private IndentMan man = new IndentMan();

    public void copy(Context context, EditText edit) {
        clipper.copy(context, edit);
    }

    public void cut(Context context, EditText edit) {
        clipper.cut(context, edit);
    }

    public void paste(Context context, EditText edit) {
        clipper.paste(context, edit);
    }

    public void addTextWatchers(EditText edit) {
        edit.addTextChangedListener(man);
        edit.setKeyListener(man);
        edit.addTextChangedListener(historian);
    }

    public void undo(EditText edit) {
        historian.undo(edit.getText());
    }

    public void redo(EditText edit) {
        historian.redo(edit.getText());
    }

    public void clear(EditText edit) {
        edit.setText("");
    }

    public void setText(EditText edit, String text) {
        edit.setText(text);
        edit.setSelection(text.length());
    }

    public String getText(EditText edit) {
        return edit.getText().toString();
    }

    public void moveCursorHome(EditText edit) {
        edit.setSelection(0);
    }

    public void moveCursorEnd(EditText edit) {
        edit.setSelection(edit.getText().length());
    }

    public void tabify(EditText edit) {
        man.tabify(edit.getEditableText());
    }

    public void untabify(EditText edit) {
        man.untabify(edit.getEditableText());
    }

    public void handleArrowDown(EditText edit) {
        Selection.extendDown(edit.getText(), edit.getLayout());
    }

    public void handleArrowLeft(EditText edit) {
        Selection.extendLeft(edit.getText(), edit.getLayout());
    }

    public void handleArrowRight(EditText edit) {
        Selection.extendRight(edit.getText(), edit.getLayout());
    }

    public void handleArrowUp(EditText edit) {
        Selection.extendUp(edit.getText(), edit.getLayout());
    }
}
