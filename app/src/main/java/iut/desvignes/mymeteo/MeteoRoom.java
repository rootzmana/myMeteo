package iut.desvignes.mymeteo;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;


@Entity(tableName = "town_table")
public class MeteoRoom implements Serializable {
    private static final long serialVersionUID = 1L;

    @PrimaryKey()
    private int id;

    private String townName;
    private double temperature;
    private String iconID;
    private double lng;
    private double lat;

    public double getLng() { return lng; }
    public void setLng(double lng) { this.lng = lng; }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public String getTownName() {
        return townName;
    }
    public void setTownName(String townName) {
        this.townName = townName;
    }

    public double getTemperature() {
        return temperature;
    }
    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getIconID() {
        return iconID;
    }
    public void setIconID(String weather) {
        this.iconID = weather;
    }

    public int getId(){ return this.id; }
    public void setId(int id) { this.id = id; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MeteoRoom that = (MeteoRoom) o;

        return id == that.id;
    }


}

