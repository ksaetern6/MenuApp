package com.example.menuapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.menuapp.fragments.cameraFragment;
import com.example.menuapp.fragments.navBarFragment;


public class MainActivity extends AppCompatActivity {

    mainPageAdapter mPageAdapter;
    ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager_main);

        // view fragments
        mPageAdapter = new mainPageAdapter(getSupportFragmentManager());
        mPager = findViewById(R.id.view_pager);
        mPager.setAdapter(mPageAdapter);
        mPager.setCurrentItem(1);



    }

    public static class mainPageAdapter extends FragmentPagerAdapter {

        // BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT means the Activity state is reached after onResume is called
        public mainPageAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 1:
                    return cameraFragment.newInstance();
                case 0:
                    return navBarFragment.newInstance();
            }
            return null;
        }
    }

    /*
    @name:
    @desc:

    *Only called once
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()) {
            case R.id.action_favorite:
                Intent intent = new Intent(MainActivity.this, loginMain.class);
                startActivity(intent);
                break;
            case R.id.action_settings:
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show();
                break;
            default:
                // user's action was not recognized, invoke superclass to handle it.
                return super.onOptionsItemSelected(item);
        } //switch

        return true;
    }
}



