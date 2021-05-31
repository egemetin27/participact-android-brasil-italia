package br.udesc.esag.participactbrasil.support;


import android.content.Context;

public class ColorUtility {

    public static String parseColorFromId(Context context, int colorId) {

        return String.format("#%06X", 0xFFFFFF & context.getResources().getColor(colorId));
    }
}
