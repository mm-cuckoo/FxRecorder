package com.cfox.fxrlib.recorder.wav.info;

import android.media.AudioFormat;

/**
 * **********************************************
 * Project_Name: FxRecorder
 * Author : CFOX
 * Github : https://github.com/CNCFOX/FxRecorder
 * Instruction :
 * Msg:
 * **********************************************
 */
public abstract class AudioInfo {

    private static final int SAMPLE_RATE_44100 = 44100;
    private static final int SAMPLE_RATE_22050 = 22050;
    private static final int SAMPLE_RATE_1600 = 1600;
    private static final int SAMPLE_RATE_11025 = 11025;

    public enum  SampleRate {
        SAMPLE_RATE_44100_HZ,
        SAMPLE_RATE_22050_HZ,
        SAMPLE_RATE_1600_HZ,
        SAMPLE_RATE_11025_HZ
    }

    /**
     *采样率，单位Hz(赫兹)。44100Hz是目前唯一一个能够在所有的设备上使用的频率，
     * 但是一些其他的例如22050、16000、11025也能够在一部分设备上使用。
     */
    private int sampleRateInHz = SAMPLE_RATE_44100;

    /**
     * 返回的音频数据的编码格式
     * AudioFormat.ENCODING_INVALID：无效的编码格式
     * AudioFormat.ENCODING_DEFAULT：默认的编码格式
     * AudioFormat.ENCODING_PCM_16BIT：每份采样数据为PCM 16bit，保证所有设备支持
     * AudioFormat.ENCODING_PCM_8BIT：样本数据格式为PCM 8bit，不保证所有设备支持
     * AudioFormat.ENCODING_PCM_FLOAT：单精度浮点样本
     */
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

    public int getSampleRateInHz() {
        return sampleRateInHz;
    }

    public void setSampleRateInHz(SampleRate sampleRateInHz) {
        switch (sampleRateInHz) {
            case SAMPLE_RATE_44100_HZ:
                this.sampleRateInHz = SAMPLE_RATE_44100;
                break;
            case SAMPLE_RATE_1600_HZ:
                this.sampleRateInHz = SAMPLE_RATE_1600;
                break;

            case SAMPLE_RATE_11025_HZ:
                this.sampleRateInHz = SAMPLE_RATE_11025;
                break;

            case SAMPLE_RATE_22050_HZ:
                this.sampleRateInHz = SAMPLE_RATE_22050;
                break;
            default:
                this.sampleRateInHz = SAMPLE_RATE_44100;
        }

    }

    public void setSampleRateInHz(int sampleRateInHz) {
        this.sampleRateInHz = sampleRateInHz;
    }

    public int getAudioFormat() {
        return audioFormat;
    }

    public void setAudioFormat(int audioFormat) {
        this.audioFormat = audioFormat;
    }

    public int getBitsPerSample() {
        if (AudioFormat.ENCODING_PCM_8BIT == audioFormat) {
            return 8;
        }
        return 16;
    }
}
