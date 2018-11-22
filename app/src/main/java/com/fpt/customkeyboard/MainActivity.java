package com.fpt.customkeyboard;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.fpt.keyboard.BaseKeyboard;
import com.fpt.keyboard.ConvertUtils;
import com.fpt.keyboard.KeyboardManager;
import com.fpt.keyboard.OnSoftKeyboardDisplayListener;
import com.fpt.keyboard.OnSpecialKeyClickListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private EditText et_abc,et_123;
    private KeyboardManager keyboardManagerAbc,keyboardManagerNumber;
    private NumberKeyboard numberKeyboard;
    private ABCKeyboard abcKeyboard;
    private HintAdapter hintAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_abc = findViewById(R.id.et_abc);
        et_123 = findViewById(R.id.et_123);

        keyboardManagerAbc = new KeyboardManager(this);
        keyboardManagerNumber = new KeyboardManager(this);
        hintAdapter = new HintAdapter();
        numberKeyboard = new NumberKeyboard(this,NumberKeyboard.DEFAULT_NUMBER_XML_LAYOUT);
        abcKeyboard = new ABCKeyboard(this, ABCKeyboard.DEFAULT_ABC_XML_LAYOUT);

        initAbcKeyboardManager();
        initAbcKeyBoard();
        initHintAdapter();
        initNumberKeyboard(this);

        keyboardManagerAbc.bindToEditor(et_abc, abcKeyboard);
        et_abc.addTextChangedListener(watcher);
        keyboardManagerNumber.bindToEditor(et_123, numberKeyboard);

    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() == 0){
                //停止所有的网络请求
                keyboardManagerAbc.setSearchResults(null);
                hintAdapter.setNewData(null);
                return;
            }
            //请求网络模糊搜索数据集
            List<String> stringList = new ArrayList<>();
            for (int i = 0; i < s.length(); i++) {
                stringList.add(String.format("测试数据%s",i+1));
            }
            keyboardManagerAbc.setSearchResults(stringList);
            hintAdapter.setNewData(stringList);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    /**
     * 初始化数字键盘
     */
    private void initNumberKeyboard(final Context mContext) {

        numberKeyboard.setEnableDotInput(true);

        numberKeyboard.setKeyStyle(new BaseKeyboard.KeyStyle() {
            @Override
            public Drawable getKeyBackground(Keyboard.Key key) {
                if(key.iconPreview != null) {
                    return key.iconPreview;
                } else {
                    return ContextCompat.getDrawable(mContext, R.drawable.keyboard_bg);
                }
            }

            @Override
            public Float getKeyTextSize(Keyboard.Key key) {
                if(key.codes[0] == mContext.getResources().getInteger(R.integer.action_done)) {
                    return ConvertUtils.sp2Px(mContext,20f);
                }
                return ConvertUtils.sp2Px(mContext,24f);
            }

            @Override
            public Integer getKeyTextColor(Keyboard.Key key) {
                if(key.codes[0] == mContext.getResources().getInteger(R.integer.action_done)) {
                    return Color.WHITE;
                }
                return null;
            }

            @Override
            public CharSequence getKeyLabel(Keyboard.Key key) {
                return null;
            }
        });

        numberKeyboard.setOnSpecialKeyClickListener(new OnSpecialKeyClickListener() {
            @Override
            public void onDoneKeyClick(CharSequence charSequence) {
                Log.d(TAG,charSequence.toString());
            }

            @Override
            public void onHideKeyClick(CharSequence charSequence) {
                Log.d(TAG,charSequence.toString());
            }
        });

    }

    /**
     * 初始化字母键盘
     */
    private void initAbcKeyBoard() {
        abcKeyboard.setOnSpecialKeyClickListener(new OnSpecialKeyClickListener() {
            @Override
            public void onDoneKeyClick(CharSequence charSequence) {
                Log.d(TAG,charSequence.toString());
            }

            @Override
            public void onHideKeyClick(CharSequence charSequence) {
                Log.d(TAG,charSequence.toString());
            }
        });
    }

    /**
     * 初始化字母键盘
     */
    private void initAbcKeyboardManager() {
        keyboardManagerAbc.setPadding(0,12,0,12);
        keyboardManagerAbc.setOnSoftKeyboardDisplayListener(new OnSoftKeyboardDisplayListener() {
            @Override
            public void onShow(String str) {

            }

            @Override
            public void onDismiss(String str) {
                keyboardManagerAbc.setSearchResults(null);
                hintAdapter.setNewData(null);
            }
        });
        keyboardManagerAbc.setRecyclerViewAdapter(hintAdapter);
    }

    /**
     * 初始化隐藏内容
     */
    private void initHintAdapter() {
        hintAdapter.setOnItemClickListener(new HintAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.Adapter adapter, View view, int position) {
                String str = hintAdapter.getData().get(position);
                keyboardManagerAbc.setSearchResults(null);
                hintAdapter.setNewData(null);
                et_abc.setText(str);
                et_abc.setSelection(str.length());

                et_abc.clearFocus();
                et_123.requestFocus();
                et_123.setSelection(et_123.getText().length());
            }
        });
    }
}
