package com.example.alessio.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Contacts;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Alessio on 21/05/2016.
 */
public class reciever extends BroadcastReceiver {

    protected void onCreate(Bundle savedInstanceState) {
        Log.d("ricevuto!", "ricevuto!");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("ricevuto!", "ricevuto!");
        Intent i = new Intent(context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
