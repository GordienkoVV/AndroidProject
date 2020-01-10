package com.example.treking_gps;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.contentLayout)
    protected View contentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (getSupportFragmentManager().getFragments().isEmpty()) {
            Fragment fragment = new TrackingSettings();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.contentLayout, fragment, fragment.getClass().getSimpleName())
                    .commit();
        }
    }

}
