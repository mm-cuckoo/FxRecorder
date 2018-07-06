package com.cfox.fxrecorder.application;

import android.app.Application;
import android.os.Environment;

import com.cfox.fxrlib.file.FileControl;


/**
 * **********************************************
 * Project_Name: FxRecorder
 * Author : CFOX
 * Github : https://github.com/CNCFOX/FxRecorder
 * Instruction :
 * Msg:
 * **********************************************
 */
public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        String pathBase = Environment.getExternalStorageDirectory().getPath();
        FileControl control = FileControl.getInstance();
        control.setFileExtRule(new FileControl.FileExtRule() {
            @Override
            public String rule() {
                return "-" + 123;
            }
        });

        control.setRecorderFilePath(pathBase + "/MyRecorder001/");
        control.setRecorderFileName("myRecorderName");
    }
}
