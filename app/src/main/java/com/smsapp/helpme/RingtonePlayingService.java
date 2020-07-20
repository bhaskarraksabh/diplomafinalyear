package com.smsapp.helpme;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

public class RingtonePlayingService extends Service {
    private Ringtone ringtone;

    boolean stopPlaying=false;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(AudioManager.STREAM_RING, am.getStreamMaxVolume(AudioManager.STREAM_RING), 0);

        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        this.ringtone = RingtoneManager.getRingtone(this, ringtoneUri);
        ringtone.play();
        Timer mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (!ringtone.isPlaying() && !stopPlaying) {
                    ringtone.play();
                }
            }
        }, 1000 * 1, 1000 * 1);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        stopPlaying=true;
        this.ringtone.stop();
    }
}
