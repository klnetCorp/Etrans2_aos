package etrans.klnet.co.kr.etrans;


import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by asm on 2018-02-27.
 */

public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "FirebaseMsgService";

    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        //추가한것
        Log.i("CHECK", "onMessageReceived"+remoteMessage.getData().toString());
        String title = "";
        String message = "";
        if( remoteMessage.getNotification() == null   ){
            title = remoteMessage.getData().get("title");
            message = remoteMessage.getData().get("body");
        }
        else{
            title = remoteMessage.getNotification().getTitle();
            message = remoteMessage.getNotification().getBody();
        }
        Log.e("###",title + message);
        String msg = remoteMessage.getData().get("msg");
        JSONObject data = null;
        try {
            data = new JSONObject(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("###",msg + data.toString());
        String add;
        if (remoteMessage.getData().get("add") != null) {
            add = remoteMessage.getData().get("add");
        } else {
            add = "";
        }
        sendNotification(title, message, data, add);
    }

    private void sendNotification(String title, String body, JSONObject data, String add) {
        String seq = null;
        String type = null;
        String doc_gubun = null;
        String param = null;
        if (data != null) {
            try {
                seq = data.getString("seq");
                type = data.getString("type");
                doc_gubun = data.getString("doc_gubun");
                param = data.getString("param");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.d("CHECK", "seq:" + seq);
        Log.d("CHECK", "type:" + type);
        Log.d("CHECK", "doc_gubun:" + doc_gubun);
        Log.d("CHECK", "param:" + param);
        Log.d("CHECK", "add:" + add);

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("push_id", seq);
        intent.putExtra("msg", body);
        intent.putExtra("recv_id", "");
        intent.putExtra("type", doc_gubun);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent3 = new Intent(this, MainActivity.class);
        intent3.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent3.putExtra("push_id", seq);
        intent3.putExtra("msg", body);
        intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent3);
    }

}
