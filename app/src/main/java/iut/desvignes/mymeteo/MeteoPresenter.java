package iut.desvignes.mymeteo;

import android.arch.lifecycle.LiveData;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.os.SystemClock;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by androidS4 on 13/03/18.
 */

public class MeteoPresenter implements Serializable{
    private static final long serialVersionUID = 1L;

    private transient MeteoView view;
    private transient Repository repository;
    private transient LiveData<PagedList<MeteoRoom>> townsList;
    private transient MeteoDao meteoDao;

    MeteoRoom lastTownDeleted;
    private long lastClickTimeStamp = 0;
    private long nextClickTimeStamp = 0;

    public void setMeteoDao(MeteoDao meteoDao){
        this.meteoDao = meteoDao;
        this.townsList = new LivePagedListBuilder<>(meteoDao.getAllTowns(), 30).build();
    }

    public void setView(MeteoView view){
        this.view = view;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public LiveData<PagedList<MeteoRoom>> getTownsList(){
        return townsList;
    }


    // Méthode de gestion de la BD

    public void undo() {
        meteoDao.insert(lastTownDeleted);
    }

    public void delete(int pos) {
        List<MeteoRoom> room = meteoDao.getAllTownsList();
        lastTownDeleted = room.get(pos);
        meteoDao.deleteSelectedTown(room.get(pos));
        view.notifyItemDeleted();
    }

    public void onRefreshData(boolean isNetworkOn, boolean pref, ExecutorService service) {
        if(!isNetworkOn)
            view.showMessage(R.string.network_off);
        else if(!pref)
            view.showMessage(R.string.enable_refresh);
        else if(lastClickTimeStamp < nextClickTimeStamp){
            lastClickTimeStamp = SystemClock.elapsedRealtime();
            view.showMessage(R.string.wait_until);
        }else{
            service.submit(()->refreshData());
        }
    }

    private void refreshData() {
        lastClickTimeStamp = SystemClock.elapsedRealtime();
        nextClickTimeStamp = SystemClock.elapsedRealtime() + 300000;
        List<MeteoRoom> oldListRoom = meteoDao.getAllTownsList();
        ArrayList<MeteoRoom> newListRoom = new ArrayList<MeteoRoom>();
        MeteoRoom townRoom;
        MeteoModel townRetrofit;
        for(int i = 0; i < oldListRoom.size(); i++){
            townRetrofit = repository.getTownById(oldListRoom.get(i).getId());
            townRoom = MeteoModel.createMeteoRoom(townRetrofit);
            newListRoom.add(townRoom);
        }
        meteoDao.update(newListRoom);
    }

    public int getImageID(MeteoRoom town){
        return view.getIconId(town.getIconID());
    }

    // Méthode invoqué par le dialogue
    public void addTown(String name){
        MeteoModel townRetrofit = repository.getTownByName(name);
        MeteoRoom town = MeteoModel.createMeteoRoom(townRetrofit);
        meteoDao.insert(town);
    }

    public void launchMap(MeteoRoom town) {
        List<MeteoRoom> list = meteoDao.getAllTownsList();
        String[] arrayName = new String[list.size()];
        String[] arrayIcon = new String[list.size()];
        double[] arrayLat = new double[list.size()];
        double[] arrayLng = new double[list.size()];
        for(int i = 0; i < list.size(); i++){
            arrayName[i] = list.get(i).getTownName();
            arrayIcon[i] = list.get(i).getIconID();
            arrayLat[i] = list.get(i).getLat();
            arrayLng[i] = list.get(i).getLng();
        }
        view.launchMap(town, arrayName, arrayIcon, arrayLat, arrayLng);
    }

    public void getPrefTown(MeteoRoom town){
        view.setPrefTown(town);
        view.showMessage(R.string.add_favorite);
    }

}
