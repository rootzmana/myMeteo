package iut.desvignes.mymeteo;

import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by androidS4 on 06/03/18.
 */
@Dao
public interface MeteoDao {

    @Query("SELECT * FROM town_table")
    DataSource.Factory<Integer, MeteoRoom> getAllTowns();

    @Query("SELECT * FROM town_table")
    List<MeteoRoom> getAllTownsList();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MeteoRoom... towns);

    @Delete
    void deleteSelectedTown(MeteoRoom town);

    @Update
    void update(ArrayList<MeteoRoom> towns);
}