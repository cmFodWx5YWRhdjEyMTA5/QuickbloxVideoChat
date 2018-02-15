package core.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;

import com.quickblox.auth.session.QBSessionManager;

import core.CoreApp;
import core.utils.Toaster;

public abstract class CoreSplashActivity extends CoreBaseActivity {
    private static final int SPLASH_DELAY = 1500;

    private static Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }


    protected abstract void proceedToTheNextActivity();

    protected boolean sampleConfigIsCorrect(){
        return CoreApp.getInstance().getQbConfigs() != null;
    }

    protected void proceedToTheNextActivityWithDelay() {
        mainThreadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                proceedToTheNextActivity();
            }
        }, SPLASH_DELAY);
    }

    protected boolean checkConfigsWithSnackebarError(){
        if (!sampleConfigIsCorrect()){
//            showSnackbarErrorParsingConfigs();
            Toaster.longToast("Config's not correct");
            return false;
        }

        return true;
    }


    protected boolean checkSignIn() {
        return QBSessionManager.getInstance().getSessionParameters() != null;
    }
}
