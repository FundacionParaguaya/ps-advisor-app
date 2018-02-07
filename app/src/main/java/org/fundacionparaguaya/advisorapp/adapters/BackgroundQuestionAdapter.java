package org.fundacionparaguaya.advisorapp.adapters;

import android.content.Intent;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.yarolegovich.discretescrollview.transform.DiscreteScrollItemTransformer;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.fragments.callbacks.BackgroundQuestionCallback;
import org.fundacionparaguaya.advisorapp.models.BackgroundQuestion;

import java.util.List;

public class BackgroundQuestionAdapter extends RecyclerView.Adapter {

    final static int STRING_INPUT = 1;
    final static int LOCATION_INPUT = 2;
    final static int PHOTO_INPUT = 3;
    final static int DROPDOWN_INPUT =4; //TODO: implement dropdowns
    final static int SUBMIT_BUTTON = 5;

    List<BackgroundQuestion> mQuestionsList;
    BackgroundQuestionCallback mBackgroundQuestionCallback;

    public BackgroundQuestionAdapter(BackgroundQuestionCallback listener){
        mBackgroundQuestionCallback = listener;
    }

    public void setQuestionsList(List<BackgroundQuestion> questionsList)
    {
        mQuestionsList = questionsList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {

        if(position == mQuestionsList.size()){
            return SUBMIT_BUTTON;
        }
        else {
            BackgroundQuestion question = mQuestionsList.get(position);

            if (question.getOptions() != null && question.getOptions().size() > 1) {
                return DROPDOWN_INPUT;
            }
            else {
                switch (question.getResponseType()) {
                    case STRING:
                    case PHONE_NUMBER:
                    case INTEGER:
                        return STRING_INPUT;

                    case PHOTO:
                        return PHOTO_INPUT;

                    case LOCATION:
                        return LOCATION_INPUT;

                    default:
                        return -1;
                }
            }
        }
    }

    //inflate the correct type of question view
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        View itemView;
        RecyclerView.ViewHolder vh = null;

        switch (viewType)
        {
            case STRING_INPUT:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_questiontext, parent, false);
                vh = new TextQuestionViewHolder(itemView);
                break;

            case LOCATION_INPUT:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_questionlocation, parent, false);
                vh = new LocationViewHolder(itemView);
                break;

