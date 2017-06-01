package redbao.yang.com.myredbao;

import android.app.Notification;
import android.app.PendingIntent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.text.TextUtils.isEmpty;

/**
 * 监听通知消息
 */
public class MyNotificationService extends NotificationListenerService {

    private static String TAG="MyNotificationService";
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Notification notification = sbn.getNotification();
        if (null == notification) return;

        Bundle extras = notification.extras;
        if (null == extras) return;

        Log.i(TAG,extras.toString());
        List<String> textList = new ArrayList<>();
        String title = extras.getString("android.title");
        if (!isEmpty(title)) textList.add(title);

        String detailText = extras.getString("android.text");
        if (!isEmpty(detailText)) textList.add(detailText);

        if (textList.size() == 0) return;
        for (String text : textList) {
            if (!isEmpty(text) && text.contains("[微信红包]")) {
                final PendingIntent pendingIntent = notification.contentIntent;
                try {
                    pendingIntent.send();
                } catch (PendingIntent.CanceledException e) {
                }
                break;
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Toast.makeText(this,"通知已关闭",Toast.LENGTH_SHORT).show();
    }

}
