package com.xlh.krystal.littlejoker;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.xlh.krystal.littlejoker.fragment.Fragment_gif;
import com.xlh.krystal.littlejoker.fragment.Fragment_joke;
import com.xlh.krystal.littlejoker.fragment.Fragment_picture;
import com.xlh.krystal.littlejoker.fragment.Fragment_recommend;
import com.xlh.krystal.littlejoker.fragment.Fragment_video;

import java.util.ArrayList;
import java.util.List;

/**
 * 这里是显示所有内容的Activity
 */
public class ContentActivity extends AppCompatActivity {

    private RadioGroup content_rg;//RadioGroup
    private ViewPager content_vp;//viewpager
    private List<Fragment> lists = new ArrayList<>();//fragment集合
    private LinearLayout.LayoutParams params;//滑动条参数
    private View position_view;//这个是那个用来跟踪fragment滑动用的view
    private int screenWidth ;//这是获取的屏幕的宽度


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        init();
        initFragment();
        initParams();
        setAdapter();
        setListener();
    }

    /**
     * 设置适配器
     */
    private void setAdapter() {
        content_vp.setAdapter(new MyAdater(getSupportFragmentManager()));
    }

    /**
     * 这是viewpager的适配器
     */
    private class MyAdater extends FragmentPagerAdapter {

        public MyAdater(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return lists.get(position);
        }

        @Override
        public int getCount() {
            return lists.size();
        }
    }

    /**
     * 初始化fragment
     */
    private void initFragment() {
        Fragment_gif fragment_gif = new Fragment_gif();
        Fragment_joke fragment_joke = new Fragment_joke();
        Fragment_picture fragment_picture = new Fragment_picture();
        Fragment_recommend fragment_recommend = new Fragment_recommend();
        Fragment_video fragment_video = new Fragment_video();
        lists.add(fragment_recommend);
        lists.add(fragment_picture);
        lists.add(fragment_joke);
        lists.add(fragment_video);
        lists.add(fragment_gif);
    }

    /**
     * 初始化那个滑动条view 的参数
     */
    private void initParams() {
        WindowManager manager = (WindowManager) this.getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        params = new LinearLayout.LayoutParams(screenWidth/5,position_view.getLayoutParams().height);
        position_view.setLayoutParams(params);
}

    /**
     * 各种设置监听
     */
    private void setListener() {
        content_vp.addOnPageChangeListener(new MyScrollListener());
        content_rg.setOnCheckedChangeListener(new MyRGBtnListener());
    }

    /**
     * 这是那个radiogroup的监听
     */
    private class MyRGBtnListener implements RadioGroup.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            for (int i = 0; i < group.getChildCount() ; i++) {
                if (((RadioButton)group.getChildAt(i)).isChecked()){
                    content_vp.setCurrentItem(i);
                }
            }
        }
    }

    /**
     * 这个那个viewpager的滑动监听
     */
    private class MyScrollListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            params.leftMargin = (int)((position+positionOffset)*params.width);
            position_view.setLayoutParams(params);
        }

        @Override
        public void onPageSelected(int position) {
            RadioButton btn = (RadioButton) content_rg.getChildAt(position);
            btn.setChecked(true);

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    /**
     * 初始化view
     */
    private void init() {
        content_rg = (RadioGroup) findViewById(R.id.content_rg);
        content_vp = (ViewPager) findViewById(R.id.content_vp);
        position_view = findViewById(R.id.position_view);
    }
}
