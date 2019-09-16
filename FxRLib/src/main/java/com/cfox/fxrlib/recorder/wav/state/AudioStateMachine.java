package com.cfox.fxrlib.recorder.wav.state;

import android.os.Message;
import android.util.Log;

import com.cfox.fxrlib.recorder.wav.info.StartInfo;
import com.cfox.fxrlib.recorder.wav.state.base.IState;
import com.cfox.fxrlib.recorder.wav.state.base.StateMachine;


public class AudioStateMachine extends StateMachine {
    private static final String TAG = "AudioStateMachine";

    public static final int KEY_IDLE = 1;
    public static final int KEY_START = 2;
    public static final int KEY_RESUME = 3;
    public static final int KEY_PAUSE = 4;
    public static final int KEY_STOP = 5;

    private IAudioManager mAudioManager;
    private IdleState mIdleState;
    private StartState mStartState;
    private ResumeState mResumeState;
    private PauseState mPauseState;

    public AudioStateMachine(IAudioManager audioManager) {
        super("AudioStateMachine");
        mAudioManager = audioManager;
        mIdleState = new IdleState(this, mAudioManager);
        mStartState = new StartState(this, mAudioManager);
        mResumeState = new ResumeState(this, mAudioManager);
        mPauseState = new PauseState(this, mAudioManager);

        addState(mIdleState);
        addState(mStartState);
        addState(mResumeState);
        addState(mPauseState);
        setInitialState(mIdleState);
    }

    void sendTransitionTo(int key) {
        Log.d(TAG, "sendTransitionTo: " + key);
        IState state = null;
        switch (key) {
            case KEY_START:
                state = mStartState;
                break;
            case KEY_RESUME:
                state = mResumeState;
                break;
            case KEY_PAUSE:
                state = mPauseState;
                break;
            case KEY_STOP:
                state = mIdleState;
                break;
        }
        transitionTo(state);
    }

    public void sendStart(StartInfo info) {
        Message message = obtainMessage(KEY_START);
        message.obj = info;
        sendMessage(message);
    }


    public void sendResume() {
        Message message = obtainMessage(KEY_RESUME);
        sendMessage(message);
    }


    public void sendPause() {
        Message message = obtainMessage(KEY_PAUSE);
        sendMessage(message);
    }


    public void sendStop() {
        Message message = obtainMessage(KEY_STOP);
        sendMessage(message);
    }

}
