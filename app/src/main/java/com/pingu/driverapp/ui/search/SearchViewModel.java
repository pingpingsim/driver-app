package com.pingu.driverapp.ui.search;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

public class SearchViewModel extends ViewModel {
    private Context context;
    private MutableLiveData<String> mText;

    @Inject
    public SearchViewModel(Context context) {
        this.context = context;
        mText = new MutableLiveData<>();
        mText.setValue("This is search fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}