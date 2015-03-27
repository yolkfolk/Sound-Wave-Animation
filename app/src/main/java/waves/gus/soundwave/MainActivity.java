package waves.gus.soundwave;

import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

public class MainActivity extends Activity {
    private SoundWave sView;

    public static MediaRecorder recorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/audiorecordtest.3gp";

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try{
            recorder.prepare();
            recorder.start();
        }
        catch(IOException e){
            Log.e("Recorder", "Prepare Failed");
        }

        sView = (SoundWave) findViewById(R.id.soundWave);
    }
}
