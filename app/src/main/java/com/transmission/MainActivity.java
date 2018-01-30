package com.transmission;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private Button mCreateQrBtn;
    private Button mScanQrBtn;
    private View mMainBtnPart;
    private View mMainQrPart;
    private View mCloseQrBtn;
    private ImageView mQrImage;
    private ProgressBar mQrProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMainBtnPart = findViewById(R.id.main_btn_part);
        mMainQrPart = findViewById(R.id.main_qr_part);
        mCreateQrBtn = findViewById(R.id.btn_create_qr);
        mCreateQrBtn.setOnClickListener(this);
        mScanQrBtn = findViewById(R.id.btn_scan_qr);
        mScanQrBtn.setOnClickListener(this);
        mQrImage = findViewById(R.id.main_qr_image);
        mCloseQrBtn = findViewById(R.id.close_qr_btn);
        mCloseQrBtn.setOnClickListener(this);
        mQrProgressBar = findViewById(R.id.qr_load_progress);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_create_qr:
                mMainBtnPart.setVisibility(View.GONE);
                mMainQrPart.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_scan_qr:
                break;
            case R.id.close_qr_btn:
                mMainQrPart.setVisibility(View.GONE);
                mMainBtnPart.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }
}
