package com.example.textviewdebug;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.ArraySet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.textviewdebug.databinding.TestLayoutBinding;

import java.lang.reflect.*;
import java.util.List;
import java.util.Objects;

public class TestFragment extends Fragment {

    private TestLayoutBinding binding;
    static int h;
    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private Handler testHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         mHandlerThread = new HandlerThread("miuiwebview monitor");
         mHandlerThread.start();
         testHandler = new Handler(Looper.getMainLooper()) {
             @Override
             public void handleMessage(@NonNull Message msg) {
                 super.handleMessage(msg);
                 if (msg.what == 0x7f) {
                     long sendTime = msg.getData().getLong("sendTime");
                     long delay = System.currentTimeMillis() - sendTime;
                     Log.d("MYTEST", "use" + delay);
                 }
             }
         };
//         mHandler = new Handler(mHandlerThread.getLooper()) {
//             @Override
//             public void handleMessage(Message msg){
//                 try {
//                     Thread.sleep(1000); // payload
//                     binding.textViewLog.setText(msg.toString()); // ? ui thread
//                 } catch (InterruptedException e) {
//                     e.printStackTrace();
//                 }
//             }
//         };
        //
        binding = TestLayoutBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                NavHostFragment.findNavController(TestFragment.this)
//                        .navigate(R.id.action_TestFragment_to_FirstFragment);
                ScrollView sView = binding.scrollView3;
                int height = getViewHeight(sView);
                Log.d("TESTSCRLL", " " + height + " " + getDensityDPI() + " " + sView.getPaddingBottom());
                ViewGroup.LayoutParams lp = sView.getLayoutParams();
                lp.height = height;
                sView.setLayoutParams(lp);
                sView.post(new Runnable() {
                    @Override
                    public void run() {
                        h = sView.getMeasuredHeight();
                        Log.d("TESTSCRLL","h" + h);
                    }
                });

            }
        });
        //
        binding.buttonTransition.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent en = new Intent(getContext(), CalleeActivity.class);
                startActivity(en);
//                try {
//                    Thread.sleep(2000);
//                } catch (Exception e) {
//                }
                //startPostponedEnterTransition();
            }
        });
        //
        binding.buttonOutActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String packageName = "com.android.settings";
                String activity = "com.android.settings.Settings";
                ComponentName component = new ComponentName(packageName, activity);
                Intent intent = new Intent();
                intent.setComponent(component);
                //getActivity().overridePendingTransition(R.anim.enter_anim, R.anim.enter_anim);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.enter_anim,0);
                //getActivity().overridePendingTransition(R.anim.enter_transition,R.anim.exit_transition);
            }
        });
        //
        binding.buttonOtherActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String packageName = "com.example.sharesheettest";
                String activity = "com.example.sharesheettest.MainActivity";
                ComponentName component = new ComponentName(packageName, activity);
                Intent intent = new Intent();
                intent.setComponent(component);
                startActivity(intent);
            }
        });
        //
        binding.buttonXSpace.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String packageName = "com.kugou.android";
                String activity = "com.kugou.android.app.MediaActivity";
                ComponentName component = new ComponentName(packageName, activity);
                Intent intent = new Intent();
                intent.setComponent(component);
                intent.setAction(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        // 测试WebVeiw
        binding.buttonWeb.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent it = new Intent(getContext(), WebViewActivity.class);
                startActivity(it);
            }
        });
        // restart process(test for web)
        binding.buttonRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityManager am = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
                //am.restartPackage("com.android.browser");
                am.killBackgroundProcesses("com.baidu.searchbox");
                List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
                List<ActivityManager.AppTask> tasks= am.getAppTasks();
            }
        });
        // test handler/msg
        binding.buttonHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message msg = Message.obtain();
                msg.arg1 = 1;
                msg.arg2 = 2;
                msg.what = 0x7f;
                Bundle data = new Bundle();
                data.putLong("sendTime", System.currentTimeMillis());
                data.putStringArray("Data", new String[] {"this", "is","a","String"});
                msg.setData(data);
                //mHandler.sendMessage(msg);
                //testHandler.sendMessage(msg);
                testHandler.sendMessageDelayed(msg, 500);
                // ArraySet<String>
                ArraySet<String> deps = new ArraySet<>();
                deps.add("e1");
                deps.add("e2");
                deps.add("e3");
                if (deps.contains("e2")) {
                    binding.textViewLog.setText("true");
                } else {
                    binding.textViewLog.setText("false");
                }
            }
        });
        // test UserExtend
        binding.buttonExtend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  UserDesign ud = new UserDesign();
                  ud.doSome();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // for debug
    private int getViewHeight(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        int height = view.getMeasuredHeight();
        // int width = imageView.getMeasuredWidth();
        return height;
    }

    private float getDensityDPI() {
        DisplayMetrics dm = new DisplayMetrics();
        dm = getContext().getResources().getDisplayMetrics();
        return dm.density;
    }

}