package com.cfox.fxrlib.recorder.wav.audio;

import android.media.AudioTrack;
import android.util.Log;

import com.cfox.fxrlib.log.FxLog;
import com.cfox.fxrlib.recorder.wav.IAudioWaveDataListener;
import com.cfox.fxrlib.recorder.wav.info.PlayerAudioInfo;
import com.cfox.fxrlib.recorder.wav.info.StartInfo;
import com.cfox.fxrlib.recorder.wav.state.IAudioManager;
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
public class AudioPlayer implements IAudioManager {
    private static final String TAG = "AudioPlayer";
    private int mAudioStatus = AudioStatus.STOP;
    private AudioTrack mAudioTrack;
    private int mAudioBufferSize;
    private WavFileReader mWavFileReader;
    private PlayStatusListener mStatusChangeListener;
    private IAudioWaveDataListener mWaveDataListener;
    private long mFilePointer;

    public interface PlayStatusListener {
        void statusChange(int status);
    }

    public void setStatusChangeListener(PlayStatusListener mStatusChengeListener) {
        this.mStatusChangeListener = mStatusChengeListener;
    }

    public void setWaveDataListener(IAudioWaveDataListener waveDataListener) {
        this.mWaveDataListener = waveDataListener;
    }

    public boolean start(StartInfo startInfo) {
        String filePath = (String) startInfo.get(StartInfo.KEY_FILE_PATH, null);
        mWavFileReader = (WavFileReader) startInfo.get(StartInfo.KEY_WAV_FILE, null);
        try {
            if (!mWavFileReader.openFile(filePath)){
                sendStatus(AudioStatus.ERROR_OPEN_FILE);
                FxLog.d(TAG, "Audio File read fail");
                return false;
            }
        } catch (IOException e) {
            sendStatus(AudioStatus.ERROR_OPEN_FILE);
            e.printStackTrace();
            return false;
        }
        PlayerAudioInfo playerAudioInfo = PlayerAudioInfo.parse(mWavFileReader.getWavFileHeader());

        mAudioBufferSize = AudioTrack.getMinBufferSize(playerAudioInfo.getSampleRateInHz(),
                playerAudioInfo.getChannelConfig(), playerAudioInfo.getAudioFormat());

        if (mAudioBufferSize == AudioTrack.ERROR_BAD_VALUE) {
            FxLog.e(TAG, "Invalid parameter !");
            sendStatus(AudioStatus.ERROR_GET_BUFFER_SIZE_FAIL);
            return false;
        }

        mAudioTrack = new AudioTrack(playerAudioInfo.getStreamType(), playerAudioInfo.getSampleRateInHz(),
                playerAudioInfo.getChannelConfig(), playerAudioInfo.getAudioFormat(), mAudioBufferSize, playerAudioInfo.getMode());

        if (mAudioTrack.getState() == AudioTrack.STATE_UNINITIALIZED) {
            sendStatus(AudioStatus.ERROR_AUDIO_INITIALIZED_FAIL);
            FxLog.e(TAG, "AudioTrack initialize fail !");
            return false;
        }
        changeStatus(AudioStatus.STARTING);
        new Thread(AudioPlayRunnable).start();
        FxLog.i(TAG, "Start audio player success !");
        return true;
    }

    public void pause() {
        FxLog.d(TAG, "pause......");
        try {
            mFilePointer = mWavFileReader.getFilePointer();
            mAudioTrack.pause();
            mAudioTrack.flush();
            changeStatus(AudioStatus.PAUSE);
        } catch (IOException e) {
            sendStatus(AudioStatus.ERROR_OPEN_FILE);
            e.printStackTrace();
        }
    }


    public void resume() {
        try {
            mWavFileReader.seek(mFilePointer);
            changeStatus(AudioStatus.RESUME);
            if (mAudioTrack != null) {
                mAudioTrack.play();
            }
            new Thread(AudioPlayRunnable).start();
        } catch (IOException e) {
            sendStatus(AudioStatus.ERROR_OPEN_FILE);
            e.printStackTrace();
        }
    }

    public void stop() {
        changeStatus(AudioStatus.STOP);
        if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
            mAudioTrack.stop();
        }

        mAudioTrack.release();
        Log.i(TAG, "Stop audio player success !");
    }

    private void changeStatus(int status) {
        mAudioStatus = status;
        sendStatus(mAudioStatus);
    }

    private void sendStatus(int status) {
        if (mStatusChangeListener != null) {
            mStatusChangeListener.statusChange(status);
        }
    }

    public int getAudioStatus() {
        return mAudioStatus;
    }

    private boolean isPlaying() {
        return mAudioStatus != AudioStatus.STOP;
    }

    private void playData(byte[] audioData, int offsetInBytes, int sizeInBytes) {
        FxLog.d(TAG, "playdata :" + getAudioStatus());
        if (isPlaying() && mAudioTrack.write(audioData, offsetInBytes, sizeInBytes) == sizeInBytes) {
            mAudioTrack.play();
        } else {
            FxLog.e(TAG, "Could not write all the samples to the audio device !");
        }
        FxLog.d(TAG, "OK, Played " + sizeInBytes + " bytes !");
    }

    private Runnable AudioPlayRunnable = new Runnable() {
        @Override
        public void run() {
            byte[] buffer = new byte[mAudioBufferSize * 2];
            while (isPlaying() && mWavFileReader.readData(buffer, 0, buffer.length) > 0) {
                if (getAudioStatus() == AudioStatus.PAUSE || getAudioStatus() == AudioStatus.STOP) {
                    break;
                }
                playData(buffer, 0, buffer.length);
                if (mWaveDataListener != null) {
                    mWaveDataListener.waveData(buffer);
                }
            }
            playRelease();
        }
    };

    private void playRelease() {
        if (getAudioStatus() == AudioStatus.PAUSE) {
            return;
        }
        mWavFileReader.release();
        stop();
    }
}
