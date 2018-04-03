package org.fundacionparaguaya.advisorapp.ui.login;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.fundacionparaguaya.advisorapp.AdvisorApplication;
import org.fundacionparaguaya.advisorapp.BuildConfig;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.data.model.User;
import org.fundacionparaguaya.advisorapp.data.remote.AuthenticationManager;
import org.fundacionparaguaya.advisorapp.data.remote.Server;
import org.fundacionparaguaya.advisorapp.injection.InjectionViewModelFactory;
import org.fundacionparaguaya.advisorapp.ui.common.widget.EvenBetterSpinner;
import org.fundacionparaguaya.advisorapp.ui.dashboard.DashActivity;
import org.fundacionparaguaya.advisorapp.util.MixpanelHelper;
import org.joda.time.DateTime;

import javax.inject.Inject;

import io.rmiri.buttonloading.ButtonLoading;

/**
 * The fragment for the login page.
 */

public class LoginFragment extends Fragment {

    // Threshold for minimal keyboard height.
    private static final int MIN_KEYBOARD_HEIGHT_PX = 150;

    protected EditText mEmailView;
    protected EditText mPasswordView;
    protected ButtonLoading mSubmitButton;
    protected TextView mIncorrectCredentialsView;
    protected EvenBetterSpinner mServerSpinner;

    @Inject
    SharedPreferences mSharedPrefs;

    @Inject protected InjectionViewModelFactory mViewModelFactory;
    private LoginViewModel mViewModel;

    private ImageView mFPLogo;
    private MixpanelAPI mMixpanel;

    private String FIRST_TIME_USER_KEY = "FIRST_TIME_USER_KEY";

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

        mSubmitButton = view.findViewById(R.id.login_loginbutton);

        mFPLogo = (ImageView) view.findViewById(R.id.login_fplogo);

        return view;
    }

    /**
     * Now that the view has been instantiated, attach listeners here.
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayAdapter<Server> spinAdapter = new ArrayAdapter<Server>(
                this.getContext(), R.layout.item_tv_spinner);

        spinAdapter.clear();
        spinAdapter.addAll(mViewModel.getServers());

        mServerSpinner.setAdapter(spinAdapter);
        mServerSpinner.setOnItemClickListener((parent, view1, position, id) -> {
            Server server = spinAdapter.getItem(position);
            mViewModel.setSelectedServer(server);
        });

        if(mViewModel.getServers().length>0) {
            mServerSpinner.selectFirstItem();
            mViewModel.setSelectedServer(mViewModel.getServers()[0]);
        }

        mPasswordView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin();
                return false;
            }
            return true;
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
        mSubmitButton.setOnClickListener((v)->attemptLogin());

        new InitialLoginTask(mViewModel.getAuthManager()).execute();

        mViewModel.getAuthStatus().observe(this, (AuthenticationManager.AuthenticationStatus value) -> {
            if(value!=null)
            {
                switch (value) {
                    case AUTHENTICATED:
                        MixpanelHelper.updateLastLogin(getContext(), DateTime.now());
                        MixpanelHelper.LoginEvent.success(getContext());

                        continueToDash(getActivity());
                        break;

                    case PENDING:
                        //context: https://github.com/rasoulmiri/ButtonLoading/issues/1 (see comment by @bhylak)
                        mSubmitButton.post(()-> mSubmitButton.setProgress(true));
                        break;

                    default:
                        mSubmitButton.setProgress(false);
                        break;
                }
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
            mPasswordView.setError(getString(R.string.all_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.all_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            MixpanelHelper.LoginEvent.validationError(getContext());
            mSubmitButton.setProgress(false);
            focusView.requestFocus();
        } else {
            new LoginTask(this, mViewModel.getAuthManager()).execute(
                    User.builder().username(email).password(password).build());
        }
    }

    void continueToDash(Context context) {
        Intent nextActivity;

        if(mSharedPrefs.getBoolean(FIRST_TIME_USER_KEY, true)) {
            nextActivity = new Intent(context, IntroActivity.class);

            mSharedPrefs.edit()
                    .putBoolean(FIRST_TIME_USER_KEY, false)
                    .apply();
        }
        else
        {
            nextActivity = new Intent(context, DashActivity.class);
        }

        context.startActivity(nextActivity);
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
        mLoginFragment.mSubmitButton.setProgress(true);
    }

    @Override
    protected AuthenticationManager.AuthenticationStatus doInBackground(User... user) {
        if (user.length > 0)
            return mAuthManager.login(user[0]);

        return mAuthManager.status().getValue();
    }

    @Override
    protected void onPostExecute(AuthenticationManager.AuthenticationStatus result) {
        mLoginFragment.mSubmitButton.setProgress(false);

        super.onPostExecute(result);

        switch (result) {
            case UNAUTHENTICATED:
                mLoginFragment.mIncorrectCredentialsView.setText(R.string.login_incorrectcredentials);
                mLoginFragment.mIncorrectCredentialsView.setVisibility(View.VISIBLE);
                mLoginFragment.mPasswordView.setText("");
                mLoginFragment.mServerSpinner.setEnabled(true);
                mLoginFragment.mEmailView.setEnabled(true);
                mLoginFragment.mPasswordView.setEnabled(true);

                Context c =  mLoginFragment.getActivity();

                if(c !=null)
                {
                    MixpanelHelper.LoginEvent.unauthenticatedFail(c);
                }

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