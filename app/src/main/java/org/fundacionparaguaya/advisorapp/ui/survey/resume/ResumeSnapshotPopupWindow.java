package org.fundacionparaguaya.advisorapp.ui.survey.resume;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.kyleduo.blurpopupwindow.library.BlurPopupWindow;
import org.fundacionparaguaya.advisorapp.AdvisorApplication;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.data.model.Family;
import org.fundacionparaguaya.advisorapp.data.model.Snapshot;
import org.fundacionparaguaya.advisorapp.data.model.Survey;
import org.fundacionparaguaya.advisorapp.injection.InjectionViewModelFactory;
import org.ocpsoft.prettytime.PrettyTime;

import javax.inject.Inject;
import java.util.Date;

/**
 * Pop up window that allows the user to resume an existing snapshot in progress.
 */

public class ResumeSnapshotPopupWindow extends BlurPopupWindow {
    public static final String TAG = "ResumeSnapshotPopup";
    private OnContinueCallback mOnContinueCallback;
    private OnDismissCallback mOnDismissCallback;

    @Inject
    protected InjectionViewModelFactory mViewModelFactory;
    private ResumeSnapshotPopupViewModel mViewModel;

    public ResumeSnapshotPopupWindow(@NonNull Context context) {
        super(context);

        if (!isInEditMode())
            throw new UnsupportedOperationException("Default constructor is only for tools!");
    }

    public ResumeSnapshotPopupWindow(@NonNull Context context,
                                     @NonNull Snapshot snapshot,
                                     OnContinueCallback onContinueCallback,
                                     OnDismissCallback onDismissCallback) {
        super(context);

        // createContentView() will be called by super before the rest of this code executes
        mViewModel.setSnapshot(snapshot);
        mOnContinueCallback = onContinueCallback;
        mOnDismissCallback = onDismissCallback;


        if (mOnContinueCallback == null && mOnDismissCallback == null) {
            Log.w(TAG, "init: No callbacks were provided, so the popup won't do anything!");
        }
    }

    @Override
    protected View createContentView(ViewGroup parent) {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.view_resumesnapshotpopup, parent, false);

        ((AdvisorApplication) getContext().getApplicationContext())
                .getApplicationComponent()
                .inject(this);

        mViewModel = ViewModelProviders
                .of((FragmentActivity) getContext(), mViewModelFactory)
                .get(ResumeSnapshotPopupViewModel.class);

        AppCompatButton continueButton = view.findViewById(R.id.btn_resumesnapshotpopup_continue);
        continueButton.setOnClickListener((event) -> {
            if (mOnContinueCallback != null) {
                mOnContinueCallback.onContinue(this,
                        mViewModel.getSnapshot(), mViewModel.getSurvey(), mViewModel.getFamily());
            }
        });

        AppCompatButton dismissButton = view.findViewById(R.id.btn_resumesnapshotpopup_dismiss);
        dismissButton.setOnClickListener((event) -> {
            if (mOnDismissCallback != null) {
                mOnDismissCallback.onDismiss(this,
                        mViewModel.getSnapshot(), mViewModel.getSurvey(), mViewModel.getFamily());
            }
        });

        AppCompatTextView title = view.findViewById(R.id.tv_resumesnapshotpopup_snapshottitle);
        mViewModel.family().observe((FragmentActivity) getContext(), (family) -> {
            String familyString;
            if (family != null) {
                familyString = family.getMember().getLastName();
            } else {
                // this snapshot is for a new family
                familyString = getContext()
                        .getString(R.string.all_new);
            }

            title.setText(getContext().getString(R.string.resumesnapshotpopup_snapshottitle,
                    familyString));
        });

        AppCompatTextView surveyName = view.findViewById(R.id.tv_resumesnapshotpopup_snapshotsurveyname);
        mViewModel.survey().observe((FragmentActivity) getContext(), (survey) -> {
            String surveyString;
            if (survey != null) {
                surveyString = survey.getTitle();
            } else {
                surveyString = "";
            }

            surveyName.setText(surveyString);
        });

        AppCompatTextView timeAgo = view.findViewById(R.id.tv_resumesnapshotpopup_snapshottimeago);
        mViewModel.snapshot().observe((FragmentActivity) getContext(), (snapshot) -> {
            String dateString;
            if (snapshot != null) {
                Date dateCreated = snapshot.getCreatedAt();
                dateString = new PrettyTime().format(dateCreated);
            } else {
                dateString = "";
            }

            timeAgo.setText(dateString);
        });

        ViewCompat.setBackgroundTintList(view.findViewById(R.id.layout_resumesnapshot_surveyinfo),
                ContextCompat.getColorStateList(getContext(), R.color.lightPrimary));

        return view;
    }


    public static class Builder extends BlurPopupWindow.Builder<ResumeSnapshotPopupWindow> {
        private Snapshot mSnapshot;
        private OnContinueCallback mOnContinueCallback;
        private OnDismissCallback mOnDismissCallback;

        public Builder(@NonNull Context context) {
            super(context);
            setScaleRatio(0.25f)
                    .setGravity(Gravity.CENTER)
                    .setBlurRadius(10)
                    .setTintColor(Color.parseColor("#20FFFFFF"))
                    .setDismissOnTouchBackground(false);
        }

        public Builder snapshot(@NonNull Snapshot snapshot) {
            mSnapshot = snapshot;
            return this;
        }

        public Builder onContinue(OnContinueCallback callback) {
            mOnContinueCallback = callback;
            return this;
        }

        public Builder onDismiss(OnDismissCallback callback) {
            mOnDismissCallback = callback;
            return this;
        }

        @Override
        protected ResumeSnapshotPopupWindow createPopupWindow() {
            // provides the window so that the super.build() function can adjust it
            return new ResumeSnapshotPopupWindow(mContext, mSnapshot, mOnContinueCallback, mOnDismissCallback);
        }
    }

    public interface OnContinueCallback {
        void onContinue(ResumeSnapshotPopupWindow popupWindow, Snapshot snapshot, Survey survey, Family family);
    }

    public interface OnDismissCallback {
        void onDismiss(ResumeSnapshotPopupWindow popupWindow, Snapshot snapshot, Survey survey, Family family);
    }
}
