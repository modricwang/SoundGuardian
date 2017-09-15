package wang.yi_ru.prerelease;

import android.app.Application;

import com.iflytek.cloud.SpeechUtility;

/**
 * Created by modri on 2017/3/6.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 应用程序入口处调用,避免手机内存过小，杀死后台进程,造成SpeechUtility对象为null
        // 设置你申请的应用appid
        SpeechUtility.createUtility(this, "appid=" + getString(R.string.app_id));

    }
}
