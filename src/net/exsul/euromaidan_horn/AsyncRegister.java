package net.exsul.euromaidan_horn;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: enela_000
 * Date: 21.02.14
 * Time: 6:36
 * To change this template use File | Settings | File Templates.
 */
public class AsyncRegister extends AsyncTask<Void, Void, String> {
    disclaimer me;
    public AsyncRegister( disclaimer _me ) {
        me = _me;
    }

    @Override
    protected String doInBackground(Void... params) {
        String msg = "";
        try {
            if (me.gcm == null) {
                me.gcm = GoogleCloudMessaging.getInstance(me.context);
            }
            me.regid = me.gcm.register(me.SENDER_ID);
            msg = "Device registered, registration ID=" + me.regid;

            // You should send the registration ID to your server over HTTP,
            // so it can use GCM/HTTP or CCS to send messages to your app.
            // The request to your server should be authenticated if your app
            // is using accounts.
            sendRegistrationIdToBackend();

            // For this demo: we don't need to send it because the device
            // will send upstream messages to a server that echo back the
            // message using the 'from' address in the message.

            // Persist the regID - no need to register again.
            storeRegistrationId(me.context, me.regid);
        } catch (IOException ex) {
            msg = "Error :" + ex.getMessage();
            // If there is an error, don't just keep trying to register.
            // Require the user to click a button again, or perform
            // exponential back-off.
        }
        return msg;
    }

    @Override
    protected void onPostExecute(String msg) {
    }


    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * or CCS to send messages to your app. Not needed for this demo since the
     * device sends upstream messages to a server that echoes back the message
     * using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        // Your implementation here.
    }

    void RegistrationID( Boolean register, String id ) {


    }


    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = me.getGCMPreferences(context);
        int appVersion = me.getAppVersion(context);
        Log.i(me.TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(me.PROPERTY_REG_ID, regId);
        editor.putInt(me.PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

}
