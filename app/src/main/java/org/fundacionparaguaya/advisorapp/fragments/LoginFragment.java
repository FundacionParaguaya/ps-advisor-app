package org.fundacionparaguaya.advisorapp.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.fundacionparaguaya.advisorapp.AdvisorApplication;
import org.fundacionparaguaya.advisorapp.BuildConfig;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.activities.DashActivity;
import org.fundacionparaguaya.advisorapp.adapters.SelectedFirstSpinnerAdapter;
import org.fundacionparaguaya.advisorapp.data.remote.AuthenticationManager;
import org.fundacionparaguaya.advisorapp.data.remote.Server;
import org.fundacionparaguaya.advisorapp.models.User;
import org.fundacionparaguaya.advisorapp.util.MixpanelHelper;
import org.fundacionparaguaya.advisorapp.viewmodels.InjectionViewModelFactory;
import org.fundacionparaguaya.advisorapp.viewmodels.LoginViewModel;
import org.joda.time.DateTime;

import javax.inject.Inject;

import static org.fundacionparaguaya.advisorapp.data.remote.AuthenticationManager.AuthenticationStatus.AUTHENTICATED;

/**
 * The fragment for the login page.
 */

public class LoginFragment extends Fragment {

    // Threshold for minimal keyboard height.
    private static final int MIN_KEYBOARD_HEIGHT_PX = 150;

    protected EditText mEmailView;
    protected EditText mPasswordView;
    protected Button mSubmitButton;
    protected TextView mIncorrectCredentialsView;
    protected AppCompatSpinner mServerSpinner;

    @Inject protected InjectionViewModelFactory mViewModelFactory;
    private LoginViewModel mViewModel;

    private ImageView mFPLogo;
    private MixpanelAPI mMixpanel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((AdvisorApplication) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        mViewModel = ViewModelProviders
                .of((FragmentActivity) getActivity(), mViewModelFactory)
                .get(LoginViewModel.class);

        mMixpanel = MixpanelAPI.getInstance(getContext(), BuildConfig.MIXPANEL_API_KEY_STRING);
    }

