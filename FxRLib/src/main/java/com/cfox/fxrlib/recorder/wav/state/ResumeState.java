package com.cfox.fxrlib.recorder.wav.state;

import android.os.Message;
import android.util.Log;

import com.cfox.fxrlib.recorder.wav.state.base.State;

import static com.cfox.fxrlib.recorder.wav.state.AudioStateMachine.KEY_PAUSE;
import static com.cfox.fxrlib.recorder.wav.state.AudioStateMachine.KEY_STOP;


public class ResumeState extends State {

    private static final String TAG = "ResumeState";
    
    private IAudioManager mAudioCapture;
    private AudioStateMachine mStateMachine;

    public ResumeState(AudioStateMachine stateMachine, IAudioManager audioCapture) {
        this.mStateMachine = stateMachine;
        this.mAudioCapture = audioCapture;
    }


    @Override
    public boolean processMessage(Message msg) {
        Log.d(TAG, "processMessage: " + msg.what);
        switch (msg.what) {
            case KEY_PAUSE:
                mAudioCapture.pause();
                break;
            case KEY_STOP:
                mAudioCapture.stop();
                break;
            default:
                Log.d(TAG, "processMessage1111: " + msg.what);
                return false;
        }
        Log.d(TAG, "processMessage2222: " + msg.what);
        mStateMachine.sendTransitionTo(msg.what);
        return true;
    }
}
