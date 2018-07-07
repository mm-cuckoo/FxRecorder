package com.cfox.fxrlib.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.cfox.fxrlib.recorder.RecorderCapture;
import com.cfox.fxrlib.recorder.RecorderPlayer;
import com.cfox.fxrlib.service.bind.CreateRecorderBind;
import com.cfox.fxrlib.service.bind.PlayRecorderBind;

/**
 * **********************************************
 * Project_Name: FxRecorder
 * Author : CFOX
 * Github : https://github.com/CNCFOX/FxRecorder
 * Instruction :
 * Msg:
 * **********************************************
 */
public class RecorderService extends Service implements ServiceConstants {

    private RecorderCapture mRecorderCapture;
    private RecorderPlayer mRecorderPlayer;
    private CreateRecorderBind mCreateRecorderBind;
    private PlayRecorderBind mPlayRecorderBind;

    @Override
    public void onCreate() {
        super.onCreate();
        mRecorderCapture = new RecorderCapture();
        mRecorderPlayer = new RecorderPlayer();
        mCreateRecorderBind = new CreateRecorderBind(mRecorderCapture);
        mPlayRecorderBind = new PlayRecorderBind(mRecorderPlayer);
    }

    @Override
    public IBinder onBind(Intent intent) {
        String action = intent.getAction();
        if (action.equals(ServiceConstants.BIND_TYPE_RECORDER)) {
            return mCreateRecorderBind;
        }

        if (action.equals(ServiceConstants.BIND_TYPE_PALY)) {
            return mPlayRecorderBind;
        }
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRecorderCapture = null;
        mRecorderPlayer = null;
        mCreateRecorderBind = null;
        mPlayRecorderBind = null;
    }
}
