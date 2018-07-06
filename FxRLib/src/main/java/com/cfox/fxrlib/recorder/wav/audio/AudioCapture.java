package com.cfox.fxrlib.recorder.wav.audio;

import android.media.AudioRecord;

import com.cfox.fxrlib.log.FxLog;
import com.cfox.fxrlib.recorder.wav.info.CaptureAudioInfo;


/**
 * **********************************************
 * Project_Name: FxRecorder
 * Author : CFOX
 * Github : https://github.com/CNCFOX/FxRecorder
 * Instruction :
 * Msg:
 * **********************************************
 */
public class AudioCapture {

    private static final String TAG = "AudioCapture";

    private final Object obj = new Object();

    private AudioRecord mAudioRecord;
    private boolean isRecording = false;
    private boolean isPausing = false;
    private int mRecordStatus = AudioStatus.STOP;
    private Thread mStartRecorderThread;
    private byte[] mAudioBufferByte;

    private AudioStatusListener mAudioStatusListener;
    public interface AudioStatusListener {
        void statusChange(int status);
    }

    public void setStatusChengeListener(AudioStatusListener mStatusChengeListener) {
        this.mAudioStatusListener = mStatusChengeListener;
    }

    private OnAudioFrameCapturedListener mAudioFrameCapturedListener;
    public interface OnAudioFrameCapturedListener {
        void onAudioFrameCaptured(byte[] audioData);
        void onAudioFrameCapturedError();
    }

    public void setOnAudioFrameCapturedListener(OnAudioFrameCapturedListener listener) {
        mAudioFrameCapturedListener = listener;
    }

    public boolean startCapture(CaptureAudioInfo audioInfo) {

        if (isRecording) {
            changeStatus(AudioStatus.STARTING);
            return false;
        }

        int minBufferSize = AudioRecord.getMinBufferSize(audioInfo.getSampleRateInHz(),
                audioInfo.getChannelConfig(), audioInfo.getAudioFormat());

        if (minBufferSize == AudioRecord.ERROR_BAD_VALUE) {
            FxLog.e(TAG, "Invalid parameter !");
            sendStatus(AudioStatus.ERROR_GET_BUFFER_SIZE_FAIL);
            return false;
        }

        mAudioBufferByte = new byte[minBufferSize * 2];

        mAudioRecord = new AudioRecord(audioInfo.getAudioSource(), audioInfo.getSampleRateInHz(),
                audioInfo.getChannelConfig(), audioInfo.getAudioFormat(), minBufferSize * 4);
        if (mAudioRecord.getState() == AudioRecord.STATE_UNINITIALIZED) {
            FxLog.e(TAG, "AudioRecord initialize fail !");
            sendStatus(AudioStatus.ERROR_AUDIO_INITIALIZED_FAIL);
            return false;
        }

        mAudioRecord.startRecording();
        isRecording = true;
        changeStatus(AudioStatus.STARTING);
        mStartRecorderThread = new Thread(recorderRunnable);
        mStartRecorderThread.start();
        FxLog.e(TAG, "AudioRecord start success !");
        return true;
    }

    public void pauseCapture() {
        if (isRecording) {
            isPausing = true;
            changeStatus(AudioStatus.PAUSE);
        }
    }

    public void resumeCapture() {
        if (isPausing) {
            changeStatus(AudioStatus.RESUME);
            synchronized (obj) {
                obj.notify();
                isPausing = false;
            }
        }
    }

    public void stopCapure() {
        if (!isRecording || mStartRecorderThread == null || mAudioRecord == null) {
            return;
        }

        isRecording = false;
        isPausing = false;
        synchronized (obj) {
            obj.notify();
        }

        if (mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
            mAudioRecord.stop();
        }
        mAudioRecord.release();
        mAudioRecord = null;

        try {
            mStartRecorderThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        changeStatus(AudioStatus.STOP);
    }


    public int getAudioStatus() {
        return mRecordStatus;
    }

    private void changeStatus(int status) {
        mRecordStatus = status;
        sendStatus(mRecordStatus);
    }

    private void sendStatus(int status) {
        if (mAudioStatusListener != null) {
            mAudioStatusListener.statusChange(status);
        }
    }

    private Runnable recorderRunnable = new Runnable() {
        @Override
        public void run() {

            if (mAudioRecord == null) {
                sendStatus(AudioStatus.ERROR_AUDIO);
                FxLog.e(TAG, "Error AudioRecord is null");
                return;
            }
            while (isRecording) {
                synchronized (obj) {
                    while (mRecordStatus == AudioStatus.PAUSE && isRecording) {
                        try {
                            obj.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (!isRecording) break;
                }
                int status = mAudioRecord.read(mAudioBufferByte, 0 , mAudioBufferByte.length);
                if (status == AudioRecord.ERROR_INVALID_OPERATION) {
                    sendStatus(AudioStatus.ERROR_INVALID_OPERATION);
                    FxLog.e(TAG, "Error ERROR_INVALID_OPERATION");
                    if (mAudioFrameCapturedListener != null) {
                        mAudioFrameCapturedListener.onAudioFrameCapturedError();
                    }
                } else if (status == AudioRecord.ERROR_BAD_VALUE) {
                    sendStatus(AudioStatus.ERROR_BAD_VALUE);
                    FxLog.e(TAG, "Error ERROR_BAD_VALUE");
                    if (mAudioFrameCapturedListener != null) {
                        mAudioFrameCapturedListener.onAudioFrameCapturedError();
                    }
                } else {
                    FxLog.d("TAG", "Audio captured: " + mAudioBufferByte.length);
                    if (mAudioFrameCapturedListener != null) {
                        mAudioFrameCapturedListener.onAudioFrameCaptured(mAudioBufferByte);
                    }
                }
            }
        }
    };
}
