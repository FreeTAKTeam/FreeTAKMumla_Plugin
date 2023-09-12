package com.atakmap.android.mumbleptt;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaDataSource;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.atakmap.android.cot.CotMapComponent;
import com.atakmap.android.dropdown.DropDownManager;
import com.atakmap.android.ipc.AtakBroadcast;
import com.atakmap.android.maps.MapActivity;
import com.atakmap.android.maps.MapView;

import com.atakmap.android.mumbleptt.plugin.mumblePttLifecycle;
import com.atakmap.android.widgets.LinearLayoutWidget;
import com.atakmap.android.widgets.MapWidget;
import com.atakmap.android.widgets.RootLayoutWidget;
import com.atakmap.coremap.cot.event.CotDetail;
import com.atakmap.coremap.cot.event.CotEvent;
import com.atakmap.coremap.cot.event.CotPoint;
import com.atakmap.coremap.maps.assets.Icon;
import com.atakmap.android.widgets.MarkerIconWidget;
import com.atakmap.coremap.log.Log;
import com.atakmap.android.mumbleptt.plugin.R;
import com.atakmap.coremap.maps.time.CoordinatedTime;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.UUID;

public class mumblePttWidget extends MarkerIconWidget
        implements MapWidget.OnPressListener, MapWidget.OnUnpressListener, MapWidget.OnClickListener {
    private final static int ICON_WIDTH = 64;
    private final static int ICON_HEIGHT = 128;
    private static SharedPreferences sharedPreference = null;
    private MediaRecorder mediaRecorder;
    private ParcelFileDescriptor[] fdPair;
    private ParcelFileDescriptor readFD, writeFD;
    public int toggled = 0;
    private MapView mapView;
    private Display display;

    public static final String TAG = "mumblePttWidget";

    public mumblePttWidget(MapView mapView) {
        setName("Mumble PTT");
        RootLayoutWidget root = (RootLayoutWidget) mapView.getComponentExtra("rootLayoutWidget");
        LinearLayoutWidget brLayout = root.getLayout(RootLayoutWidget.BOTTOM_RIGHT);
        brLayout.addWidget(this);
        addOnPressListener(this);
        addOnUnpressListener(this);
        addOnClickListener(this);
        toggleIcon(toggled);
        this.sharedPreference = PreferenceManager.getDefaultSharedPreferences(mapView.getContext().getApplicationContext());
        this.mapView = mapView;
        sd1a.register(mapView.getContext(), sdra);

        WindowManager wm = (WindowManager) mapView.getContext().getSystemService(Context.WINDOW_SERVICE);
        display = wm.getDefaultDisplay();


        fdPair = new ParcelFileDescriptor[0];
        try {
            fdPair = ParcelFileDescriptor.createPipe();
        } catch (IOException e) {
            e.printStackTrace();
        }
        readFD = fdPair[0];
        writeFD = fdPair[1];
        mediaRecorder = new MediaRecorder();
    }

    public void toggleIcon(int toggle) {
        int drawId;
        switch (toggle) {
            case 1:
                drawId = R.drawable.on;
                break;
            default:
                drawId = R.drawable.off;
                break;
        }

        String imageUri = "android.resource://com.atakmap.android.mumbleptt.plugin/" + drawId;

        Log.d(TAG, "imageURi " + imageUri);
        Icon.Builder builder = new Icon.Builder();
        builder.setAnchor(0, 0);
        builder.setColor(Icon.STATE_DEFAULT, Color.WHITE);
        builder.setSize(ICON_WIDTH, ICON_HEIGHT);
        builder.setImageUri(Icon.STATE_DEFAULT, imageUri);

        Icon icon = builder.build();
        setIcon(icon);
    }

    private final mumblePttSpeech2Text.SpeechDataListener sd1a = new mumblePttSpeech2Text.SpeechDataListener();
    private final mumblePttSpeech2Text.SpeechDataReceiver sdra = new mumblePttSpeech2Text.SpeechDataReceiver() {
        public void onSpeechDataReceived(Bundle activityInfoBundle) {

            String spokenwords = activityInfoBundle.getString("textFromSpeech");
            Log.i(TAG, String.format("SPOKENWORDS: %s", spokenwords));
            if (spokenwords.equalsIgnoreCase("contact")) {

                CotEvent cotEvent = new CotEvent();

                CoordinatedTime time = new CoordinatedTime();
                cotEvent.setTime(time);
                cotEvent.setStart(time);
                cotEvent.setStale(time.addDays(1));

                cotEvent.setUID(UUID.randomUUID().toString());
                cotEvent.setHow("m-g");

                cotEvent.setPoint(new CotPoint(mapView.getSelfMarker().getPoint()));
                cotEvent.setType("a-h-U");

                CotDetail cotContact = new CotDetail("contact");
                cotContact.setAttribute("callsign", String.format(Locale.US, "%s", "contact from voice"));

                CotDetail detail = new CotDetail("detail");
                detail.addChild(cotContact);
                cotEvent.setDetail(detail);

                if (cotEvent.isValid()) {
                    CotMapComponent.getInternalDispatcher().dispatch(cotEvent);
                    CotMapComponent.getExternalDispatcher().dispatch(cotEvent);
                    Toast.makeText(mapView.getContext(), "Creating CoT", Toast.LENGTH_SHORT).show();
                }
            }

        }
    };

    @Override
    public void onMapWidgetPress(MapWidget mapWidget, MotionEvent event) {
        if (mapWidget == this) {
            if (sharedPreference.getBoolean("plugin_mumbleptt_ptt", false)) {
                Intent i = new Intent("se.lublin.mumla.action.TALK");
                AtakBroadcast.getInstance().sendSystemBroadcast(i);
                toggleIcon(((++toggled) % 2));
/*
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                mediaRecorder.setAudioSamplingRate(8000);
                //mediaRecorder.setOutputFile(writeFD.getFileDescriptor());
                mediaRecorder.setOutputFile("/sdcard/atak/mumbleAudioMessage.amr");
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                try {
                    mediaRecorder.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaRecorder.start();
 */
            }
        }
    }
/*
    public class MyAudioSource extends MediaDataSource {
        private byte[] buf;
        private ByteArrayInputStream is;
        private int length;

        //public MyAudioSource(ParcelFileDescriptor.AutoCloseInputStream parcelFileDescriptor){
        public MyAudioSource(String audioPath){
            super();

            try {
                length = parcelFileDescriptor.read(this.buf);
                Log.i(TAG, String.format("Read %d bytes",length));
            } catch(IOException e) {
                e.printStackTrace();
            }

            try {
                buf = Files.readAllBytes(Paths.get("/sdcard/atak/mumbleAudioMessage.amr"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            is=new ByteArrayInputStream(buf);
        }

        public long getSize() {
            return buf.length;
        }

        public int readAt(long position, byte[] buffer, int offset, int size){
            is.reset();
            is.skip(position);
            return is.read(buffer,offset,size);
        }

        @Override
        public void close() throws IOException {
            is.close();
        }
    }
*/
    @Override
    public void onMapWidgetUnpress(MapWidget mapWidget, MotionEvent event) {
        if (mapWidget == this) {
            if (sharedPreference.getBoolean("plugin_mumbleptt_ptt", false)) {
                Intent i = new Intent("se.lublin.mumla.action.TALK");
                AtakBroadcast.getInstance().sendSystemBroadcast(i);
                toggleIcon(((++toggled) % 2));

                //mediaRecorder.stop();
                //mediaRecorder.reset();

                if (sharedPreference.getBoolean("plugin_mumbleptt_s2t", false)) {
                    // this makes use of an activity that cannot know anything
                    // about ATAK.   This is the same problem as we have with
                    // notifications.  They run outside of the current ATAK
                    // classloader paradigm.
                    Intent intent = new Intent();
                    intent.setClassName("com.atakmap.android.mumbleptt.plugin",
                            "com.atakmap.android.mumbleptt.mumblePttSpeech2Text");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ((Activity) mapView.getContext()).startActivityForResult(intent, 0);
                }

                //mediaRecorder.reset();
                //final ParcelFileDescriptor.AutoCloseInputStream reader = new ParcelFileDescriptor.AutoCloseInputStream(readFD);

/*
                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).build());
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        Log.i(TAG, "DONE PLAYBACK");
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    }
                });
                mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                        Log.i(TAG, "ON ERROR PLAYBACK");
                        return false;
                    }
                });

                MyAudioSource myAudioSource = new MyAudioSource("/sdcard/atak/mumbleAudioMessage.amr");
                mediaPlayer.setDataSource(myAudioSource);

                try {
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();

 */
            }
        }

    }

    @Override
    public void onMapWidgetClick(MapWidget mapWidget, MotionEvent event) {
        if (mapWidget == this) {
            if (!sharedPreference.getBoolean("plugin_mumbleptt_ptt", false)) {
                Intent i = new Intent("se.lublin.mumla.action.TALK");
                AtakBroadcast.getInstance().sendSystemBroadcast(i);
                toggleIcon(((++toggled) % 2));
            }
        }
    }

}
