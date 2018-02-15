package webrtc.activities;

import android.os.Bundle;

import com.codifier.quickbloxvideochat.QuickbloxUserLogin;
import com.quickblox.users.model.QBUser;

import core.ui.activity.CoreSplashActivity;
import core.utils.SharedPrefsHelper;
import webrtc.services.CallService;

public class SplashActivity extends CoreSplashActivity {

    private SharedPrefsHelper sharedPrefsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPrefsHelper = SharedPrefsHelper.getInstance();

        if (sharedPrefsHelper.hasQbUser()) {
            startLoginService(sharedPrefsHelper.getQbUser());
            startOpponentsActivity();
            return;
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
        OpponentsActivity.start(SplashActivity.this, false);
        finish();
    }
}