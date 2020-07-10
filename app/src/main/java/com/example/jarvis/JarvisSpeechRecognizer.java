package com.example.jarvis;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.lang.ref.WeakReference;
import java.util.List;

public class JarvisSpeechRecognizer {
    private SpeechRecognizer jarvisSpeechRecognizer;
    private Intent jarvisSpeechRecognizerIntent;
    private final Messenger jarvisMessenger = new Messenger(new JarvisSpeechRecognizer.JarvisHandler(this));
    private Context myContext;
    private static JarvisSpeechRecognizer objJarvisSR = null;

    private JarvisSpeechRecognizer(Context context){
        this.myContext = context;
        this.jarvisSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this.myContext);
        this.jarvisSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        this.jarvisSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        this.jarvisSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.getPackageName());
        this.jarvisSpeechRecognizer.setRecognitionListener(new JarvisRecognitionListener(this.myContext, this.jarvisMessenger));
    }

    public static JarvisSpeechRecognizer getSpeechRecognizer(Context context){
        if(objJarvisSR == null){
            objJarvisSR = new JarvisSpeechRecognizer(context);
        }
        return objJarvisSR;
    }

    public void setRecognitionListener(Context context){
        this.jarvisSpeechRecognizer.setRecognitionListener(new JarvisRecognitionListener(context, this.jarvisMessenger));
    }

    public void startListening(){
        this.jarvisSpeechRecognizer.startListening(this.jarvisSpeechRecognizerIntent);
    }


    protected static class JarvisHandler extends Handler {
        private final int MSG_START_RECOGNIZER = 1;
        private final int MSG_STOP_RECOGNIZER = 2;

        private WeakReference<JarvisSpeechRecognizer> mtarget;

        public JarvisHandler(JarvisSpeechRecognizer target){
            this.mtarget = new WeakReference<>(target);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            final JarvisSpeechRecognizer target = this.mtarget.get();

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
}

class JarvisRecognitionListener implements RecognitionListener{
    private final int MSG_START_RECOGNIZER = 1;
    private final int MSG_STOP_RECOGNIZER = 2;

    private String CHANNEL_ID = "test";
    private JarvisTTS jarvisTTS;
    private static Message jarvisStartMessage;
    private static Message jarvisStopMessage;

    Context myContext;
    private final Messenger jarvisMessenger;

    public JarvisRecognitionListener(Context context, final Messenger jarvisMessenger){
        this.myContext = context;
        this.jarvisMessenger = jarvisMessenger;
        this.jarvisTTS = JarvisTTS.getJarvisTTS(context);
    }

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
            Toast.makeText(myContext, re.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResults(Bundle results) {

        List<String> result = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        for(String str : result){
            System.out.println(str);
            processCommand(str);
        }

        jarvisStartMessage = Message.obtain(null, MSG_START_RECOGNIZER);
        muteBeepSoundOfRecorder();
        try{
            jarvisMessenger.send(jarvisStartMessage);
        }catch(RemoteException re){
            Toast.makeText(myContext, re.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {

        List<String> result = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        for(String str : result) {
            System.out.println(str);
            if(str.toLowerCase().equalsIgnoreCase(myContext.getString(R.string.hotword))) {
                processCommand(str);
            }
        }


        jarvisStartMessage = Message.obtain(null, MSG_START_RECOGNIZER);
        muteBeepSoundOfRecorder();
        unMuteBeepSound();
        try{
            jarvisMessenger.send(jarvisStartMessage);
        }catch(RemoteException re) {
            Toast.makeText(myContext, re.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        System.out.println("on event speech");
    }

    @TargetApi(28)
    private void muteBeepSoundOfRecorder() {
        AudioManager jarvisAudioManager = (AudioManager) myContext.getSystemService(Context.AUDIO_SERVICE);
        int currentVol = jarvisAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
        if (jarvisAudioManager != null) {
            jarvisAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, jarvisAudioManager.getStreamMinVolume(AudioManager.STREAM_NOTIFICATION), 0);
        }
    }

    private void unMuteBeepSound() {
        AudioManager jarvisAudioManager = (AudioManager) myContext.getSystemService(Context.AUDIO_SERVICE);
        if (jarvisAudioManager != null) {
            jarvisAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, jarvisAudioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION), 0);
        }
    }

    private void showNotification(){
        this.jarvisTTS.tts.speak(myContext.getString(R.string.welcomeMSG), TextToSpeech.QUEUE_FLUSH, null, "WelcomeMSG");
        createNotificationChannel();

        Intent intent = new Intent(myContext, JarvisAssistance.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(myContext, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent bubbleIntent =
                PendingIntent.getActivity(myContext, 0, intent, 0 /* flags */);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(myContext, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Jarvis")
                        .setContentText("Hi..I am here")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setFullScreenIntent(fullScreenPendingIntent, true);

        Notification incomingCallNotification = notificationBuilder.build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(myContext);
        notificationManager.notify(786, incomingCallNotification);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = myContext.getString(R.string.channel_name);
            String description = myContext.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = myContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void processCommand(String str){
        if(str.equals(myContext.getString(R.string.hotword))){
            showNotification();
        }else if(str.contains("alarm")){

        }
    }

}
