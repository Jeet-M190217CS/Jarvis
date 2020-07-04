package com.example.jarvis;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW}, PackageManager.PERMISSION_GRANTED);
    }

    public void startService(View v){
        Intent newIntent = new Intent(this, JarvisVoiceRecognizerService.class);
        startService(newIntent);
        this.moveTaskToBack(true);
    }

    public void stopService(View v){
        //Intent newIntent = new Intent(this, JarvisAssistance.class);
        //startActivity(newIntent);

        Uri number = Uri.parse("tel:5551234");
        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
        //callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(callIntent);
    }
}



class JarvisVoiceRecognizerTask extends AsyncTask<Void, Void, Void>{

    private SpeechRecognizer jarvisSpeechRecognizer;
    private Intent jarvisSpeechRecognizerIntent;
    private Context myContext;
    public JarvisVoiceRecognizerTask(Context context){
        this.myContext = context;
        this.jarvisSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this.myContext);
        this.jarvisSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        this.jarvisSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        this.jarvisSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {

            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                List<String> result = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                for(String str : result){
                    System.out.println(str);
                }
            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
    }


    @Override
    protected Void doInBackground(Void... voids) {
        int a = 0;

        //while(a == 0) {
            this.jarvisSpeechRecognizer.startListening(this.jarvisSpeechRecognizerIntent);
        //}

        return null;
    }
}