    @Override
    public void onDestroyView() {
        mMixpanel.flush();
        super.onDestroyView();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        setRetainInstance(true);

        mIncorrectCredentialsView = (TextView) view.findViewById(R.id.login_incorrect_credentials);

        mServerSpinner = view.findViewById(R.id.spinner_login_serverselect);

        mEmailView = (EditText) view.findViewById(R.id.login_email);
        mPasswordView = (EditText) view.findViewById(R.id.login_password);

        ImageView mHelpButton = view.findViewById(R.id.login_help);
        mFPLogo = (ImageView) view.findViewById(R.id.login_fplogo);

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

        // Top-level window decor view.
        final View decorView = this.getActivity().getWindow().getDecorView();

        // Look for Keyboard show
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private final Rect windowVisibleDisplayFrame = new Rect();
            private int lastVisibleDecorViewHeight;

            @Override
            public void onGlobalLayout() {
                // Retrieve visible rectangle inside window.
                decorView.getWindowVisibleDisplayFrame(windowVisibleDisplayFrame);
                final int visibleDecorViewHeight = windowVisibleDisplayFrame.height();

                // Decide whether keyboard is visible from changing decor view height.
                if (lastVisibleDecorViewHeight != 0) {
                    if (lastVisibleDecorViewHeight > visibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX) {
                        // Calculate current keyboard height (this includes also navigation bar height when in fullscreen mode). (Uncomment to use)
                        //int currentKeyboardHeight = decorView.getHeight() - windowVisibleDisplayFrame.bottom;

                        // Keyboard is showing, move to show everything
                        mFPLogo.setVisibility(View.GONE);
                    } else if (lastVisibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX < visibleDecorViewHeight) {
                        //Keyboard is not showing, center view
                        mFPLogo.setVisibility(View.VISIBLE);
                    }
                }
                // Save current decor view height for the next call.
                lastVisibleDecorViewHeight = visibleDecorViewHeight;
            }
        });

        //hide incorrect login textview on touch
        View.OnClickListener hideIncorrectCredentials =
                (event) -> mIncorrectCredentialsView.setVisibility(View.INVISIBLE);

        mEmailView.setOnClickListener(hideIncorrectCredentials);
        mPasswordView.setOnClickListener(hideIncorrectCredentials);

        //Hide for later implementation
        mHelpButton.setVisibility(View.GONE);

        mHelpButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //TODO: Implement Help button (set visible above when ready to implement)
                //using this as a temporary login method
                //getActivity().finish();
            }
        });

        mViewModel.getAuthStatus().observe(this, (value) -> {
            if (value == AUTHENTICATED) {

                MixpanelHelper.updateLastLogin(getContext(), DateTime.now());
                MixpanelHelper.LoginEvent.success(getContext());

                launchMainActivity(getActivity());
            }
        });
        new InitialLoginTask(mViewModel.getAuthManager()).execute();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SelectedFirstSpinnerAdapter<Server> spinAdapter = new SelectedFirstSpinnerAdapter<>(
                this.getContext(), R.layout.item_tv_spinner);

        spinAdapter.setValues(mViewModel.getServers());
        mViewModel.getSelectedServer().observe(this, spinAdapter::setSelected);

        mServerSpinner.setAdapter(spinAdapter);
        mServerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Server server = spinAdapter.getDataAt(i);
                mViewModel.setSelectedServer(server);
                spinAdapter.setSelected(server);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //mSpinnerAdapter.setSelected(-1);
            }
        });
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
            MixpanelHelper.LoginEvent.validationError(getContext());
            focusView.requestFocus();
        } else {
            new LoginTask(this, mViewModel.getAuthManager()).execute(new User(email, password, true));
        }
    }

    void launchMainActivity(Context context) {
        Intent dashboard = new Intent(context, DashActivity.class);
        context.startActivity(dashboard);
        getActivity().finish();
    }

    /**
     * A task to attempt to login the application using saved credentials.
     */
    private static class InitialLoginTask extends AsyncTask<Void, Void, Void> {
        private AuthenticationManager mAuthManager;

        InitialLoginTask(AuthenticationManager authManager) {
            mAuthManager = authManager;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAuthManager.login();
            return null;
        }
    }
}

class LoginTask extends AsyncTask<User, Void, AuthenticationManager.AuthenticationStatus> {
    private static final String TAG = "LoginTask";

    LoginFragment mLoginFragment;
    AuthenticationManager mAuthManager;

    LoginTask(LoginFragment loginFragment, AuthenticationManager authManager) {
        this.mLoginFragment = loginFragment;
        this.mAuthManager = authManager;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mLoginFragment.mServerSpinner.setEnabled(false);
        mLoginFragment.mEmailView.setEnabled(false);
        mLoginFragment.mPasswordView.setEnabled(false);
        mLoginFragment.mSubmitButton.setEnabled(false);
    }

    @Override
    protected AuthenticationManager.AuthenticationStatus doInBackground(User... user) {
        if (user.length > 0)
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
                mLoginFragment.mServerSpinner.setEnabled(true);
                mLoginFragment.mEmailView.setEnabled(true);
                mLoginFragment.mPasswordView.setEnabled(true);
                mLoginFragment.mSubmitButton.setEnabled(true);

                MixpanelHelper.LoginEvent.unauthenticatedFail(mLoginFragment.getContext());

                break;
                // TODO: Tie into the connectivity watcher to determine whether the app is online
//            case UNKNOWN:
//                mLoginFragment.mIncorrectCredentialsView.setText(R.string.login_error);
//                mLoginFragment.mIncorrectCredentialsView.setVisibility(View.VISIBLE);
//                mLoginFragment.mServerSpinner.setEnabled(true);
//                mLoginFragment.mEmailView.setEnabled(true);
//                mLoginFragment.mPasswordView.setEnabled(true);
//                mLoginFragment.mSubmitButton.setEnabled(true);
//
//                MixpanelHelper.LoginEvent.unknownFail(mLoginFragment.getContext());
//
//                break;
        }
    }
}