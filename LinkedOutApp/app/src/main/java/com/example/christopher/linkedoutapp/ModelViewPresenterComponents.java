package com.example.christopher.linkedoutapp;

import android.graphics.Color;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by imcdo on 3/15/2017.
 */

public interface ModelViewPresenterComponents {

    interface UserPhotoView {
        void onClickGallery();
    }

    interface UserPhotoPresenter {

    }


    interface RegisterActivityView {

        void onClickSignUp();

        void onClickRegister();
    }

    interface UserPhotoModel {


    }

    interface RegisterModel {

    }


    interface RegisterPresenterContract {
        void clickRegister();
    }
}


