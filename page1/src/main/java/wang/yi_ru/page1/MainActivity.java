package wang.yi_ru.page1;

import android.app.Application;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.iflytek.cloud.SpeakerVerifier;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechListener;
import com.iflytek.cloud.SpeechUtility;

public class MainActivity extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //setContentView(R.layout.activity_main);
        SpeechUtility.createUtility(this, "appid=" + getString(R.string.app_id));
    }

}
