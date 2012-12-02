package org.jitu.wagtail;

import android.content.Context;
import android.widget.EditText;

public class EditControl {
    private EditHistorian historian = new EditHistorian();
    private ClipboardControl clipper = ClipboardControl.newInstance();

    public void copy(Context context, EditText edit) {
        clipper.copy(context, edit);
    }

    public void cut(Context context, EditText edit) {
        clipper.cut(context, edit);
    }

    public void paste(Context context, EditText edit) {
        clipper.paste(context, edit);
    }

    public void addTextWatcher(EditText edit) {
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
}
