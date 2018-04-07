package org.fundacionparaguaya.adviserplatform.ui.login;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import com.instabug.library.InstabugTrackingDelegate;
import org.fundacionparaguaya.adviserplatform.R;
import org.fundacionparaguaya.adviserplatform.util.Utilities;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    private static final String LOGIN_FRAG_TAG = "LOGIN_FRAG";

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        InstabugTrackingDelegate.notifyActivityGotTouchEvent(ev, this);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FragmentManager manager = getSupportFragmentManager();
        LoginFragment loginFragment = (LoginFragment) manager.findFragmentByTag(LOGIN_FRAG_TAG);

        if (loginFragment == null)
            loginFragment = new LoginFragment();

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.login_root, loginFragment, LOGIN_FRAG_TAG);
        transaction.commit();

        //will show dialog prompting for update if not available
        Utilities.isGooglePlayServicesAvailable(this);
    }
}

