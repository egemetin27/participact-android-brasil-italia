package br.udesc.esag.participactbrasil.support;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;

public class StringUtils {

    public static SpannableString formatForTextView(String head, String body) {
        SpannableString result = new SpannableString(head + body);
        result.setSpan(new StyleSpan(Typeface.BOLD), 0, head.length(), 0);
        return result;
    }

}
