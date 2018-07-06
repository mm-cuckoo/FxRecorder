package com.cfox.fxrlib.recorder.wav;


import com.cfox.fxrlib.recorder.wav.audio.AudioCapture;
import com.cfox.fxrlib.recorder.wav.info.CaptureAudioInfo;
import com.cfox.fxrlib.recorder.wav.wavfile.WavFileWriter;

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
public class NativeRecorderWavCapture implements AudioCapture.AudioStatusListener,
        AudioCapture.OnAudioFrameCapturedListener {

    private AudioCapture mAudioCapture;
    private WavFileWriter mWavFileWriter;
    private IAudioStatusListener mStatusListener;
    private IAudioWaveDataListener mWaveDataListener;

    public void setWaveDataListener(IAudioWaveDataListener mWaveDataListener) {
        this.mWaveDataListener = mWaveDataListener;
    }

    public void setStatusListener(IAudioStatusListener mStatusListener) {
        this.mStatusListener = mStatusListener;
    }


    public NativeRecorderWavCapture() {
        mAudioCapture = new AudioCapture();
        mWavFileWriter = new WavFileWriter();
        mAudioCapture.setStatusChengeListener(this);
    }


    public void startRecorder(String filePath) {

        CaptureAudioInfo audioInfo = CaptureAudioInfo.getInstance();
        try {
            mWavFileWriter.openFile(filePath, audioInfo.getSampleRateInHz(),
                    audioInfo.getChannels(), audioInfo.getBitsPerSample());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mAudioCapture.setOnAudioFrameCapturedListener(this);
        mAudioCapture.startCapture(audioInfo);
    }

    public void pauseRecorder() {
        mAudioCapture.pauseCapture();
    }

    public void resumeRecorder() {
        mAudioCapture.resumeCapture();
    }

    public void stopRecorder() {
        mAudioCapture.stopCapure();
        closeWavFileWriter();
    }

    public int getRecorderStatus() {
        return mAudioCapture.getAudioStatus();
    }

    @Override
    public void statusChange(int status) {
        if (mStatusListener != null) {
            mStatusListener.statusChange(status);
        }
    }

    @Override
    public void onAudioFrameCaptured(byte[] audioData) {
        mWavFileWriter.writeData(audioData, 0, audioData.length);
        if (mWaveDataListener != null) {
            mWaveDataListener.waveData(audioData);
        }
    }

    @Override
    public void onAudioFrameCapturedError() {
        closeWavFileWriter();
    }

    private void closeWavFileWriter() {
        try {
            mWavFileWriter.closeFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
