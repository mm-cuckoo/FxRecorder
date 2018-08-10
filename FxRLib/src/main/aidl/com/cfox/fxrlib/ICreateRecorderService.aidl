// IRecorderCreateService.aidl
package com.cfox.fxrlib;

import android.os.Bundle;
import com.cfox.fxrlib.ICallBack;
import com.cfox.fxrlib.IWaveCallBack;

interface ICreateRecorderService {
        oneway void startRecorder(in Bundle bundle ,ICallBack callback);
        oneway void setWaveListener(IWaveCallBack callback);
        oneway void pauseRecorder(in Bundle bundle , ICallBack callback);
        oneway void resumeRecorder(in Bundle bundle , ICallBack callback);
        oneway void stopRecorder(in Bundle bundle, ICallBack callback);
        oneway void getRecorderStatus(ICallBack callback);
}
