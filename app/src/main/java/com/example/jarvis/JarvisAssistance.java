package com.example.jarvis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;

import java.util.ArrayList;
import java.util.Locale;

public class JarvisAssistance extends AppCompatActivity {


    private JarvisTTS assistanceTTS;
    private JarvisSpeechRecognizer jarvisSR;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jarvis_assistance);
        this.assistanceTTS = JarvisTTS.getJarvisTTS(getApplicationContext());
        this.jarvisSR = JarvisSpeechRecognizer.getSpeechRecognizer(this);
        this.jarvisSR.setRecognitionListener(this);
        this.assistanceTTS.tts.speak("Hello !!! What can I do for you ???",TextToSpeech.QUEUE_FLUSH,null,"");
    }

    private void processCommand(ArrayList<String> texts){
        for(String str : texts){
            System.out.println("jeet"+str);
        }
    }
}
