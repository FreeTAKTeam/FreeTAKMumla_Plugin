package com.atakmap.android.mumbleptt;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;
import java.util.Locale;

public class mumblePttSpeech2Text extends Activity {
    private static final int RESULT_SPEECH = 1;
    private static final String TAG = "mumblePttSpeech2Text";
    private Intent returnIntent;
    private String textFromSpeech;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Talk Now");
        //intent.putExtra(RecognizerIntent.EXTRA_AUDIO_INJECT_SOURCE

        try {
            startActivityForResult(intent, RESULT_SPEECH);
        } catch (ActivityNotFoundException a) {
            Log.i(TAG, "speech failed");
        }
        returnIntent = new Intent("com.atakmap.android.mumbleptt.SPEECH_INFO");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    textFromSpeech = text.get(0);
                    Bundle textBundle = new Bundle();
                    textBundle.putString("textFromSpeech", textFromSpeech);
                    returnIntent.putExtra("textFromSpeech", textBundle);
                    broadcast();
                } else {
                    finish();
                }
                break;
            }
            default:
                finish();
        }
    }

    private void broadcast() {
        sendBroadcast(returnIntent);
        finish();
    }

    public interface SpeechDataReceiver {
        void onSpeechDataReceived(Bundle activityInfoBundle);
    }

    public static class SpeechDataListener extends BroadcastReceiver {
        private boolean registered = false;
        private mumblePttSpeech2Text.SpeechDataReceiver sdra = null;

        synchronized public void register(Context context,
                                          mumblePttSpeech2Text.SpeechDataReceiver sdra) {
            if (!registered)
                context.registerReceiver(this,
                        new IntentFilter("com.atakmap.android.mumbleptt.SPEECH_INFO"));

            this.sdra = sdra;
            registered = true;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            synchronized (this) {
                try {
                    Bundle activityInfoBundle = intent
                            .getBundleExtra("textFromSpeech");
                    if (activityInfoBundle != null && sdra != null)
                        sdra.onSpeechDataReceived(activityInfoBundle);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
                if (registered) {
                    context.unregisterReceiver(this);
                    registered = false;
                }
            }
        }
    }

}
