package com.local.android.teleasistenciaticplus.act.zonasegura;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.local.android.teleasistenciaticplus.R;
import com.local.android.teleasistenciaticplus.lib.helper.AppLog;
import com.local.android.teleasistenciaticplus.lib.helper.AppSharedPreferences;
import com.local.android.teleasistenciaticplus.modelo.Constants;

/**
 * Actividad que muestra la zona segura
 */
public class actZonaSeguraHomeSet extends FragmentActivity
        implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerDragListener {

    private GoogleMap map;

    private LatLng miPos = null;
    private Circle mCircle;
    private double radio;

    private String TAG = "actZonaSeguraHomeSet";

    private Marker miMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_act_zonasegura_sethome);

        /** Manejador de Mapa */
        /** Sets a callback object which will be triggered when the GoogleMap instance is ready to be used. */
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /** Recuperamos el objeto MAP para todas los métodos */
        map = mapFragment.getMap();

        /** Mostramos el botón que nos lleva directamente a la posición GPS */
        map.setMyLocationEnabled(true);
        map.getUiSettings().setCompassEnabled(true);

        /** Establecemos el listener para poder hacer un click largo */
        map.setOnMapLongClickListener(this);
        map.setOnMarkerDragListener(this);

        /** Preparamos para leer el radio de la zona segura desde el slider */
        SeekBar miSeekBar = (SeekBar) findViewById(R.id.seekbar_radio_zona_segura);

        miSeekBar.setMax( Constants.MAX_ZONA_SEGURA_RADIO );

        miSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                AppLog.d(TAG, "Se ha terminado de tocar");
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub


                if (miMarker == null) {
                    return;
                }

                if ( progress < 10 ) {
                    progress = 10;
                }

                progress = progress / 10;
                progress = progress * 10;

                radio = progress;

                TextView miTextView = (TextView) findViewById(R.id.text_numero_metros);
                miTextView.setText(radio + " m");

                if (mCircle != null) {
                    map.clear();
                    drawMarkerWithCircle(miPos, radio);
                }

                // Guardamos los datos
                new AppSharedPreferences().setZonaSeguraData(miPos, radio);
            }
        });
    }

    @Override
    public void onMapLongClick(LatLng point) {
        AppLog.d(TAG, "Creado nuevo marcador: " + point.toString());
        // Borramos primero los marcadores previos
        map.clear();
        //Guardamos la posición
        miPos = point;
        drawMarkerWithCircle(miPos, radio);
    }

    @Override
    public void onMapReady(GoogleMap map) {

        /** En el caso de haber una zona segura previa habría que centrar la cámara allí */
        String[] coordenadasYradio = new AppSharedPreferences().getZonaSeguraData();

        AppLog.d(TAG, "Leida latitud : " + coordenadasYradio[0] + " longitud " + coordenadasYradio[1] + " radio " + coordenadasYradio[2]);

        /**
         map:cameraTargetLat="37.886"
         map:cameraTargetLng="-4.7486"
         **/

        //TODO que el slider muestre el valor guardado si lo hubiere

        /** Si tenemos datos previos de una posición asignada */
        if (coordenadasYradio[0] != "") {

            Double miLatitud = Double.valueOf(coordenadasYradio[0]);
            Double miLongitud = Double.valueOf(coordenadasYradio[1]);
            LatLng miLatLng = new LatLng(miLatitud, miLongitud);
            Double miRadio = Double.valueOf(coordenadasYradio[2]);

            this.radio = miRadio;
            this.miPos = miLatLng;

            TextView miTextView = (TextView) findViewById(R.id.text_numero_metros);
            miTextView.setText(radio + " m");

            AppLog.d(TAG, coordenadasYradio[1] + "," + coordenadasYradio[1] + "," + coordenadasYradio[2]);

            // Se mueve la cámara a la posición
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(miLatLng, Constants.DEFAULT_MAP_ZOOM));
            // Se pinta el marcador y el círculo
            drawMarkerWithCircle(miLatLng, miRadio);

        } else {

            //Si no hay datos previos, centramos la posición en Córdoba
            LatLng miLatLng = new LatLng( Constants.DEFAULT_LATITUDE, Constants.DEFAULT_LONGITUDE );
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(miLatLng, Constants.DEFAULT_MAP_ZOOM - 5));

        }
    }

    /**
     * Métodos del interfaz draggable
     */
    @Override
    public void onMarkerDragStart(Marker marker) {
        mCircle.remove();
    }

    @Override
    public void onMarkerDrag(Marker marker) {
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        miPos = marker.getPosition();
        drawMarkerWithCircle(miPos, radio);
    }


    private void drawMarkerWithCircle(LatLng point, double radio) {
        double radiusInMeters = radio;
        //int strokeColor = 0xffff0000; //red outline
        //int shadeColor = 0x44ff0000; //opaque red fill
        int strokeColor = 0x3300aa33;
        int shadeColor = 0x3300aa33;


        CircleOptions circleOptions = new CircleOptions().center(point).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(1);
        mCircle = map.addCircle(circleOptions);

        //Añadimos el marcador
        miMarker = map.addMarker(

                new MarkerOptions().
                        position(point).
                        title(point.toString()).
                        draggable(true).
                        icon( BitmapDescriptorFactory.fromResource( R.drawable.zonasegura_home_marker) )

                );

        //Guardamos los valores en el Shared Preferences
        new AppSharedPreferences().setZonaSeguraData(point, radio);

        AppLog.d(TAG, "Guardada AppShared latitud : " + point.latitude + " longitud " + point.longitude + " radio " + radio);

    }

    /**
     * Salida del activity
     *
     * @param view
     */
    public void setZonaSeguraSalirButton(View view) {
        finish();
    }

}