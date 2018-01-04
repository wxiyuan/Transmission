package com.transmission.ui;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.transmission.R;
import com.transmission.entry.DialogEntry;

public class CustomAlertDialog extends DialogFragment {

    private static final String KEY_ENTRY_ARGS = "entry_args";

    public static CustomAlertDialog newInstance(@NonNull DialogEntry entry) {
        Bundle args = new Bundle();
        args.putParcelable(KEY_ENTRY_ARGS, entry);
        CustomAlertDialog fragment = new CustomAlertDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args == null) {
            return null;
        }
        if (args.getParcelable(KEY_ENTRY_ARGS) == null
                || !(args.getParcelable(KEY_ENTRY_ARGS) instanceof DialogEntry)) {
            return null;
        }
        DialogEntry entry = args.getParcelable(KEY_ENTRY_ARGS);
        return inflater.inflate(R.layout.custom_dialog, container);
    }

}
