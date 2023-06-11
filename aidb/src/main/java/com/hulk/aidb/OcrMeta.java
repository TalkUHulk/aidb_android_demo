package com.hulk.aidb;

import androidx.annotation.NonNull;

public class OcrMeta {

    public OcrMeta(int n_point){
        point_num = n_point;
        points = new float[2 * n_point];
    }
    int point_num;
    public float[] points;
    public float conf;
    public float conf_rotate;
    public String text;

    @NonNull
    @Override
    public String toString() {
        return "OcrMeta -- bbox:[" + text.toString() + "] conf:"
                + Float.toString(conf);
    }
}
