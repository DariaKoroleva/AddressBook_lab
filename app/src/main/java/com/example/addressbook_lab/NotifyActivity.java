package com.example.addressbook_lab;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class NotifyActivity extends AppCompatActivity {
    public static final String NOTIFY_KEY = "notify_key";

    TextView textNameFamily;
    Button alarmOffButton;
    Ringtone ring;

    Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //contact = (Contact) getIntent().getSerializableExtra(NOTIFY_KEY);

        //инициализируем кнопки
        textNameFamily = (TextView) findViewById(R.id.text_name_family);
        alarmOffButton = (Button) findViewById(R.id.notifyOffButton);


        //fillTextNameFamily();
        playSound(getApplicationContext());
        setContentView(R.layout.activity_notify);
    }


    private void fillTextNameFamily(){
       textNameFamily.setText(contact.getLastName() + " " + contact.getFirstName() +  " " + contact.getPatronymic());
    }


    public void turn_off_alarm(View v){
        ring.stop();
        this.finish();
    }

    private void playSound(Context context){
        try {
            Uri notify = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            ring = RingtoneManager.getRingtone(context, notify);
            ring.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Button.OnClickListener oclBtnOk = v -> {
    };
}