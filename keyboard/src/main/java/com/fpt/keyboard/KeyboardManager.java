package com.fpt.keyboard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;

import java.lang.reflect.Method;
import java.util.List;

public class KeyboardManager {

    protected static final String TAG = "KeyboardManager";

    protected Context mContext;

    protected ViewGroup mRootView;

    protected KeyboardWithSearchView mKeyboardWithSearchView;

    protected FrameLayout.LayoutParams mKeyboardContainerLayoutParams;

    protected BaseKeyboard.DefaultKeyStyle mDefaultKeyStyle = new BaseKeyboard.DefaultKeyStyle();

    private Handler mHandler = new Handler(Looper.getMainLooper());

    public KeyboardManager(Context context) {
        mContext = context;
        if (mContext instanceof Activity) {
            mRootView = (ViewGroup) ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
            mKeyboardWithSearchView = new KeyboardWithSearchView(mContext);
            mKeyboardContainerLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                    .LayoutParams.WRAP_CONTENT);
            mKeyboardContainerLayoutParams.gravity = Gravity.BOTTOM;
        } else {
            Log.e(TAG, "context must be activity");
        }
    }

    public void setRecyclerViewAdapter(RecyclerView.Adapter adapter) {
        mKeyboardWithSearchView.setRecyclerViewAdapter(adapter);
    }

    public void setPadding(int dpl,int dpt,int dpr,int dpb) {
        BaseKeyboardView keyboardView = mKeyboardWithSearchView.getBaseKeyboardView();
        keyboardView.setPadding(ConvertUtils.dp2Px(mContext,dpl),ConvertUtils.dp2Px(mContext,dpt),ConvertUtils.dp2Px(mContext,dpr),ConvertUtils.dp2Px(mContext,dpb));
    }

    public void setSearchResults(List data) {
        mKeyboardWithSearchView.setResults(data,false);
    }

    public void bindToEditor(EditText editText, BaseKeyboard keyboard) {
        hideSystemSoftKeyboard(editText);
        editText.setTag(R.id.bind_keyboard_2_editor, keyboard);
        if (keyboard.getKeyStyle() == null) {
            keyboard.setKeyStyle(mDefaultKeyStyle);
        }
        editText.setOnFocusChangeListener(editorFocusChangeListener);
    }

    private BaseKeyboard getBindKeyboard(EditText editText) {
        if (editText != null) {
            return (BaseKeyboard) editText.getTag(R.id.bind_keyboard_2_editor);
        }
        return null;
    }

    private void initKeyboard(BaseKeyboard keyboard) {
        mKeyboardWithSearchView.getBaseKeyboardView().setKeyboard(keyboard);
        mKeyboardWithSearchView.getBaseKeyboardView().setEnabled(true);
        mKeyboardWithSearchView.getBaseKeyboardView().setPreviewEnabled(false);
        mKeyboardWithSearchView.getBaseKeyboardView().setOnKeyboardActionListener(keyboard);
    }

    public void setShowAnchorView(View showAnchorView, EditText editText) {
        editText.setTag(R.id.anchor_view,showAnchorView);
    }

    public void showSoftKeyboard(EditText editText) {
        mRootView.addOnLayoutChangeListener(mOnLayoutChangeListener);
        BaseKeyboard keyboard = getBindKeyboard(editText);
        if (keyboard == null) {
            Log.e(TAG, "edit text not bind to keyboard");
            return;
        }
        keyboard.setEditText(editText);
        initKeyboard(keyboard);
        mKeyboardWithSearchView.getKeyboardViewContainer().setPadding(ConvertUtils.dp2Px(mContext, keyboard.getPadding().left),
                ConvertUtils.dp2Px(mContext, keyboard.getPadding().top),
                ConvertUtils.dp2Px(mContext, keyboard.getPadding().right),
                ConvertUtils.dp2Px(mContext, keyboard.getPadding().bottom));
        if(mRootView.indexOfChild(mKeyboardWithSearchView) == -1) {
            mRootView.addView(mKeyboardWithSearchView, mKeyboardContainerLayoutParams);
        }else {
            mKeyboardWithSearchView.setVisibility(View.VISIBLE);
        }
        mKeyboardWithSearchView.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.down_to_up));

        if (onSoftKeyboardDisplayListener != null){
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    BaseKeyboard keyboard = (BaseKeyboard) mKeyboardWithSearchView.getBaseKeyboardView().getKeyboard();
                    EditText editText = keyboard.getEditText();
                    onSoftKeyboardDisplayListener.onShow(editText.getText().toString());
                }
            },200);
        }
    }

    private void hideSoftKeyboard() {
        mKeyboardWithSearchView.setVisibility(View.GONE);
        mKeyboardWithSearchView.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.up_to_hide));

        if (onSoftKeyboardDisplayListener != null){
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    BaseKeyboard keyboard = (BaseKeyboard) mKeyboardWithSearchView.getBaseKeyboardView().getKeyboard();
                    EditText editText = keyboard.getEditText();
                    onSoftKeyboardDisplayListener.onDismiss(editText.getText().toString());
                }
            },200);
        }
    }

    public void hideKeyboard() {
        if(mRootView != null) {
            mRootView.clearFocus();
        }
    }

    public static void hideSystemSoftKeyboard(EditText editText) {
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= 11) {
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(editText, false);
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            editText.setInputType(InputType.TYPE_NULL);
        }
    }
    private OnSoftKeyboardDisplayListener onSoftKeyboardDisplayListener;

    public void setOnSoftKeyboardDisplayListener(OnSoftKeyboardDisplayListener onSoftKeyboardDisplayListener) {
        this.onSoftKeyboardDisplayListener = onSoftKeyboardDisplayListener;
    }

    private final View.OnFocusChangeListener editorFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(final View v, boolean hasFocus) {
            if (v instanceof EditText) {
                if (hasFocus) {
                    showSoftKeyboard((EditText) v);
                } else {
                    hideSoftKeyboard();
                }
            }
        }
    };

    private final ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            int hasMoved = 0;
            Object heightTag = mRootView.getTag(R.id.scroll_height_by_keyboard);
            if (heightTag != null) {
                hasMoved = (int) heightTag;
            }
            if(mKeyboardWithSearchView.getVisibility() == View.GONE) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mRootView.getViewTreeObserver().removeOnGlobalLayoutListener(mOnGlobalLayoutListener);
                }else {
                    mRootView.getViewTreeObserver().removeGlobalOnLayoutListener(mOnGlobalLayoutListener);
                }
                if (hasMoved > 0) {
                    mRootView.getChildAt(0).scrollBy(0, -1 * hasMoved);
                    mRootView.setTag(R.id.scroll_height_by_keyboard, 0);
                }
            } else {
                BaseKeyboard keyboard = (BaseKeyboard) mKeyboardWithSearchView.getBaseKeyboardView().getKeyboard();
                EditText editText = keyboard.getEditText();


                Rect rect = new Rect();
                mRootView.getWindowVisibleDisplayFrame(rect);

                int[] etLocation = new int[2];
                editText.getLocationOnScreen(etLocation);
                int keyboardTop = etLocation[1] + editText.getHeight() + editText.getPaddingTop() + editText.getPaddingBottom() + 1 ;   //1px is a divider
                Object anchor = editText.getTag(R.id.anchor_view);
                View mShowAnchorView = null;
                if(anchor != null && anchor instanceof View) {
                    mShowAnchorView = (View) anchor;
                }
                if (mShowAnchorView != null) {
                    int[] saLocation = new int[2];
                    mShowAnchorView.getLocationOnScreen(saLocation);
                    keyboardTop = saLocation[1] + mShowAnchorView.getHeight() + mShowAnchorView.getPaddingTop() + mShowAnchorView   //1px is a divider
                            .getPaddingBottom() + 1;
                }
                int moveHeight = keyboardTop + mKeyboardWithSearchView.getHeight() - rect.bottom;
                //height > 0 rootview 需要继续上滑
                if(moveHeight > 0) {
                    mRootView.getChildAt(0).scrollBy(0, moveHeight);
                    mRootView.setTag(R.id.scroll_height_by_keyboard,hasMoved + moveHeight);
                }else {
                    int moveBackHeight = Math.min(hasMoved,Math.abs(moveHeight));
                    if(moveBackHeight >0) {
                        mRootView.getChildAt(0).scrollBy(0, -1 * moveBackHeight);
                        mRootView.setTag(R.id.scroll_height_by_keyboard,hasMoved - moveBackHeight);
                    }
                }

            }
        }
    };

    private final View.OnLayoutChangeListener mOnLayoutChangeListener = new View.OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int
                oldRight, int oldBottom) {
            int hasMoved = 0;
            Object heightTag = mRootView.getTag(R.id.scroll_height_by_keyboard);
            if (heightTag != null) {
                hasMoved = (int) heightTag;
            }
            if(mKeyboardWithSearchView.getVisibility() == View.GONE) {
                mRootView.removeOnLayoutChangeListener(mOnLayoutChangeListener);
                if (hasMoved > 0) {
                    mRootView.getChildAt(0).scrollBy(0, -1 * hasMoved);
                    mRootView.setTag(R.id.scroll_height_by_keyboard, 0);
                }
            } else {
                BaseKeyboard keyboard = (BaseKeyboard) mKeyboardWithSearchView.getBaseKeyboardView().getKeyboard();
                EditText editText = keyboard.getEditText();

                Rect rect = new Rect();
                mRootView.getWindowVisibleDisplayFrame(rect);

                int[] etLocation = new int[2];
                editText.getLocationOnScreen(etLocation);
                int keyboardTop = etLocation[1] + editText.getHeight() + editText.getPaddingTop() + editText.getPaddingBottom() + 1 ;   //1px is a divider
                Object anchor = editText.getTag(R.id.anchor_view);
                View mShowAnchorView = null;
                if(anchor != null && anchor instanceof View) {
                    mShowAnchorView = (View) anchor;
                }
                if (mShowAnchorView != null) {
                    int[] saLocation = new int[2];
                    mShowAnchorView.getLocationOnScreen(saLocation);
                    keyboardTop = saLocation[1] + mShowAnchorView.getHeight() + mShowAnchorView.getPaddingTop() + mShowAnchorView   //1px is a divider
                            .getPaddingBottom() + 1;
                }
                int moveHeight = keyboardTop + mKeyboardWithSearchView.getHeight() - rect.bottom;
                //height > 0 rootview 需要继续上滑
                if(moveHeight > 0) {
                    mRootView.getChildAt(0).scrollBy(0, moveHeight);
                    mRootView.setTag(R.id.scroll_height_by_keyboard,hasMoved + moveHeight);
                }else {
                    int moveBackHeight = Math.min(hasMoved,Math.abs(moveHeight));
                    if(moveBackHeight >0) {
                        mRootView.getChildAt(0).scrollBy(0, -1 * moveBackHeight);
                        mRootView.setTag(R.id.scroll_height_by_keyboard,hasMoved - moveBackHeight);
                    }
                }

            }
        }
    };

}
