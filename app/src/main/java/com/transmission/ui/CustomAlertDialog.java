package com.transmission.ui;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_No_Border);
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
            TextView positiveBtn = view.findViewById(R.id.custom_dialog_positive);
            positiveBtn.setText(positive);
            positiveBtn.setVisibility(View.VISIBLE);
            positiveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CustomAlertDialog.this.dismiss();
                }
            });
        }
        if (!TextUtils.isEmpty(negative)) {
            TextView negativeBtn = view.findViewById(R.id.custom_dialog_negative);
            negativeBtn.setText(negative);
            negativeBtn.setVisibility(View.VISIBLE);
            negativeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CustomAlertDialog.this.dismiss();
                }
            });
        }
        if (TextUtils.isEmpty(positive) || TextUtils.isEmpty(negative)) {
            View splitV = view.findViewById(R.id.custom_dialog_split_v);
            splitV.setVisibility(View.GONE);
        }
        return view;
    }

}
