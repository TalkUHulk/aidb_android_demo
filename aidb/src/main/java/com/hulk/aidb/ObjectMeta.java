package com.hulk.aidb;

import androidx.annotation.NonNull;

public class ObjectMeta {

    public ObjectMeta(float _x1, float _y1, float _x2, float _y2, float _conf, int _label){
        x1 = _x1;
        y1 = _y1;
        x2 = _x2;
        y2 = _y2;
        conf = _conf;
        label = _label;
    }

    public float x1;
    public float y1;
    public float x2;
    public float y2;
    public float conf;
    public int label;

    @NonNull
    @Override
    public String toString() {
        return "ObjectMeta -- bbox:[" + Float.toString(x1) + "," + Float.toString(y1) + ","+ Float.toString(x2) + ","+ Float.toString(y1) +"] conf:"
                + Float.toString(conf);
    }
}
