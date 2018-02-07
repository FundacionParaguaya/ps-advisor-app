package org.fundacionparaguaya.advisorapp.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
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
import org.fundacionparaguaya.advisorapp.activities.DashActivity;
import org.fundacionparaguaya.advisorapp.data.remote.AuthenticationManager;
import org.fundacionparaguaya.advisorapp.models.User;
import org.fundacionparaguaya.advisorapp.viewmodels.InjectionViewModelFactory;
import org.fundacionparaguaya.advisorapp.viewmodels.LoginViewModel;

import javax.inject.Inject;

import static org.fundacionparaguaya.advisorapp.data.remote.AuthenticationManager.AuthenticationStatus.AUTHENTICATED;

/**
 * The fragment for the login page.
 */

public class LoginFragment extends Fragment {
    EditText mEmailView;
    EditText mPasswordView;
    Button mSubmitButton;
    TextView mIncorrectCredentialsView;
    TextView mPasswordReset;
    ImageView mHelpButton;

    AuthenticationManager mAuthManager;

    @Inject InjectionViewModelFactory mViewModelFactory;

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
        mAuthManager = mLoginViewModel.getAuthManager();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        setRetainInstance(true);

        mIncorrectCredentialsView = (TextView) view.findViewById(R.id.login_incorrect_credentials);
        mPasswordReset = (TextView) view.findViewById(R.id.login_passwordreset);

        mEmailView = (EditText) view.findViewById(R.id.login_email);
        mPasswordView = (EditText) view.findViewById(R.id.login_password);

        mHelpButton = (ImageView) view.findViewById(R.id.login_help);

        mSubmitButton = (Button) view.findViewById(R.id.login_loginbutton);
        mSubmitButton.setOnClickListener((event) -> attemptLogin());

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return false;
                }
                return true;
            }
        });

        //hide incorrect login textview on touch
        View.OnClickListener hideIncorrectCredentials =
                (event) -> mIncorrectCredentialsView.setVisibility(View.INVISIBLE);

        mEmailView.setOnClickListener(hideIncorrectCredentials);
        mPasswordView.setOnClickListener(hideIncorrectCredentials);

        //Hide for later implementation
        mPasswordReset.setVisibility(View.GONE);
        mHelpButton.setVisibility(View.GONE);

        mPasswordReset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //TODO: Implement password reset (set visible above when ready to implement)
            }
        });

        mHelpButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //TODO: Implement Help button (set visible above when ready to implement)
                //using this as a temporary login method
                //getActivity().finish();
            }
        });

        mAuthManager.getStatus().observe(this, (value) -> {
            if (value == AUTHENTICATED) {
                launchMainActivity(getActivity());
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
        } else {
            new LoginTask(this).execute(new User(email, password, true));
        }
    }

    void launchMainActivity(Context context) {
        Intent dashboard = new Intent(context, DashActivity.class);
        context.startActivity(dashboard);
        getActivity().finish();
    }
}
class LoginTask extends AsyncTask<User, Void, AuthenticationManager.AuthenticationStatus> {
    private static final String TAG = "LoginTask";

    LoginFragment mLoginFragment;
    AuthenticationManager mAuthManager;

    LoginTask(LoginFragment loginFragment) {
        this.mLoginFragment = loginFragment;
        this.mAuthManager = mLoginFragment.mAuthManager;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        mLoginFragment.mEmailView.setEnabled(false);
        mLoginFragment.mPasswordView.setEnabled(false);
        mLoginFragment.mSubmitButton.setEnabled(false);
    }

    @Override
    protected AuthenticationManager.AuthenticationStatus doInBackground(User... user) {
        mAuthManager.login(user[0]);

        return mAuthManager.getStatus().getValue();
    }

    @Override
    protected void onPostExecute(AuthenticationManager.AuthenticationStatus result) {
        super.onPostExecute(result);

        switch (result) {
            case UNAUTHENTICATED:
                mLoginFragment.mIncorrectCredentialsView.setText(R.string.login_incorrectcredentials);
                mLoginFragment.mIncorrectCredentialsView.setVisibility(View.VISIBLE);
                mLoginFragment.mPasswordView.setText("");
                mLoginFragment.mEmailView.setEnabled(true);
                mLoginFragment.mPasswordView.setEnabled(true);
                mLoginFragment.mSubmitButton.setEnabled(true);
                break;
            case UNKNOWN:
                mLoginFragment.mIncorrectCredentialsView.setText(R.string.login_error);
                mLoginFragment.mIncorrectCredentialsView.setVisibility(View.VISIBLE);
                mLoginFragment.mEmailView.setEnabled(true);
                mLoginFragment.mPasswordView.setEnabled(true);
                mLoginFragment.mSubmitButton.setEnabled(true);
                break;
        }
    }
}