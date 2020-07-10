package com.example.jarvis;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class JarvisTTS {
    public TextToSpeech tts;
    private static JarvisTTS obj = null;

    private JarvisTTS(Context context){
        this.tts =  new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    int result = tts.setLanguage(Locale.ENGLISH);
                }
            }
        });
    }

    public static JarvisTTS getJarvisTTS(Context context){
        if(obj == null){
            obj = new JarvisTTS(context);
        }
        return obj;
    }

}
