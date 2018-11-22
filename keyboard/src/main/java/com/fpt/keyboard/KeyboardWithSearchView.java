package com.fpt.keyboard;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.List;

public class KeyboardWithSearchView extends LinearLayout {
    private Context mContext;

    private RecyclerView mRecyclerView;

    private BaseKeyboardView mBaseKeyboardView;

    private LinearLayout mKeyboardViewContainer;

    public KeyboardWithSearchView(Context context) {
        super(context);
        init(context);
    }

    public KeyboardWithSearchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public KeyboardWithSearchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public KeyboardWithSearchView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    protected RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    protected BaseKeyboardView getBaseKeyboardView() {
        return mBaseKeyboardView;
    }

    protected LinearLayout getKeyboardViewContainer() {
        return mKeyboardViewContainer;
    }

    private void init(Context context) {
        this.mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.layout_recycler_keyboard_view,this,true);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.search_recycler_view);
        mBaseKeyboardView = (BaseKeyboardView) view.findViewById(R.id.keyboard_view);
        mKeyboardViewContainer = (LinearLayout) view.findViewById(R.id.keyboard_container);
    }

    protected void setRecyclerViewAdapter(RecyclerView.Adapter adapter) {
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false));
    }

    /**
     * 设置结果集,并更新recycylerview是否显示
     * @param list
     * @param hasFixedSize
     */
    protected void setResults(List list, boolean hasFixedSize) {
        if(mRecyclerView.getAdapter() == null) {
            throw new RuntimeException("this view has not invoked init method");
        }
        mRecyclerView.getLayoutManager().scrollToPosition(0);
        if(list == null || list.size() ==0) {
            mRecyclerView.setVisibility(GONE);
        } else {
            mRecyclerView.setVisibility(VISIBLE);
        }
        mRecyclerView.setHasFixedSize(hasFixedSize);
    }

}
