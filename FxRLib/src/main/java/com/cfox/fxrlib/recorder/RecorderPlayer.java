package com.cfox.fxrlib.recorder;

import android.os.Bundle;
import android.os.RemoteException;

import com.cfox.fxrlib.ICallBack;
import com.cfox.fxrlib.IWaveCallBack;
import com.cfox.fxrlib.file.FileEngine;
import com.cfox.fxrlib.log.FxLog;
import com.cfox.fxrlib.recorder.wav.IAudioStatusListener;
import com.cfox.fxrlib.recorder.wav.IAudioWaveDataListener;
import com.cfox.fxrlib.recorder.wav.NativeRecorderWavPlayer;
import com.cfox.fxrlib.recorder.wav.audio.AudioStatus;

import java.io.IOException;

/**
 * **********************************************
 * Project_Name: FxRecorder
 * Author : CFOX
 * Github : https://github.com/CNCFOX/FxRecorder
 * Instruction :
 * Msg:
 * **********************************************
 */
public class RecorderPlayer implements IAudioStatusListener,
        IAudioWaveDataListener, AudioStatus {

    private static final String TAG = "RecorderPlayer";

    private NativeRecorderWavPlayer mNativeRecorderWavPlayer;
    private ICallBack mCallBack;
    private FileEngine mFileEngine;
    private IWaveCallBack mWaveCallBack;

    public RecorderPlayer() {
        mFileEngine = new FileEngine();
        mNativeRecorderWavPlayer = new NativeRecorderWavPlayer();
        mNativeRecorderWavPlayer.setStatusListener(this);
        mNativeRecorderWavPlayer.setWaveDataListener(this);
    }

    public RecorderPlayer setCallBack(ICallBack callBack) {
        this.mCallBack = callBack;
        return this;
    }

    public void startPlay(String filePath){
        if (!mFileEngine.verifyFile(filePath)) {
            FxLog.e(TAG, "no player file ....");
            statusChange(ERROR_NO_PLAY_FILE);
            return;
        }

        try {
            mNativeRecorderWavPlayer.startPlay(filePath);
        } catch (IOException e) {
            statusChange(ERROR_PALY_FAILURE);
            FxLog.e(TAG, "play error",e);
        }

    }

    public void pausePlay() {
        try {
            mNativeRecorderWavPlayer.pausePlay();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void resumePlay() {
        try {
            mNativeRecorderWavPlayer.resumePaly();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopPlay() {
        mNativeRecorderWavPlayer.stopPlay();
    }

    public void back(long length) {
        try {
            mNativeRecorderWavPlayer.back(length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void forward(long length) {
        try {
            mNativeRecorderWavPlayer.forward(length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getRecorderStatus() {
        Bundle bundle = new Bundle();
        bundle.putInt(RecorderConstants.GET_PLAY_RECORDER_STATUS, mNativeRecorderWavPlayer.getRecorderStatus());
        sendCallBack(bundle);
    }

    public void setWaveCallBack(IWaveCallBack waveCallBack) {
        this.mWaveCallBack = waveCallBack;
    }
    @Override
    public void statusChange(int status) {
        FxLog.d(TAG, "status:" + status);
        Bundle bundle = new Bundle();
        bundle.putString(RecorderConstants.BUNDLE_KEY, RecorderConstants.PLAY_RECORDER_STATUS);
        bundle.putInt(RecorderConstants.PLAY_RECORDER_STATUS, status);
        sendCallBack(bundle);
    }

    @Override
    public void waveData(byte[] audioData) {
        Bundle bundle = new Bundle();
        bundle.putString(RecorderConstants.BUNDLE_KEY, RecorderConstants.PLAY_RECORDER_WAVE_DATE);
        bundle.putByteArray(RecorderConstants.PLAY_RECORDER_WAVE_DATE, audioData);
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
