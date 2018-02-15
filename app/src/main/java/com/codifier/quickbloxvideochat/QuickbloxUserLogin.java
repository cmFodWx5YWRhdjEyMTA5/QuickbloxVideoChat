package com.codifier.quickbloxvideochat;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.core.helper.Utils;
import com.quickblox.users.model.QBUser;

import core.utils.SharedPrefsHelper;
import core.utils.Toaster;
import webrtc.activities.BaseActivity;
import webrtc.activities.OpponentsActivity;
import webrtc.services.CallService;
import webrtc.utils.Consts;
import webrtc.utils.UsersUtils;

/**
 * Created by Deepank on 11-Jan-18.
 * Codifier Technologies Pvt. Ltd.
 * deepank.dwivedi@gmail.com
 */

public class QuickbloxUserLogin extends BaseActivity {

    private QBUser userForSave;

    public static void start(Context context) {
        Intent intent = new Intent(context, QuickbloxUserLogin.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startSignUpNewUser(createUserWithEnteredData());
    }

    private void startSignUpNewUser(final QBUser newUser) {
        requestExecutor.signUpNewUser(newUser, new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser result, Bundle params) {
                        System.out.println("QuickbloxUserLogin.signUpSuccess");
                        loginToChat(result);
                    }
                    @Override
                    public void onError(QBResponseException e) {
                        System.out.println("QuickbloxUserLogin.onError");
                        if (e.getHttpStatusCode() == Consts.ERR_LOGIN_ALREADY_TAKEN_HTTP_STATUS) {
                            signInCreatedUser(newUser, true);
                        } else {
                            Toaster.longToast(R.string.sign_up_error);
                        }
                    }
                }
        );
    }

    private void loginToChat(final QBUser qbUser) {
        qbUser.setPassword(Consts.DEFAULT_USER_PASSWORD);
        userForSave = qbUser;
        startLoginService(qbUser);
        System.out.println("QuickbloxUserLogin.loginToChat");
    }

    private void saveUserData(QBUser qbUser) {
        SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();
        sharedPrefsHelper.save(Consts.PREF_CURREN_ROOM_NAME, qbUser.getTags().get(0));
        sharedPrefsHelper.saveQbUser(qbUser);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        if (resultCode == Consts.EXTRA_LOGIN_RESULT_CODE) {
            boolean isLoginSuccess = data.getBooleanExtra(Consts.EXTRA_LOGIN_RESULT, false);
            String errorMessage = data.getStringExtra(Consts.EXTRA_LOGIN_ERROR_MESSAGE);

            if (isLoginSuccess) {
                saveUserData(userForSave);

                signInCreatedUser(userForSave, false);
            } else {
                Toaster.longToast(getString(R.string.login_chat_login_error) + errorMessage);
            }
        }
    }


    private void signInCreatedUser(final QBUser user, final boolean deleteCurrentUser) {
        requestExecutor.signInUser(user, new QBEntityCallbackImpl<QBUser>() {
            @Override
            public void onSuccess(QBUser result, Bundle params) {
                if (deleteCurrentUser) {
                    removeAllUserData(result);
                } else {
                    startOpponentsActivity();
                    Toaster.longToast("User Signedin");
                }
                System.out.println("QuickbloxUserLogin.onSuccess");
            }

            @Override
            public void onError(QBResponseException responseException) {
                System.out.println("QuickbloxUserLogin.onError");
                Toaster.longToast(R.string.sign_up_error);
            }
        });
    }

    private void startOpponentsActivity() {
        OpponentsActivity.start(QuickbloxUserLogin.this, false);
        finish();
    }


    private void removeAllUserData(final QBUser user) {
        requestExecutor.deleteCurrentUser(user.getId(), new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                UsersUtils.removeUserData(getApplicationContext());
                startSignUpNewUser(createUserWithEnteredData());
            }

            @Override
            public void onError(QBResponseException e) {
                Toaster.longToast(R.string.sign_up_error);
            }
        });
    }

    private void startLoginService(QBUser qbUser) {
        Intent tempIntent = new Intent(this, CallService.class);
        PendingIntent pendingIntent = createPendingResult(Consts.EXTRA_LOGIN_RESULT_CODE, tempIntent, 0);
        CallService.start(this, qbUser, pendingIntent);
        System.out.println("QuickbloxUserLogin.startLoginService");
    }

    private String getCurrentDeviceId() {
        return Utils.generateDeviceId(this);
    }

    private QBUser createUserWithEnteredData() {
        return createQBUserWithCurrentData("Codifier2",
                "deeepankdwivedi");
    }

    private QBUser createQBUserWithCurrentData(String userName, String chatRoomName) {
        System.out.println("QuickbloxUserLogin.createQBUserWithCurrentData");
        QBUser qbUser = null;
        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(chatRoomName)) {
            StringifyArrayList<String> userTags = new StringifyArrayList<>();
            userTags.add(chatRoomName);

            qbUser = new QBUser();
            qbUser.setFullName(userName);
            qbUser.setLogin(getCurrentDeviceId());
            qbUser.setPassword(Consts.DEFAULT_USER_PASSWORD);
            qbUser.setTags(userTags);
        }

        return qbUser;
    }


}
