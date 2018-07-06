package com.cfox.fxrecorder.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cfox.fxrecorder.Constants;
import com.cfox.fxrecorder.R;
import com.cfox.fxrlib.ICallBack;
import com.cfox.fxrlib.ICreateRecorderService;
import com.cfox.fxrlib.IWaveCallBack;
import com.cfox.fxrlib.recorder.RecorderConstants;
import com.cfox.fxrlib.recorder.wav.audio.AudioStatus;
import com.cfox.fxrlib.service.RecorderService;
import com.cfox.fxrlib.service.ServiceConstants;

/**
 * **********************************************
 * Project_Name: FxRecorder
 * Author : CFOX
 * Github : https://github.com/CNCFOX/FxRecorder
 * Instruction :
 * Msg:
 * **********************************************
 */
public class RecorderActivity extends BaseActivity implements View.OnClickListener,
        ServiceConstants , RecorderConstants , Constants,AudioStatus {

    private static final String TAG = "RecorderActivity";

    private TextView mTvRecorderStatusView;
    private TextView mTvWaveData;
    private Button mBtnStart;
    private Button mBtnPause;
    private Button mBtnResume;
    private Button mBtnStop;
    
    private ICreateRecorderService mCreateRecorder;

    private ICallBack.Stub callBack = new ICallBack.Stub() {
        @Override
        public void back(final Bundle bundle) throws RemoteException {

            String key = bundle.getString(BUNDLE_KEY);

            switch (key) {
                case RECORDER_STATUS:
                    Message message = mHandler.obtainMessage(HANDLER_KEY_RECORDER_STATUS);
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                    break;
            }
        }
    };

    private IWaveCallBack.Stub waveCallBack = new IWaveCallBack.Stub() {
        @Override
        public void back(Bundle bundle) throws RemoteException {
            Message message = mHandler.obtainMessage(HANDLER_KEY_RECORDER_WAVE);
            message.setData(bundle);
            mHandler.sendMessage(message);
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case HANDLER_KEY_RECORDER_STATUS:
                    recorderStatusChange(msg.getData());
                    break;

                case HANDLER_KEY_RECORDER_WAVE:
                    recorderWaveData(msg.getData());
                    break;

            }

        }
    };

    private void recorderWaveData(Bundle data) {
        byte[] waveByte = data.getByteArray(RECORDER_WAVE_DATE);
        Log.d(TAG, "" + waveByte.toString());
    }

    private void recorderStatusChange(Bundle data) {
        int recorderStatus = data.getInt(RECORDER_STATUS);
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

        mTvRecorderStatusView.setText(status);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);
        initBindService();
        initView();
        initEvn();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }

    private void initBindService() {
        Intent intent = new Intent(this, RecorderService.class);
        intent.setAction(ServiceConstants.BIND_TYPE_RECORDER);
        bindService(intent, conn, BIND_AUTO_CREATE);
    }

    private void initView() {
        mTvRecorderStatusView = (TextView) findViewById(R.id.tv_recorder_status);
        mTvWaveData = (TextView) findViewById(R.id.tv_wave_data);
        mBtnStart = (Button) findViewById(R.id.btn_start_recorder);
        mBtnPause = (Button) findViewById(R.id.btn_pause_recorder);
        mBtnResume = (Button) findViewById(R.id.btn_resume_recorder);
        mBtnStop = (Button) findViewById(R.id.btn_stop_recorder);
    }

    private void initEvn() {
        mBtnStart.setOnClickListener(this);
        mBtnPause.setOnClickListener(this);
        mBtnResume.setOnClickListener(this);
        mBtnStop.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        
        switch (view.getId()) {
            case R.id.btn_start_recorder:
                startRecorder();
                break;
            case R.id.btn_pause_recorder:
                pauseRecorder();
                break;
            
            case R.id.btn_resume_recorder:
                resumRecorder();
                break;
            case R.id.btn_stop_recorder:
                stopRecorder();
                break;
        }
        
    }

    private void startRecorder() {

        String path = Environment.getExternalStorageDirectory().getPath();
        try {
            mCreateRecorder.startRecorder(null,callBack);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void pauseRecorder() {
        try {
            mCreateRecorder.pauseRecorder(null, callBack);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void resumRecorder() {
        try {
            mCreateRecorder.resumeRecorder(null, callBack);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void stopRecorder() {
        try {
            mCreateRecorder.stopRecorder(null, callBack);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mCreateRecorder = ICreateRecorderService.Stub.asInterface(iBinder);
            try {
                mCreateRecorder.setWaveListener(waveCallBack);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mCreateRecorder = null;
        }
    };
}