            case PHOTO_INPUT:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_questionphoto, parent, false);
                vh = new PictureViewHolder(itemView);
                break;

            case DROPDOWN_INPUT:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_questiondropdown, parent, false);
                vh = new DropdownViewHolder(itemView);
                break;

            case SUBMIT_BUTTON:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_backgroundquestion_submit, parent, false);
                vh = new SubmitViewHolder(itemView);
                break;
        }

        if(vh==null)
        {
            throw new IllegalArgumentException("View holder was not a valid type for background questions");
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try
        {
            QuestionViewHolder questionViewHolder = (QuestionViewHolder)holder;
            questionViewHolder.setQuestion(mQuestionsList.get(position));
            questionViewHolder.setQuestionResponseListener(mBackgroundQuestionCallback);
        }
        catch (ClassCastException e)
        {
            Log.e("", e.getMessage());
        }
    }


    @Override
    public int getItemCount() {
        if(mQuestionsList == null) return 0; //if no questions, no submit button
        else return mQuestionsList.size() + 1; //+1 for the submit button
    }


    public static class TextQuestionViewHolder extends QuestionViewHolder {

        AppCompatEditText familyInfoEntry;

        @Override
        public void setQuestion(BackgroundQuestion question) {
            super.setQuestion(question);


            familyInfoEntry = itemView.findViewById(R.id.et_questiontext_answer);

            switch (question.getResponseType())
            {
                case INTEGER:
                    familyInfoEntry.setInputType(InputType.TYPE_CLASS_NUMBER);
                    break;

                default:
                    familyInfoEntry.setInputType(InputType.TYPE_CLASS_TEXT);
            }

        }

        @Override
        public void setQuestionResponseListener(BackgroundQuestionCallback listener) {
            super.setQuestionResponseListener(listener);

            familyInfoEntry.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    String answer = familyInfoEntry.getText().toString();
                    mBackgroundQuestionCallback.onQuestionAnswered(mQuestion, answer);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }

        public TextQuestionViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class DropdownViewHolder extends QuestionViewHolder {

        LinearLayout familyInfoItem;
        Spinner mSpinnerOptions;
        ArrayAdapter<String> mSpinnerAdapter;


        public DropdownViewHolder(View itemView) {
            super(itemView);

            mSpinnerOptions = (Spinner) itemView.findViewById(R.id.spinner_questiondropdown);

            mSpinnerOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String s = (String)adapterView.getItemAtPosition(i);
                    mBackgroundQuestionCallback.onQuestionAnswered(mQuestion, s);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    //TODO should remove last response
                }
            });
        }


        @Override
        public void setQuestion(BackgroundQuestion question) {
            super.setQuestion(question);

            if(question.getOptions() != null){
                createAdapter(question.getOptions());
            } else {
                throw new IllegalArgumentException("This question has no options");
            }
        }

        private void createAdapter(List<String> options)
        {
            mSpinnerAdapter =
                    new ArrayAdapter<String>(itemView.getContext(), R.layout.item_tv_spinner, options);

            mSpinnerOptions.setAdapter(mSpinnerAdapter);
        }

    }


    public static class LocationViewHolder extends QuestionViewHolder{

        LinearLayout familyInfoItem;
        TextView familyInfoQuestion;
        AppCompatEditText familyInfoEntry;

        public LocationViewHolder(View itemView) {
            super(itemView);

            familyInfoQuestion = (TextView) itemView.findViewById(R.id.tv_questionall_title);
            familyInfoEntry = itemView.findViewById(R.id.et_questiontext_answer);
        }


        public String onResponse()
        {
            return familyInfoEntry.getText().toString();
        }

    }

    public static class PictureViewHolder extends QuestionViewHolder{

        LinearLayout familyInfoItem;
        ImageButton cameraButton;
        ImageButton galleryButton;
        ImageView responsePicture;

        public PictureViewHolder(View itemView) {
            super(itemView);
            familyInfoItem = (LinearLayout) itemView.findViewById(R.id.item_picturequestion);
            cameraButton = (ImageButton) itemView.findViewById(R.id.camera_button);
            galleryButton = (ImageButton) itemView.findViewById(R.id.gallery_button);

            cameraButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    itemView.getContext().startActivity(intent);
                }
            });
        }

        public void onResponse()
        {
            responsePicture.setVisibility(View.VISIBLE);
            cameraButton.setVisibility(View.INVISIBLE);
            galleryButton.setVisibility(View.INVISIBLE);
        }

    }

    abstract public static class QuestionViewHolder extends RecyclerView.ViewHolder
    {
        protected BackgroundQuestion mQuestion;
        BackgroundQuestionCallback mBackgroundQuestionCallback;
        TextView mTvQuestionTitle;
        ImageButton mBtnNext;

        public QuestionViewHolder(View itemView) {
            super(itemView);

            mBtnNext = itemView.findViewById(R.id.btn_questionall_next);
            mTvQuestionTitle = itemView.findViewById(R.id.tv_questionall_title);
        }

        public void setQuestionResponseListener(BackgroundQuestionCallback listener)
        {
            mBackgroundQuestionCallback = listener;
            mBtnNext.setOnClickListener(mBackgroundQuestionCallback::onNext);
        }

        /**Stores the question that is being set and sets the title of the question
         *
         * @param question Question to set
         */
        public void setQuestion(BackgroundQuestion question)
        {
            mQuestion = question;
            mTvQuestionTitle.setText(question.getDescription());
        }
    }

    public class SubmitViewHolder extends RecyclerView.ViewHolder{

        LinearLayout submitButtonContainer;
        Button submitButton;

        public SubmitViewHolder(View itemView) {
            super(itemView);

            submitButtonContainer = (LinearLayout) itemView.findViewById(R.id.submit_button_view);
            submitButton = (Button) itemView.findViewById(R.id.submit_button);

            submitButton.setOnClickListener((view)-> mBackgroundQuestionCallback.onFinish());
        }
    }

    /**Fades the questions that are not centered in the Discrete Scroll View**/
    public static class QuestionFadeTransformer implements DiscreteScrollItemTransformer
    {
        @Override
        public void transformItem(View item, float position) {
            //pos inbetween -1 and 1, inclusive

            //first normalize so between 0 and 1
            //1 is max value

            if (item.getId() != R.id.submit_button_view) {
                float absPosition = Math.abs(position);
                absPosition = 1 - absPosition; //flip value.. so 1 is max

                float output = 0.7f * (absPosition) + 0.2f; //in between 100% and 20% output

                item.setAlpha(output);
            }
        }
    }

}

