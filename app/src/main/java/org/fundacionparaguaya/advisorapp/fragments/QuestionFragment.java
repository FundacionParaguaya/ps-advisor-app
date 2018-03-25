package org.fundacionparaguaya.advisorapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.instabug.library.Instabug;

import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.adapters.SelectedFirstSpinnerAdapter;
import org.fundacionparaguaya.advisorapp.adapters.SurveyQuestionReviewAdapter;
import org.fundacionparaguaya.advisorapp.fragments.callbacks.QuestionCallback;
import org.fundacionparaguaya.advisorapp.fragments.callbacks.ReviewCallback;
import org.fundacionparaguaya.advisorapp.models.BackgroundQuestion;
import org.fundacionparaguaya.advisorapp.util.Utilities;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static java.lang.String.format;

public abstract class QuestionFragment extends Fragment {

    protected BackgroundQuestion mQuestion;
    protected TextView mTvQuestionTitle;
    private static String QUESTION_KEY = "QUESTION_KEY";

    public static QuestionFragment build(Class<? extends QuestionFragment> questionType, int questionIndex)
    {
        Bundle b = new Bundle();
        b.putInt(QUESTION_KEY, questionIndex);

        try{
            QuestionFragment fragment = questionType.getConstructor().newInstance();
            fragment.setArguments(b);
            return fragment;
        } catch (IllegalAccessException | java.lang.InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalArgumentException("Question must have a default constructor.");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int questionIndex = getArguments().getInt(QUESTION_KEY, -1);

        if(questionIndex == -1)
        {
            throw new IllegalArgumentException("QuestionFragment must have a question index set");
        }

        mQuestion = getCallback().getQuestion(questionIndex);
    }

    public void notifyResponseCallback(BackgroundQuestion q, String s)
    {
        getCallback().onResponse(q, s);
    }

    /** Returns the response for this question that is currently saved by the callback **/
    public String getSavedResponse()
    {
        return getCallback().getResponse(mQuestion);
    }

    private QuestionCallback<BackgroundQuestion, String> getCallback() {
        try {
            @SuppressWarnings("unchecked")
            QuestionCallback<BackgroundQuestion, String> callback = (QuestionCallback<BackgroundQuestion, String>)getParentFragment();
            return callback;
        }
        catch (ClassCastException e) {
            throw new ClassCastException("Parent fragment of QuestionFragment must implement interface " +
                    "QuestionCallback<BackgroundQuestion, String");
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTvQuestionTitle = view.findViewById(R.id.tv_questionall_title);
        initQuestionView();
    }
    /**
     * Sets all of the views to match the current question for this fragment
     */
    protected void initQuestionView()
    {
        mTvQuestionTitle.setText(mQuestion.getDescription());
    }

    public static class TextQuestionFrag extends QuestionFragment implements TextWatcher{

        private AppCompatEditText familyInfoEntry;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View  v = inflater.inflate(R.layout.item_questiontext, container, false);
            familyInfoEntry = v.findViewById(R.id.et_questiontext_answer);

            return v;
        }

        @Override
        protected void initQuestionView()
        {
            switch (mQuestion.getResponseType())
            {
                case INTEGER:
                    familyInfoEntry.setInputType(InputType.TYPE_CLASS_NUMBER);
                    break;

                default:
                    familyInfoEntry.setInputType(InputType.TYPE_CLASS_TEXT);
                    break;
            }

            if(familyInfoEntry!=null) {
                familyInfoEntry.setText(getSavedResponse());
            }

            familyInfoEntry.addTextChangedListener(this);

            super.initQuestionView();
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String answer = familyInfoEntry.getText().toString();
            notifyResponseCallback(mQuestion, answer);
        }
    }

    public static class DropdownQuestionFrag extends QuestionFragment {

        private Spinner mSpinnerOptions;
        private SelectedFirstSpinnerAdapter<String> mSpinnerAdapter;

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View  v = inflater.inflate(R.layout.item_questiondropdown, container, false);

            mSpinnerOptions = (Spinner)v.findViewById(R.id.spinner_questiondropdown);
            mSpinnerOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String selectedOption = mSpinnerAdapter.getDataAt(i);
                    mSpinnerAdapter.setSelected(i);

                    notifyResponseCallback(mQuestion,
                            mQuestion.getOptions().get(selectedOption));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });

