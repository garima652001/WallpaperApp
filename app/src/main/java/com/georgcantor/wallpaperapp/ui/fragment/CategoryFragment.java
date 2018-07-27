package com.georgcantor.wallpaperapp.ui.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.georgcantor.wallpaperapp.R;
import com.georgcantor.wallpaperapp.model.Category;
import com.georgcantor.wallpaperapp.ui.adapter.CategoryAdapter;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment {

    private List<Category> categoryList = new ArrayList<>();
    private int column_no;

    public CategoryFragment() {
    }

    public static CategoryFragment newInstance() {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.expRecView);
        recyclerView.setHasFixedSize(true);
        checkScreenSize();
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), column_no));
        CategoryAdapter categoryAdapter = new CategoryAdapter(getActivity());

        populate();
        categoryAdapter.setCategoryList(categoryList);
        recyclerView.setAdapter(categoryAdapter);
        return view;
    }

    public void populate() {
        categoryList.add(new Category(getResources().getString(R.string.Animals), getResources().getString(R.string.animals)));
        categoryList.add(new Category(getResources().getString(R.string.Textures), getResources().getString(R.string.backgrounds)));
        categoryList.add(new Category(getResources().getString(R.string.Architecture), getResources().getString(R.string.buildings)));
        categoryList.add(new Category(getResources().getString(R.string.Business), getResources().getString(R.string.business)));
        categoryList.add(new Category(getResources().getString(R.string.Communication), getResources().getString(R.string.computer)));
        categoryList.add(new Category(getResources().getString(R.string.Education), getResources().getString(R.string.education)));
        categoryList.add(new Category(getResources().getString(R.string.Fashion), getResources().getString(R.string.fashion)));
        categoryList.add(new Category(getResources().getString(R.string.Emotions), getResources().getString(R.string.feelings)));
        categoryList.add(new Category(getResources().getString(R.string.Food), getResources().getString(R.string.food)));
        categoryList.add(new Category(getResources().getString(R.string.Health), getResources().getString(R.string.health)));
        categoryList.add(new Category(getResources().getString(R.string.Craft), getResources().getString(R.string.industry)));
        categoryList.add(new Category(getResources().getString(R.string.Music), getResources().getString(R.string.music)));
        categoryList.add(new Category(getResources().getString(R.string.Nature), getResources().getString(R.string.nature)));
        categoryList.add(new Category(getResources().getString(R.string.People), getResources().getString(R.string.people)));
        categoryList.add(new Category(getResources().getString(R.string.Places), getResources().getString(R.string.places)));
        categoryList.add(new Category(getResources().getString(R.string.Religion), getResources().getString(R.string.religion)));
        categoryList.add(new Category(getResources().getString(R.string.Technology), getResources().getString(R.string.science)));
        categoryList.add(new Category(getResources().getString(R.string.Sports), getResources().getString(R.string.sports)));
        categoryList.add(new Category(getResources().getString(R.string.Transportation), getResources().getString(R.string.transportation)));
        categoryList.add(new Category(getResources().getString(R.string.Travel), getResources().getString(R.string.travel)));
    }

    public void checkScreenSize() {
        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        switch (screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                column_no = 4;
                break;
            case Configuration.SCREENLAYOUT_SIZE_UNDEFINED:
                column_no = 3;
                break;
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                column_no = 3;
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                column_no = 2;
                break;
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                column_no = 2;
                break;
            default:
                column_no = 2;
        }
    }
}
