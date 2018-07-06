package com.cfox.fxrlib.recorder.wav;

/**
 * **********************************************
 * Project_Name: FxRecorder
 * Author : CFOX
 * Github : https://github.com/CNCFOX/FxRecorder
 * Instruction :
 * Msg:
 * **********************************************
 */
public interface IAudioWaveDataListener {
    void waveData(byte[] audioData);
}
