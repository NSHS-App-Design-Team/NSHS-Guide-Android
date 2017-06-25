package com.nshsappdesignteam.nshsguide.tabs.tutorial;

import android.animation.ArgbEvaluator;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.nshsappdesignteam.nshsguide.R;
import com.nshsappdesignteam.nshsguide.helper.clean.CleanActivity;
import com.nshsappdesignteam.nshsguide.util.Settings;

import java.util.ArrayList;
import java.util.List;

public class TutorialActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener, CleanActivity
{
	private final List<ImageView> indicators = new ArrayList<>(4);
	private final int[] pagesLayoutIds = new int[] {
			R.layout.fragment_tutorial_p1,
			R.layout.fragment_tutorial_p2,
			R.layout.fragment_tutorial_p3
	};
	private final int[] colors = new int[] {
			R.color.purple,
			R.color.blue,
			R.color.cyan
	};
	private final ArgbEvaluator argbEvaluator = new ArgbEvaluator();
	private ImageView nextButton;
	private ViewPager viewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tutorial);

		attachViewsToVars();
		setListeners();
		setStatusBarColor();
		setUpViewPager();
	}
	public void attachViewsToVars()
	{
		indicators.add((ImageView) findViewById(R.id.scrollIndicator1));
		indicators.add((ImageView) findViewById(R.id.scrollIndicator2));
		indicators.add((ImageView) findViewById(R.id.scrollIndicator3));
		nextButton = (ImageView) findViewById(R.id.nextButton);
		viewPager = (ViewPager) findViewById(R.id.viewPager);
	}
	public void setListeners()
	{
		nextButton.setOnClickListener(this);
	}
	public void setUpRecycler(){}
	public void populateRecycler(){}
	private void setStatusBarColor()
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
	}
	private void setUpViewPager()
	{
		TutorialPagerAdapter pagerAdapter = new TutorialPagerAdapter(getSupportFragmentManager(), pagesLayoutIds);
		viewPager.setAdapter(pagerAdapter);
		viewPager.addOnPageChangeListener(this);
		setCurrentPage(0);
	}
	//note: pageNum starts at 0
	private void setCurrentPage(int pageNum)
	{
		for (ImageView indicator : indicators)
			indicator.setSelected(false);
		indicators.get(pageNum).setSelected(true);
	}
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.nextButton:
				incrementPage();
				break;
		}
	}
	private void incrementPage()
	{
		if (viewPager.getCurrentItem() + 1 == indicators.size())
			exitTutorial();
		else
			viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
	}
	private void exitTutorial()
	{
		Settings.SINGLETON.setFirstRunFalse();
		finish();
	}
	@Override
	public void onBackPressed(){}  //disable back press

	/*
	VIEW PAGER
	 */
	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
	{
		setCurrentPage(position);
		findBgColorFromPositionAndOffset(position, positionOffset);
	}
	private void findBgColorFromPositionAndOffset(int position, float positionOffset)
	{
		int color;
		if (position < colors.length - 1)
			color = (int) argbEvaluator.evaluate(positionOffset, ContextCompat.getColor(this, colors[position]), ContextCompat.getColor(this, colors[position + 1]));
		else
			color = ContextCompat.getColor(this, colors[position]);
		viewPager.setBackgroundColor(color);
	}
	@Override
	public void onPageSelected(int position)
	{
		setCurrentPage(position);
	}
	@Override
	public void onPageScrollStateChanged(int state) {}
}