package waves.gus.soundwave;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import static waves.gus.soundwave.MainActivity.recorder;

/**
 * Created by gus on 25/03/15.
 */
public class SoundWave extends View {
    private Path path;
    private Paint paint;
    Context context;

    DisplayMetrics display = this.getResources().getDisplayMetrics();
    private float width = display.widthPixels, height = display.heightPixels; //height is measured from the top

    float space = 1f;
    float theta = 0f;
    final float baseAmplitude = 5f; //what all noise will "restore" to -- initial amplitude
    final float maxAmplitude = 140f; //amplitude limit
    float currentAmplitude = baseAmplitude;
    float degrade = 0.01f; //degradation factor that brings currentAmplitude back to baseAmplitude
    float period = width/1.5f; //distance between waves
    float dx = ((float)(2*Math.PI)/period)*space; //space between points
    float[] y_values = new float[(int)(width/space)]; //4 waves, increase or decrease as you like

    float current_amp = 0f; //net amplitude from mic; allows us to subtly draw sine

    public SoundWave(Context context, AttributeSet attrs){
        super(context,attrs);
        this.context = context;

        path = new Path();

        paint = new Paint();
        paint.setAntiAlias(true); /* makes lines look smoooooth */
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(3f);

        path.moveTo(0,height/2);
    }

    void calc_wave(){
        theta += 0.2; //speed of oscillations
        float x = theta;

        getAmplitudeValue();
        for(int i = 0; i < y_values.length; i++){
            y_values[i] = (float)Math.sin(x)*currentAmplitude;
            x+=dx;
        }
    }

    void getAmplitudeValue(){
        current_amp = (float)recorder.getMaxAmplitude(); //current amplitude of audio pickup
        degrade = 0.001f;//+0.001f*(currentAmplitude-baseAmplitude); //degradation should be lower when amplitude is high, to avoid jitter between waves

        if(currentAmplitude-degrade >= baseAmplitude){ //don't go below the base amplitude
            currentAmplitude -= degrade;
        }

        if(current_amp == 0){} //no audio (extremely rare) or lapse (likely) in audio pickup
        else{
            float increase = (current_amp/35000)*maxAmplitude; //increase 35000 for lower sensitivity, decrease for greater sensitivity
            float y = baseAmplitude+increase;

            if(y > maxAmplitude){currentAmplitude = maxAmplitude;} //don't exceed maximum amplitude
            else currentAmplitude = y; //jump to the spot
        }
    }

    @Override
    protected  void onDraw(Canvas canvas){
        super.onDraw(canvas);
        calc_wave(); //fill array
        draw_wave(canvas); //draw array
        invalidate();
    }

    void draw_wave(Canvas canvas){
        float h = height/2;
        path.moveTo(0,h/2+y_values[0]); //start position
        for(int i = 1; i < y_values.length-1; i+=2){
            path.quadTo(i*space,h/2+y_values[i],(i+1)*space,h/2+y_values[i+1]); //create bezier between critical points
        }
        canvas.drawPath(path, paint);
        path.reset();
    }
}