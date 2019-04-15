package org.fundacionparaguaya.adviserplatform.ui.activities;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.fundacionparaguaya.assistantadvisor.AdviserAssistantApplication;
import org.fundacionparaguaya.adviserplatform.data.remote.AuthenticationManager;
import org.fundacionparaguaya.adviserplatform.ui.dashboard.DashActivity;
import org.fundacionparaguaya.adviserplatform.ui.login.IntroActivity;
import org.fundacionparaguaya.adviserplatform.ui.login.LoginActivity;
import org.fundacionparaguaya.adviserplatform.ui.login.LoginViewModel;
import org.fundacionparaguaya.adviserplatform.util.AppConstants;
import org.fundacionparaguaya.adviserplatform.util.MixpanelHelper;
import org.joda.time.DateTime;

public class SplashActivity extends BaseCompatActivity {


    private String LOG_TAG = SplashActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((AdviserAssistantApplication) this.getApplication())
                .getApplicationComponent()
                .inject(this);

        mViewModel = ViewModelProviders
                .of(this, mViewModelFactory)
                .get(LoginViewModel.class);

        mViewModel.getAuthStatus().observe(this, (AuthenticationManager.AuthenticationStatus value) -> {
            if (value != null) {
                switch (value) {
                    case AUTHENTICATED:
                        MixpanelHelper.updateLastLogin(SplashActivity.this, DateTime.now());
                        MixpanelHelper.LoginEvent.success(SplashActivity.this, mViewModel.getUsername(),
                                mViewModel.getSelectedServerHost());

                        continueToDash(this, SplashActivity.this);
                        break;

                    case PENDING:
                        Log.d(LOG_TAG, "Pending login...");
                        break;

                    default:
                        Intent intent = new Intent(this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                }
            }
        });

    }

    void continueToDash(Activity currentActivity, Context context) {
        Intent nextActivity;

        if (mSharedPrefs.getBoolean(AppConstants.FIRST_TIME_USER_KEY, true)) {
            nextActivity = new Intent(context, IntroActivity.class);

            mSharedPrefs.edit()
                    .putBoolean(AppConstants.FIRST_TIME_USER_KEY, false)
                    .apply();
        } else {
            nextActivity = new Intent(context, DashActivity.class);
            nextActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        context.startActivity(nextActivity);
        currentActivity.finish();
    }
}

