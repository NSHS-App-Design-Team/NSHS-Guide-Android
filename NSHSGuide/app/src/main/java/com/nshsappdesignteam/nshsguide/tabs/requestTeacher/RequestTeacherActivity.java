package com.nshsappdesignteam.nshsguide.tabs.requestTeacher;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.nshsappdesignteam.nshsguide.R;
import com.nshsappdesignteam.nshsguide.util.Internet;

public class RequestTeacherActivity extends AppCompatActivity
{
	private TextInputEditText firstNameText;
	private TextInputEditText lastNameText;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_request_teacher);

		//display the back arrow (up enabled) at the top left corner (home)
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle(R.string.tab_request_teacher);
		setStatusBarColor();

		attachViewsToVars();
	}
	private void setStatusBarColor()
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
			getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
	}
	private void attachViewsToVars()
	{
		firstNameText = (TextInputEditText) findViewById(R.id.firstNameText);
		lastNameText = (TextInputEditText) findViewById(R.id.lastNameText);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.send, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.sendButton:
				processSend();
				return true;
			case android.R.id.home:
				onBackPressed();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	private void processSend()
	{
		if (!requiredFieldsCompleted()) {
			Toast.makeText(this, R.string.toast_error_fields_incomplete, Toast.LENGTH_LONG).show();
			return;
		}
		if (Internet.SINGLETON.isDisconnected()) {
			Toast.makeText(this, R.string.toast_error_no_internet, Toast.LENGTH_LONG).show();
			return;
		}

		Internet.SINGLETON.sendTeacherFirstLastNameToDatabase(getFirstName(), getLastName());
		Toast.makeText(this, R.string.toast_teacher_request_sent, Toast.LENGTH_LONG).show();
		onBackPressed();
	}
	private boolean requiredFieldsCompleted()
	{
		return !getFirstName().isEmpty() && !getLastName().isEmpty();
	}
	private String getFirstName()
	{
		return getTextOfEditText(firstNameText);
	}
	private String getLastName()
	{
		return getTextOfEditText(lastNameText);
	}
	private String getTextOfEditText(TextInputEditText editText)
	{
		return editText.getText().toString();
	}
}
