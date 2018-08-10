package com.cfox.fxrlib.service.connection;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.cfox.fxrlib.ICallBack;
import com.cfox.fxrlib.IPlayerRecorderService;
import com.cfox.fxrlib.IWaveCallBack;
import com.cfox.fxrlib.log.FxLog;

/**
 * **********************************************
 * Project_Name: FxRecorder
 * Author : CFOX
 * Github : https://github.com/CNCFOX/FxRecorder
 * Instruction :
 * Msg:
 * **********************************************
 */
public class PlayerServiceConnection implements ServiceConnection {
    private static final String TAG = "PlayerServiceConnection";

    private IWaveCallBack mWaveCallBack;
    private IPlayerRecorderService mPlayerRecorederService;

    public PlayerServiceConnection(IWaveCallBack waveCallBack) {
        this.mWaveCallBack = waveCallBack;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mPlayerRecorederService = IPlayerRecorderService.Stub.asInterface(service);
        if (mWaveCallBack != null) {
            try {
                mPlayerRecorederService.setWaveListener(mWaveCallBack);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mPlayerRecorederService = null;
    }

    public void startPlay(Bundle bundle, ICallBack.Stub callBack) {
        if (!checkConnected()) {
            return;
        }
        try {
            mPlayerRecorederService.startPlay(bundle,callBack);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public void pausePlay(ICallBack.Stub callBack) {
        pausePlay(null, callBack);
    }

    public void pausePlay(Bundle bundle,ICallBack.Stub callBack) {
        if (!checkConnected()) {
            return;
        }
        try {
            mPlayerRecorederService.pausePlay(bundle, callBack);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void resumePlay( ICallBack.Stub callBack) {
        resumePlay(null, callBack);

    }

    public void resumePlay(Bundle bundle, ICallBack.Stub callBack) {
        if (!checkConnected()) {
            return;
        }

        try {
            mPlayerRecorederService.resumePlay(bundle, callBack);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    public void stopPlay(ICallBack.Stub callBack) {
        stopPlay(null, callBack);
    }

    public void stopPlay(Bundle bundle,ICallBack.Stub callBack) {
        if (!checkConnected()) {
            return;
        }
        try {
            mPlayerRecorederService.stopPlay(bundle, callBack);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void forwardPlay(int length) {
        if (!checkConnected()) {
            return;
        }
        try {
            mPlayerRecorederService.forward(length);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void backPlay(int length) {
        if (!checkConnected()) {
            return;
        }
        try {
            mPlayerRecorederService.back(length);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private boolean checkConnected() {
        if (mPlayerRecorederService == null) {
            FxLog.e(TAG,"player recorder service not connected!!!!!!!!!!!");
            return false;
        }
        return true;
    }
}
