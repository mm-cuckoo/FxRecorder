package com.cfox.fxrlib.file;

import android.os.Environment;

import com.cfox.fxrlib.log.FxLog;

import java.io.File;

/**
 * **********************************************
 * Project_Name: FxRecorder
 * Author : CFOX
 * Github : https://github.com/CNCFOX/FxRecorder
 * Instruction :
 * Msg:
 * **********************************************
 */
public class FileControl {
    private static final String TAG = "FileControl";
    private static final String DEFOULT_FILE_PATH = "/FxRecorder/";

    private static FileControl sFileControl = new FileControl();

    private String mDefoultPath = Environment.getExternalStorageDirectory().getPath() + DEFOULT_FILE_PATH;
    private String mRecorderFilePath = DEFOULT_FILE_PATH;
    private String mRecorderFileName = "fx_";
    private boolean mIsOpenExt = true;
    private FileExtRule mFileExtRule;

    private FileControl() {}

    public interface FileExtRule {
        public String rule();
    }


    public static FileControl getInstance() {
        return sFileControl;
    }

    public void setFileExtRule(FileExtRule fileExtRule) {
        mFileExtRule = fileExtRule;
    }

    public void openFileAutoExt(boolean isOpen) {
        mIsOpenExt = isOpen;
    }

    public void setRecorderFilePath(String filePath) {
        mRecorderFilePath = addFilePathEndSeparator(filePath);
    }

    public void setRecorderFilePath(String filePath, String fileName) {
        setRecorderFilePath(filePath);
        setRecorderFileName(fileName);
    }

    public void setRecorderFileName(String fileName) {
        mRecorderFileName = fileName;
    }

    public boolean createFilePath(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return true;
        }

        return file.mkdirs();
    }

    public boolean verifyFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return true;
        }
        FxLog.e(TAG,filePath);
        return false;
    }

    public String getRecorderFilePath(String filePath , String fileName) {

        String recorderFilePath = null;
        String recorderFileName = null;

        if (filePath == null) {
            recorderFilePath = getFilePath();
        } else {
            recorderFilePath = filePath;
        }

        if (fileName == null) {
            recorderFileName = mRecorderFileName;
        } else {
            recorderFileName = fileName;
        }

        if (createFilePath(recorderFilePath)) {
            recorderFilePath += addTimeExtToFileName(recorderFileName, mIsOpenExt);
        } else {
            recorderFilePath = null;
        }
        return recorderFilePath;
    }

    private String getFilePath() {
        if (!mRecorderFilePath.equals(DEFOULT_FILE_PATH)) {
            return mRecorderFilePath;
        }
        return mDefoultPath;
    }

    private String addTimeExtToFileName(String fileName, boolean isOpen) {
        if (isOpen) {
            return fileName + getFileExtRule().rule() + ".wav";
        } else {
            return fileName + ".wav";
        }
    }

    private FileExtRule getFileExtRule() {
        if (mFileExtRule == null) {
            mFileExtRule = new FileExtRule() {
                @Override
                public String rule() {
                    return String.valueOf(System.currentTimeMillis());
                }
            };
        }
        return mFileExtRule;
    }

    private String addFilePathEndSeparator (String filePath) {
        if (filePath != null && filePath.lastIndexOf(File.separator) < filePath.length() - 1) {
            filePath += "/";
        }
        return filePath;
    }
}
