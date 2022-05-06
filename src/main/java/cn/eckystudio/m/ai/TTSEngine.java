package cn.eckystudio.m.ai;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/*
 * Copyright (C) 2017 Ecky Studio
 * Author:Ecky Leung(liangyi)
 * Creation Date:2019-08-19 20:12
 * Function:provide some usual methods about Screen,Layout to use easily
 */
public class TTSEngine {

    SpeechConfig mConfig;
    TextToSpeech mTTS;
    boolean mInitStatus = false;

    public TTSEngine()
    {
        mConfig = new SpeechConfig();
    }

    public TTSEngine(SpeechConfig config){
        mConfig = config;
    }

    public void init(Context context)
    {
        mTTS = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    mTTS.setPitch(mConfig.Pitch);
                    mTTS.setSpeechRate(mConfig.SpeechRate);
                    int result = mTTS.setLanguage(mConfig.Language);
                    if (result == TextToSpeech.LANG_MISSING_DATA  || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        result = mTTS.setLanguage(Locale.ENGLISH);
                        if (result == TextToSpeech.LANG_AVAILABLE) {
                            speakImmediately("Don't support current language!");
                        }
                        return;
                    }
                    mInitStatus = true;
                }
            }
        });
    }

    static TTSEngine sEngine;
    public static TTSEngine getInstance(Context context){
        if(sEngine == null){
            sEngine = new TTSEngine();
            sEngine.init(context);
        }
        return sEngine;
    }

    public void dispose()
    {
        System.out.println("?=>liangyi dispose tts");
        if(mTTS != null)
        {
            mTTS.stop();
            mTTS.shutdown();
        }
    }

    public void speakImmediately(String words)
    {
        mTTS.speak(words,TextToSpeech.QUEUE_FLUSH,null);//TextToSpeech.QUEUE_FLUSH 中断当前播放，立即播放现存内容
    }

    public void speakNormal(String words){
        mTTS.speak(words,TextToSpeech.QUEUE_ADD,null);
    }


    public static class SpeechConfig{
        float Pitch = 1.0f;//设置音调
        float SpeechRate = 1.0f;//设置语速
        Locale Language = Locale.SIMPLIFIED_CHINESE;//设置语言
    }
}
