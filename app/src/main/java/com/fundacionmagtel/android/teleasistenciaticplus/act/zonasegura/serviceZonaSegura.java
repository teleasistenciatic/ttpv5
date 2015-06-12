package com.fundacionmagtel.android.teleasistenciaticplus.act.zonasegura;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.fundacionmagtel.android.teleasistenciaticplus.R;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.filesystem.FileOperation;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.helper.AppLog;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.helper.AppSharedPreferences;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.helper.AppTime;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.sms.SmsLauncher;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.sound.PlaySound;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.stats.StatsFileLogTextGenerator;
import com.fundacionmagtel.android.teleasistenciaticplus.modelo.Constants;
import com.fundacionmagtel.android.teleasistenciaticplus.modelo.DebugLevel;
import com.fundacionmagtel.android.teleasistenciaticplus.modelo.TipoAviso;

/**
 * Servicio Zona Segura que comprueba la distancia con un punto dado
 * @author Juan Jose Ferres
 */

public class serviceZonaSegura extends Service implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        Constants {

    private static final String TAG = "ZonaSeguraService";

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;

    Location mCurrentLocation;
    String mLastUpdateTime;

    int mStartMode;         //Indica como responde si el servicio se mata
    IBinder mBinder;        // interfaz

    /* Datos de posicion de zona segura */
    double zonaSeguraLatitud;
    double zonaSeguraLongitud;
    double zonaSeguraRadio;

    /* Vector de actualizaciones de posición */

    FifoPosicionTiempo miFifoPosiciontiempo = new FifoPosicionTiempo(Constants.DEFAULT_ZONA_SEGURA_POOL);

    //private Timer mTimer = new Timer();
    private final Messenger mMessenger = new Messenger(new IncomingMessageHandler()); // Target we publish for clients to send messages to IncomingHandler.


    /**
     * Método de framework onCreate
     */
    @Override
    public void onCreate() {
        
        super.onCreate();
        AppLog.d(TAG, "Servicio Zona Segura iniciado.");

        FileOperation.fileLogErase(Constants.DEBUG_ZONA_SEGURA_LOG_FILE);

        /*Toast.makeText(getBaseContext(), (String) "Servicio iniciado",
                Toast.LENGTH_SHORT).show();*/

        //mTimer.scheduleAtFixedRate(new MyTask(), 0, 2000L);

        //Se indica que el servicio está funcionando
        try {
            setSharedPreferenceData(Constants.ZONA_SEGURA_SERVICIO_INICIADO, "true");
        } catch (Exception e) {
            AppLog.e(TAG,"Error fatal al guardar SharedPreferences",e);
        }

        //show error dialog if GooglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            String errorGooglePlay = getResources().getString(R.string.error_google_play_no_zona_segura);
            Toast.makeText(getBaseContext(), errorGooglePlay, Toast.LENGTH_SHORT).show();
            AppLog.e(TAG, errorGooglePlay);
            return; //Salida del servicio

        }

        //El servicio sólo funcionará si tenemos una posición guardada de lat/long/radio en las
        //Sharedpreferences

        AppSharedPreferences miAppSharedPreferences = new AppSharedPreferences();

        boolean hasZonaSeguraGpsPos = miAppSharedPreferences.hasZonaSegura();

        if (!hasZonaSeguraGpsPos) {

            String errorZonaSegura = getResources().getString(R.string.error_zona_segura_no_home_set);
            Toast.makeText(getBaseContext(), errorZonaSegura, Toast.LENGTH_LONG).show();

            AppLog.e(TAG, errorZonaSegura);
            return; //Salida del servicio

        } else { //Existe zona segura

            String[] datosZonaSegura = miAppSharedPreferences.getZonaSeguraData();

            //Leemos de las sharedpreferences y guardamos la posición y
            //radio de la zona segura
            zonaSeguraLatitud = Double.parseDouble(datosZonaSegura[0]);
            zonaSeguraLongitud = Double.parseDouble(datosZonaSegura[1]);
            zonaSeguraRadio = Double.parseDouble(datosZonaSegura[0]);

        }

