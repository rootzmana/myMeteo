package iut.desvignes.mymeteo;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

/**
 * Created by matth on 25/03/2018.
 */

public class DialogMaps extends DialogFragment {

    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        EditText name = new EditText(getActivity());
        builder.setView(name);
        Bundle args = this.getArguments();
        String nameFilled = args.getString("name");
        builder.setTitle(R.string.add_this_town);
        name.setText(nameFilled);
        builder.setMessage(R.string.dialog_message);
        builder.setPositiveButton(R.string.ok,
                (dialog, which) -> {
                    Listener listener = (Listener) getActivity();
                    listener.onOk(DialogMaps.this, name.getText().toString());
                });
        builder.setNegativeButton(R.string.cancel, null);
        return builder.create();
    }

    public static void show(AppCompatActivity activity, String name) {
        DialogMaps dialog = new DialogMaps();
        Bundle args = new Bundle();
        args.putString("name", name);
        dialog.setArguments(args);
        dialog.show(activity.getSupportFragmentManager(), "Dialog");
    }

    public interface Listener {
        void onOk(DialogMaps dialog, String townName);
    }
}

