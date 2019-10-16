package iut.desvignes.mymeteo;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements MeteoView, Dialog.Listener{

    //Initialisation des vues de l'activité
    private Toolbar appBar;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private CoordinatorLayout meteoCoordinator;
    private ExecutorService pool;
    private MeteoPresenter presenter;
    private MeteoAdapter adapter;

    // Check le réseau, active le bouton refresh si connecté, désactive sinon
    private boolean isNetworkOn;
    private boolean getPrefRefresh;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    ConnectivityManager cm  = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                    if (networkInfo != null)
                        isNetworkOn = true;
                    else
                        isNetworkOn = false;
                }
            };

    // attributs pour la gestion du Widget
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private MeteoRoom townPref = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pool = Executors.newSingleThreadExecutor();

        //Affectation des vues de l'activité
        appBar = findViewById(R.id.appbar);
        setSupportActionBar(appBar);
        meteoCoordinator = findViewById(R.id.meteoCoordinator);
        floatingActionButton = findViewById(R.id.fab);

        // on click du bouton flottant
        floatingActionButton.setOnClickListener(view -> {
            Dialog.show(this);
        });

        //Lien Vue-Presenter + gestion du cycle de vie
        if (getLastCustomNonConfigurationInstance() != null)
            presenter = (MeteoPresenter) getLastCustomNonConfigurationInstance();
        else if (savedInstanceState != null)
            presenter = (MeteoPresenter) savedInstanceState.getSerializable("presenter");
        else
            presenter = new MeteoPresenter();
        presenter.setView(this);

        //Lien avec la BD
        MeteoDatabase db = MeteoDatabase.getInstance(this);
        presenter.setMeteoDao(db.getMeteoDao());

        // gestion du recycler View
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // gestion adapter + repository
        adapter = new MeteoAdapter(pool, presenter);
        presenter.getTownsList().observe(this, towns -> {
                                                                adapter.submitList(towns);
                                                            });
        recyclerView.setAdapter(adapter);
        presenter.setRepository(new Repository());

        /////////////////////// WIDGET //////////////////////////
        townPref = new MeteoRoom();
        townPref.setTownName("Pas de ville favori");
        townPref.setTemperature(42);
        townPref.setIconID("01d");

        setResult(RESULT_CANCELED);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        configureWidget(getApplicationContext());
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);

        // Gestion du balayage
        ItemTouchHelper.SimpleCallback callback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        pool.submit(()->presenter.delete(viewHolder.getAdapterPosition()));
                    }
                };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }


    // Méthodes pour le widget
    @Override
    public void setPrefTown(MeteoRoom town){
        this.townPref = town;
        configureWidget(getApplicationContext());
    }

    public void configureWidget(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        MeteoWidgetProvider.setFavoriteTown(townPref);
        MeteoWidgetProvider.updateAppWidget(context, appWidgetManager, mAppWidgetId, townPref);
    }


    //------ Méthode pour le menu appBar -------//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_layout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_refresh :
                presenter.onRefreshData(isNetworkOn, getPrefRefresh,  pool);
                return true;
            case R.id.action_settings :
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //------ Méthodes de l'interface Vue -------//
    @Override
    public void showMessage(int messageId)
    {
        if(!isUiThread()){
            runOnUiThread(() -> showMessage(messageId));
            return;
        }

        Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getIconId(String icon) {
        int id = getResources().getIdentifier("icon_" + icon, "drawable", this.getPackageName());
        return id;
    }

    @Override
    public void launchMap(MeteoRoom town, String[] arrayName, String[] arrayIcon, double[] arrayLat, double[] arrayLng) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("currentTown", town);
        intent.putExtra("arrayName", arrayName);
        intent.putExtra("arrayIcon", arrayIcon);
        intent.putExtra("arrayLat", arrayLat);
        intent.putExtra("arrayLng", arrayLng);
        startActivity(intent);
    }


    // --------- Méthode Adapter ----------------- //

    @Override
    public void notifyItemDeleted() {

        Snackbar.make(meteoCoordinator, R.string.item_deleted, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pool.submit(() -> presenter.undo());
                    }
                }).show();
    }


    //---------- Cycle de vie --------//
    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return presenter;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (! isChangingConfigurations())
            outState.putSerializable("presenter", presenter);
    }

    @Override
    protected void onDestroy() {
        pool.shutdown();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(receiver, filter);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        getPrefRefresh = prefs.getBoolean("enable_refresh", true);
        if(getIntent() != null && getIntent().getExtras() != null){
            String name = getIntent().getExtras().getString("cityNameAdded");
            pool.submit(() -> presenter.addTown(name));
        }
    }

    @Override
    protected void onPause() {
        this.unregisterReceiver(receiver);
        super.onPause();
    }

    // gestion threads
    private boolean isUiThread(){
        return Looper.myLooper() == Looper.getMainLooper();
    }


    // Méthode de l'interface Listener du Dialog
    @Override
    public void onOk(Dialog dialog, String name) {
        pool.submit(() -> presenter.addTown(name));
    }
}
