package com.nshsappdesignteam.nshsguide.tabs.feedback;

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
import com.nshsappdesignteam.nshsguide.util.Settings;

public class FeedbackActivity extends AppCompatActivity
{
	private TextInputEditText nameText;
	private TextInputEditText emailText;
	private TextInputEditText feedbackText;
	private static final String SECRET_CODE_NAME = "hoover";
	private static final String SECRET_CODE_FEEDBACK = "hawley smoot";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);

		//display the back arrow (up enabled) at the top left corner (home)
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle(R.string.tab_feedback);
		setStatusBarColor();

		attachViewsToVars();
		setSavedNameAndEmail();
	}
	private void setStatusBarColor()
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
			getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
	}
	private void attachViewsToVars()
	{
		nameText = (TextInputEditText) findViewById(R.id.nameText);
		emailText = (TextInputEditText) findViewById(R.id.emailText);
		feedbackText = (TextInputEditText) findViewById(R.id.feedbackText);
	}
	private void setSavedNameAndEmail()
	{
		String name = Settings.SINGLETON.getName();
		String email = Settings.SINGLETON.getEmail();
		if (!name.isEmpty())
			nameText.setText(name);
		if (!email.isEmpty())
			emailText.setText(email);
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
		if (isSecretCode()) {
			Settings.SINGLETON.setDeveloperModeOn();
			Toast.makeText(this, R.string.toast_developer_mode_on_message, Toast.LENGTH_LONG).show();
			showRegID();
			return;
		}
		if (Internet.SINGLETON.isDisconnected()) {
			Toast.makeText(this, R.string.toast_error_no_internet, Toast.LENGTH_LONG).show();
			return;
		}

		Settings.SINGLETON.setName(getName());
		Settings.SINGLETON.setEmail(getEmail());

		Internet.SINGLETON.sendNameEmailFeedbackToDatabase(getName(), getEmail(), getFeedback());
		Toast.makeText(this, R.string.toast_feedback_sent, Toast.LENGTH_LONG).show();
		onBackPressed();
	}
	private boolean isSecretCode()
	{
		return getName().equalsIgnoreCase(SECRET_CODE_NAME) && getFeedback().equalsIgnoreCase(SECRET_CODE_FEEDBACK);
	}
	private boolean requiredFieldsCompleted()
	{
		return !getName().isEmpty() && !getFeedback().isEmpty();
	}
	private void showRegID()
	{
		String regID = Settings.SINGLETON.getRegID();

		if (regID.isEmpty())
		{
			Internet.SINGLETON.getRegIdIfNotRegistered(false);
			regID = Settings.SINGLETON.getRegID();

			if (regID.isEmpty())
			{
				regID = "Couldn't retrieve regID";
			}
		}

		feedbackText.setText(regID);
	}
	private String getName()
	{
		return getTextOfEditText(nameText);
	}
	private String getEmail()
	{
		return getTextOfEditText(emailText);
	}
	private String getFeedback()
	{
		return getTextOfEditText(feedbackText);
	}
	private String getTextOfEditText(TextInputEditText editText)
	{
		return editText.getText().toString();
	}
}
