package com.codifier.quickbloxvideochat;

import android.os.Bundle;
import android.util.Log;

import com.quickblox.users.model.QBUser;

import core.ui.activity.CoreSplashActivity;
import core.utils.SharedPrefsHelper;
import webrtc.activities.OpponentsActivity;
import webrtc.services.CallService;

public class DashboardActivity extends CoreSplashActivity {
    SharedPrefsHelper sharedPrefsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPrefsHelper = SharedPrefsHelper.getInstance();

        if (sharedPrefsHelper.hasQbUser()) {
            startLoginService(sharedPrefsHelper.getQbUser());
            startOpponentsActivity();
            Log.e("has user","Already signed in");
            return;
        }
        else {
            Log.e("has user","Not signed in");

        }

        if (checkConfigsWithSnackebarError()) {
            proceedToTheNextActivityWithDelay();
        }
    }

    @Override
    protected void proceedToTheNextActivity() {
        QuickbloxUserLogin.start(this);
        finish();
    }

    protected void startLoginService(QBUser qbUser) {
        CallService.start(this, qbUser);
    }

    private void startOpponentsActivity() {
        OpponentsActivity.start(DashboardActivity.this, false);
        finish();
    }

}