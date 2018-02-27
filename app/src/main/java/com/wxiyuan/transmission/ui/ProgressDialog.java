package com.wxiyuan.transmission.ui;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wxiyuan.transmission.R;
import com.wxiyuan.transmission.entry.DialogEntry;

public class ProgressDialog extends DialogFragment {

    private static final String KEY_ENTRY_ARGS = "entry_args";

    public static ProgressDialog newInstance(@NonNull DialogEntry entry) {
        Bundle args = new Bundle();
        args.putParcelable(KEY_ENTRY_ARGS, entry);
        ProgressDialog instance = new ProgressDialog();
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_Progress);
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
        View view = inflater.inflate(R.layout.progress_dialog, container);
        String message = entry.getMessage();
        TextView msgTv = view.findViewById(R.id.progress_dlg_message);
        if (TextUtils.isEmpty(message)) {
            msgTv.setVisibility(View.GONE);
        } else {
            msgTv.setText(message);
        }
        return view;
    }

}
