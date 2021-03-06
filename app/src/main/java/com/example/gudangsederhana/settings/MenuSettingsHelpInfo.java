package com.example.gudangsederhana.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.gudangsederhana.R;
import com.example.gudangsederhana.SplashScreen;

public class MenuSettingsHelpInfo extends AppCompatActivity {

    RelativeLayout layout;
    TextView tvVersion, tvBack;
    String version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_settings_help_info);

        tvBack = findViewById(R.id.tvKembali);
        SpannableString content = new SpannableString("Content");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tvBack.setText(content);
        tvBack.setOnClickListener(v -> {
            onBackPressed();
        });

        tvVersion = findViewById(R.id.tvVersion_shi);
        try {
            Context context = MenuSettingsHelpInfo.this;
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionName.trim();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            version = "error...";
        }

        tvVersion.setText("Versi " + version);
        layout = (RelativeLayout)findViewById(R.id.rlMs_hi);
        AnimationDrawable animationDrawable = (AnimationDrawable)layout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();
    }
}