package com.example.jarvis;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaActionSound;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.AlarmClock;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;

public class JarvisVoiceRecognizerService extends Service{
    private int currentVol = 0;
    private JarvisTTS jarvisTTS;
    private JarvisSpeechRecognizer jarvisSpeechRecognizer;


    @Override
    public void onCreate() {
        super.onCreate();
        this.jarvisTTS = JarvisTTS.getJarvisTTS(getApplicationContext());
        this.jarvisSpeechRecognizer = JarvisSpeechRecognizer.getSpeechRecognizer(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        this.jarvisSpeechRecognizer.startListening();
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "onDestroy", Toast.LENGTH_LONG).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
