package com.cfox.fxrlib.recorder.wav.info;

import java.util.HashMap;
import java.util.Map;

public class StartInfo {
    public static final String KEY_AUDIO_INFO = "audio_Info";
    public static final String KEY_FILE_PATH = "file_path";
    public static final String KEY_WAV_FILE = "wav_file";

    private Map<String , Object> mMap = new HashMap<>();


    public void put(String key, Object object) {
        mMap.put(key, object);
    }

    public Object get(String key, String def) {
        if (mMap.containsKey(key)) {
            return mMap.get(key);
        }
        return def;
    }

}
