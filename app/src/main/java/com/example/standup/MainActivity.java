package com.example.standup;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import static android.app.AlarmManager.ELAPSED_REALTIME_WAKEUP;

public class MainActivity extends AppCompatActivity {
    AlarmManager alarmManager;
    ToggleButton alarmToggle;
    long repeatInterval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
    long triggerTime = SystemClock.elapsedRealtime() + repeatInterval;
    private NotificationManager notificationManager;
    private static final int NOTIFICATION_ID = 42;
    private static final String PRIMARY_CHANNEL_ID =
            "primary_notification_channel";
    Intent notifyIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notifyIntent = new Intent(this, AlarmReceiver.class);

        alarmToggle = findViewById(R.id.alarmToggle);

        notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);

        boolean alarmUp = (PendingIntent.getBroadcast(this, NOTIFICATION_ID, notifyIntent,
                PendingIntent.FLAG_NO_CREATE) != null);
        alarmToggle.setChecked(alarmUp);

        final PendingIntent notifyPendingIntent = PendingIntent.getBroadcast
                (this, NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmToggle.setOnCheckedChangeListener(

                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton,
                                                 boolean isChecked) {
                        String toastMessage;
                        if (isChecked) {
                            alarmManager.setInexactRepeating(ELAPSED_REALTIME_WAKEUP,
                                    triggerTime, repeatInterval, notifyPendingIntent);
                            toastMessage = getString(R.string.su_alarm_on);
                        } else {
                            if (alarmManager != null) {
                                alarmManager.cancel(notifyPendingIntent);
                            }
                            notificationManager.cancelAll();
                            toastMessage = getString(R.string.su_alarm_off);
                        }

                        Toast.makeText(MainActivity.this, toastMessage, Toast.LENGTH_SHORT)
                                .show();
                    }
                });
        createNotificationChannel();
    }

    public void createNotificationChannel() {
        notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel
                    (PRIMARY_CHANNEL_ID,
                            "Stand up notification",
                            NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription
                    ("Notifies every 15 minutes to stand up and walk");
            notificationManager.createNotificationChannel(notificationChannel);
        }

    }
}
