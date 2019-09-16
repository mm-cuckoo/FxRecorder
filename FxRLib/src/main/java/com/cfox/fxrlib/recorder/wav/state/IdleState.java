package com.cfox.fxrlib.recorder.wav.state;

import android.os.Message;
import android.util.Log;

import com.cfox.fxrlib.recorder.wav.info.StartInfo;
import com.cfox.fxrlib.recorder.wav.state.base.State;

import static com.cfox.fxrlib.recorder.wav.state.AudioStateMachine.KEY_START;


public class IdleState extends State {
    private static final String TAG = "IdleState";

    private IAudioManager mAudioCapture;
    private AudioStateMachine mStateMachine;

    public IdleState(AudioStateMachine stateMachine, IAudioManager audioCapture) {
        this.mStateMachine = stateMachine;
        this.mAudioCapture = audioCapture;
    }


    @Override
    public boolean processMessage(Message msg) {
        Log.d(TAG, "processMessage: " + msg.what);
        if (msg.what == KEY_START
                && msg.obj instanceof StartInfo
                && mAudioCapture.start((StartInfo) msg.obj)) {
            mStateMachine.sendTransitionTo(msg.what);
            return true;
        }
        return false;
    }
}
