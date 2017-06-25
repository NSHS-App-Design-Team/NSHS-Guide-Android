package com.nshsappdesignteam.nshsguide;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.nshsappdesignteam.nshsguide.tabs.absentTeachers.AbsentTeachersFragment;
import com.nshsappdesignteam.nshsguide.tabs.blocks.BlocksFragment;
import com.nshsappdesignteam.nshsguide.tabs.feedback.FeedbackActivity;
import com.nshsappdesignteam.nshsguide.tabs.requestTeacher.RequestTeacherActivity;
import com.nshsappdesignteam.nshsguide.tabs.settings.SettingsActivity;
import com.nshsappdesignteam.nshsguide.tabs.tutorial.TutorialActivity;
import com.nshsappdesignteam.nshsguide.tabs.yourTeachers.YourTeachersFragment;
import com.nshsappdesignteam.nshsguide.tictactoe.TicTacToeActivity;
import com.nshsappdesignteam.nshsguide.util.Internet;
import com.nshsappdesignteam.nshsguide.util.Notifications;
import com.nshsappdesignteam.nshsguide.util.Settings;
import com.nshsappdesignteam.nshsguide.util.Tabs;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener
{
	//navigation drawer
	private NavigationView navigationView;
	private DrawerLayout drawerLayout;
	public static final String START_WITH_FRAGMENT_KEY = "startWithFragment";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setTheme(R.style.AppTheme); //close splash screen
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setUpNavigationDrawer();
		openDefaultPage();

		Internet.SINGLETON.getRegIdIfNotRegistered(true);
		Notifications.SINGLETON.toggleBlocks();
		if (Settings.SINGLETON.getTeachersList().isEmpty())
			Internet.SINGLETON.queryTeachersList();

		checkShouldRunTutorial();
	}
	//uses intent from notifications to override "launcher intent"
	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);
		setIntent(intent);
		openDefaultPage();
	}
	private void openDefaultPage()
	{
		String savedLandingPage = getIntent().getStringExtra(START_WITH_FRAGMENT_KEY);
		int itemId;
		if (savedLandingPage == null)
			itemId = Settings.SINGLETON.getDefaultLandingPage();
		else
			itemId = Tabs.SINGLETON.tabIdFromSavedLandingPage(savedLandingPage);
		selectDrawerItem(itemId);
		navigationView.setCheckedItem(itemId);
	}
	private void checkShouldRunTutorial()
	{
		if (!Settings.SINGLETON.isFirstRun())
			return;

		Intent intent = new Intent(this, TutorialActivity.class);
		startActivity(intent);
	}
	//if back is pressed while nav drawer is open, close the nav drawer
	@Override
	public void onBackPressed()
	{
		if (drawerLayout.isDrawerOpen(GravityCompat.START))
		{
			drawerLayout.closeDrawers();
			return;
		}
		super.onBackPressed();
	}
	private void setUpNavigationDrawer()
	{
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_24dp);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
		navigationView = (NavigationView) findViewById(R.id.navigation);
		navigationView.setNavigationItemSelectedListener(this);
		navigationView.getHeaderView(0).setOnClickListener(this);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == android.R.id.home)
		{
			drawerLayout.openDrawer(GravityCompat.START);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public boolean onNavigationItemSelected(MenuItem menuItem)
	{
		selectDrawerItem(menuItem.getItemId());
		return true;
	}
	//what happens when the user clicks something in the navigation drawer
	private void selectDrawerItem(@IdRes int id)
	{
		Fragment fragmentToTransitionTo;
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		switch (id)
		{
			case R.id.tabAbsentTeachers:
				fragmentToTransitionTo = new AbsentTeachersFragment();
				break;
			case R.id.tabYourTeachers:
				fragmentToTransitionTo = new YourTeachersFragment();
				break;
			case R.id.tabBlocks:
				fragmentToTransitionTo = new BlocksFragment();
				break;
			case R.id.tabSettings:
				startActivity(SettingsActivity.class);
				return;
			case R.id.tabFeedback:
				startActivity(FeedbackActivity.class);
				return;
			case R.id.tabRequestTeacher:
				startActivity(RequestTeacherActivity.class);
				return;
			default:
				Log.wtf("MainActivity", "Unidentified nav drawer id");
				return;
		}
		transaction.replace(R.id.fragmentContainer, fragmentToTransitionTo);
		transaction.commit();

		drawerLayout.closeDrawers();
	}
	public void setToolbarToBlockMonthToolbar()
	{
		swapToolbarIds(R.id.appBarLayout, R.layout.toolbar_block_month);
		setTitle(R.string.tab_blocks);
	}
	public void setToolbarToNormal()
	{
		swapToolbarIds(R.id.appBarLayout, R.layout.toolbar);
	}
	private void swapToolbarIds(@IdRes int oldLayoutId, @LayoutRes int newLayoutId)
	{
		View oldLayout = findViewById(oldLayoutId);
		ViewGroup parent = (ViewGroup) oldLayout.getParent();
		int index = parent.indexOfChild(oldLayout);
		parent.removeView(oldLayout);

		View normalLayout = getLayoutInflater().inflate(newLayoutId, parent, false);
		parent.addView(normalLayout, index);

		setUpNavigationDrawer();
	}
	private void startActivity(Class className)
	{
		drawerLayout.closeDrawers();
		Intent intent = new Intent(this, className);
		startActivity(intent);
	}
	@Override
	public void onClick(View v)
	{
		Log.i("MainActivity", "Something clicked");
		Intent intent = new Intent(this, TicTacToeActivity.class);
		startActivity(intent);
	}
}
