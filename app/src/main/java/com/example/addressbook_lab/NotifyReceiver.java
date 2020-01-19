package com.example.addressbook_lab;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static android.content.Intent.getIntent;


public class NotifyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Contact contact = (Contact) intent.getSerializableExtra(NotifyActivity.NOTIFY_KEY);
        Intent alarmManagerIntent = new Intent(context, NotifyActivity.class);
        alarmManagerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //alarmManagerIntent.putExtra(NotifyActivity.NOTIFY_KEY, contact);
        context.startActivity(alarmManagerIntent);
    }
}
