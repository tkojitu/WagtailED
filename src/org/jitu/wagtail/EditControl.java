package org.jitu.wagtail;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.Editable;
import android.widget.EditText;

public class EditControl {
    private EditHistorian historian = new EditHistorian();

    public void copy(Context context, EditText edit) {
        ClipData data = newSelectedData(edit);
        if (data == null) {
            return;
        }
        getClipboard(context).setPrimaryClip(data);
    }

    private ClipData newSelectedData(EditText edit) {
        CharSequence seq = getSelectedText(edit);
        if (seq.length() == 0) {
            return null;
        }
        return ClipData.newPlainText("text", seq);
    }

    private CharSequence getSelectedText(EditText edit) {
        CharSequence seq = edit.getText();
        int st = edit.getSelectionStart();
        int ed = edit.getSelectionEnd();
        return seq.subSequence(st, ed);
    }

    private ClipboardManager getClipboard(Context context) {
        return (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    public void cut(Context context, EditText edit) {
        copy(context, edit);
        delete(edit);
    }

    private void delete(EditText edit) {
        int st = edit.getSelectionStart();
        int ed = edit.getSelectionEnd();
        if (st == ed) {
            return;
        }
        Editable editable = edit.getEditableText();
        editable.delete(st, ed);
    }

    public void paste(Context context, EditText edit) {
        delete(edit);
        insertClip(context, edit);
    }

    private void insertClip(Context context, EditText edit) {
        int st = edit.getSelectionStart();
        ClipData data = getClipboard(context).getPrimaryClip();
        Editable editable = edit.getEditableText();
        CharSequence text = data.getItemAt(0).getText();
        editable.insert(st, text);
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
    }

    public String getText(EditText edit) {
        return edit.getText().toString();
    }
}
