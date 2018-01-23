package org.fundacionparaguaya.advisorapp.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.fragments.LoginFragment;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FragmentManager manager = getSupportFragmentManager();
        LoginFragment loginFragment = (LoginFragment) manager.findFragmentByTag("LOGIN");

        if (loginFragment == null) {
            loginFragment = new LoginFragment();
        }

        addFragmentToActivity(manager, loginFragment, R.id.login_root, "LOGIN");
    }

    private void addFragmentToActivity(FragmentManager manager,
                                       Fragment fragment,
                                       int frameId,
                                       String tag) {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(frameId, fragment, tag);
        transaction.commit();
    }
}

