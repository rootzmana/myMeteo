package iut.desvignes.mymeteo;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;


// Classe qui gère le Widget
public class MeteoWidgetProvider extends AppWidgetProvider {

    private static MeteoRoom favoriteTown ;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];
            updateAppWidget(context, appWidgetManager, appWidgetId, favoriteTown);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, MeteoRoom town) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        views.setTextViewText(R.id.nameWidget, town.getTownName());
        views.setTextViewText(R.id.tempWidget, Double.toString(town.getTemperature()) +"°C");

        int id = context.getResources().getIdentifier("icon_" + town.getIconID(), "drawable", context.getPackageName());
        views.setImageViewResource(R.id.imageWidget, id);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    static void setFavoriteTown(MeteoRoom town){
        favoriteTown = town;
    }


}
