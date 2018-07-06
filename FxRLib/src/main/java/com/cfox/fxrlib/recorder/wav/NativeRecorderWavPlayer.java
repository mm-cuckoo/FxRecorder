package com.cfox.fxrlib.recorder.wav;

import com.cfox.fxrlib.log.FxLog;
import com.cfox.fxrlib.recorder.wav.audio.AudioPlayer;
import com.cfox.fxrlib.recorder.wav.audio.AudioStatus;
import com.cfox.fxrlib.recorder.wav.info.PlayerAudioInfo;
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
public class NativeRecorderWavPlayer implements AudioPlayer.PalyStatusListener{

    private static final String TAG = "NativeRecorderWavPlayer";
    private AudioPlayer mAudioPlayer;
    private WavFileReader mWavFileReader;
    private IAudioStatusListener mStatusListener;
    private IAudioWaveDataListener mWaveDataListener;
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
        mAudioPlayer.setStatusChengeListener(this);
    }


    public void startPlay(String filePath) throws IOException {
        FxLog.d(TAG, "Audio File path:" + filePath);
        if (!mWavFileReader.openFile(filePath)){
            FxLog.d(TAG, "Audio File read fail");
            return;
        }
        PlayerAudioInfo audioInfo = PlayerAudioInfo.parse(mWavFileReader.getWavFileHeader());
        if (mAudioPlayer.statrPlay(audioInfo)) {
            new Thread(AudioPlayRunnable).start();
        }
    }

    public void pausePlay() throws IOException {
        mFilePointer = mWavFileReader.getFilePointer();
        mAudioPlayer.pausePlay();
    }

    public void resumePaly() throws IOException {
        if (mAudioPlayer.resumePlay()) {
            mWavFileReader.seek(mFilePointer);
            new Thread(AudioPlayRunnable).start();
        }
    }

    public void stopPlay() {
        mAudioPlayer.stopPlay();
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

    private Runnable AudioPlayRunnable = new Runnable() {
        @Override
        public void run() {
            byte[] buffer = new byte[getBufferSizse() * 2];
            while (mAudioPlayer.isPalying() && mWavFileReader.readData(buffer, 0, buffer.length) > 0) {
                int audioStatus = mAudioPlayer.getAudioStatus();
                if (audioStatus == AudioStatus.PAUSE || audioStatus == AudioStatus.STOP) {
                    break;
                }

                mAudioPlayer.playData(buffer, 0, buffer.length);
                if (mWaveDataListener != null) {
                    mWaveDataListener.waveData(buffer);
                }
            }
            playRelease();
        }
    };

    private void playRelease() {
        if (mAudioPlayer.getAudioStatus() == AudioStatus.PAUSE) {
            return;
        }
        mWavFileReader.release();
        mAudioPlayer.release();
    }

    private int getBufferSizse() {
        return mAudioPlayer.getAudioBufferSzie();
    }

    @Override
    public void statusChange(int status) {
        if (mStatusListener != null) {
            mStatusListener.statusChange(status);
        }
    }
}
