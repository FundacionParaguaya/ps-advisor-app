package org.fundacionparaguaya.advisorapp.viewmodels;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import org.fundacionparaguaya.advisorapp.models.BackgroundQuestion;
import org.fundacionparaguaya.advisorapp.models.Family;
import org.fundacionparaguaya.advisorapp.models.Location;
import org.fundacionparaguaya.advisorapp.repositories.FamilyRepository;

import java.util.Arrays;
import java.util.List;

public class AddFamilyViewModel extends ViewModel {
    private FamilyRepository mFamilyRepository;
    private Family familyCreated;
    private MutableLiveData<List<BackgroundQuestion>> mBgQuestions;

    private BackgroundQuestion mQuestionFamilyName;
    private BackgroundQuestion mQuestionLocation;
    private BackgroundQuestion mQuestionPhoneNumber;
    private BackgroundQuestion mQuestionFamilyId;
    private BackgroundQuestion mQuestionPicture;

    public AddFamilyViewModel(FamilyRepository familyRepository){
        mFamilyRepository = familyRepository;
        mBgQuestions=new MutableLiveData<>();
        //Here is the problem. The family needs to have more parameters passed in now. using build() didn't seem to work here
        familyCreated = Family.builder().name("").code("").build();

        fillQuestions();
    }


    public void  fillQuestions(){
        List<BackgroundQuestion> questionList;
//
//        mQuestionFamilyName = new BackgroundQuestion(
//                "family_name",
//                "Family's Name",
//                ResponseType.STRING,
//                BackgroundQuestion.QuestionType.PERSONAL);
//
//        mQuestionLocation = new BackgroundQuestion(
//                "family_location",
//                "Family's Location",
//                ResponseType.LOCATION,
//                BackgroundQuestion.QuestionType.PERSONAL);
//
//        mQuestionPhoneNumber = new BackgroundQuestion(
//                "family_phonenumber",
//                "Family's Phone Number",
//                ResponseType.PHONE_NUMBER,
//                BackgroundQuestion.QuestionType.PERSONAL);
//
//        mQuestionFamilyId = new BackgroundQuestion(
//                "family_Uid",
//                "Family's ID",
//                ResponseType.STRING,
//                BackgroundQuestion.QuestionType.PERSONAL);
//
//        mQuestionPicture = new BackgroundQuestion(
//                "family_picture",
//                "Family's Picture",
//                ResponseType.PHOTO,
//                BackgroundQuestion.QuestionType.PERSONAL);

        questionList = Arrays.asList(mQuestionFamilyName, mQuestionLocation, mQuestionPhoneNumber, mQuestionFamilyId, mQuestionPicture);

        mBgQuestions.setValue(questionList);
    }

    public MutableLiveData<List<BackgroundQuestion>> getQuestions(){
        return mBgQuestions;
    }

    public void addFamilyResponse(BackgroundQuestion q, Object o)
    {
        if(q.equals(mQuestionFamilyName))
        {
            String name = (String)o;
            familyCreated.setName(name);
        }
        else if(q.equals(mQuestionLocation))
        {
            Location location = (Location)o;
            familyCreated.setLocation(location);
        }

    }

    public void saveFamily()
    {
        mFamilyRepository.saveFamily(familyCreated);
    }

    public int getFamilyId(){
        return familyCreated.getId();
    }


}
