package com.cfox.fxrlib.recorder.wav;


import com.cfox.fxrlib.recorder.wav.audio.AudioCapture;
import com.cfox.fxrlib.recorder.wav.info.CaptureAudioInfo;
import com.cfox.fxrlib.recorder.wav.info.StartInfo;
import com.cfox.fxrlib.recorder.wav.state.AudioStateMachine;

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
    private AudioStateMachine mAudioStateMachine;
//    private WavFileWriter mWavFileWriter;
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
//        mWavFileWriter = new WavFileWriter();
        mAudioStateMachine =  new AudioStateMachine(mAudioCapture);
        mAudioStateMachine.start();
        mAudioCapture.setStatusChengeListener(this);
        mAudioCapture.setOnAudioFrameCapturedListener(this);
    }


    public void startRecorder(String filePath) {

        CaptureAudioInfo audioInfo = CaptureAudioInfo.getInstance();
        StartInfo startInfo = new StartInfo();
        startInfo.put(StartInfo.KEY_AUDIO_INFO, audioInfo);
        startInfo.put(StartInfo.KEY_FILE_PATH, filePath);
        mAudioStateMachine.sendStart(startInfo);
    }

    public void pauseRecorder() {
        mAudioStateMachine.sendPause();
    }

    public void resumeRecorder() {
        mAudioStateMachine.sendResume();
    }

    public void stopRecorder() {
        mAudioStateMachine.sendStop();
//        closeWavFileWriter();
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
//        mWavFileWriter.writeData(audioData, 0, audioData.length);
        if (mWaveDataListener != null) {
            mWaveDataListener.waveData(audioData);
        }
    }

//    @Override
//    public void onAudioFrameCapturedError() {
//        closeWavFileWriter();
//    }

//    private void closeWavFileWriter() {
//        try {
//            mWavFileWriter.closeFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
