package com.next.view.search;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.next.view.round.RoundImageView;
import com.next.view.round.RoundRelativeLayout;

/**
 * ClassName:搜索控件2类
 *
 * @author Afton
 * @time 2024/1/6
 * @auditor
 */
public class SearchView2 extends LinearLayout {

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
    private RoundImageView deleteButton;

    //输入控件
    private EditText editText;

    //搜索布局控件
    private RoundRelativeLayout searchLayout;

    //搜索监听接口
    private OnSearchListener onSearchListener;

    //搜索输入提示
    private String searchHint = "";

    public SearchView2(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    /**
     * 设置背景圆角
     *
     * @param radius 圆角
     */
    public void setBackgroundRadius(float radius) {
        this.searchLayout.setRadius(radius);
    }

    /**
     * 设置搜索输入提示
     *
     * @param hint 提示
     */
    public void setSearchHint(String hint) {
        this.searchHint = hint;
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
     * 清空输入内容
     */
    public void clearText() {
        this.editText.setText("");
    }

    /**
     * 获取输入内容
     *
     * @return 输入内容
     */
    public Editable getText() {
        return this.editText.getText();
    }

    /**
     * 初始化
     */
    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.next_view_search2, this);

        this.tipsView = this.findViewById(R.id.tv_tips);
        this.tipsLayout = this.findViewById(R.id.layout_search_normal);
        this.deleteButton = this.findViewById(R.id.btn_delete);
        this.editText = this.findViewById(R.id.edit_search);
        this.searchLayout = this.findViewById(R.id.layout_search);

        this.isOpenInputMode(false);
        this.setSearchListener();
        this.setOnFocusChangeListener();
        this.setTextChangedListener();
    }

    /**
     * 设置输入监听
     */
    private void setTextChangedListener() {
        this.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();
                SearchView2.this.changeHint(text);
            }
        });
    }

    /**
     * 修改提示
     *
     * @param text 输入内容
     */
    private void changeHint(String text) {
        if (!text.isEmpty()) {
            this.editText.setHint("搜索");
        } else {
            this.editText.setHint(this.searchHint);
        }
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
            this.deleteButton.setVisibility(VISIBLE);
            this.editText.setVisibility(VISIBLE);
            this.closeSearchLayoutClickListener();
            this.showSoftInput();
        } else {
            this.closeSoftInput();

            if (!TextUtils.isEmpty(this.getText().toString())) {
                return;
            }

            this.tipsLayout.setVisibility(VISIBLE);
            this.deleteButton.setVisibility(GONE);
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
                this.sendInputListener(this.getText());
                this.isOpenInputMode(false);

                if (TextUtils.isEmpty(this.getText())) {
                    int newWidth = (int) (this.getTextWidth(this.tipsView, this.tipsView.getText().toString()) + this.getDimension(R.dimen.dp_42));
                    int oldWidth = (int) (this.getTextWidth(this.editText, this.editText.getHint().toString()) + this.getDimension(R.dimen.dp_42));
                    this.animateLayoutChange(this.searchLayout, newWidth, oldWidth);
                }
                return true;
            }
            return false;
        });

        this.deleteButton.setOnClickListener(v -> {
            if (!this.getText().toString().isEmpty()) {
                String text = this.getText().toString();
                //清空输入内容
                this.clearText();

                int newWidth = (int) (this.getTextWidth(this.editText, this.editText.getHint().toString()) + this.getDimension(R.dimen.dp_42));
                int oldWidth = (int) (this.getTextWidth(this.editText, text) + this.getDimension(R.dimen.dp_42));
                this.animateLayoutChange(this.searchLayout, newWidth, oldWidth);
            } else {
                this.isOpenInputMode(false);

                int newWidth = (int) (this.getTextWidth(this.tipsView, this.tipsView.getText().toString()) + this.getDimension(R.dimen.dp_42));
                int oldWidth = (int) (this.getTextWidth(this.editText, this.editText.getHint().toString()) + this.getDimension(R.dimen.dp_42));
                this.animateLayoutChange(this.searchLayout, newWidth, oldWidth);
            }
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
        this.searchLayout.setOnClickListener(v -> {
            if (TextUtils.isEmpty(this.getText())) {
                this.isOpenInputMode(true);

                int newWidth = (int) (this.getTextWidth(this.editText, this.editText.getHint().toString()) + this.getDimension(R.dimen.dp_42));
                int oldWidth = (int) (this.getTextWidth(this.tipsView, this.tipsView.getText().toString()) + this.getDimension(R.dimen.dp_42));
                this.animateLayoutChange(this.searchLayout, newWidth, oldWidth);
            }
        });
    }

    /**
     * 获取资源文件中的尺寸
     *
     * @param dimenResId 资源id
     * @return 尺寸
     */
    private float getDimension(@DimenRes int dimenResId) {
        return this.getContext().getResources().getDimension(dimenResId);
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

    /**
     * 布局变化动画
     *
     * @param view     文本控件
     * @param newWidth 新宽度
     * @param oldWidth 旧宽度
     */
    private void animateLayoutChange(View view, int newWidth, int oldWidth) {
        ValueAnimator animator = ValueAnimator.ofInt(oldWidth, newWidth);
        //设置动画持续时间
        animator.setDuration(150);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            //更新父控件的布局参数
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.width = (int) animation.getAnimatedValue();
            view.setLayoutParams(layoutParams);
            //请求重新布局
            view.requestLayout();
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //更新父控件的布局参数
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                view.setLayoutParams(layoutParams);
                //请求重新布局
                view.requestLayout();
            }
        });
        animator.start();
    }

    /**
     * 获取文本宽度
     *
     * @param textView 文本控件
     * @param text     文本
     * @return 文本宽度
     */
    private int getTextWidth(TextView textView, String text) {
        Paint paint = textView.getPaint();
        float textSize = textView.getTextSize();
        paint.setTextSize(textSize);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.width();
    }

    /**
     * 设置搜索监听接口
     *
     * @param onSearchListener 搜索监听接口
     */
    public void setOnSearchListener(OnSearchListener onSearchListener) {
        this.onSearchListener = onSearchListener;
    }
}