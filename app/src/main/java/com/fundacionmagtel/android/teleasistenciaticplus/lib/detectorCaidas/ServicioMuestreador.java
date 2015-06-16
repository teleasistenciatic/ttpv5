package com.fundacionmagtel.android.teleasistenciaticplus.lib.detectorCaidas;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import com.fundacionmagtel.android.teleasistenciaticplus.modelo.Constants;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.helper.AppLog;

/**
 * Servicio encargado de escuchar el sensor acelerómetro y procesar los datos
 *
 * Created by SAMUAN on 13/04/2015.
 */
public class ServicioMuestreador extends Service implements SensorEventListener, Constants {

    private Monitor monitor;
    private WakeLock wakeLock;
    private PowerManager mgr;
    private HandlerThread handlerThread;
    private Handler handler;
    private SensorManager sensor;
    private String TAG="ServicioMuestreador";

    /**
     * Método de framework onCreate
     */
    @Override
    public void onCreate() {
        super.onCreate();
        AppLog.i(TAG, "creando");

        mgr = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
        monitor=new Monitor(getResources());

        sensor =(SensorManager) getSystemService(SENSOR_SERVICE);

        handlerThread=new HandlerThread("sensorcaidas");
        handlerThread.start();
        handler=new Handler(handlerThread.getLooper());
    }

    /**
     * Activa el servicio. Iniciar la captación de datos del sensor acelerómetro y arranca un
     * nuevo hilo donde se recibirán los eventos del sensor.
     *
     * @param intent The Intent supplied to startService(Intent), as given.
     * @param flags Additional data about this start request. Currently either 0, START_FLAG_REDELIVERY, or START_FLAG_RETRY.
     * @param startId A unique integer representing this specific request to start. Use with stopSelfResult(int).
     * @return The return value indicates what semantics the system should use for the service's current started state. It may be one of the constants associated with the START_CONTINUATION_MASK bits.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AppLog.i(TAG, "servicio onStartcommmand " + startId);
        wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
        wakeLock.acquire();
        sensor.registerListener(ServicioMuestreador.this, sensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 20000, handler);
        setSharedPreferenceData(DETECTOR_CAIDAS_SERVICIO_INICIADO,"true");
        return START_STICKY;
    }

    /**
     * Método de framework onDestroy
     */
    @Override
    public void onDestroy() {
        handlerThread.quit();
        sensor.unregisterListener(this);
        wakeLock.release();
        setSharedPreferenceData(DETECTOR_CAIDAS_SERVICIO_INICIADO,"false");
    }

    /**
     * Método de framework onBind
     * @param intent
     * @return
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Método de framework onSensorChanged
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        monitor.gestionar(event);
    }

    /**
     * Método de framework onAccuracyChanged
     * @param sensor
     * @param accuracy
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {   }

    /**
     * Método para guardar en preferencias el estado del servicio.
     * @param map clave para identificar el dato.
     * @param valor indica si está activo o inactivo.
     */
    private void setSharedPreferenceData(String map, String valor) {
        SharedPreferences.Editor editor = getSharedPreferences(APP_SHARED_PREFERENCES_FILE, Context.MODE_MULTI_PROCESS).edit();
        editor.putString(map, valor);
        editor.commit();
    }

}