            return v;
        }

        @Override
        protected void initQuestionView() {
            super.initQuestionView();

            if(mQuestion.getOptions() != null){

                mSpinnerAdapter =
                        new SelectedFirstSpinnerAdapter<>(getContext(), R.layout.item_tv_questionspinner);

                mSpinnerAdapter.setValues(mQuestion.getOptions().keySet().toArray(
                        new String[mQuestion.getOptions().size()]));

                mSpinnerOptions.setAdapter(mSpinnerAdapter);

                String existingResponse = getSavedResponse();

                if(existingResponse==null || existingResponse.isEmpty())
                {
                    mSpinnerAdapter.showEmptyPlaceholder(getContext().getResources().
                            getString(R.string.spinner_placeholder));
                }
                else {
                    mSpinnerAdapter.setSelected(existingResponse);
                }

            } else {
                throw new IllegalArgumentException("This question has no options");
            }
        }
    }

    public static class LocationQuestionFrag extends QuestionFragment{

        private Button mLocationPicker;
        private int PLACE_PICKER_REQUEST = 1;

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
            View  v = inflater.inflate(R.layout.item_questionlocation, container, false);

            mLocationPicker = v.findViewById(R.id.btn_set_location);

            mLocationPicker.setOnClickListener(view -> {
                if(Utilities.isGooglePlayServicesAvailable(getActivity())) {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    Intent intent;
                    try {
                        intent = builder.build(getActivity());
                        startActivityForResult(intent, PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesNotAvailableException|GooglePlayServicesRepairableException e) {
                        Instabug.reportException(e);
                    }
                }});
            return v;
        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(getContext(), data);
                Double latitude = place.getLatLng().latitude;
                Double longitude = place.getLatLng().longitude;
                String location = String.valueOf(latitude) + "," + String.valueOf(longitude);
                notifyResponseCallback(mQuestion, location);
            }
        }
    }


    public static class DateQuestionFrag extends QuestionFragment{
        public static final String TAG = "DateQuestionFrag";

        private DatePicker mDatePicker;

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View  v = inflater.inflate(R.layout.item_questiondate, container, false);
            mDatePicker = v.findViewById(R.id.dp_questiondate_answer);

            String savedResponse = getSavedResponse();
            Date date = new Date();

            // try to parse the saved response if it exists
            if (savedResponse!= null) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                try {
                    date = format.parse(savedResponse);
                } catch (ParseException e) {
                    Log.w(TAG, "onCreateView: Malformed saved date.");
                    e.printStackTrace();
                }
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            mDatePicker.init(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    (view, year, monthOfYear, dayOfMonth) ->
                           notifyResponseCallback(mQuestion, format(Locale.US,
                                   "%04d-%02d-%02d", year, monthOfYear, dayOfMonth))
            );

            return v;
        }
    }

    public static class PictureQuestionFrag extends QuestionFragment {

        private AppCompatImageButton mTakePicture;
        private AppCompatImageButton mOpenGallery;

        private static final int REQUEST_IMAGE_CAPTURE = 1;
        private static final int PICK_IMAGE = 2;

        private String mFamilyPhotoPath;

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.item_questionpicture, container, false);

            mTakePicture = v.findViewById(R.id.camera_bkgquestion);
            mOpenGallery = v.findViewById(R.id.gallery_bkgquestion);

            mTakePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        // Create the File where the photo should go
                        File familyPhotoFile = null;
                        try {
                            familyPhotoFile = createImageFile();
                        } catch (IOException ex) {
                            // Error occurred while creating the File
                        }
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, familyPhotoFile);
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            });

            mOpenGallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                }
            });

            return v;
        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                notifyResponseCallback(mQuestion, mFamilyPhotoPath);
            } else if (requestCode == PICK_IMAGE && data != null) {
                try {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    mFamilyPhotoPath = cursor.getString(columnIndex);
                    cursor.close();
                    notifyResponseCallback(mQuestion, mFamilyPhotoPath);

                } catch (Exception e){

                }
            }
        }

        private File createImageFile() throws IOException {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(
                    imageFileName,
                    ".jpg",
                    storageDir
            );

            mFamilyPhotoPath= image.getAbsolutePath();
            return image;
        }
    }


    public static class ReviewPageFragment extends Fragment {
        private RecyclerView mRv;
        private SurveyQuestionReviewAdapter mSurveyReviewAdapter;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            mSurveyReviewAdapter = new SurveyQuestionReviewAdapter();
            mSurveyReviewAdapter.setQuestions(((ReviewCallback<BackgroundQuestion, String>)getParentFragment()).getQuestions());
            ((ReviewCallback<BackgroundQuestion, String>)getParentFragment()).getResponses().observe(this, mSurveyReviewAdapter::setResponses);
        }


        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            View v = inflater.inflate(R.layout.item_questionsreview, container, false);

            mRv = v.findViewById(R.id.rv_questionsreview);
            mRv.setLayoutManager(new LinearLayoutManager(v.getContext()));
            mRv.setAdapter(mSurveyReviewAdapter);

            return v;
        }

        @Override
        public void onDestroy() {
            try {
                ((ReviewCallback) getParentFragment()).getResponses().removeObservers(this);
            }
            catch (NullPointerException e)
            {
                Instabug.reportException(e);
            }

            super.onDestroy();
        }
    }
}
