package com.fpt.customkeyboard;

import android.content.Context;
import android.text.Editable;

import com.fpt.keyboard.BaseKeyboard;
import com.fpt.keyboard.OnSpecialKeyClickListener;

public class ABCKeyboard extends BaseKeyboard {
    public static final int DEFAULT_ABC_XML_LAYOUT = R.xml.keyboard_abc;
    private OnSpecialKeyClickListener onSpecialKeyClickListener;

    public ABCKeyboard(Context context, int xmlLayoutResId) {
        super(context, xmlLayoutResId);
    }

    public ABCKeyboard(Context context, int xmlLayoutResId, int modeId, int width, int height) {
        super(context, xmlLayoutResId, modeId, width, height);
    }

    public ABCKeyboard(Context context, int xmlLayoutResId, int modeId) {
        super(context, xmlLayoutResId, modeId);
    }

    public ABCKeyboard(Context context, int layoutTemplateResId, CharSequence characters, int columns, int horizontalPadding) {
        super(context, layoutTemplateResId, characters, columns, horizontalPadding);
    }

    @Override
    protected boolean specialLimit(String currentText) {
        return false;
    }

    public void setOnSpecialKeyClickListener(OnSpecialKeyClickListener onSpecialKeyClickListener) {
        this.onSpecialKeyClickListener = onSpecialKeyClickListener;
    }

    @Override
    public boolean handleSpecialKey(int primaryCode) {
        Editable editable = getEditText().getText();

        if(primaryCode == getKeyCode(R.integer.action_done)) {
            if(onSpecialKeyClickListener != null) {
                onSpecialKeyClickListener.onDoneKeyClick(editable);
            }
            return true;
        }
        if (primaryCode == getKeyCode(R.integer.hide_keyboard)){
            if(onSpecialKeyClickListener != null) {
                onSpecialKeyClickListener.onHideKeyClick(editable);
            }
            return true;
        }
        return false;
    }
}
