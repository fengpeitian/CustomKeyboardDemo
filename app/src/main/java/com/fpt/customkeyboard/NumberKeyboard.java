package com.fpt.customkeyboard;

import android.content.Context;
import android.text.Editable;

import com.fpt.keyboard.BaseKeyboard;
import com.fpt.keyboard.OnSpecialKeyClickListener;

public class NumberKeyboard extends BaseKeyboard {

    public static final int DEFAULT_NUMBER_XML_LAYOUT = R.xml.keyboard_number;

    private boolean enableDotInput = true;

    private OnSpecialKeyClickListener onSpecialKeyClickListener;

    public NumberKeyboard(Context context, int xmlLayoutResId) {
        super(context, xmlLayoutResId);
    }

    public NumberKeyboard(Context context, int xmlLayoutResId, int modeId, int width, int height) {
        super(context, xmlLayoutResId, modeId, width, height);
    }

    public NumberKeyboard(Context context, int xmlLayoutResId, int modeId) {
        super(context, xmlLayoutResId, modeId);
    }

    public NumberKeyboard(Context context, int layoutTemplateResId, CharSequence characters, int columns, int horizontalPadding) {
        super(context, layoutTemplateResId, characters, columns, horizontalPadding);
    }

    public void setOnSpecialKeyClickListener(OnSpecialKeyClickListener onSpecialKeyClickListener) {
        this.onSpecialKeyClickListener = onSpecialKeyClickListener;
    }

    public void setEnableDotInput(boolean enableDotInput) {
        this.enableDotInput = enableDotInput;
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        super.onKey(primaryCode, keyCodes);
    }

    @Override
    protected boolean specialLimit(String number) {
        if (number.contains(".")){
            String[] s = number.split("\\.");
            if (s.length == 2){
                if (s[1].length() < 2){
                    return false;
                }
            }else {
                return false;
            }
        }else {
            if (number.length() < 4){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean handleSpecialKey(int primaryCode) {
        Editable editable = getEditText().getText();
        int start = getEditText().getSelectionStart();
        //小数点
        if(primaryCode == 46) {
            if (!enableDotInput) {
                return true;
            }
            if(!editable.toString().contains(".")){
                if(!editable.toString().startsWith(".")) {
                    editable.insert(start, Character.toString((char) primaryCode));
                }else {
                    editable.insert(start, "0"+Character.toString((char) primaryCode));
                }
            }
            return true;
        }
        if(primaryCode == getKeyCode(R.integer.action_done)) {
            if(onSpecialKeyClickListener != null) {
                onSpecialKeyClickListener.onDoneKeyClick(editable);
            }
            getEditText().clearFocus();
            getEditText().setFocusableInTouchMode(false);
            return true;
        }
        if (primaryCode == getKeyCode(R.integer.hide_keyboard)){
            if(onSpecialKeyClickListener != null) {
                onSpecialKeyClickListener.onHideKeyClick(editable);
            }
            getEditText().clearFocus();
            getEditText().setFocusableInTouchMode(false);
            return true;
        }
        return false;
    }

}
