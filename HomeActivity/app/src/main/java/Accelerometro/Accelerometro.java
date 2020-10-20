package Accelerometro;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.timeseries.TimeSeries;
import com.util.DistanceFunction;
import com.util.DistanceFunctionFactory;

import java.util.ArrayList;
import java.util.List;

public class Accelerometro implements SensorEventListener {

    private double[] accelerometerValues = new double[30];
    private int counter=0;
    private SensorManager sensorManager=null; //Tutti i sensori
    private Sensor accelerometer=null;
    private SensorEventListener sel= this;
    private Context context;

    public Accelerometro(Context context)
    {
        this.context=context.getApplicationContext();
        this.sensorManager=(SensorManager) this.context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            this.accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    private double[] valoreRiferimento= { //3.629945, 4.7222466, 3.8489187, 3.847166, 3.9240105, 3.90592, 3.8509967, 3.830742, 3.8745177, 3.8638394, 3.8468213, 3.8541284, 3.89754, 3.0880797, 3.2788496,
                                        10*4.4670825, 10*2.0428765, 10*7.126476, 10*6.4707613, 10*1.4708443, 10*0.91515964, 10*3.2301805, 10*5.3027353, 10*7.8721066, 10*9.816314, 10*9.699457, 10*9.642139, 10*9.272217, 10*12.216041};//, -8.217302, -9.822078,
                                        // -9.822892, -9.54507, -9.380654, -9.367784, -9.400335, -9.371432, -9.239703, -9.43598, -9.321077};  Da 1.4 in avanti


//sarebbe da implementare uno stop, in maniera tale che il sensori si fermi per pochi ms per eseguire la media e il match
//dopodichè riparta.
//questo perché la media basa il suo algoritmo sulla dimensione della lista, se la lista cresce in continuazione
//la media non termina mai il ciclo
//su internet dice che si può usare il metodo: unregisterListener, provo a implementarlo, tuttavia non sono sicuro
    //sull'utilizzo di questo metodo: ho paura che mi cancelli anche i dati

    private void onStop() {

        sensorManager.unregisterListener(sel, accelerometer);
    }

    private void onStart() {

        sensorManager.registerListener(sel, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private double distanza(){
        DistanceFunction distFn = DistanceFunctionFactory.getDistFnByName("EuclideanDistance");


        TimeSeries ts=new TimeSeries(this.accelerometerValues);

        double distanza=  com.dtw.FastDTW.getWarpInfoBetween(new TimeSeries(this.valoreRiferimento), ts, 1, distFn).getDistance();

        return distanza;
    }

    public boolean esegui(){
        onStart();
        if(distanza()<380)
        {
            onStop();
            return true;
        }
        return false;

    }




    @Override //Prendiamo i valori dall'accellerometro e li mettiamo nella lista
    public void onSensorChanged(SensorEvent event) {

        double y;
        y= Math.abs(10*(double)event.values[1]);

        if (counter >=30)
        {
            counter=10;
            for (int i=0; i<10; i++)
            {
                accelerometerValues[i]=accelerometerValues[19+i];
            }

        }

        accelerometerValues[counter]=y;
        counter++;

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {} //A noi non serve
}

/*
    DistanceFunction distFn = DistanceFunctionFactory.getDistFnByName("EuclideanDistance");

    TimeSeries ts1=new TimeSeries(3);
    TimeSeries ts2=new TimeSeries(3);

    double d=  com.dtw.FastDTW.getWarpInfoBetween(ts1, ts2, 3, distFn).getDistance();*/


