package wang.yi_ru.prerelease;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class SplashScreen extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        final SplashScreen This = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ImageView imageView = (ImageView)findViewById(R.id.imageView2);
        imageView.setImageResource(R.drawable.splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(This,WelcomeActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2700);
    }
}
