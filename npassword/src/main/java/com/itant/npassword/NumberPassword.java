package com.itant.npassword;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;


public class NumberPassword extends FrameLayout implements TextWatcher {

    public static final int DEFAULT_MAX_LENGTH = 6;
    public static final int DEFAULT_HEIGHT = 56;
    private int maxPasswordLength;

    public NumberPassword(final Context context, AttributeSet attrs) {
        super(context, attrs);


        LayoutInflater.from(context).inflate(R.layout.view_easy_password, this);

        final LinearLayout ll_indicator = findViewById(R.id.ll_indicator);
        final EditText et_password = findViewById(R.id.et_password);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NumberPassword);

        // 密码长度
        maxPasswordLength = typedArray.getInteger(R.styleable.NumberPassword_max_length, DEFAULT_MAX_LENGTH);
        if (maxPasswordLength <= 0) {
            maxPasswordLength = DEFAULT_MAX_LENGTH;
        }

        // 布局高度
        float dpHeight = typedArray.getDimension(R.styleable.NumberPassword_frame_height, DEFAULT_HEIGHT);
        if (dpHeight <= 0) {
            dpHeight = DEFAULT_HEIGHT;
        }
        final float pxHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpHeight, context.getResources().getDisplayMetrics());

        // 布局背景
        int bgResource = typedArray.getResourceId(R.styleable.NumberPassword_frame_background, R.drawable.shape_default_frame_bg);

        // 指示器
        int indicatorResource = typedArray.getResourceId(R.styleable.NumberPassword_indicator, R.drawable.shape_default_indicator);

        initIndicator(context, ll_indicator, bgResource, indicatorResource, pxHeight, maxPasswordLength);
        initEditText(context, et_password, maxPasswordLength);

        typedArray.recycle();
    }

    // 指示器
    private List<RelativeLayout> indicatorViewList = new ArrayList<>();
    private void initIndicator(final Context context, final LinearLayout ll_indicator,
                               final int bgResource, final int indicatorResource, final float pxHeight, final int maxLength) {
        ll_indicator.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    ll_indicator.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    ll_indicator.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                // 整体背景和高度
                ll_indicator.setBackgroundResource(bgResource);
                ViewGroup.LayoutParams totalParams = ll_indicator.getLayoutParams();
                totalParams.height = (int) pxHeight;
                ll_indicator.setLayoutParams(totalParams);

                // LinearLayout的宽度
                int totalWidth = ll_indicator.getWidth();
                // 一个指示器的宽度
                int imageViewWidth = totalWidth / maxLength;

                for (int i = 0; i < maxLength; i++) {
                    RelativeLayout indicatorWrapper = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.view_indicator, null);
                    // 指示器的样子
                    View indicator = indicatorWrapper.findViewById(R.id.view_indicator);
                    indicator.setBackgroundResource(indicatorResource);

                    // 指示器的容器宽高
                    LayoutParams wrapperParams = new LayoutParams(imageViewWidth, (int) pxHeight);
                    indicatorWrapper.setLayoutParams(wrapperParams);
                    if (indicatorWrapper.getParent() != null) {
                        ((ViewGroup)indicatorWrapper.getParent()).removeView(indicatorWrapper);
                    }

                    ll_indicator.addView(indicatorWrapper);
                    indicatorViewList.add(indicatorWrapper);
                }
            }
        });
    }

    private void initEditText(Context context, EditText editText, int maxLength) {
        // 自动获取焦点，弹出软键盘
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        ((Activity)context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        // 单行居中显示
        editText.setSingleLine(true);
        editText.setEllipsize(TextUtils.TruncateAt.END);
        editText.setGravity(Gravity.CENTER);
        // 隐藏光标
        editText.setCursorVisible(false);
        // 隐藏文字
        editText.setTextSize(0);
        editText.setTextColor(Color.TRANSPARENT);
        // 设置密码长度
        editText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(maxLength) });

        editText.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        int currentIndex = editable.length()-1;
        for (int i = 0, j = indicatorViewList.size(); i < j; i++) {
            if (i <= currentIndex) {
                indicatorViewList.get(i).setVisibility(View.VISIBLE);
            } else {
                indicatorViewList.get(i).setVisibility(INVISIBLE);
            }
        }

        if (onPasswordChangeListener != null) {
            onPasswordChangeListener.onPasswordChange(editable.toString(), maxPasswordLength);
        }
    }

    private OnPasswordChangeListener onPasswordChangeListener;

    public void setOnPasswordChangeListener(OnPasswordChangeListener onPasswordChangeListener) {
        this.onPasswordChangeListener = onPasswordChangeListener;
    }

    /**
     *
     * @return password length
     */
    public int getPasswordLength() {
        return maxPasswordLength;
    }
}