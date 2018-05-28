package top.defaults.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;

import top.defaults.logger.Logger;
import top.defaults.view.clickabletextview.R;

import static top.defaults.view.TextButtonEffect.EFFECT_DEFAULT;

public class TextButton extends android.support.v7.widget.AppCompatTextView {

    @ColorInt int defaultTextColor;
    @ColorInt int pressedTextColor;
    @ColorInt int disabledTextColor;
    boolean isUnderlined;
    int effectType;
    private TextButtonEffect effect;

    private Rect viewRect = new Rect();

    public TextButton(Context context) {
        this(context, null);
    }

    public TextButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClickable(true);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextButton);
        defaultTextColor = typedArray.getColor(R.styleable.TextButton_defaultTextColor, getCurrentTextColor());
        pressedTextColor = typedArray.getColor(R.styleable.TextButton_pressedTextColor, calculatePressedColor(defaultTextColor));
        disabledTextColor = typedArray.getColor(R.styleable.TextButton_disabledTextColor, calculateDisabledColor(defaultTextColor));
        isUnderlined = typedArray.getBoolean(R.styleable.TextButton_underline, false);
        effectType = typedArray.getInt(R.styleable.TextButton_effect, EFFECT_DEFAULT);
        typedArray.recycle();

        apply();
    }

    private void apply() {
        effect = TextButtonEffect.Factory.create(this, effectType);

        if (isUnderlined) {
            setPaintFlags(getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }

        setGravity(Gravity.CENTER);
    }

    private int calculatePressedColor(@ColorInt int defaultColor) {
        int alpha = Color.alpha(defaultColor);
        alpha = Math.max(16, alpha - 96);
        return Color.argb(alpha, Color.red(defaultColor), Color.green(defaultColor), Color.blue(defaultColor));
    }

    private int calculateDisabledColor(@ColorInt int defaultColor) {
        int alpha = Color.alpha(defaultColor);
        float[] hsv = new float[3];
        Color.colorToHSV(defaultColor, hsv);
        hsv[1] = Math.max(0, hsv[1] - 0.4f);
        return Color.HSVToColor(alpha, hsv);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Logger.d("event action: %d", event.getAction());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!isEnabled()) return false;
                if (!isClickable()) break;

                getHitRect(viewRect);
                effect.actionDown();
                break;
            case MotionEvent.ACTION_UP:
                effect.actionUp();
                int relativelyX = (int) (event.getX() + getX());
                int relativelyY = (int) (event.getY() + getY());
                if (viewRect.contains(relativelyX, relativelyY)) {
                    performClick();
                } else {
                    Logger.d("Canceled");
                }
                break;
        }

        return super.onTouchEvent(event);
    }
}
