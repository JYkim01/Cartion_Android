package net.belicon.cartion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

public class AppInformationActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mInfoVerText, mInfoPermissionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_information);

        mInfoVerText = findViewById(R.id.app_info_version_text);
        mInfoPermissionText = findViewById(R.id.app_info_permission_text);

        mInfoVerText.setText(String.valueOf(getAndroidVersion()));

        mInfoPermissionText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + getPackageName())));
                startActivity(intent);
            }
        });

        findViewById(R.id.app_info_eula_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.app_info_eula_btn:
                startActivity(new Intent(this, EulaActivity.class));
                break;
        }
    }

    private int getAndroidVersion() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            int version_code = pInfo.versionCode;
            return version_code;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }
}