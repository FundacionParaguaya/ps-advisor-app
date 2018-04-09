package org.fundacionparaguaya.adviserplatform.ui.survey.resume;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.kyleduo.blurpopupwindow.library.BlurPopupWindow;
import org.fundacionparaguaya.adviserplatform.R;
import org.fundacionparaguaya.adviserplatform.data.model.Family;
import org.fundacionparaguaya.adviserplatform.data.model.Snapshot;
import org.fundacionparaguaya.adviserplatform.data.model.Survey;
import org.ocpsoft.prettytime.PrettyTime;
import timber.log.Timber;

import java.util.Date;

/**
 * Pop up window that allows the user to resume an existing snapshot in progress.
 */

public class ResumeSnapshotPopupWindow extends BlurPopupWindow {
    public static final String TAG = "ResumeSnapshotPopup";

    private AppCompatTextView mSnapshotTitleTextView;
    private AppCompatTextView mSurveyNameTextView;
    private AppCompatTextView mTimeAgoTextView;

    private Snapshot mSnapshot;
    private Family mFamily;
    private Survey mSurvey;
    private OnContinueCallback mOnContinueCallback;
    private OnDismissCallback mOnDismissCallback;

    public ResumeSnapshotPopupWindow(@NonNull Context context) {
        super(context);

        if (!isInEditMode())
            throw new UnsupportedOperationException("Default constructor is only for tools!");
    }

    public ResumeSnapshotPopupWindow(@NonNull Context context,
                                     OnContinueCallback onContinueCallback,
                                     OnDismissCallback onDismissCallback,
                                     @Nullable Snapshot snapshot,
                                     @Nullable Survey survey,
                                     @Nullable Family family) {
        super(context);

        // createContentView() will be called by super before the rest of this code executes
        mOnContinueCallback = onContinueCallback;
        mOnDismissCallback = onDismissCallback;

        if (snapshot != null)
            setSnapshot(snapshot);
        if (survey != null)
            setSurvey(survey);
        setFamily(family);

        if (mOnContinueCallback == null && mOnDismissCallback == null) {
            Timber.w("init: No callbacks were provided, so the popup won't do anything!");
        }
    }

    @Override
    protected View createContentView(ViewGroup parent) {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.view_resumesnapshotpopup, parent, false);

        AppCompatButton continueButton = view.findViewById(R.id.btn_resumesnapshotpopup_continue);
        continueButton.setOnClickListener((event) -> {
            if (mOnContinueCallback != null) {
                mOnContinueCallback.onContinue(this, mSnapshot, mSurvey, mFamily);
            }
        });

        AppCompatButton dismissButton = view.findViewById(R.id.btn_resumesnapshotpopup_dismiss);
        dismissButton.setOnClickListener((event) -> {
            if (mOnDismissCallback != null) {
                mOnDismissCallback.onDismiss(this, mSnapshot, mSurvey, mFamily);
            }
        });

        mSnapshotTitleTextView = view.findViewById(R.id.tv_resumesnapshotpopup_snapshottitle);
        mSurveyNameTextView = view.findViewById(R.id.tv_resumesnapshotpopup_snapshotsurveyname);
        mTimeAgoTextView = view.findViewById(R.id.tv_resumesnapshotpopup_snapshottimeago);

        ViewCompat.setBackgroundTintList(view.findViewById(R.id.layout_resumesnapshot_surveyinfo),
                ContextCompat.getColorStateList(getContext(), R.color.lightPrimary));

        return view;
    }

    public void setSnapshot(@NonNull Snapshot snapshot) {
        Date dateCreated = snapshot.getCreatedAt();
        String dateString = new PrettyTime().format(dateCreated);

        mTimeAgoTextView.setText(dateString);
        mSnapshot = snapshot;
    }

    public void setSurvey(@NonNull Survey survey) {
        mSurveyNameTextView.setText(survey.getTitle());
        mSurvey = survey;
    }

    public void setFamily(@Nullable Family family) {
        String familyString;
        if (family != null) {
            familyString = family.getMember().getLastName();
        } else {
            // this snapshot is for a new family
            familyString = getContext()
                    .getString(R.string.all_new);
        }

        mSnapshotTitleTextView.setText(getContext().getString(
                R.string.surveyintro_family, familyString));
        mFamily = family;
    }

    public static Builder builder(@NonNull Context context) {
        return new Builder(context);
    }

    public static class Builder extends BlurPopupWindow.Builder<ResumeSnapshotPopupWindow> {
        private OnContinueCallback mOnContinueCallback;
        private OnDismissCallback mOnDismissCallback;
        private Snapshot mSnapshot;
        private Survey mSurvey;
        private Family mFamily;

        public Builder(@NonNull Context context) {
            super(context);
            setScaleRatio(0.25f)
                    .setGravity(Gravity.CENTER)
                    .setBlurRadius(10)
                    .setTintColor(Color.parseColor("#20FFFFFF"))
                    .setDismissOnTouchBackground(false);
        }

        public Builder popup(@NonNull ResumeSnapshotPopupWindow popup) {
            mSnapshot = popup.mSnapshot;
            mSurvey = popup.mSurvey;
            mFamily = popup.mFamily;
            mOnContinueCallback = popup.mOnContinueCallback;
            mOnDismissCallback = popup.mOnDismissCallback;
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
            return new ResumeSnapshotPopupWindow(mContext, mOnContinueCallback, mOnDismissCallback, mSnapshot, mSurvey, mFamily);
        }
    }

    public interface OnContinueCallback {
        void onContinue(ResumeSnapshotPopupWindow popupWindow, Snapshot snapshot, Survey survey, Family family);
    }

    public interface OnDismissCallback {
        void onDismiss(ResumeSnapshotPopupWindow popupWindow, Snapshot snapshot, Survey survey, Family family);
    }
}
