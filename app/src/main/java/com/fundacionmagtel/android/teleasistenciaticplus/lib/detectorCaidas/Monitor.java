package com.fundacionmagtel.android.teleasistenciaticplus.lib.detectorCaidas;

import android.content.res.Resources;
import android.hardware.SensorEvent;

import com.fundacionmagtel.android.teleasistenciaticplus.lib.helper.AppLog;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.sms.SmsLauncher;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.sound.PlaySound;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.stats.StatsFileLogTextGenerator;
import com.fundacionmagtel.android.teleasistenciaticplus.modelo.Constants;
import com.fundacionmagtel.android.teleasistenciaticplus.modelo.DebugLevel;
import com.fundacionmagtel.android.teleasistenciaticplus.modelo.TipoAviso;
import com.fundacionmagtel.android.teleasistenciaticplus.R;

import java.util.LinkedList;

/**
 * Monitoriza los datos del acelerómetro. Cuando se cumplen ciertas condiciones se pasan los datos
 * al extractor de caracteristicas, para después, realizar el procesamiento de la red neuronal y
 * clasificar esos datos.
 *
 * Created by SAMUAN on 13/04/2015.
 */
class Monitor implements Constants {

    private float gravedad=9.8066f;

    private LinkedList<Muestra> cola; //lista de muestras del acelerómetro
    private int tamaLista=500; //tamaño maximo de la lista.

    private double umbralGravedad=2.4; //límite a partir del cual se tienen en cuentan las medidas.

    private long pt=0; //peak time
    private long contadorTiempo=0;
    private String estado="muestreo";
    private Muestra[] datos=null;

    private static String TAG="RedNeuronal";

    private long tiempoInicio;
    private long tiempoActual;
    private long tiempoPasado;

    private Red red;
    private Normalizador normalizador;

    /**
     * Prepara el monitor para la captura de datos.
     * Inicia la red neuronal y el normalizador.
     * @param resources referencia para capturar el archivo con los datos de la red neuronal y datos de normalización.
     */
    public Monitor(Resources resources) {
        cola = new LinkedList<>();
        AppLog.i(TAG, "monitor inicio");
        tiempoInicio=System.currentTimeMillis();
        tiempoPasado=System.currentTimeMillis();

        //************ Captura datos de fichero.

        GestorFicheros gestorFicheros=new GestorFicheros(resources);
        gestorFicheros.leerArchivoPesos(DETECTOR_CAIDAS_ARCHIVO_RED);
        LinkedList listaDatos1=gestorFicheros.dameLista(0);
        LinkedList listaDatos2=gestorFicheros.dameLista(1);
        LinkedList listaDatos3=gestorFicheros.dameLista(2);
        LinkedList listaDatos4=gestorFicheros.dameLista(3);
        LinkedList listaDatos5=gestorFicheros.dameLista(4);
        LinkedList listaDatos6=gestorFicheros.dameLista(5);
        LinkedList listaDatos7=gestorFicheros.dameLista(6);
        LinkedList listaDatos8=gestorFicheros.dameLista(7);

        //******************  inicio red

        double[][] sinapsisA = dameSinapsis(listaDatos1);
        double[][] sinapsisB = dameSinapsis(listaDatos2);
        double[][] sinapsisC = dameSinapsis(listaDatos3);
        double[] biasA = dameBias(listaDatos4);
        double[] biasB = dameBias(listaDatos5);
        double[] biasC = dameBias(listaDatos6);

        red=new Red();
        red.agregarCapa(sinapsisA , biasA, new FuncionSigmoidal() );
        red.agregarCapa(sinapsisB , biasB, new FuncionSigmoidal() );
        red.agregarCapa(sinapsisC , biasC, new FuncionSigmoidal() );

        //*************** inicio normalizador

        double[] medias = dameBias(listaDatos7);
        double[] desvis = dameBias(listaDatos8);

        normalizador=new Normalizador();
        Normalizador.setMedia(medias);
        Normalizador.setDesviacion(desvis);

    }

    /**
     * Gestiona los eventos del acelerometro. Si se cumplen las condiciones extrae caracteristicas
     *
     * @param event Evento del sensor acelerómetro.
     */
    public void gestionar(SensorEvent event) {

        float values[] = event.values;
        float x = values[0];
        float y = values[1];
        float z = values[2];
        float xg=x/gravedad; //Divido por gravedad para pasar unidades de m/s^2 a g
        float yg=y/gravedad;
        float zg=z/gravedad;

        long tiempo=event.timestamp;
        double modulo=calcularModulo(xg,yg,zg);

        cargarMuestra(new Muestra(tiempo,modulo));
        if(estado.equals("muestreo")){ //se ha detectado un pico de gravedad
            if(modulo>umbralGravedad){
                iniciarPostpeak(modulo,tiempo);
            }
        }

        if(estado.equals("postpeak")){ //Ahora esperamos un tiempo de 2.5 segundos sin picos superiores al umbral.
            contadorTiempo=tiempo-pt;
            if(modulo>umbralGravedad) iniciarPostpeak(modulo,tiempo); //si se detecta un nuevo pico, comenzamos a contar el tiempo de nuevo.
            if(contadorTiempo>2500000000l){
                //generar array de valores.
                datos=new Muestra[cola.size()];
                cola.toArray(datos); //extraigo datos a analizar.
                iniciarActivityTest();
                AppLog.i("Acelerometro","iniciar activity test "+tiempo);
            }
        }

    }

