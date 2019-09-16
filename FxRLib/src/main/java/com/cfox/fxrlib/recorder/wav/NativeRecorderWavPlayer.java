package com.cfox.fxrlib.recorder.wav;

import com.cfox.fxrlib.log.FxLog;
import com.cfox.fxrlib.recorder.wav.audio.AudioPlayer;
import com.cfox.fxrlib.recorder.wav.info.StartInfo;
import com.cfox.fxrlib.recorder.wav.state.AudioStateMachine;
import com.cfox.fxrlib.recorder.wav.wavfile.WavFileReader;

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
public class NativeRecorderWavPlayer implements AudioPlayer.PlayStatusListener {

    private static final String TAG = "NativeRecorderWavPlayer";
    private AudioPlayer mAudioPlayer;
    private WavFileReader mWavFileReader;
    private IAudioStatusListener mStatusListener;
    private IAudioWaveDataListener mWaveDataListener;
    private AudioStateMachine mAudioStateMachine;
    private long mFilePointer;

    public void setWaveDataListener(IAudioWaveDataListener mWaveDataListener) {
        this.mWaveDataListener = mWaveDataListener;
    }

    public void setStatusListener(IAudioStatusListener mStatusListener) {
        this.mStatusListener = mStatusListener;
    }

    public NativeRecorderWavPlayer() {
        mAudioPlayer = new AudioPlayer();
        mWavFileReader = new WavFileReader();
        mAudioStateMachine = new AudioStateMachine(mAudioPlayer);
        mAudioStateMachine.start();
        mAudioPlayer.setStatusChangeListener(this);
    }


    public void startPlay(String filePath) {
        FxLog.d(TAG, "Audio File path:" + filePath);
        mAudioPlayer.setWaveDataListener(mWaveDataListener);
        StartInfo startInfo = new StartInfo();
        startInfo.put(StartInfo.KEY_FILE_PATH, filePath);
        startInfo.put(StartInfo.KEY_WAV_FILE, mWavFileReader);
        mAudioStateMachine.sendStart(startInfo);
    }

    public void pausePlay() {
        mAudioStateMachine.sendPause();
    }

    public void resumePlay() {
        mAudioStateMachine.sendResume();
    }

    public void stopPlay() {
        mAudioStateMachine.sendStop();
    }

    public void forward(long length) throws IOException {
        mWavFileReader.forward(length);
    }

    public void back(long length) throws IOException {
        mWavFileReader.back(length);
    }

    public int getRecorderStatus() {
        return mAudioPlayer.getAudioStatus();
    }

    @Override
    public void statusChange(int status) {
        if (mStatusListener != null) {
            mStatusListener.statusChange(status);
        }
    }
}
