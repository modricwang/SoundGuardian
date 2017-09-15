package wang.yi_ru.prerelease;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class ManageActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_manage);
        ToggleButton toggleButton = (ToggleButton) findViewById(R.id.manage_toggle_service);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent intent = new Intent(ManageActivity.this, FloatingService.class);
                if (isChecked) {
                    startService(intent);
                } else {
                    stopService(intent);
                }
            }
        });
        Log.e("Context Manage",getApplicationContext().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_manage, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_register:
                Intent intent=new Intent(ManageActivity.this, RegisterActivity.class);
                intent.putExtra("Registered",true);
                startActivity(intent);
                return true;

            case R.id.menu_password:
                startActivity(new Intent(ManageActivity.this, PasswordActivity.class));
                return true;

            default:
                return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(ManageActivity.this, FloatingService.class));
        stopService(new Intent(ManageActivity.this, DialogFloatingService.class));
    }
}
