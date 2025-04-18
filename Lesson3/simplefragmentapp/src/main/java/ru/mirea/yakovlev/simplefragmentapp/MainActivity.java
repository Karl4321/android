package ru.mirea.yakovlev.simplefragmentapp;

import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity {
    private Fragment fragment1, fragment2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            fragment1 = new FirstFragment();
            fragment2 = new SecondFragment();

            FragmentManager fragmentManager = getSupportFragmentManager();
            Button btnFirstFragment = (Button) findViewById(R.id.btnFirstFragment);
            btnFirstFragment.setOnClickListener(v -> {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, fragment1)
                        .commit();
            });

            Button btnSecondFragment = (Button) findViewById(R.id.btnSecondFragment);
            btnSecondFragment.setOnClickListener(v -> {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, fragment2)
                        .commit();
            });
        }
    }
}