package com.cfox.fxrlib.recorder.wav.audio;

import android.media.AudioTrack;
import android.util.Log;

import com.cfox.fxrlib.log.FxLog;
import com.cfox.fxrlib.recorder.wav.info.PlayerAudioInfo;

/**
 * **********************************************
 * Project_Name: FxRecorder
 * Author : CFOX
 * Github : https://github.com/CNCFOX/FxRecorder
 * Instruction :
 * Msg:
 * **********************************************
 */
public class AudioPlayer {
    private static final String TAG = "AudioPlayer";
    private boolean isPlaying = false;
    private int mAudioStatus = AudioStatus.STOP;
    private AudioTrack mAudioTrack;
    private int mAudioBufferSzie;
    private PalyStatusListener mStatusChangeListener;

    public interface PalyStatusListener {
        void statusChange(int status);
    }

    public void setStatusChengeListener(PalyStatusListener mStatusChengeListener) {
        this.mStatusChangeListener = mStatusChengeListener;
    }

    public int getAudioBufferSzie() {
        return mAudioBufferSzie;
    }

    public boolean statrPlay(PlayerAudioInfo audioInfo) {
        if (isPlaying) {
            FxLog.i(TAG, "Player already started !");
            return false;
        }
        mAudioBufferSzie = AudioTrack.getMinBufferSize(audioInfo.getSampleRateInHz(),
                audioInfo.getChannelConfig(), audioInfo.getAudioFormat());

        if (mAudioBufferSzie == AudioTrack.ERROR_BAD_VALUE) {
            FxLog.e(TAG, "Invalid parameter !");
            sendStatus(AudioStatus.ERROR_GET_BUFFER_SIZE_FAIL);
            return false;
        }

        mAudioTrack = new AudioTrack(audioInfo.getStreamType(), audioInfo.getSampleRateInHz(),
                audioInfo.getChannelConfig(), audioInfo.getAudioFormat(), mAudioBufferSzie, audioInfo.getMode());

        if (mAudioTrack.getState() == AudioTrack.STATE_UNINITIALIZED) {
            sendStatus(AudioStatus.ERROR_AUDIO_INITIALIZED_FAIL);
            FxLog.e(TAG, "AudioTrack initialize fail !");
            return false;
        }
        changeStatus(AudioStatus.STARTING);
        isPlaying = true;
        FxLog.i(TAG, "Start audio player success !");
        return true;
    }

    public void pausePlay() {

        if (!isPlaying) {
            return;
        }

        if (mAudioStatus == AudioStatus.RESUME || mAudioStatus == AudioStatus.STARTING) {
            FxLog.d(TAG, "pausePlay......");
            changeStatus(AudioStatus.PAUSE);
            mAudioTrack.pause();
            mAudioTrack.flush();
        }
    }


    public boolean resumePlay() {
        if (isPlaying && mAudioStatus == AudioStatus.PAUSE) {
            changeStatus(AudioStatus.RESUME);
            if (mAudioTrack != null) {
                mAudioTrack.play();
                return true;
            }
        }
        return false;
    }

    public void stopPlay() {
        if (!isPlaying) {
            return;
        }

        isPlaying = false;
        if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
            mAudioTrack.stop();
        }

        mAudioTrack.release();

        changeStatus(AudioStatus.STOP);
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

    public boolean isPalying() {
        return isPlaying;
    }

    public boolean playData(byte[] audioData, int offsetInBytes, int sizeInBytes) {
        if (!isPlaying) {
            FxLog.e(TAG, "Player not started !");
            return false;
        }

        if (mAudioTrack.write(audioData, offsetInBytes, sizeInBytes) != sizeInBytes) {
            FxLog.e(TAG, "Could not write all the samples to the audio device !");
        }

        mAudioTrack.play();

        FxLog.d(TAG, "OK, Played " + sizeInBytes + " bytes !");

        return true;
    }

    public void release() {
        stopPlay();
    }
}
