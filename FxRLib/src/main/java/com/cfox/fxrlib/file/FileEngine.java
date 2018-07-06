package com.cfox.fxrlib.file;

/**
 * **********************************************
 * Project_Name: FxRecorder
 * Author : CFOX
 * Github : https://github.com/CNCFOX/FxRecorder
 * Instruction :
 * Msg:
 * **********************************************
 */
public class FileEngine {

    private FileControl mFileControl;

    public FileEngine() {
        mFileControl = FileControl.getInstance();
    }

    public String getRecorderFilePath(String filePath, String fileName) {
        return mFileControl.getRecorderFilePath(filePath, fileName);
    }

    public boolean verifyFile(String filePath) {

        return mFileControl.verifyFile(filePath);
    }
}
