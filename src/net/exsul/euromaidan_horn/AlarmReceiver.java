package net.exsul.euromaidan_horn;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.widget.Toast;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by enela_000 on 21.02.14.
 */
public class AlarmReceiver extends BroadcastReceiver {

    final public static String ONE_TIME = "onetime";
    public static chat me = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
        //Acquire the lock
        wl.acquire();

        //You can do the processing here.
        Bundle extras = intent.getExtras();
        StringBuilder msgStr = new StringBuilder();

        if(extras != null && extras.getBoolean(ONE_TIME, Boolean.FALSE)){
            //Make sure this intent has been sent by the one-time timer button.
            msgStr.append("One time Timer : ");
        }
        Format formatter = new SimpleDateFormat("hh:mm:ss a");
        msgStr.append(formatter.format(new Date()));

        Toast.makeText(context, msgStr, Toast.LENGTH_LONG).show();

        new network(me).execute(null, null, null);
        me = null;

        //Release the lock
        wl.release();
    }

    public static int GetLastId( Context context ) {
        SharedPreferences pf = disclaimer.getGCMPreferences(context);
        return pf.getInt("last_id", 0);
    }

    public static void SetLastId( Context context, int id ) {
        SharedPreferences pf = disclaimer.getGCMPreferences(context);
        SharedPreferences.Editor editor = pf.edit();
        editor.putInt("last_id", id);
        editor.commit();
    }
}
