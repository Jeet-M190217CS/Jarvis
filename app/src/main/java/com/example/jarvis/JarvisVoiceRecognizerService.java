package com.example.jarvis;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaActionSound;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.List;

public class JarvisVoiceRecognizerService extends Service{

    private SpeechRecognizer jarvisSpeechRecognizer;
    private Intent jarvisSpeechRecognizerIntent;
    private Context myContext;
    private static final int MSG_START_RECOGNIZER = 1;
    private static final int MSG_STOP_RECOGNIZER = 2;
    private final Messenger jarvisMessenger = new Messenger(new JarvisHandler(this));
    private Message jarvisStartMessage;
    private Message jarvisStopMessage;
    private int currentVol = 0;


    @Override
    public void onCreate() {
        super.onCreate();
        this.jarvisSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        this.jarvisSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        this.jarvisSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        this.jarvisSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        this.jarvisSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                System.out.println("on ready speech");
            }

            @Override
            public void onBeginningOfSpeech() {
                System.out.println("on beginning speech");
            }

            @Override
            public void onRmsChanged(float rmsdB) {
                System.out.println("on rms change ");
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                System.out.println("on buffer recevied");
            }

            @Override
            public void onEndOfSpeech() {
                System.out.println("on END OF speech");
            }

            @Override
            public void onError(int error) {
                System.out.println("on error speech"+error);
                muteBeepSoundOfRecorder();
                jarvisStartMessage = Message.obtain(null, MSG_START_RECOGNIZER);
                try{
                    jarvisMessenger.send(jarvisStartMessage);
                }catch(RemoteException re){
                    Toast.makeText(JarvisVoiceRecognizerService.this, re.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onResults(Bundle results) {

                List<String> result = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                for(String str : result){
                    if(str.toLowerCase().equalsIgnoreCase("hello jarvis")) {
                        Toast.makeText(JarvisVoiceRecognizerService.this, "Hello Jarvis", Toast.LENGTH_LONG).show();
                        processCommand();
                    }
                }
                jarvisStartMessage = Message.obtain(null, MSG_START_RECOGNIZER);
                muteBeepSoundOfRecorder();
                try{
                    jarvisMessenger.send(jarvisStartMessage);
                }catch(RemoteException re){
                    Toast.makeText(JarvisVoiceRecognizerService.this, re.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

                List<String> result = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                for(String str : result) {
                    if(str.toLowerCase().equalsIgnoreCase("hello jarvis")) {
                        Toast.makeText(JarvisVoiceRecognizerService.this, "Hello Jarvis", Toast.LENGTH_LONG).show();
                        processCommand();
                    }
                }

                jarvisStartMessage = Message.obtain(null, MSG_START_RECOGNIZER);
                muteBeepSoundOfRecorder();
                unMuteBeepSound();
                try{
                    jarvisMessenger.send(jarvisStartMessage);
                }catch(RemoteException re) {
                    Toast.makeText(JarvisVoiceRecognizerService.this, re.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                System.out.println("on event speech");
            }
        });
        Toast.makeText(this, "onCreate", Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        muteBeepSoundOfRecorder();
        this.jarvisSpeechRecognizer.startListening(this.jarvisSpeechRecognizerIntent);

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

    @TargetApi(28)
    private void muteBeepSoundOfRecorder() {
        AudioManager jarvisAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        this.currentVol = jarvisAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
        if (jarvisAudioManager != null) {
            jarvisAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, jarvisAudioManager.getStreamMinVolume(AudioManager.STREAM_NOTIFICATION), 0);
        }
    }

    private void unMuteBeepSound() {
        AudioManager jarvisAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (jarvisAudioManager != null) {
            jarvisAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, jarvisAudioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION), 0);
        }
    }

    protected static class JarvisHandler extends Handler{
        private WeakReference<JarvisVoiceRecognizerService> mtarget;

        public JarvisHandler(JarvisVoiceRecognizerService target){
            this.mtarget = new WeakReference<>(target);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            final JarvisVoiceRecognizerService target = this.mtarget.get();

            switch(msg.what){
                case MSG_START_RECOGNIZER:
                    target.jarvisSpeechRecognizer.startListening(target.jarvisSpeechRecognizerIntent);
                    break;
                case MSG_STOP_RECOGNIZER:
                    target.jarvisSpeechRecognizer.stopListening();
                    break;
            }
        }
    }

    private void processCommand(){

    }
}