    /**
     * Realizo el test de actividad. Si la actividad es baja se extraen caracteristicas y
     * se pasa a red neuronal.
     *
     * La respuesta de la red neuronal generará sms en caso de caida.
     */
    private void iniciarActivityTest(){
        //capturar datos de lista
        estado="activitytest";
        AppLog.i("Acelerometro","iniciar activity test");

        //calcular AAMV , media de las diferencias.
        long tiempoInicioCalculo=pt+1000000000; //se toma desde 1 sg a 2.5 sg despues del impacto
        int marcador=0;
        double difTotal=0;
        long tiempoFinalCalculo = pt + 2500000000l;
        int marcadorFin=datos.length-1;


        for(int i=0;i<datos.length;i++){
            //buscar el dato con tiempo > tiempoIniciocalculo
            if( datos[i].getTiempo()>tiempoInicioCalculo ){
                marcador=i;
                break;
            }
        }
        for(int i=marcador;i<datos.length;i++){
            if(datos[i].getTiempo()>tiempoFinalCalculo){
                marcadorFin=i;
                break;
            }
        }
        for(int j=marcador;j<marcadorFin;j++){
            double dif=Math.abs( datos[j].getAceleracion() - datos[j+1].getAceleracion() );
            difTotal=difTotal+dif;
        }
        difTotal=difTotal/(marcadorFin-marcador);
        AppLog.i(TAG,"Filtro AAMV: "+difTotal);

        //si valor supera 0.05g entonces se descarta como caida
        //si es menor o igual se considera caida y se envian datos a clasificador
        if(difTotal>0.05){

        }else {

            AppLog.i(TAG,"tiempo de pico "+pt);
            Extractor extractor = new Extractor(pt, datos);
            double[] resul=extractor.getCaracteristicas();

            if(resul!=null){
                //monitor devuelve los 8 valores
                //ahora hay que normalizar.
                resul=normalizador.normaliza(resul);
                red.setVectorEn(resul);
                red.calcular();
                double[] laSalida=red.getVectorEn(); //respuesta de la red neuronal

                //de que tipo es?
                double mayor=0;
                int marca=-1;
                for(int k=0;k<laSalida.length;k++){
                    if( laSalida[k]>mayor ){
                        mayor=laSalida[k];
                        marca=k;
                    }
                }
                marca=marca+1;

                switch (marca){
                    case 1:
                        AppLog.i(TAG,"Monitor | Sentado");
                        if(Constants.DEBUG_LEVEL == DebugLevel.DEBUG) PlaySound.play(R.raw.sentado);

                        /////////////////////////////////////////////////////
                        StatsFileLogTextGenerator.write("aviso", "sentado");
                        /////////////////////////////////////////////////////

                        break;
                    case 2:
                        AppLog.i(TAG,"Monitor | Correr");
                        if(Constants.DEBUG_LEVEL == DebugLevel.DEBUG) PlaySound.play(R.raw.correr);

                        /////////////////////////////////////////////////////
                        StatsFileLogTextGenerator.write("aviso", "correr");
                        /////////////////////////////////////////////////////

                        break;
                    case 3:
                        AppLog.i(TAG,"Monitor | Golpe");
                        if(Constants.DEBUG_LEVEL == DebugLevel.DEBUG) PlaySound.play(R.raw.golpe);

                        /////////////////////////////////////////////////////
                        StatsFileLogTextGenerator.write("aviso", "golpe");
                        /////////////////////////////////////////////////////

                        break;
                    case 4:
                        AppLog.i(TAG,"Monitor | Caida");
                        if(Constants.DEBUG_LEVEL == DebugLevel.DEBUG) PlaySound.play(R.raw.caida);

                        //código para el envio de sms.
                        SmsLauncher miSmsLauncher = new SmsLauncher(TipoAviso.CAIDADETECTADA);
                        Boolean hayListaContactos = miSmsLauncher.generateAndSend();

                        break;
                }
            }
        }
        estado="muestreo";
    }

    /**
     * Cambia el estado a "postpeak".
     *
     * @param modulo valor de la aceleración
     * @param tiempo tiempo de la muestra
     */
    private void iniciarPostpeak(double modulo,long tiempo){
        contadorTiempo=0;
        pt=tiempo;
        estado="postpeak";
        AppLog.i(TAG,"Post peak | Modulo: "+modulo+" Tiempo: "+tiempo);
    }

    /**
     * Añade un objeto muestra a la cola.
     * Si la cola se llena elimina por la cabeza.
     *
     * @param muestra Una muestra de datos con aceleración y tiempo de captura.
     */
    private void cargarMuestra(Muestra muestra){
        cola.add(muestra);
        if(cola.size()>tamaLista) cola.poll();
    }

    /**
     * Calcula el módulo del vector aceleración dado por el acelerómetro
     * @param x valor de aceleración en eje x.
     * @param y valor de aceleración en eje y.
     * @param z valor de aceleración en eje z.
     * @return Devuelve el valor del módulo de la aceleración.
     */
    private double calcularModulo(double x, double y, double z){
        return Math.sqrt(    Math.pow(x,2) + Math.pow(y,2)+ Math.pow(z,2)   );
    }

    /**
     * Convierte una lista con los datos en una matriz de sinapsis
     * @param lista la lista que contiene los datos double
     * @return vector
     */
    private static double[][] dameSinapsis(LinkedList lista){
        double[][] sinapsis;
        int longi=((double[])lista.get(0)).length;
        sinapsis=new double[lista.size()][longi];
        for(int i=0;i<lista.size();i++){
            sinapsis[i]=(double[])lista.get(i);
        }
        return sinapsis;
    }

    /**
     * Convierte una lista de datos en un vector con los datos de bias.
     * @param lista la lista que contiene los datos double
     * @return vector
     */
    private static double[] dameBias(LinkedList lista){
        double[] bias;
        bias=(double[])lista.get(0);
        return bias;
    }

}
