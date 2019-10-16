package iut.desvignes.mymeteo;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {MeteoRoom.class}, version = 1)
public abstract class MeteoDatabase extends RoomDatabase{

    public abstract MeteoDao getMeteoDao();

    private static MeteoDatabase instance;

    public static synchronized MeteoDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    MeteoDatabase.class,
                    "meteo.db")
                    .build();
        }
        return instance;
    }

}
