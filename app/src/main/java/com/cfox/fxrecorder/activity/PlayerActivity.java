package com.cfox.fxrecorder.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cfox.fxrecorder.Constants;
import com.cfox.fxrecorder.R;
import com.cfox.fxrlib.ICallBack;
import com.cfox.fxrlib.IPlayerRecorderService;
import com.cfox.fxrlib.IWaveCallBack;
import com.cfox.fxrlib.recorder.RecorderConstants;
import com.cfox.fxrlib.recorder.wav.audio.AudioStatus;
import com.cfox.fxrlib.service.RecorderService;
import com.cfox.fxrlib.service.ServiceConstants;
import com.cfox.fxrlib.service.connection.PlayerServiceConnection;


/**
 * **********************************************
 * Project_Name: FxRecorder
 * Author : CFOX
 * Github : https://github.com/CNCFOX/FxRecorder
 * Instruction :
 * Msg:
 * **********************************************
 */
public class PlayerActivity extends Activity implements View.OnClickListener,
        ServiceConstants, RecorderConstants, Constants, AudioStatus {

    private static final String TAG = "PlayerActivity";
    private TextView mTvPlayStart;
    private TextView mTvWaveData;
    private Button mBtnStartPlay;
    private Button mBtnPausePaly;
    private Button mBtnResumePaly;
    private Button mBtnStopPaly;
    private Button mBtnBack;
    private Button mBtnForward;

    private IPlayerRecorderService playerRecorderService;
    private PlayerServiceConnection conn;

    private ICallBack.Stub callBack = new ICallBack.Stub() {
        @Override
        public void back(final Bundle bundle) throws RemoteException {

            String key = bundle.getString(BUNDLE_KEY);

            switch (key) {
                case PLAY_RECORDER_STATUS:
                    Message message = mHandler.obtainMessage(HANDLER_KEY_PALY_STATUS);
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                    break;
            }
        }
    };

    private IWaveCallBack.Stub mWaveCallBack = new IWaveCallBack.Stub() {
        @Override
        public void back(Bundle bundle) throws RemoteException {
            Message message = mHandler.obtainMessage(HANDLER_KEY_PLAY_WAVE);
            message.setData(bundle);
            mHandler.sendMessage(message);
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case HANDLER_KEY_PALY_STATUS:
                    recorderStatusChange(msg.getData());
                    break;

                case HANDLER_KEY_PLAY_WAVE:
                    recorderWaveData(msg.getData());
                    break;

            }

        }
    };

    private void recorderWaveData(Bundle data) {
        byte[] waveByte = data.getByteArray(PLAY_RECORDER_WAVE_DATE);
        Log.d(TAG, "" + new String (waveByte));
    }

    private void recorderStatusChange(Bundle data) {
        int recorderStatus = data.getInt(PLAY_RECORDER_STATUS);
        String status = "Recorder Status :";
        switch (recorderStatus) {
            case STARTING:
                status += "STARTING";
                break;

            case PAUSE:
                status += "PAUSE";
                break;

            case RESUME:
                status += "RESUME";
                break;

            case STOP:
                status += "STOP";
                break;
        }

        mTvPlayStart.setText(status);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initBindService();
        initView();
        initEvn();

    }

    private void initBindService() {
        conn = new PlayerServiceConnection(mWaveCallBack);
        Intent intent = new Intent(this, RecorderService.class);
        intent.setAction(ServiceConstants.BIND_TYPE_PALY);
        bindService(intent, conn, BIND_AUTO_CREATE);
    }

    private void initView() {
        mTvPlayStart = (TextView) findViewById(R.id.tv_play_status);
        mTvWaveData = (TextView) findViewById(R.id.tv_wave_data);
        mBtnStartPlay = (Button) findViewById(R.id.btn_start_play);
        mBtnPausePaly = (Button) findViewById(R.id.btn_pause_play);
        mBtnResumePaly = (Button) findViewById(R.id.btn_resume_play);
        mBtnStopPaly = (Button) findViewById(R.id.btn_stop_play);
        mBtnBack = (Button) findViewById(R.id.btn_back);
        mBtnForward = (Button) findViewById(R.id.btn_forward);
    }

    private void initEvn() {
        mBtnStartPlay.setOnClickListener(this);
        mBtnPausePaly.setOnClickListener(this);
        mBtnResumePaly.setOnClickListener(this);
        mBtnStopPaly.setOnClickListener(this);
        mBtnBack.setOnClickListener(this);
        mBtnForward.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_start_play:
                startPlay();
                break;
            case R.id.btn_pause_play:
                pausePlay();
                break;

            case R.id.btn_resume_play:
                resumPlay();
                break;
            case R.id.btn_stop_play:
                stopPlay();
                break;

            case R.id.btn_back:
                backPlay();
                break;

            case R.id.btn_forward:
                forwardPlay();
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (conn != null) {
            unbindService(conn);
        }
    }

    private void startPlay() {
        String path = Environment.getExternalStorageDirectory().getPath() + "/AAAPath/hello-123.wav";

        Bundle bundle = new Bundle();
        bundle.putString(FILE_PATH, path);
        conn.startPlay(bundle, callBack);
    }

    private void pausePlay() {
        conn.pausePlay( callBack);
    }

    private void resumPlay() {
        conn.resumePlay( callBack);
    }

    private void stopPlay() {
        conn.stopPlay( callBack);
    }

    private void forwardPlay() {
        conn.forwardPlay(1024 * 50);
    }

    private void backPlay() {
        conn.backPlay(1024 * 50);
    }
}
