package com.cfox.fxrlib.recorder.wav.info;

import android.media.AudioFormat;
import android.media.MediaRecorder;

/**
 * **********************************************
 * Project_Name: FxRecorder
 * Author : CFOX
 * Github : https://github.com/CNCFOX/FxRecorder
 * Instruction :
 * Msg:
 * **********************************************
 */
public class CaptureAudioInfo extends AudioInfo {

    private static CaptureAudioInfo sCaptureAudioInfo = new CaptureAudioInfo();

    /**
     * audioSource：录音来源，查看MediaRecorder.AudioSource类对录音来源的定义。
     * MediaRecorder.AudioSource.DEFAULT   默认音频来源
     * MediaRecorder.AudioSource.MIC       麦克风
     * MediaRecorder.AudioSource.VOICE_UPLINK  上行线路的声音来源
     */
    private int audioSource = MediaRecorder.AudioSource.MIC;

    /**
     * 音频声道的配置(输入)
     * AudioFormat.CHANNEL_IN_MONO：单声道
     * AudioFormat.CHANNEL_IN_STEREO：立体声
     * 其中，CHANNEL_IN_MONO可以保证在所有设备上使用。
     */
    private int channelConfig = AudioFormat.CHANNEL_IN_MONO;

    public static CaptureAudioInfo getInstance() {
        return sCaptureAudioInfo;
    }

    public int getAudioSource() {
        return audioSource;
    }

    public void setAudioSource(int audioSource) {
        this.audioSource = audioSource;
    }

    public int getChannelConfig() {
        return channelConfig;
    }

    public void setChannelConfig(int channelConfig) {
        this.channelConfig = channelConfig;
    }

    public int getChannels () {
        if (channelConfig == AudioFormat.CHANNEL_IN_STEREO) {
            return 2;
        }
        return 1;
    }
}
