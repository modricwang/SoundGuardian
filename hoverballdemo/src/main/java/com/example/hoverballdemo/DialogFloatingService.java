package com.example.hoverballdemo;

import android.app.Dialog;
import android.app.Service;
import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputConnection;
import android.widget.ImageView;
import android.widget.Toast;

public class DialogFloatingService extends Service {
    /**
     * 定义浮动窗口布局
     */
    Dialog mDialog;
    /**
     * 悬浮窗的布局
     */
    WindowManager.LayoutParams wmParams;
    LayoutInflater inflater;
    /**
     * 创建浮动窗口设置布局参数的对象
     */
    WindowManager mWindowManager;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    public void onCreate() {
        // TODO 自动生成的方法存根
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO 自动生成的方法存根
        initWindow();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    /**
     * 初始化
     */
    private void initWindow() {
        mDialog = new Dialog(DialogFloatingService.this);
        mDialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));

        //得到容器，通过这个inflater来获得悬浮窗控件
        inflater = LayoutInflater.from(getApplication());
        // 获取浮动窗口视图所在布局
        View view = inflater.inflate(R.layout.dialog_layout, null);

        ImageView iv = (ImageView) view.findViewById(R.id.imageView);
        iv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO 自动生成的方法存根
                Toast.makeText(getApplicationContext(), "ImageView onclick", Toast.LENGTH_SHORT).show();
            }
        });

        // 添加悬浮窗的视图
        mDialog.setContentView(view);
        mDialog.setTitle("对话框悬浮窗");

        mDialog.setCanceledOnTouchOutside(true);
        mDialog.show();
    }


}

