package iut.desvignes.mymeteo;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by matth on 25/03/2018.
 */

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
    }
}
