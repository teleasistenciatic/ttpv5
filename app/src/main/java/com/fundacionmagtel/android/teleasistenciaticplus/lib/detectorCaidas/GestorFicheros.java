package com.fundacionmagtel.android.teleasistenciaticplus.lib.detectorCaidas;

import android.content.res.Resources;

import com.fundacionmagtel.android.teleasistenciaticplus.lib.helper.AppLog;
import com.fundacionmagtel.android.teleasistenciaticplus.modelo.GlobalData;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;

/**
 * Clase encargada de leer el archivo de datos con los pesos de la red neuronal,
 * los datos de bias de la red neuronal y la media y desviación de las muestras.
 *
 * Created by SAMUAN on 12/05/2015.
 */
class GestorFicheros {
    private LinkedList lista;
    private Resources resources;

    public GestorFicheros(Resources resources) {
        lista=new LinkedList();
        this.resources=resources;
    }

    /**
     * Este método lee el archivo que se le indique y captura los datos contenidos en él.
     * Los datos los almacena en una lista de listas. Las sublistas almacenan vectores de double.
     *
     * @param archivoPesos El archivo que se va a leer.
     */
    public void leerArchivoPesos(String archivoPesos){
        String linea;
        LinkedList listavectores=new LinkedList();

        double[] valoresD;
        try{

            int idenArchivoRed=resources.getIdentifier(archivoPesos,"raw", GlobalData.getAppContext().getPackageName());
            AppLog.i("MONITOR", "monitor idenArchivoRed " + idenArchivoRed);
            InputStream flujo= resources.openRawResource(idenArchivoRed);
            BufferedReader br= new BufferedReader(new InputStreamReader(flujo));

            while((linea=br.readLine())!=null){
                if(linea.length()>0 && !linea.startsWith("#")){
                    if(linea.contains("DATA")){
                        //crear la lista y apuntar para guardar datos en ella.
                        listavectores=new LinkedList();
                        lista.add(listavectores);
                    }else{ //tiene que ser un vector de datos.
                        String[] valores = linea.split(",");
                        if(valores.length>1){
                            valoresD=new double[valores.length];
                            for(int i=0;i<valores.length;i++){
                                valoresD[i]=Double.parseDouble(valores[i]);
                            }
                            listavectores.add(valoresD);
                        }
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * El método devuelve la sublista que se le indique.
     *
     * @param i indice de la sublista.
     * @return la sublista indicada.
     */
    public LinkedList dameLista(int i){
        return (LinkedList)lista.get(i);
    }













}
