package com.ardeapps.menomesta.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.ardeapps.menomesta.R;

import static android.view.Gravity.CENTER;

/**
 * Created by Arttu on 16.2.2018.
 */

public class IconView extends android.support.v7.widget.AppCompatTextView {

    public IconView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public IconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public IconView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        // Load attributes
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.IconViewFont, 0, 0);
        try {
            int defaultSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, context.getResources().getDisplayMetrics());
            String fontInAssets = ta.getString(R.styleable.IconViewFont_iconFont);
            int colorInAssets = ta.getColor(R.styleable.IconViewFont_iconColor, ContextCompat.getColor(context, R.color.color_text_light));
            int sizeInAssets = ta.getDimensionPixelSize(R.styleable.IconViewFont_iconSize, defaultSize);
            setGravity(CENTER);
            setTextColor(colorInAssets);
            setTextSize(TypedValue.COMPLEX_UNIT_PX, sizeInAssets);
            setTypeface(Typefaces.get(context, fontInAssets));
        } finally {
            ta.recycle();
        }
    }
}