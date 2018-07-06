// IPlayerRecorderService.aidl
package com.cfox.fxrlib;

import android.os.Bundle;
import com.cfox.fxrlib.ICallBack;
import com.cfox.fxrlib.IWaveCallBack;

interface IPlayerRecorderService {
        oneway void startPlay(in Bundle bundle , ICallBack callback);
        oneway void setWaveListener(IWaveCallBack callbakc);
        oneway void pausePlay(in Bundle bundle , ICallBack callback);
        oneway void resumePlay(in Bundle bundle , ICallBack callback);
        oneway void stopPlay(in Bundle bundle, ICallBack callback);
        oneway void back(long lenth);
        oneway void forward(long length);
        oneway void getRecorderStatus(ICallBack callback);
}
