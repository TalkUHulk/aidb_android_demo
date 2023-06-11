package com.hulk.aidb;

import android.graphics.Bitmap;
import android.graphics.RectF;
import androidx.annotation.NonNull;
import java.util.Arrays;
import com.hulk.aidb.FaceMeta;
import com.hulk.aidb.ObjectMeta;
import com.hulk.aidb.OcrMeta;

public class AIDBMeta {

    public AIDBMeta(int num, int type){
        if(type == 0){
            face_meta = new FaceMeta[face_num];
            face_num = num;
        } else if(type == 1) {
            object_num = num;
            object_meta = new ObjectMeta[object_num];
        } else if(type == 2) {
            ocr_num = num;
            ocr_meta = new OcrMeta[ocr_num];
        } else if(type == 3) {
            // bitmap
        }

    }

    public void setBitmapFlag(boolean flag1, boolean flag2) {
        has_bitmap = flag1;
        override = flag2;
    }

    public AIDBMeta(){ }

    public AIDBMeta(int error){
        error_code = -1;
    }
    public int face_num = 0;
    public int object_num = 0;
    public int ocr_num = 0;
    public String label;
    public float conf;
    public boolean has_bitmap = false;
    public boolean override = false;
    public FaceMeta[] face_meta;
    public ObjectMeta[] object_meta;
    public OcrMeta[] ocr_meta;
    public Bitmap bitmap;
    public float time;
    public int error_code;


    @NonNull
    @Override
    public String toString() {
        return "AIDBMeta -- face number:" + Integer.toString(face_num);
    }

}
