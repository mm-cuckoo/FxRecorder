package com.cfox.fxrlib.recorder.wav.state;


import com.cfox.fxrlib.recorder.wav.info.StartInfo;

public interface IAudioManager {

    boolean start(StartInfo startInfo);
    void pause();
    void resume();
    void stop();
}
