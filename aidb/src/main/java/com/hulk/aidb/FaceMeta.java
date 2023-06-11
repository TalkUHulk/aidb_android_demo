package com.hulk.aidb;
import androidx.annotation.NonNull;

public class FaceMeta {
    public FaceMeta(float _x1, float _y1, float _x2, float _y2, float _conf){
        x1 = _x1;
        y1 = _y1;
        x2 = _x2;
        y2 = _y2;
        conf = _conf;
        landmark_num = 5;
        landmarks = new float[landmark_num * 2];
    }

    public FaceMeta(float _x1, float _y1, float _x2, float _y2, float _conf, int n_ldm){
        x1 = _x1;
        y1 = _y1;
        x2 = _x2;
        y2 = _y2;
        conf = _conf;
        landmark_num = n_ldm;
        landmarks = new float[landmark_num * 2];
    }

    public FaceMeta(int n_ldm){
        landmark_num = n_ldm;
        landmarks = new float[n_ldm * 2];
    }

    public float x1;
    public float y1;
    public float x2;
    public float y2;
    public float conf;
    public int landmark_num;
    public float[] landmarks;
    public float yaw;
    public float pitch;
    public float roll;

    @NonNull
    @Override
    public String toString() {
        return "FaceMeta -- bbox:[" + Float.toString(x1) + "," + Float.toString(y1) + ","+ Float.toString(x2) + ","+ Float.toString(y1) +"] conf:"
                + Float.toString(conf);
    }
}
