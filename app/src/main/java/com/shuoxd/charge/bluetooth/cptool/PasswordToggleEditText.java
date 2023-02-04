package com.shuoxd.charge.bluetooth.cptool;


import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import com.shuoxd.charge.R;

public class PasswordToggleEditText extends AppCompatEditText {
    private boolean passwordShown;

    public PasswordToggleEditText(@NonNull Context context) {
        super(context);
        init();
    }

    public PasswordToggleEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PasswordToggleEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setTransformationMethod(passwordShown ? null : new PasswordTransformationMethod());
        int rDrawable = passwordShown ? R.drawable.icon_eye_open : R.drawable.icon_eye_closed;
        setCompoundDrawablesWithIntrinsicBounds(0, 0, rDrawable, 0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            Drawable[] drawables = getCompoundDrawables();
            Drawable dRight = drawables[2];
            Rect rBounds = dRight.getBounds();
            final int x = (int) event.getX();
            final int y = (int) event.getY();
            int r = this.getWidth() - this.getPaddingRight();
            if (x >= (r - rBounds.width()) && x <= r
                    && y >= this.getPaddingTop() && y <= (this.getHeight() - this.getPaddingBottom())) {
                event.setAction(MotionEvent.ACTION_CANCEL);//use this to prevent the keyboard from coming up
                togglePassword();
            }
        }
        return super.onTouchEvent(event);
    }

    private void togglePassword() {
        passwordShown = !passwordShown;
        setTransformationMethod(passwordShown ? null : new PasswordTransformationMethod());
        int rDrawable = passwordShown ? R.drawable.icon_eye_open : R.drawable.icon_eye_closed;
        setCompoundDrawablesWithIntrinsicBounds(0, 0, rDrawable, 0);
    }


}
