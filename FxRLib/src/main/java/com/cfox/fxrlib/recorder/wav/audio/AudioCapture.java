package com.cfox.fxrlib.recorder.wav.audio;

import android.media.AudioRecord;

import com.cfox.fxrlib.log.FxLog;
import com.cfox.fxrlib.recorder.wav.info.CaptureAudioInfo;
import com.cfox.fxrlib.recorder.wav.info.StartInfo;
import com.cfox.fxrlib.recorder.wav.state.IAudioManager;
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
public class AudioCapture implements IAudioManager {

    private static final String TAG = "AudioCapture";

    private final Object obj = new Object();

    private AudioRecord mAudioRecord;
    private WavFileWriter mWavFileWriter;
    private int mRecordStatus = AudioStatus.STOP;
    private Thread mStartRecorderThread;
    private byte[] mAudioBufferByte;

    private AudioStatusListener mAudioStatusListener;

    public AudioCapture() {
        mWavFileWriter = new WavFileWriter();
    }

    public interface AudioStatusListener {
        void statusChange(int status);
    }

    public void setStatusChengeListener(AudioStatusListener mStatusChengeListener) {
        this.mAudioStatusListener = mStatusChengeListener;
    }

    private OnAudioFrameCapturedListener mAudioFrameCapturedListener;

    public interface OnAudioFrameCapturedListener {
        void onAudioFrameCaptured(byte[] audioData);
    }

    public void setOnAudioFrameCapturedListener(OnAudioFrameCapturedListener listener) {
        mAudioFrameCapturedListener = listener;
    }

    public boolean start(StartInfo startInfo) {
        CaptureAudioInfo captureAudioInfo = (CaptureAudioInfo) startInfo.get(StartInfo.KEY_AUDIO_INFO, null);
        String filePath = (String) startInfo.get(StartInfo.KEY_FILE_PATH, null);
        try {
            mWavFileWriter.openFile(filePath, captureAudioInfo.getSampleRateInHz(),
                    captureAudioInfo.getChannels(), captureAudioInfo.getBitsPerSample());
        } catch (IOException e) {
            sendStatus(AudioStatus.ERROR_OPEN_FILE);
            e.printStackTrace();
        }

        int minBufferSize = AudioRecord.getMinBufferSize(captureAudioInfo.getSampleRateInHz(),
                captureAudioInfo.getChannelConfig(), captureAudioInfo.getAudioFormat());

        if (minBufferSize == AudioRecord.ERROR_BAD_VALUE) {
            FxLog.e(TAG, "Invalid parameter !");
            sendStatus(AudioStatus.ERROR_GET_BUFFER_SIZE_FAIL);
            return false;
        }

        mAudioBufferByte = new byte[minBufferSize * 2];

        mAudioRecord = new AudioRecord(captureAudioInfo.getAudioSource(), captureAudioInfo.getSampleRateInHz(),
                captureAudioInfo.getChannelConfig(), captureAudioInfo.getAudioFormat(), minBufferSize * 4);
        if (mAudioRecord.getState() == AudioRecord.STATE_UNINITIALIZED) {
            FxLog.e(TAG, "AudioRecord initialize fail !");
            sendStatus(AudioStatus.ERROR_AUDIO_INITIALIZED_FAIL);
            return false;
        }

        mAudioRecord.startRecording();
        changeStatus(AudioStatus.STARTING);
        mStartRecorderThread = new Thread(recorderRunnable);
        mStartRecorderThread.start();
        FxLog.e(TAG, "AudioRecord start success !");
        return true;
    }

    public void pause() {
        changeStatus(AudioStatus.PAUSE);
    }

    public void resume() {
        changeStatus(AudioStatus.RESUME);
        synchronized (obj) {
            obj.notify();
//            isPausing = false;
        }
//        if (isPausing) {
//            changeStatus(AudioStatus.RESUME);
//            synchronized (obj) {
//                obj.notify();
//                isPausing = false;
//            }
//        }
    }

    @Override
    public void stop() {
        synchronized (obj) {
            obj.notify();
        }

        changeStatus(AudioStatus.STOP);
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
        closeWavFileWriter();

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
            while (mRecordStatus != AudioStatus.STOP) {
                synchronized (obj) {
                    while (mRecordStatus == AudioStatus.PAUSE) {
                        try {
                            obj.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (mRecordStatus == AudioStatus.STOP) break;
                }
                int status = mAudioRecord.read(mAudioBufferByte, 0, mAudioBufferByte.length);
                if (status == AudioRecord.ERROR_INVALID_OPERATION) {
                    sendStatus(AudioStatus.ERROR_INVALID_OPERATION);
                    FxLog.e(TAG, "Error ERROR_INVALID_OPERATION");
                    onAudioFrameCapturedError();
                } else if (status == AudioRecord.ERROR_BAD_VALUE) {
                    sendStatus(AudioStatus.ERROR_BAD_VALUE);
                    FxLog.e(TAG, "Error ERROR_BAD_VALUE");
                    onAudioFrameCapturedError();
                } else {
                    FxLog.d("TAG", "Audio captured: " + mAudioBufferByte.length);
                    mWavFileWriter.writeData(mAudioBufferByte, 0, mAudioBufferByte.length);
                    if (mAudioFrameCapturedListener != null) {
                        mAudioFrameCapturedListener.onAudioFrameCaptured(mAudioBufferByte);
                    }
                }
            }
        }
    };

    private void onAudioFrameCapturedError() {
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
