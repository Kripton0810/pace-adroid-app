package com.kripton.paceclasses;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class Search extends AppCompatActivity {
    List<String> popular_list;
    RecyclerView popular_tags_rv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        init();
        popular_list = new ArrayList<>();
        popular_list.add("Web Development");
        popular_list.add("Core JAVA");
        popular_list.add("PHP");
        popular_list.add("Android Development");
        popular_list.add("Coding");
        popular_list.add("System Design");
        PopularSearchAdapter adapter = new PopularSearchAdapter(popular_list);
        popular_tags_rv.setAdapter(adapter);
    }
    private void init()
    {
        popular_tags_rv = findViewById(R.id.popular_tags_rv);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(Search.this);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        popular_tags_rv.setLayoutManager(layoutManager);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateFade(Search.this);
    }
}