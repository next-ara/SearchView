package com.next.view.search;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * ClassName:搜索控件类
 *
 * @author Afton
 * @time 2023/9/20
 * @auditor
 */
public class SearchView extends LinearLayout {

    //搜索监听接口
    public interface OnSearchListener {

        /**
         * 搜索
         *
         * @param charSequence 输入内容
         */
        void onSearch(CharSequence charSequence);
    }

    //文本提示控件
    private TextView tipsView;

    //搜索提示布局控件
    private LinearLayout tipsLayout;

    //搜索按钮控件
    private ImageView searchButton;

    //输入控件
    private EditText editText;

    //搜索布局控件
    private RelativeLayout searchLayout;

    //搜索监听接口
    private OnSearchListener onSearchListener;

    public SearchView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    /**
     * 设置搜索输入提示
     *
     * @param hint 提示
     */
    public void setSearchHint(String hint) {
        this.editText.setHint(hint);
    }

    /**
     * 设置提示
     *
     * @param content 内容
     */
    public void setTips(String content) {
        this.tipsView.setText(content);
    }

    /**
     * 初始化
     */
    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.next_view_search, this);

        this.tipsView = this.findViewById(R.id.tv_tips);
        this.tipsLayout = this.findViewById(R.id.layout_search_normal);
        this.searchButton = this.findViewById(R.id.btn_search);
        this.editText = this.findViewById(R.id.edit_search);
        this.searchLayout = this.findViewById(R.id.layout_search);

        this.isOpenInputMode(false);
        this.setSearchListener();
        this.setOnFocusChangeListener();
    }

    /**
     * 打开输入键盘
     */
    private void showSoftInput() {
        this.editText.setFocusable(true);
        this.editText.setFocusableInTouchMode(true);
        this.editText.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(editText, 0);
    }

    /**
     * 关闭输入键盘
     */
    private void closeSoftInput() {
        this.editText.clearFocus();
        InputMethodManager imm = (InputMethodManager) this.editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.editText.getWindowToken(), 0);
    }

    /**
     * 是否开启输入模式
     *
     * @param isOpenInputMode 是否开启输入模式
     */
    private void isOpenInputMode(boolean isOpenInputMode) {
        if (isOpenInputMode) {
            this.tipsLayout.setVisibility(GONE);
            this.searchButton.setVisibility(VISIBLE);
            this.editText.setVisibility(VISIBLE);
            this.closeSearchLayoutClickListener();
            this.showSoftInput();
        } else {
            this.closeSoftInput();

            if (!TextUtils.isEmpty(this.editText.getText().toString())) {
                return;
            }

            this.tipsLayout.setVisibility(VISIBLE);
            this.searchButton.setVisibility(GONE);
            this.editText.setVisibility(GONE);
            this.setSearchLayoutClickListener();
        }
    }

    /**
     * 设置搜索监听接口
     */
    private void setSearchListener() {
        this.editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                this.sendInputListener(editText.getText());
                this.isOpenInputMode(false);
                return true;
            }
            return false;
        });

        this.searchButton.setOnClickListener(v -> {
            this.sendInputListener(editText.getText());
            this.isOpenInputMode(false);
        });
    }

    /**
     * 设置输入焦点变化监听接口
     */
    private void setOnFocusChangeListener() {
        this.editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                this.isOpenInputMode(false);
            }
        });
    }

    /**
     * 设置搜索布局点击事件监听接口
     */
    private void setSearchLayoutClickListener() {
        this.searchLayout.setOnClickListener(v -> this.isOpenInputMode(true));
    }

    /**
     * 关闭搜索布局点击事件监听
     */
    private void closeSearchLayoutClickListener() {
        this.searchLayout.setOnClickListener(null);
    }

    /**
     * 发送输入监听
     *
     * @param charSequence 内容
     */
    private void sendInputListener(CharSequence charSequence) {
        if (this.onSearchListener != null) {
            this.onSearchListener.onSearch(charSequence);
        }
    }

    public void setOnSearchListener(OnSearchListener onSearchListener) {
        this.onSearchListener = onSearchListener;
    }
}