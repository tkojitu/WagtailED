package org.jitu.wagtail;

import android.content.Context;
import android.text.ClipboardManager;
import android.text.Editable;
import android.widget.EditText;

@SuppressWarnings("deprecation")
public class ClipboardControlApi1 extends ClipboardControl {
    public void copy(Context context, EditText edit) {
        CharSequence seq = newSelectedData(edit);
        if (seq == null) {
            return;
        }
        getClipboard(context).setText(seq);
    }

    private CharSequence newSelectedData(EditText edit) {
        CharSequence seq = getSelectedText(edit);
        if (seq.length() == 0) {
            return null;
        }
        return seq;
    }

    private ClipboardManager getClipboard(Context context) {
        return (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    protected void insertClip(Context context, EditText edit) {
        int st = edit.getSelectionStart();
        CharSequence seq = getClipboard(context).getText();
        Editable editable = edit.getEditableText();
        editable.insert(st, seq);
    }
}
