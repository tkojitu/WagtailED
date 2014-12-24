package org.jitu.wagtail;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.Selection;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.TextKeyListener;
import android.view.KeyEvent;
import android.view.View;

public class IndentMan extends TextKeyListener implements TextWatcher {
    private boolean enterDown = false;
    private int index = -1;
    private String whites = "  ";

    public IndentMan() {
        super(TextKeyListener.Capitalize.NONE, false);
    }

    public boolean onKeyDown(View view, Editable content, int keyCode, @NonNull KeyEvent event) {
        enterDown = (keyCode == KeyEvent.KEYCODE_ENTER);
        return super.onKeyDown(view, content, keyCode, event);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        index = start + count;
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (enterDown) {
            enterDown = false;
            String leadingWhites = getLeadingWhites(s);
            s.insert(index, leadingWhites);
        }
        enterDown = false;
    }

    private String getLeadingWhites(Editable s) {
        if (index > s.length()) {
            return "";
        }
        if (s.charAt(index - 1) != '\n') {
            return "";
        }
        if (index - 2 < 0) {
            return "";
        }
        int n = s.toString().lastIndexOf("\n", index - 2);
        n = (n < 0) ? 0 : n + 1;
        StringBuilder buf = new StringBuilder();
        while (n < s.length() && s.charAt(n) == ' ') {
            buf.append(' ');
            ++n;
        }
        return buf.toString();
    }

    public void tabify(Editable s) {
        int st = Selection.getSelectionStart(s);
        int ed = Selection.getSelectionEnd(s);
        s.setSpan(this, st, ed, Spanned.SPAN_MARK_POINT);
        try {
            Selection.setSelection(s, ed);
            for (;;) {
                int pos = moveBeginningOfLine(s);
                s.insert(pos, whites);
                pos = moveBeginningOfLine(s);
                int mark = s.getSpanStart(this);
                if (pos <= mark) {
                    break;
                }
                if (pos == backwardChar(s)) {
                    break;
                }
            }
            st = s.getSpanStart(this);
            ed = s.getSpanEnd(this);
            Selection.setSelection(s, st, ed);
        } finally {
            s.removeSpan(this);
        }
    }

    private int moveBeginningOfLine(Editable s) {
        int pos = Selection.getSelectionStart(s);
        if (pos == 0) {
            Selection.setSelection(s, 0);
            return 0;
        }
        for (; pos > 0; --pos) {
            if (s.charAt(pos - 1) == '\n') {
                Selection.setSelection(s, pos);
                return pos;
            }
        }
        Selection.setSelection(s, 0);
        return 0;
    }

    private int backwardChar(Editable s) {
        int pos = Selection.getSelectionStart(s);
        if (pos == 0) {
            return 0;
        }
        Selection.setSelection(s, pos - 1);
        return pos - 1;
    }

    public void untabify(Editable s) {
        int st = Selection.getSelectionStart(s);
        int ed = Selection.getSelectionEnd(s);
        s.setSpan(this, st, ed, Spanned.SPAN_MARK_POINT);
        try {
            Selection.setSelection(s, ed);
            for (;;) {
                int pos = moveBeginningOfLine(s);
                for (int i = 0; i < whites.length(); ++i) {
                    if (s.length() == 0 || s.charAt(pos) != ' ') {
                        break;
                    }
                    s.delete(pos, pos + 1);
                }
                pos = moveBeginningOfLine(s);
                int mark = s.getSpanStart(this);
                if (pos <= mark) {
                    break;
                }
                if (pos == backwardChar(s)) {
                    break;
                }
            }
            st = s.getSpanStart(this);
            ed = s.getSpanEnd(this);
            Selection.setSelection(s, st, ed);
        } finally {
            s.removeSpan(this);
        }
    }
}
