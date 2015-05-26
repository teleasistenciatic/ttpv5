package com.local.android.teleasistenciaticplus.act.zonasegura;

/**
 * Created by FESEJU on 18/05/2015.
 */
public class PosicionTiempo {

    Double latitude;
    Double longitude;
    float accuracy;
    String provider;
    String time;
    Boolean inZone;

    public PosicionTiempo(Double latitude,
                          Double longitude,
                          float accuracy,
                          String provider,
                          String time,
                          Boolean inZone) {

        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.provider = provider;
        this.time = time;
        this.inZone = inZone;

    }

    @Override
    public String toString() {
        return "PosicionTiempo{"
                + "latitude=" + latitude + ", "
                + "longitude=" + longitude + ", accuracy=" + accuracy + ", "
                + "provider=" + provider + ", time=" + time + ", "
                + "inZone=" + inZone + '}';
    }

}
