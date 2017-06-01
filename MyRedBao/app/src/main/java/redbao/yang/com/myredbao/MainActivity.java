package redbao.yang.com.myredbao;

import android.Manifest;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button tv_accessibility=null;
    private Button tv_notification=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //打开 无障碍 设置页面
        tv_accessibility= (Button) findViewById(R.id.bt_main);
        tv_accessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            }
        });
        //打开 通知 设置页面
        tv_notification= (Button) findViewById(R.id.bt2_main);
        tv_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isAccessibleEnabled()){
            tv_accessibility.setText("无障碍已打开");
            tv_accessibility.setTextColor(Color.BLUE);

        }else {
            tv_accessibility.setText("请打开无障碍");
            tv_accessibility.setTextColor(Color.BLACK);
        }
        if(isNotificationEnabled()){
            tv_notification.setText("通知已打开");
            tv_notification.setTextColor(Color.BLUE);
        }else {
            tv_notification.setText("请打开通知");
            tv_notification.setTextColor(Color.BLACK);
        }
    }

    private boolean isAccessibleEnabled() {
        AccessibilityManager manager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);

        List<AccessibilityServiceInfo> runningServices = manager.getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK);
        for (AccessibilityServiceInfo info : runningServices) {
            if (info.getId().equals(getPackageName() + "/.MyAccessibilityService")) {
                return true;
            }
        }
        return false;
    }

    private boolean isNotificationEnabled() {
        ContentResolver contentResolver = getContentResolver();
        String enabledListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");

        if (!TextUtils.isEmpty(enabledListeners)) {
            return enabledListeners.contains(getPackageName() + "/" + getPackageName() + ".MyNotificationService");
        } else {
            return false;
        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return;

        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};

        boolean flag = true;
        for (String s : permissions) {
            if (checkSelfPermission(s) != PackageManager.PERMISSION_GRANTED) {
                flag = false;
                break;
            }
        }

        if (!flag) requestPermissions(permissions, 233);
    }
}
