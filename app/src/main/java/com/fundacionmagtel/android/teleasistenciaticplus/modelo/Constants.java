package com.fundacionmagtel.android.teleasistenciaticplus.modelo;

/**
 * Created by FESEJU on 11/02/2015.
 * Interfaz de constantes generales de la aplicación
 * @author Juan Jose Ferres
 */

public interface Constants {

    ////////////////////////////////////////////////
    // VALORES DE DESARROLLO DE LA APLICACIÓN
    ////////////////////////////////////////////////

    public static final DebugLevel DEBUG_LEVEL = DebugLevel.PRODUCCION;
    public static final boolean FAKE_SMS = false;
    public static final int LIMITE_CARACTERS_SMS = 159;

    public static final Boolean LOG_TO_FILE = true;
    public static final String DEBUG_LOG_FILE = "teleasistencia.log.txt";

    ////////////////////////////////////////////////
    // NUMERO MAXIMO DE MENSAJES EN MODO PILOTAJE
    ////////////////////////////////////////////////
    public static final int LIMITE_SMS_POR_DEFECTO = 20;
    public static final String SMS_ENVIADOS_SHARED_PREFERENCES = "numerosmsenviados";

    ////////////////////////////////////////////////
    // MODULO ESTADISTICO
    ////////////////////////////////////////////////

    public static final Boolean STATS_TO_FILE = true;
    public static final String STATS_LOG_DIR = "ttpvstats"; //directorio en el cual se crearan las estadisticas
    public static final String STAT_FILE_SHARED_PREFERENCES = "ficherostatssesion";
    public static final String DEBUG_ZONA_SEGURA_LOG_FILE = "teleasistenciagps.log.txt";
    public static final Boolean STATS_LOG_ZONA_SEGURA_GPS = true;
    public static final Boolean STATS_LOG_BATERIA = true;

    ////////////////////////////////////////////////
    // MISCELANEA
    ////////////////////////////////////////////////

    public static final long LOADING_SCREEN_DELAY = 3000; //Con 1000 a veces da problemas, no le ha dado tiempo a terminar de ejecutar la lectura del archivo
    public static final long SMS_SENDING_DELAY = 5000; //Frecuencia de envío de mensajes
    public static final long MEMORY_DIVIDER = 1048576L; //BytestoMegabytes
    public static final boolean SHOW_ANIMATION = true; //Decide si se realizan transiciones entre actividades o fragmentos
    public static final boolean PLAY_SOUNDS = true; //Decide si se reproducen sonidos en la aplicación
    public static final boolean PLAY_BIENVENIDO_SOUND = true; //Decide si se reproducen el infernal sonido de bienvenida

        /*
        1024 bytes      == 1 kilobyte
        1024 kilobytes  == 1 megabyte

        1024 * 1024     == 1048576*/


    ////////////////////////////////////////////////
    // OPERACIONES HTTP
    ////////////////////////////////////////////////

    public static final int HTTP_OPERATION_DELAY = 3000;

    ////////////////////////////////////////////////
    // FICHERO DE SHAREDPREFERENCES
    ////////////////////////////////////////////////

    public static final String APP_SHARED_PREFERENCES_FILE = "teleasistencia.prefs";

    ////////////////////////////////////////////////
    // VALORES DE SHAREDPREFERENCES
    ////////////////////////////////////////////////

    public static final String NOMBRE_APP_SHARED_PREFERENCES_NO_MOSTRAR_AVISO_TARIFICACION = "avisotarificacion";

    String DETECTOR_CAIDAS_ARRANCAR_AL_INICIO = "caidas";
    String DETECTOR_CAIDAS_ACTIVAR = "activo";
    String DETECTOR_CAIDAS_DESACTIVAR = "inactivo";
    String DETECTOR_CAIDAS_SERVICIO_INICIADO="servicioiniciado";
    String DETECTOR_CAIDAS_ARCHIVO_RED = "pesosprueba";

    public static final String NOMBRE_APP_SHARED_PREFERENCES_DATETIME_ULTIMO_SMS_ENVIADO = "ultimosmsenviado";

    public static final String ZONA_SEGURA_ARRANCAR_AL_INICIO = "zonasegura";
    public static final String ZONA_SEGURA_SERVICIO_INICIADO = "zonaseguraservicioiniciado";
    public static final String ZONA_SEGURA_LATITUD = "zonaseguralatitud";
    public static final String ZONA_SEGURA_LONGITUD = "zonaseguralongitud";
    public static final String ZONA_SEGURA_RADIO = "zonaseguraradio";

    public static final String GPS_LATITUD = "gpslatitud";
    public static final String GPS_LONGITUD = "gpslongitud";
    public static final String GPS_PRECISION = "gpsprecision";
    public static final String GPS_ULTIMA_ACTUALIZACION = "gpsultimaactualizacion";
    public static final String GPS_ULTIMA_ACTUALIZACION_FORMATO_NUMERICO = "gpsultimaactualizacionformatonumerico";

    ///////////////////////////////////////////////////////////////////////////
    // MONITOR BATERIA
    ///////////////////////////////////////////////////////////////////////////
    public static final String MONITOR_BATERIA_ARRANCAR_AL_INICIO = "ActivarAlInicio" ;
    public static final String MONITOR_BATERIA_NIVEL_ALERTA = "NivelAlerta";
    public static final String MONITOR_BATERIA_TASA_REFRESCO = "Intervalo";
    public static final String MONITOR_BATERIA_ULTIMO_NIVEL_REGISTRADO = "UltimoNivel";

    ////////////////////////////////////////////////////////
    // MANOS LIBRES (CONSTANTS)
    ////////////////////////////////////////////////////////
    public static final String MANOS_LIBRES_ACTIVAR_AL_INICIO = "ManosLibresActivarAlInicio";

    ////////////////////////////////////////////////
    // ZONA SEGURA
    ////////////////////////////////////////////////

    public static final boolean TOAST_DATOS_ZONA_SEGURA = false; //Mostrar o no los datos de la zona segura

    public static final int DEFAULT_ZONA_SEGURA_POOL = 10; //El tamaño de pool del FIFO

    public static final double DEFAULT_LATITUDE = 37.886;
    public static final double DEFAULT_LONGITUDE = -4.7486;

    public static final int MAX_ZONA_SEGURA_RADIO = 5000;
    public static final float DEFAULT_MAP_ZOOM = 15;

    public static final long GPS_READ_INTERVAL = 1000 * 120; //10*1000 para debug
    public static final long GPS_READ_FASTEST_INTERVAL = 1000 * 120; //5*1000 para debug

    ////////////////////////////////////////////////
    // LIMITE DE CARACTERES DE CADENAS
    ////////////////////////////////////////////////
    public static final int MAX_NAME_SIZE = 20;
    public static final int MAX_APELLIDOS_SIZE = 20;

    ////////////////////////////////////////////////
    // TIEMPO MAXIMO PARA CONSIDERAR POSICION DE GPS CORRECTA
    ////////////////////////////////////////////////
    public static final int MAX_GPS_TIME = 300;
}