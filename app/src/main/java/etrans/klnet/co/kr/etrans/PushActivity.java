package etrans.klnet.co.kr.etrans;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;


public class PushActivity extends Activity {

    String push_seq = null;
    String push_msg = null;
    String push_obj_id = null;
    String push_sub_obj_id = null;
    String push_recv_id = null;
    String push_type = null;
    String push_isbackground = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push);

        if (getIntent().getExtras() != null) {

            String msg = getIntent().getExtras().getString("msg");
            JSONObject data = null;
            try {
                data = new JSONObject(msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (data != null) {
                try {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("push_id", data.getString("seq"));
                    intent.putExtra("msg", getIntent().getExtras().getString("body"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Log.i("CHECK", "push_id1 :"+data.getString("seq"));
                    Log.i("CHECK", "msg1 :"+getIntent().getExtras().getString("body"));

                    DataSet.getInstance().push_id = getIntent().getExtras().getString("body");
                    DataSet.getInstance().msg = data.getString("seq");
                    DataSet.getInstance().isbackground = "true";
                    this.startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


        finish();
    }


}
