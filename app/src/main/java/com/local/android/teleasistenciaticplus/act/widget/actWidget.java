package com.local.android.teleasistenciaticplus.act.widget;

import com.local.android.teleasistenciaticplus.act.actLoadingScreen;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.local.android.teleasistenciaticplus.R;

/**
 * Implementación de la acción del Widget que levanta la Actividad Principal.
 */
public class actWidget extends AppWidgetProvider
{

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++)
            actualizarWidget(context, appWidgetManager, appWidgetIds[i]);
    }


    @Override
    public void onEnabled(Context context)
    {
        // Enter relevant functionality for when the first layout_widget is created
    }

    @Override
    public void onDisabled(Context context)
    {
        // Enter relevant functionality for when the last layout_widget is disabled
    }

    static void actualizarWidget(Context context, AppWidgetManager widgetManager, int widgetId)
    {
        // Creamos los Intent y PendingIntent para lanzar la actividad principal de la APP
        PendingIntent widgetPendingIntent;
        Intent widgetIntent;
        widgetIntent = new Intent(context,actLoadingScreen.class);
        //widgetIntent = new Intent(context,actMain.class);
        widgetPendingIntent = PendingIntent.getActivity(context, widgetId, widgetIntent,
                PendingIntent.FLAG_UPDATE_CURRENT );
        // Recupero las vistas del layout del widget
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.layout_widget);
        // Asigno un listener para el botón del widget y lanzar el PendingIntent.
        views.setOnClickPendingIntent(R.id.ib_boton_rojo, widgetPendingIntent);
        // Solicito al widget manager que actualice el layout del widget
        widgetManager.updateAppWidget(widgetId, views);
        Log.i("actWidget.onUpdate()","Widget actualizado.");
    }

    /////////////////////////////////
    // IMPLEMENTACIÓN BÁSICA DEL RECEPTOR DE EVENTOS DEL WIDGET. Lo dejo comentado para uso futuro.
    /////////////////////////////////
    /*
    public void onReceive(Context context, Intent receivedIntent)
    {
        //super.onReceive(context, intent);

        // Recupero una instancia de mi widgetManager
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context.getPackageName(),
                    actWidget.class.getName());

        // Vector con los id de los widget activos.
        int[] widgetIds = widgetManager.getAppWidgetIds(thisWidget);

        // Control
        Log.i("actWidget.onReceive()","widgetId: "+widgetIds[0]);
        Log.i("actWidget.onReceive()","Recibido evento con acción: "+receivedIntent.getAction());

        // Estudio el evento que he recibido.
        // if(receivedIntent.getAction().equals())

        // Fuerzo la actualización del Widget
        onUpdate(context, widgetManager, widgetIds);
    }
    */

    /////////////////////////////////
    // IMPLEMENTACION DE LA RESPUESTA AL EVENTO DE ACTUALIZAR EL WIDGET
    /////////////////////////////////
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager widMgr, int widId,
                                          Bundle cambios)
    {
        RemoteViews vistas = new RemoteViews(context.getPackageName(), R.layout.layout_widget);
        int ancho, alto;
        int medida;
        int idImagenLogo;
        // Primero capturo el ancho y alto mínimos del tamaño actual del widget.
        ancho=cambios.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        alto=cambios.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        // Convierto las medidas en celdas ocupadas
        ancho=getCellsForSize(ancho);
        alto=getCellsForSize(alto);
        // Veo cual es mayor
        Log.i("actWidget.widgetChanged","Ancho por alto del widget en celdas: "+ancho+" x "+alto);
        // Me quedo con el valor mas bajo.
        medida=Math.min(ancho,alto);
        // Elijo la imagen para el ImageButton del widget de medidas apropiadas.
        switch (medida)
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
            // case 4:
            default:
                idImagenLogo = R.drawable.logo_transparente_4x4;
                break;
        }
        vistas.setImageViewResource(R.id.ib_boton_rojo, idImagenLogo);
        // Actualizo
        widMgr.updateAppWidget(widId, vistas);
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
