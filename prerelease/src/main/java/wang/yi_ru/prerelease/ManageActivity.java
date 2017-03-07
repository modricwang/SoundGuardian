package wang.yi_ru.prerelease;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ManageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
    }

    public void buttonListener(View v) {
        Intent intent = new Intent(ManageActivity.this, FloatingService.class);
        switch (v.getId()) {
            case R.id.open_button:
                startService(intent);
                break;
            case R.id.close_button:
                stopService(intent);
                break;
            default:
                break;
        }
    }

    private boolean isServiceRunning(String serviceName) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
