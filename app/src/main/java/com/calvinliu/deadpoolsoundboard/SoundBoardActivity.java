package com.calvinliu.deadpoolsoundboard;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SoundBoardActivity extends AppCompatActivity {

    Toolbar toolbar;

    ArrayList<SoundObject> soundList = new ArrayList<>();

    RecyclerView SoundView;
    SoundBoardRecyclerAdapter SoundAdapter = new SoundBoardRecyclerAdapter(soundList);
    RecyclerView.LayoutManager SoundLayoutManager;

    private View mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soundboard);

        mLayout = findViewById(R.id.activity_soundboard);

        toolbar = (Toolbar) findViewById(R.id.soundboard_toolbar);
        setSupportActionBar(toolbar);

        List<String> nameList = Arrays.asList(getResources().getStringArray(R.array.soundNames));

        SoundObject[] soundItems = {new SoundObject(nameList.get(0), R.raw.blurry_line),
                new SoundObject(nameList.get(1), R.raw.brown_pants),
                new SoundObject(nameList.get(2), R.raw.cancer_everywhere),
                new SoundObject(nameList.get(3), R.raw.cancer_in_spanish),
                new SoundObject(nameList.get(4), R.raw.chimichanga),
                new SoundObject(nameList.get(5), R.raw.dont_swallow),
                new SoundObject(nameList.get(6), R.raw.fancy_red_suit),
                new SoundObject(nameList.get(7), R.raw.feel_just_like_a_little_girl),
                new SoundObject(nameList.get(8), R.raw.make_a_difference),
                new SoundObject(nameList.get(9), R.raw.maximum_effort),
                new SoundObject(nameList.get(10), R.raw.nothing_we_dont_share),
                new SoundObject(nameList.get(11), R.raw.pickup_line),
                new SoundObject(nameList.get(12), R.raw.real_pain),
                new SoundObject(nameList.get(13), R.raw.shoot_baby),
                new SoundObject(nameList.get(14), R.raw.stove_on),
                new SoundObject(nameList.get(15), R.raw.today),
                new SoundObject(nameList.get(16), R.raw.touching_myself),
                new SoundObject(nameList.get(17), R.raw.very_own_movie)};

        soundList.addAll(Arrays.asList(soundItems));

        SoundView = (RecyclerView) findViewById(R.id.soundboardRecyclerView);

        SoundLayoutManager = new GridLayoutManager(this, 3);

        SoundView.setLayoutManager(SoundLayoutManager);

        SoundView.setAdapter(SoundAdapter);

        requestPermissions();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        EventHandlerClass.releaseMediaPlayer();
    }

    private void requestPermissions(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }

            if (!Settings.System.canWrite(this)) {

                Snackbar.make(mLayout, "The app needs access to your settings", Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Context context = v.getContext();
                        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        intent.setData(Uri.parse("package:" + context.getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }).show();
            }
        }
    }
}
