package org.fundacionparaguaya.adviserplatform.ui.activities;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;

import org.fundacionparaguaya.adviserplatform.AdviserApplication;
import org.fundacionparaguaya.adviserplatform.R;
import org.fundacionparaguaya.adviserplatform.data.remote.AuthenticationManager;
import org.fundacionparaguaya.adviserplatform.ui.dashboard.DashActivity;
import org.fundacionparaguaya.adviserplatform.ui.login.IntroActivity;
import org.fundacionparaguaya.adviserplatform.ui.login.LoginActivity;
import org.fundacionparaguaya.adviserplatform.ui.login.LoginViewModel;
import org.fundacionparaguaya.adviserplatform.util.AppConstants;
import org.fundacionparaguaya.adviserplatform.util.MixpanelHelper;
import org.fundacionparaguaya.adviserplatform.util.Utilities;
import org.joda.time.DateTime;

import javax.inject.Inject;

public class SplashActivity extends BaseCompatActivity {


    private String LOG_TAG = SplashActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((AdviserApplication) this.getApplication())
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
        }

        context.startActivity(nextActivity);
        currentActivity.finish();
    }
}

