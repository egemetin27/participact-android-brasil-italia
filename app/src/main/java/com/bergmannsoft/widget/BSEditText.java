package com.bergmannsoft.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

import br.com.participact.participactbrasil.R;

@SuppressLint("AppCompatCustomView")
public class BSEditText extends EditText {

    private static final String TAG = BSEditText.class.getSimpleName();

    private boolean mandatory = false;

    public BSEditText(Context context) {
        super(context);
        init(context, null);
    }

    public BSEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BSEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            Boolean mandatory = attrs.getAttributeBooleanValue(null, "mandatory", false);
            this.mandatory = true;
            if (mandatory == null || !mandatory) {
                this.mandatory = mandatory;
                Log.d(TAG, "mandatory: " + mandatory);
                setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.mandatory_not_sign, 0, 0, 0);
            }
        }
    }

    public boolean isMandatory() {
        return mandatory;
    }
}