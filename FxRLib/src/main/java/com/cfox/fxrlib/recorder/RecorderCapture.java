package com.cfox.fxrlib.recorder;

import android.os.Bundle;
import android.os.RemoteException;

import com.cfox.fxrlib.ICallBack;
import com.cfox.fxrlib.IWaveCallBack;
import com.cfox.fxrlib.file.FileEngine;
import com.cfox.fxrlib.log.FxLog;
import com.cfox.fxrlib.recorder.wav.IAudioStatusListener;
import com.cfox.fxrlib.recorder.wav.IAudioWaveDataListener;
import com.cfox.fxrlib.recorder.wav.NativeRecorderWavCapture;
import com.cfox.fxrlib.recorder.wav.audio.AudioStatus;

/**
 * **********************************************
 * Project_Name: FxRecorder
 * Author : CFOX
 * Github : https://github.com/CNCFOX/FxRecorder
 * Instruction :
 * Msg:
 * **********************************************
 */
public class RecorderCapture implements IAudioStatusListener,
        IAudioWaveDataListener , AudioStatus{

    private static final String TAG = "RecorderCapture";

    private NativeRecorderWavCapture mNativeRecorderWavCapture;
    private ICallBack mCallBack;
    private FileEngine mFileEngine;
    private IWaveCallBack mWaveCallBack;

    public RecorderCapture() {
        mFileEngine = new FileEngine();
        mNativeRecorderWavCapture = new NativeRecorderWavCapture();
        mNativeRecorderWavCapture.setStatusListener(this);
        mNativeRecorderWavCapture.setWaveDataListener(this);
    }

    public RecorderCapture setCallBack(ICallBack callBack) {
        this.mCallBack = callBack;
        return this;
    }

    public void startRecorder(String recorderFilePath, String fileName){
        String filePath = mFileEngine.getRecorderFilePath(recorderFilePath, fileName);
        FxLog.d(TAG, filePath);
        if (filePath != null) {
            mNativeRecorderWavCapture.startRecorder(filePath);
        } else {
            statusChange(ERROR_FILE_PATH_NULL);
        }
    }

    public void pauseRecorder() {
        mNativeRecorderWavCapture.pauseRecorder();
    }

    public void resumeRecorder() {
        mNativeRecorderWavCapture.resumeRecorder();
    }

    public void stopRecorder() {
        mNativeRecorderWavCapture.stopRecorder();
    }

    public void getRecorderStatus() {
        Bundle bundle = new Bundle();
        bundle.putInt(RecorderConstants.GET_RECORDER_STATUS, mNativeRecorderWavCapture.getRecorderStatus());
        sendCallBack(bundle);
    }

    public void setWaveCallBack(IWaveCallBack waveCallBack) {
        this.mWaveCallBack = waveCallBack;
    }


    @Override
    public void statusChange(int status) {
        FxLog.d(TAG, "status:" + status);
        Bundle bundle = new Bundle();
        bundle.putString(RecorderConstants.BUNDLE_KEY, RecorderConstants.RECORDER_STATUS);
        bundle.putInt(RecorderConstants.RECORDER_STATUS, status);
        sendCallBack(bundle);
    }

    @Override
    public void waveData(byte[] audioData) {
        Bundle bundle = new Bundle();
        bundle.putString(RecorderConstants.BUNDLE_KEY, RecorderConstants.RECORDER_WAVE_DATE);
        bundle.putByteArray(RecorderConstants.RECORDER_WAVE_DATE, audioData);
        sendWaveCallBack(bundle);
    }

    private void sendCallBack(Bundle bundle) {
        try {
            if (mCallBack != null) {
                mCallBack.back(bundle);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void sendWaveCallBack(Bundle bundle) {
        if (mWaveCallBack != null) {
            try {
                mWaveCallBack.back(bundle);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
