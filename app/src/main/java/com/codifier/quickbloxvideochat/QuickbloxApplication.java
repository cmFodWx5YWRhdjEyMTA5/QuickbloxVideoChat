package com.codifier.quickbloxvideochat;

import core.CoreApp;
import core.models.QbConfigs;
import webrtc.util.QBResRequestExecutor;

/**
 * Created by Deepank on 16-Jan-18.
 * Codifier Technologies Pvt. Ltd.
 * deepank.dwivedi@gmail.com
 */

public class QuickbloxApplication extends CoreApp {
    public static final String TAG = CoreApp.class.getSimpleName();

    private static QuickbloxApplication instance;
    private static final String QB_CONFIG_DEFAULT_FILE_NAME = "qb_config.json";
    private QbConfigs qbConfigs;
    private QBResRequestExecutor qbResRequestExecutor;


    public static QuickbloxApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initApplication();
    }

    private void initApplication() {
        instance = this;
    }

    public synchronized QBResRequestExecutor getQbResRequestExecutor() {
        return qbResRequestExecutor == null
                ? qbResRequestExecutor = new QBResRequestExecutor()
                : qbResRequestExecutor;
    }

}