        //Tras crear la petición de posición, cada vez que se produzca un cambio en la posición
        //se llamará al método onLocationChanged
        createLocationRequest();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();
    }

    /**
     * Método que se llama una vez y establece los tiempos de lectura
     * del GPS, tanto directamente de cualquier valor que lo ofrezca
     * como si otra aplicación lo lee.
     */
    protected void createLocationRequest() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(Constants.GPS_READ_INTERVAL);
        mLocationRequest.setFastestInterval(Constants.GPS_READ_FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Ejecución del servicio. Este se ejecuta indefinidamente e incluso
     * puede arrancarse a sí mismo.
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()
        Log.i(TAG, "Received start id " + startId + ": " + intent);

        return START_STICKY; //// Run until explicitly stopped.
    }

    /**
     * Método de framework onbind
     * @param intent
     * @return
     */
    @Override
    public IBinder onBind(Intent intent) {
        AppLog.d(TAG, "onBind");
        return mMessenger.getBinder();
    }

    /**
     * Método de framework onUnBind
     * @param intent
     * @return
     */
    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()
        return true; //si se puede Rebind
    }

    /**
     * Método de framework onRebind
     * @param intent
     */
    @Override
    public void onRebind(Intent intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    }

    /**
     * Método de framework onDestroy
     */
    @Override
    public void onDestroy() {
        /*
        // The service is no longer used and is being destroyed
        if (mTimer != null) {
            mTimer.cancel();
        }*/

        AppLog.d(TAG, "Servicio detenido.");
        try {
            setSharedPreferenceData(Constants.ZONA_SEGURA_SERVICIO_INICIADO, "false");
        } catch ( Exception e ) {
            AppLog.e(TAG, "Error fatal al guardar las SharedPreferences", e);
        }

        mGoogleApiClient.disconnect();
    }

    /**
     * Método de framework onConnected
     * @param bundle
     */
    @Override
    public void onConnected(Bundle bundle) {
        AppLog.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    /**
     * Comienzo de lectura de actualizaciones de localización
     */
    protected void startLocationUpdates() {
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        AppLog.d(TAG, "Location update started ..............: ");
    }

    /**
     * Método de framework onConnectionSuspended
     * @param i
     */
    @Override
    public void onConnectionSuspended(int i) {

    }

    /**
     * Método de framework onConnentionFailed
     * @param connectionResult
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        AppLog.d(TAG, "Connection failed: " + connectionResult.toString());
    }

    /**
     * Cada vez que hay un cambio de posición dentro de los parámetros
     * establecidos por setInterval.
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        //AppLog.d(TAG, "Firing onLocationChanged...........................................");

        mCurrentLocation = location;
        //mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        mLastUpdateTime = new AppTime().getTimeDate();

        /////////////////////////////////////
        checkZonaSegura();
        /////////////////////////////////////
    }

    /**
     * Devuelve el valor de la distancia entre dos puntos, usando la función
     * Location.distanciaBetween. Aquí no se tiene en cuenta la precisión de
     * la medición.
     *
     * @return float de la distancia entre dos puntos
     */
    private double distanciaEntreHomeYPosicionActual() {

        float[] resultado = new float[3]; //el resultado de distancebetween requiere un float[]

        Location.distanceBetween(mCurrentLocation.getLatitude(),
                mCurrentLocation.getLongitude(),
                zonaSeguraLatitud,
                zonaSeguraLongitud,
                resultado);

        return resultado[0];

    }
    /**
     * Métido de comprobación -en cada cambio de posición- de si
     * ha salido de la zona segura.
     *
     * El sistema usa una cola FIFO y sólo dispara el evento cuando esta
     * cola se ha llenado y todas las lecturas están fuera de la zona
     * segura (un mecanismo de control de que no salga y entre
     * rapidamente de la Zona Segura).
     */
    private void checkZonaSegura() {

        //AppLog.d(TAG, "Check Zona Segura initiated .............");

        if ( mCurrentLocation != null ) {

            boolean inSecureZone = personInSecureZone(mCurrentLocation.getLatitude(),
                    mCurrentLocation.getLongitude(),
                    zonaSeguraLatitud,
                    zonaSeguraLongitud,
                    zonaSeguraRadio, (float) mCurrentLocation.getAccuracy());

            String lat = String.valueOf(mCurrentLocation.getLatitude());
            String lng = String.valueOf(mCurrentLocation.getLongitude());

            String distancia =  String.valueOf( distanciaEntreHomeYPosicionActual() );

            String mostrar = "Hora : " + mLastUpdateTime + "\n" +
                    "Latitud: " + lat + "\n" +
                    "Longitud: " + lng + "\n" +
                    "Precision: " + mCurrentLocation.getAccuracy() + "\n" +
                    "DISTANCIA: " + distancia + "\n" +
                    "ZONA SEGURA: " + inSecureZone + "\n" +
                    "POOL: " + miFifoPosiciontiempo.size() + "\n" +
                    "Proveedor: " + mCurrentLocation.getProvider();

            PosicionTiempo miPosicionTiempo = new PosicionTiempo(mCurrentLocation.getLatitude(),
                    mCurrentLocation.getLongitude(),
                    mCurrentLocation.getAccuracy(),
                    mCurrentLocation.getProvider(),
                    mLastUpdateTime,
                    inSecureZone);

            /* Se añaden la posicion a un pool de posiciones, tantas como Constants.DEFAULT_ZONA_SEGURA_POOL */
            miFifoPosiciontiempo.add(miPosicionTiempo);

            try {
                setSharedPreferenceDataGps(
                        String.valueOf(mCurrentLocation.getLatitude()),
                        String.valueOf(mCurrentLocation.getLongitude()),
                        String.valueOf(mCurrentLocation.getAccuracy()),
                        String.valueOf(mLastUpdateTime)
                );
            } catch (Exception e) {
                AppLog.e(TAG, "Error al escribir las shared preferences", e);
            }


            /* Aquí tiene que venir el cálculo de si ha salido de la zona segura */

            //////////////////////////////////////////////////////////////////////
            /////////////// SALIDA ZONA SEGURA ///////////////////////////////////
            //////////////////////////////////////////////////////////////////////
            //Todas las posiciones de la lista están fuera de la zona segura
            if ( miFifoPosiciontiempo.listaPosicionTiempoAllNotInZone() ) {

                //Limpiamos la pila de posicionestiempo
                miFifoPosiciontiempo.clear();

                //Si estamos en modo depuración se indica
                if ( (Constants.PLAY_SOUNDS) && (Constants.DEBUG_LEVEL == DebugLevel.DEBUG)) {
                    PlaySound.play(R.raw.zonasegura_ha_salido_zonasegura);
                }

                /////////////////////////////////////////////////
                /////////////// ENVIO DE SMS DE AVISO ///////////
                /////////////////////////////////////////////////

                //código para el envio de sms.
                SmsLauncher miSmsLauncher = new SmsLauncher(TipoAviso.SALIDAZONASEGURA);
                Boolean temporal = miSmsLauncher.generateAndSend();

                AppLog.i(TAG,"SMS de aviso de Zona Segura enviado");

                /////////////////////////////////////////////////////
                StatsFileLogTextGenerator.write("zona segura", "ha salido zona segura");
                /////////////////////////////////////////////////////


                /////////////////////////////////////////////////
            }
            ////////////////////////////////////////////////////////////////////////

            //AppLog.d(TAG, mostrar);
            //AppLog.d(TAG, mostrar);

            //Si estamos en modo depuración se indica
            if ( (Constants.PLAY_SOUNDS) && (Constants.DEBUG_LEVEL == DebugLevel.DEBUG)) {
                PlaySound.play(R.raw.zonasegura_gps_leido);
            }

            if ( (Constants.DEBUG_LEVEL == DebugLevel.DEBUG) && (Constants.TOAST_DATOS_ZONA_SEGURA)) {
                Toast.makeText(getBaseContext(), (String) mostrar,
                        Toast.LENGTH_LONG).show();
            }

            if ( Constants.STATS_LOG_ZONA_SEGURA_GPS ) {
                /////////////////////////////////////////////////////
                StatsFileLogTextGenerator.writeGps("zona segura", (String) mostrar);
                /////////////////////////////////////////////////////
            }

        } else {
            AppLog.d(TAG, "location is null ...............");
        }
    }

    /**
     * ¿Se encuentra la persona en la Zona Segura?
     *
     * Aquí sí se tiene en cuenta la precisión.
     * Si estamos a 10 metros, pero la precisión es de 1km, no
     * podemos tomar la decisión correctamente.
     *
     * @param pointALatitude
     * @param pointALongitude
     * @param pointBLatitude
     * @param pointBLongitude
     * @param secureZoneRadius
     * @param accuracy
     * @return
     */
    private boolean personInSecureZone(Double pointALatitude,
                                       Double pointALongitude,
                                       Double pointBLatitude,
                                       Double pointBLongitude,
                                       Double secureZoneRadius,
                                       float accuracy) {

        float[] resultado = new float[3];

        Location.distanceBetween(pointALatitude,
                pointALongitude,
                pointBLatitude,
                pointBLongitude,
                resultado);

        //Procesamos la distancia teniendo en cuenta el radio de la zona segura
        //y la precisión de la medición

        double distancia;

        if ( resultado[0] > accuracy ) {
            distancia = resultado[0] - accuracy;
        } else { //Si la precision es demasiado grande no podemos fiarnos
            distancia = 0;
        }

        // Mientras el beneficiario se encuentre a menos distancia que el radio se considera que está seguro
        // la distancia es entre el punto definido como casa y la posición actual de GPS
        // A la distancia que tenemos hay que restarle la precisión. Si nos da 50m de distancia pero 78 metros de
        // precisión, en el peor caso estará a 50mm - 78mm.

        if ( distancia < secureZoneRadius ) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Detenemos los cambios de ubicación.
     * Este métido no llega a usarse nunca en la aplicación actual.
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        AppLog.d(TAG, "Location update stopped .......................");
    }

    /*
    public static boolean isRunning() {
        return serviceIsRunning;
    }*/

    /**
     * Comprobación de que los servicios de Google Play están funcionando
     * @return
     */
    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else { // España
            return false;
        }
    }


    /////////////////////////////////////////////////////////////////////////////////
    // CLASES ANIDADAS //////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////

    /**
     * Clase anidada que maneja los mensajes de comunicación del servicio y la actividad.
     * Este sistema de comunicación no se está utilizando.
     *
     * Handle incoming messages from MainActivity
     */
    private class IncomingMessageHandler extends Handler { // Handler of incoming messages from clients.

        @Override
        public void handleMessage(Message msg) {
            AppLog.d(TAG, "handleMessage: " + msg.what);
        }

    }

    /** La forma de escribir las SharedPreferences puede dar problemas al usar las clases de la actividad.
     * con esta clase se separa su utilización y se garantiza que no da error.
     *
     * El problema de usar las clases helper de la actividad es que usan el Contexto y por ese motivo
     * es mejor usar este métido aislado.
     *
     * De haber diseñado la clase de SharredPreferences con esto en mente no hubiera sido necesario duplicar este
     * método aquí.
     *
     * @param map
     * @param valor
     */

    //////////////////////////////////////////// GETTER SETTER APPSHAREDPREFERENCES ////////////////////
    public void setSharedPreferenceData(String map, String valor) {
        SharedPreferences.Editor editor = getSharedPreferences(APP_SHARED_PREFERENCES_FILE, Context.MODE_MULTI_PROCESS).edit();
        editor.putString(map, valor);
        editor.commit();
    }

    /**
     * Obtener la posición de Zona Segura de las SharedPreferences almacenadas.
     * Para que este servicio funcione es necesario que la Zona Segura y el Radio están definidos
     * con anterioridad.
     *
     * @param latitud
     * @param longitud
     * @param precision
     * @param ultimaActualizacion
     */
    public void setSharedPreferenceDataGps(String latitud, String longitud, String precision, String ultimaActualizacion) {
        setSharedPreferenceData(Constants.GPS_LATITUD, latitud);
        setSharedPreferenceData(Constants.GPS_LONGITUD, longitud);
        setSharedPreferenceData(Constants.GPS_PRECISION, precision);
        setSharedPreferenceData(Constants.GPS_ULTIMA_ACTUALIZACION, ultimaActualizacion);
        setSharedPreferenceData(Constants.GPS_ULTIMA_ACTUALIZACION_FORMATO_NUMERICO, String.valueOf(System.currentTimeMillis()) );
    }

    /*
    public String getSharedPreferenceData(String map) {

        SharedPreferences prefs = getSharedPreferences(APP_SHARED_PREFERENCES_FILE, Context.MODE_MULTI_PROCESS);
        String value = prefs.getString(map, "");

        return value;
    }*/
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    private class MyTask extends TimerTask {
        @Override
        public void run() {
            AppLog.d(TAG, "Timer doing work.");

            try {
                //counter += incrementBy;
                //sendMessageToUI(counter);

            } catch (Throwable t) { //you should always ultimately catch all exceptions in timer tasks.
                Log.e("TimerTick", "Timer Tick Failed.", t);
            }
        }
    }*/
}