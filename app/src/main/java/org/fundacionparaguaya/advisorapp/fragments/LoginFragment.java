package org.fundacionparaguaya.advisorapp.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.fundacionparaguaya.advisorapp.AdvisorApplication;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.viewmodels.InjectionViewModelFactory;
import org.fundacionparaguaya.advisorapp.viewmodels.LoginViewModel;

import javax.inject.Inject;

/**
 * The fragment for the login page.
 */

public class LoginFragment extends Fragment {
    private EditText mEmailView;
    private EditText mPasswordView;
    private TextView mIncorrectCredentialsView;
    private TextView mPasswordReset;
    private ImageView mHelpButton;

    @Inject
    InjectionViewModelFactory mViewModelFactory;
    LoginViewModel mLoginViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ((AdvisorApplication) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        mLoginViewModel = ViewModelProviders
                .of((FragmentActivity) getActivity(), mViewModelFactory)
                .get(LoginViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mIncorrectCredentialsView = (TextView) view.findViewById(R.id.login_incorrect_credentials);
        mPasswordReset = (TextView) view.findViewById(R.id.login_passwordreset);

        mEmailView = (EditText) view.findViewById(R.id.login_email);
        mPasswordView = (EditText) view.findViewById(R.id.login_password);

        mHelpButton = (ImageView) view.findViewById(R.id.login_help);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) view.findViewById(R.id.login_loginbutton);

        mEmailSignInButton.setOnClickListener((event) -> attemptLogin());

        //hide incorrect login textview on touch
        View.OnClickListener hideIncorrectCredentials =
                (event) -> mIncorrectCredentialsView.setVisibility(View.INVISIBLE);

        mEmailView.setOnClickListener(hideIncorrectCredentials);
        mPasswordView.setOnClickListener(hideIncorrectCredentials);

        mPasswordReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Implement password reset
            }
        });

        mHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Implement Help button
                //using this as a temporary login method
                //getActivity().finish();
            }
        });

        return view;
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.login_fieldrequired));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.login_fieldrequired));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        else {
            boolean result = mLoginViewModel.login(email, password);

            if (getActivity() == null) {
                Log.e("LoginFragment", "Login fragment needs an activity, but none found.");
            }
            else if (result) {
                getActivity().finish();
            }
            else {
                mIncorrectCredentialsView.setText(R.string.login_incorrectcredentials);
                mIncorrectCredentialsView.setVisibility(View.VISIBLE);
                mPasswordView.setText(""); //erase the password field if incorrect
            }
        }
    }
}



