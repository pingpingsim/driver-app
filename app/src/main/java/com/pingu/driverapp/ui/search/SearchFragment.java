package com.pingu.driverapp.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.pingu.driverapp.R;
import com.pingu.driverapp.databinding.FragmentSearchBinding;
import com.pingu.driverapp.ui.search.tracking.TrackParcelActivity;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

public class SearchFragment extends DaggerFragment {
    private FragmentSearchBinding binding;
    private SearchViewModel searchViewModel;
    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        searchViewModel =
                ViewModelProviders.of(this, viewModelFactory).get(SearchViewModel.class);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.title_search));
        initEventListeners();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false);

        return binding.getRoot();
    }

    private void initEventListeners() {
        binding.menuTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openParcelTrackingScreen();
            }
        });
        binding.menuGuidebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void openParcelTrackingScreen() {
        Intent parcelTrackingIntent = new Intent(getContext(), TrackParcelActivity.class);
        startActivity(parcelTrackingIntent);
    }
}