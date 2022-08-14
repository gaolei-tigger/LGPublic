package com.example.textviewdebug;

import android.content.Context;
import android.util.Log;

// import java.io.DataOutputStream;
import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SymlinkUtils {

    private static final String TAG = "SymlinkUtils";
    /**
     * font overlay points to theme fonts if it exist, otherwise points to system font.
     */
    private static final String REDIRECT_TO_SYSFONT = "ln -sf /system/fonts/MiSansVF.ttf  /system/fonts/MiSansVF_Overlay.ttf";
    private static final String REDIRECT_TO_THEMEFONT = "ln -sf /data/system/theme/fonts/Roboto-Regular.ttf  /system/fonts/MiSansVF_Overlay.ttf";

    public SymlinkUtils() {

    }

    public static void issueCmdByRuntime(String cmd) {
        try {
            Runtime.getRuntime().exec(cmd);
            Log.d(TAG, cmd);
        } catch (Exception e) {
            Log.d(TAG, "run cmd exception" + e.getMessage());
        }
    }

    public static void syncFontForWebView(@NonNull Context context) {
        String externalPath = context.getExternalCacheDir().getAbsolutePath();
        String cachePath = context.getCacheDir().getAbsolutePath();
        String cmdCheckCp = "cp -rf " + externalPath + "/test.txt" + " " + externalPath + "/test2.txt";
        String cmdCheckLn = "ln -sf " + cachePath + "/test.txt" + " " + cachePath + "/test_overlay.txt";
        Log.d(TAG, cmdCheckCp);
        Log.d(TAG, cmdCheckLn);

        String cmdCheckLn2 = "ln -sf /data/system/theme/fonts/Roboto-Regular.ttf /system/fonts/MiSansVF_test.ttf";
        issueCmdByRuntime(cmdCheckLn2);

        String cmdBackToSysFont = "cp -rf /system/fonts/MiSansVF.ttf " + cachePath + "/Roboto-Regular.ttf";
        String cmdBackToSysFont2 = "cp -rf /system/fonts/MiSansVF.ttf /data/system/theme/fonts";

        issueCmdByRuntime(cmdCheckCp);
        issueCmdByRuntime(cmdCheckLn);
        issueCmdByRuntime(cmdBackToSysFont);
        issueCmdByRuntime(cmdBackToSysFont2);

        //创建文件
        Log.d(TAG, "external-path: " + context.getExternalCacheDir().getAbsolutePath());
        Log.d(TAG, "cache-path: " + context.getCacheDir().getAbsolutePath());
        File file=new File(context.getCacheDir(),"test.txt");
        Log.d(TAG, "filepath: " + file.getAbsolutePath());
        try {
            //判断文件是否存在
            if (file.exists()){
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            //开始向文件中写内容
            FileWriter fileWriter=new FileWriter(file);
            fileWriter.write("testing,,,,");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
