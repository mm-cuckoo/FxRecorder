package com.cfox.fxrlib.recorder.wav.info;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import com.cfox.fxrlib.recorder.wav.wavfile.WavFileHeader;

/**
 * **********************************************
 * Project_Name: FxRecorder
 * Author : CFOX
 * Github : https://github.com/CNCFOX/FxRecorder
 * Instruction :
 * Msg:
 * **********************************************
 */
public class PlayerAudioInfo extends AudioInfo {


    /**
     * streamType：音频流的类型
     * AudioManager.STREAM_VOICE_CALL:电话的音频流
     * AudioManager.STREAM_SYSTEM:系统的音频流
     * AudioManager.STREAM_RING:闹钟
     * AudioManager.STREAM_MUSIC:音乐
     * AudioManager.STREAM_ALARM:警告声
     * AudioManager.STREAM_NOTIFICATION:通知
     */
    private int mStreamType = AudioManager.STREAM_MUSIC;

    /**
     * channelConfig：音频声道的配置
     * AudioFormat.CHANNEL_OUT_MONO:单声道输出(左)
     * AudioFormat.CHANNEL_OUT_STEREO:立体声输出(左和右)
     */
    private int channelConfig = AudioFormat.CHANNEL_OUT_MONO;

    /**
     * mode：流或者是静态缓存
     * AudioTrack.MODE_STATIC:创建模式-在音频开始播放之前，音频数据仅仅只会从Java层写入到本地层中一次。即开始播放前一次性写入音频数据。
     * AudioTrack.MODE_STREAM:创建模式-在音频播放的时候，音频数据会同时会以流的
     */
    private int mMode = AudioTrack.MODE_STREAM;

    public int getStreamType() {
        return mStreamType;
    }

    public void setStreamType(int mStreamType) {
        this.mStreamType = mStreamType;
    }

    public int getMode() {
        return mMode;
    }

    public void setMode(int mMode) {
        this.mMode = mMode;
    }


    public int getChannelConfig() {
        return channelConfig;
    }

    public void setChannelConfig(int channelConfig) {
        this.channelConfig = channelConfig;
    }

    public static  PlayerAudioInfo parse(WavFileHeader wavFileHeader) {

        if (wavFileHeader == null) {
            return null;
        }
        PlayerAudioInfo audioInfo = new PlayerAudioInfo();
        audioInfo.setSampleRateInHz(wavFileHeader.mSampleRate);
        if (wavFileHeader.mAudioFormat == 8) {
            audioInfo.setAudioFormat(AudioFormat.ENCODING_PCM_8BIT);
        } else {
            audioInfo.setAudioFormat(AudioFormat.ENCODING_PCM_16BIT);
        }

        if (wavFileHeader.mNumChannel == 2) {
            audioInfo.setChannelConfig(AudioFormat.CHANNEL_OUT_STEREO);
        } else {
            audioInfo.setChannelConfig(AudioFormat.CHANNEL_OUT_MONO);
        }

        return audioInfo;
    }
}
