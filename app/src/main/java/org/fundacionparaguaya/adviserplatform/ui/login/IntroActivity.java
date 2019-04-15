package org.fundacionparaguaya.adviserplatform.ui.login;

import android.content.Intent;
import android.os.Bundle;

import com.cuneytayyildiz.onboarder.OnboarderActivity;
import com.cuneytayyildiz.onboarder.OnboarderPage;

import org.fundacionparaguaya.assistantadvisor.R;
import org.fundacionparaguaya.adviserplatform.ui.dashboard.DashActivity;

import java.util.Arrays;
import java.util.List;

public class IntroActivity extends OnboarderActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<OnboarderPage> pages = Arrays.asList(
                new OnboarderPage.Builder()
                        .title(getResources().getString(R.string.intro_welcome_title))
                        .description(getResources().getString(R.string.intro_welcome_description))
                        .imageResourceId(R.drawable.ic_intro_trafficlight)
                        .backgroundColor(R.color.colorPrimary)
                        .titleColor(R.color.app_white)
                        .descriptionColor(R.color.app_white)
                        .titleTextSize(36)
                        .descriptionTextSize(28)
                        .multilineDescriptionCentered(true)
                        .build(),

                new OnboarderPage.Builder()
                        .title(getResources().getString(R.string.intro_shake_title))
                        .description(getResources().getString(R.string.intro_shake_description))
                        .imageResourceId(R.drawable.ic_intro_phoneshake)
                        .backgroundColor(R.color.colorPrimary)
                        .titleColor(R.color.app_white)
                        .descriptionColor(R.color.app_white)
                        .titleTextSize(36)
                        .descriptionTextSize(28)
                        .multilineDescriptionCentered(true)
                        .build(),

                new OnboarderPage.Builder()
                        .title(getResources().getString(R.string.intro_viewfamilies_title))
                        .description(getResources().getString(R.string.intro_viewfamilies_description))
                        .imageResourceId(R.drawable.ic_intro_search)
                        .backgroundColor(R.color.colorPrimary)
                        .titleColor(R.color.app_white)
                        .descriptionColor(R.color.app_white)
                        .titleTextSize(36)
                        .descriptionTextSize(28)
                        .multilineDescriptionCentered(true)
                        .build(),

                new OnboarderPage.Builder()
                        .title(getResources().getString(R.string.intro_surveys_title))
                        .description(getResources().getString(R.string.intro_surveys_description))
                        .imageResourceId(R.drawable.ic_intro_survey)
                        .backgroundColor(R.color.colorPrimary)
                        .titleColor(R.color.app_white)
                        .descriptionColor(R.color.app_white)
                        .titleTextSize(36)
                        .descriptionTextSize(28)
                        .multilineDescriptionCentered(true)
                        .build(),

                new OnboarderPage.Builder()
                        .title(getResources().getString(R.string.intro_priorities_title))
                        .description(getResources().getString(R.string.intro_priorities_description))
                        .imageResourceId(R.drawable.ic_intro_priorities)
                        .backgroundColor(R.color.colorPrimary)
                        .titleColor(R.color.app_white)
                        .descriptionColor(R.color.app_white)
                        .titleTextSize(36)
                        .descriptionTextSize(28)
                        .multilineDescriptionCentered(true)
                        .build(),

                new OnboarderPage.Builder()
                        .title(getResources().getString(R.string.intro_more_title))
                        .description(getResources().getString(R.string.intro_more_description))
                        .imageResourceId(R.drawable.ic_intro_gift)
                        .backgroundColor(R.color.colorPrimary)
                        .titleColor(R.color.app_white)
                        .descriptionColor(R.color.app_white)
                        .titleTextSize(36)
                        .descriptionTextSize(28)
                        .multilineDescriptionCentered(true)
                        .build()
        );

        initOnboardingPages(pages);
    }

    @Override
    public void onFinishButtonPressed() {
        Intent intent = new Intent(this, DashActivity.class);
        startActivity(intent);
    }
}
