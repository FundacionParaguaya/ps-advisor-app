package org.fundacionparaguaya.adviserplatform.ui.activities;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import org.fundacionparaguaya.adviserplatform.injection.InjectionViewModelFactory;
import org.fundacionparaguaya.adviserplatform.ui.login.LoginViewModel;

import javax.inject.Inject;

public class BaseCompatActivity extends AppCompatActivity {
    @Inject
    protected SharedPreferences mSharedPrefs;

    @Inject protected InjectionViewModelFactory mViewModelFactory;
    protected LoginViewModel mViewModel;

}
