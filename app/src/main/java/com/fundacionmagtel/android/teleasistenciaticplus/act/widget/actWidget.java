package com.fundacionmagtel.android.teleasistenciaticplus.act.widget;

import com.fundacionmagtel.android.teleasistenciaticplus.act.actLoadingScreen;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.fundacionmagtel.android.teleasistenciaticplus.R;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.helper.AppLog;

/**
 * Implementación de la acción del Widget que levanta la Actividad Principal.
 * @author German Moreno
 */
public class actWidget extends AppWidgetProvider
{
    /**
     * Método de framework onReceive. Usado para log.
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        AppLog.i("actWidget.onReceive()", "He recibido el evento: " + intent.getAction());
}

    /**
     * Método de framework onUpdate
     * @param context
     * @param appWidgetManager
     * @param appWidgetIds
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++)
            actualizarWidget(context, appWidgetManager, appWidgetIds[i], null);

        AppLog.i("actWidget.onUpdate()", "Actualizo las vistas del Widget.");

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    /**
     * Método de framework onEnabled
     *
     * @param context
     */
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    /**
     * Método de framework onDisabled
     * @param context
     */
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    /**
     * Método de framwork actualizarWidget
     * @param context
     * @param widgetManager
     * @param widgetId
     */
    static void actualizarWidget(Context context, AppWidgetManager widgetManager, int widgetId,
                                 Bundle infoCambios)
    {
        // Creamos los Intent y PendingIntent para lanzar la actividad principal de la APP
        PendingIntent widgetPendingIntent;
        Intent widgetIntent;
        int idImagen;
        Bundle datosWidget;

        widgetIntent = new Intent(context,actLoadingScreen.class);

        //widgetIntent = new Intent(context,actMain.class);
        widgetPendingIntent = PendingIntent.getActivity(context, widgetId, widgetIntent,
                PendingIntent.FLAG_UPDATE_CURRENT );

        // Recupero las vistas del layout del widget
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.layout_widget);

        // Asigno un listener para el botón del widget y lanzar el PendingIntent.
        views.setOnClickPendingIntent(R.id.ib_boton_rojo, widgetPendingIntent);

        // Asigno el icono adecuado al ImageButton que es la parte visible del Widget.
        if(infoCambios == null) {
            datosWidget = widgetManager.getAppWidgetOptions(widgetId);
        }
        else {
            datosWidget = infoCambios;
        }
        idImagen = imagenQueCabe(datosWidget);
        views.setImageViewResource(R.id.ib_boton_rojo, idImagen);

        // Solicito al widget manager que actualice el layout del widget
        widgetManager.updateAppWidget(widgetId, views);

        AppLog.i("actWidget.actualizarWidget()", "Widget actualizado.");
    }

    /**
     * Método de framework onAppWidgetOptionsChanged. Redimensionado del Widget
     * @param context
     * @param widMgr
     * @param widId
     * @param cambios
     */
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager widMgr, int widId,
                                          Bundle cambios)
    {
        super.onAppWidgetOptionsChanged(context, widMgr, widId, cambios);
        //RemoteViews vistas = new RemoteViews(context.getPackageName(), R.layout.layout_widget);

        // Mando a actualizar el Widget directamente.
        actualizarWidget(context, widMgr, widId, cambios);

        /*
        // Mediante la siguiente llamada establezco el id de la imagen adecuada al tamaño.
        int id=imagenQueCabe(cambios);
        vistas.setImageViewResource(R.id.ib_boton_rojo, id);
        // Actualizo

        widMgr.updateAppWidget(widId, vistas);
        */
    }

    /**
     * Método que calcula el tamaño de la imagen del widget según el nuevo tamaño
     * @param cambios Bundle con la información del nuevo tamaño
     * @return El id de la imagen que corresponde cargar.
     */
    private static int imagenQueCabe(Bundle cambios)
    {
        int ancho, alto;
        int minimo;
        int idImagenLogo;
        // Primero capturo el ancho y alto mínimos del tamaño actual del widget.
        ancho=cambios.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        alto=cambios.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        // Convierto las medidas en celdas ocupadas
        ancho=getCellsForSize(ancho);
        alto=getCellsForSize(alto);
        // Veo cual es mayor
        AppLog.i("actWidget.imagenQueCabe()","Tamaño del widget: "+ancho+" x "+alto);
        // Me quedo con el valor mas bajo.
        minimo=Math.min(ancho,alto);
        // Elijo la imagen para el ImageButton del widget de medidas apropiadas.
        switch (minimo)
        {
            case 1:
                idImagenLogo = R.drawable.logo_transparente_1x1;
                break;
            case 2:
                idImagenLogo = R.drawable.logo_transparente_2x2;
                break;
            case 3:
                idImagenLogo = R.drawable.logo_transparente_3x3;
                break;
            case 4:
            default:
                idImagenLogo = R.drawable.logo_transparente_4x4;
                break;
        }
        return idImagenLogo;
    }


    /**
     * Devuelve el número de celdas ocupadas por el widget.
     *
     * @param size Tamaño del widget en dp.
     * @return Tamaño del widget en numero de celdas.
     */
    private static int getCellsForSize(int size)
    {
        /*
        int n = 2;
        while (70 * n - 30 < size) {
            ++n;
        }
        return n - 1;
        */
        // Formula precisa. Falla en altas resoluciones.
        // return (int)(Math.ceil(size + 30d)/70d);

        // Esta fórmula se comporta bien en todas las resoluciones hasta 4x4 de tamaño
        if( size >= 320 )
            return size/70;
        else
            return (size+15)/70;
    }
}