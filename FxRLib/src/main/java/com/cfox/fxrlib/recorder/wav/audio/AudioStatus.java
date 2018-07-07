package com.cfox.fxrlib.recorder.wav.audio;

/**
 * **********************************************
 * Project_Name: FxRecorder
 * Author : CFOX
 * Github : https://github.com/CNCFOX/FxRecorder
 * Instruction :
 * Msg:
 * **********************************************
 */
public interface AudioStatus {
    /*1 ~ 100 is status */
    int STARTING                                = 0x0001;
    int PAUSE                                   = 0x0002;
    int RESUME                                  = 0x0003;
    int STOP                                    = 0x0004;

    /* 101 ~ 200 is error status */
    int ERROR_AUDIO                             = 0x0101;
    int ERROR_GET_BUFFER_SIZE_FAIL              = 0x0102;
    int ERROR_AUDIO_INITIALIZED_FAIL            = 0x0103;
    int ERROR_INVALID_OPERATION                 = 0x0104;
    int ERROR_BAD_VALUE                         = 0x0105;
    int ERROR_FILE_PATH_NULL                    = 0x0106;
    int ERROR_NO_PLAY_FILE                      = 0x0107;
    int ERROR_PALY_FAILURE                      = 0x0108;
}
