package com.transmission.ui;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        if (entry == null) {
            return null;
        }
        View view = inflater.inflate(R.layout.custom_dialog, container);
        String title = entry.getTitle();
        String message = entry.getMessage();
        String positive = entry.getPositive();
        String negative = entry.getNegative();
        if (!TextUtils.isEmpty(title)) {
            TextView titleTv = view.findViewById(R.id.custom_dialog_title);
            titleTv.setText(title);
            titleTv.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(message)) {
            TextView messageTv = view.findViewById(R.id.custom_dialog_message);
            messageTv.setText(message);
            messageTv.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(positive)) {
            TextView positiveTv = view.findViewById(R.id.custom_dialog_positive);
            positiveTv.setText(positive);
            positiveTv.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(negative)) {
            TextView negativeTv = view.findViewById(R.id.custom_dialog_negative);
            negativeTv.setText(negative);
            negativeTv.setVisibility(View.VISIBLE);
        }
        return view;
    }

}
