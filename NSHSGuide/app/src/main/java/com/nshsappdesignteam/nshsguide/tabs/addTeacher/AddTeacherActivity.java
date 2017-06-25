package com.nshsappdesignteam.nshsguide.tabs.addTeacher;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.HashBiMap;
import com.google.common.eventbus.Subscribe;
import com.nshsappdesignteam.nshsguide.R;
import com.nshsappdesignteam.nshsguide.async.EventTeachersList;
import com.nshsappdesignteam.nshsguide.async.EventYourTeachersChanged;
import com.nshsappdesignteam.nshsguide.async.MyEventBus;
import com.nshsappdesignteam.nshsguide.helper.Block;
import com.nshsappdesignteam.nshsguide.helper.BlockImage;
import com.nshsappdesignteam.nshsguide.helper.clean.CleanActivity;
import com.nshsappdesignteam.nshsguide.tabs.yourTeachers.TeacherYours;
import com.nshsappdesignteam.nshsguide.tabs.yourTeachers.YourTeachersFragment;
import com.nshsappdesignteam.nshsguide.util.Color;
import com.nshsappdesignteam.nshsguide.util.Internet;
import com.nshsappdesignteam.nshsguide.util.Settings;
import com.nshsappdesignteam.nshsguide.util.TeachersManager;

import java.util.HashSet;
import java.util.Set;

public class AddTeacherActivity extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener, ChooseBlockDialogFragment.OnClickListener, DialogInterface.OnClickListener, CleanActivity
{
	private Toolbar toolbar;
	private LinearLayout lunchSelectionLayout;
	private BlockImage blockImage; //holds which block you have the teacherBuilder for
	private final HashBiMap<Integer, BlockImage> lunchNumImageForNum = HashBiMap.create(3);
	private final HashBiMap<Integer, BlockImage> blockNumImageForNum = HashBiMap.create(Block.BLOCK_NUMS.length);
	private AutoCompleteTextView teacherNameInputField;
	private TextInputEditText subjectInputField;
	private TextInputEditText roomNumInputField;
	private ImageView refreshTeachersButton;
	@Nullable
	private TeacherYours editedTeacher;
	private final TeacherYours.Builder teacherBuilder = new TeacherYours.Builder();
	private Set<String> teachersList;
	private boolean userEditedSomething = false;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_teacher);

		attachViewsToVars();
		setListeners();
		//display the x (up enabled) at the top left corner (home)
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		//check to see if this is actually a teacherBuilder the user wants to edit (if any info is in extras)
		Bundle extras = getIntent().getExtras();
		if (extras != null)
			showEditedTeacherFromExtras(extras);
		else
			showDefaultTeacher();

		MyEventBus.eventBus.register(this);
		sync();

		setEnterSharedElementCallback(new AddTeacherTransitionCallback(this));
	}
	public void attachViewsToVars()
	{
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		lunchSelectionLayout = (LinearLayout) findViewById(R.id.lunchSelectionLayout);
		blockImage = (BlockImage) findViewById(R.id.blockImage);
		blockNumImageForNum.put(1, (BlockImage) findViewById(R.id.block1Image));
		blockNumImageForNum.put(2, (BlockImage) findViewById(R.id.block2Image));
		blockNumImageForNum.put(3, (BlockImage) findViewById(R.id.block3Image));
		blockNumImageForNum.put(4, (BlockImage) findViewById(R.id.block4Image));
		lunchNumImageForNum.put(1, (BlockImage) findViewById(R.id.lunch1Image));
		lunchNumImageForNum.put(2, (BlockImage) findViewById(R.id.lunch2Image));
		lunchNumImageForNum.put(3, (BlockImage) findViewById(R.id.lunch3Image));
		teacherNameInputField = (AutoCompleteTextView) findViewById(R.id.teacherNameInputField);
		subjectInputField = (TextInputEditText) findViewById(R.id.subjectInputField);
		roomNumInputField = (TextInputEditText) findViewById(R.id.roomNumInputField);
		refreshTeachersButton = (ImageView) findViewById(R.id.refreshTeachersButton);
		teachersList = Settings.SINGLETON.getTeachersList();
	}
	public void setListeners()
	{
		blockImage.setOnClickListener(this);
		for (BlockImage blockNumImage : blockNumImageForNum.values())
			blockNumImage.setOnClickListener(this);
		for (BlockImage lunchNumImage : lunchNumImageForNum.values())
			lunchNumImage.setOnClickListener(this);
		teacherNameInputField.setAdapter(new TeacherAutoCompleteAdapter(this, teachersList));
		roomNumInputField.setOnEditorActionListener(this);
		refreshTeachersButton.setOnClickListener(this);
	}
	public void setUpRecycler(){}
	public void populateRecycler(){}
	private void showEditedTeacherFromExtras(Bundle extras)
	{
		editedTeacher = extras.getParcelable(YourTeachersFragment.EXTRAS_TEACHER_KEY);

		setBlockLetter(editedTeacher.blockLetter);
		setBlockNums(editedTeacher.blockNums, editedTeacher.blockLetter);
		setLunch(editedTeacher.lunch);
		teacherNameInputField.setText(editedTeacher.getName());
		teacherNameInputField.dismissDropDown();
		if (!editedTeacher.subject.isEmpty())
			subjectInputField.setText(editedTeacher.subject);
		if (!editedTeacher.roomNum.isEmpty())
			roomNumInputField.setText(editedTeacher.roomNum);
	}
	private void showDefaultTeacher()
	{
		String defaultBlockLetter = Block.BLOCK_LETTERS.get(0);
		setBlockLetter(defaultBlockLetter);
		setBlockNums(null, defaultBlockLetter);
	}
	private void sync()
	{
		if (Settings.SINGLETON.getSyncOn())
			Internet.SINGLETON.queryTeachersList();
	}
	private void saveTeacherIfConditionsAllow()
	{
		if (!userEditedSomething) {
			super.onBackPressed();
			return;
		}

		teacherBuilder.name = teacherNameInputField.getText().toString();
		teacherBuilder.subject = subjectInputField.getText().toString();
		teacherBuilder.roomNum = roomNumInputField.getText().toString();

		if (teacherBuilder.name.isEmpty()) {
			showBackPressCannotSaveDialog(R.string.dialog_message_no_teacher);
			return;
		}
		if (teacherBuilder.blockNums.isEmpty()) {
			showBackPressCannotSaveDialog(R.string.dialog_message_no_blocks_selected);
			return;
		}
		if (!teachersList.contains(teacherBuilder.name)) {
			String errorInfo = getString(R.string.dialog_message_teacher_nonexistent, teacherBuilder.name);
			showBackPressCannotSaveDialog(errorInfo);
			return;
		}

		removeEditedTeacher();
		teacherBuilder.build().save();
		TeachersManager.SINGLETON.saveNewYourAbsentTeachers();
		Toast.makeText(this, R.string.toast_saved_teacher, Toast.LENGTH_SHORT).show();
		MyEventBus.eventBus.post(new EventYourTeachersChanged(true));
		super.onBackPressed();
	}
	private void removeEditedTeacher()
	{
		if (editedTeacher != null)
			editedTeacher.remove();
	}

	/*
	SETTING FUNCTIONS THAT STORE INFO IN TEACHER_BUILDER & CHANGE UI
	*/
	private void setBlockLetter(String blockLetter)
	{
		teacherBuilder.blockLetter = blockLetter;
		blockImage.setText(blockLetter);
		blockImage.setBackgroundFromBlockLetter(blockLetter);
		changeColorsForBlockLetter(blockLetter);
	}
	private void changeColorsForBlockLetter(String blockLetter)
	{
		toolbar.setBackgroundResource(Color.colorGivenLetter(blockLetter));

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
		{
			getWindow().setNavigationBarColor(ContextCompat.getColor(this, Color.colorGivenLetter(blockLetter)));
			getWindow().setStatusBarColor(ContextCompat.getColor(this, Color.darkColorGivenLetter(blockLetter)));
		}
	}
	private void changeColorsToDefault()
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
			getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
	}
	private void toggleBlockNum(int blockNum)
	{
		if (teacherBuilder.blockNums.contains(blockNum))
		{
			//must wrap blockNum because remove() can be called using location
			teacherBuilder.blockNums.remove(Integer.valueOf(blockNum));
			blockNumImageForNum.get(blockNum).setFadeColor(true);
		}
		else
		{
			teacherBuilder.blockNums.add(blockNum);
			blockNumImageForNum.get(blockNum).setFadeColor(false);
		}
	}
	private void setBlockNums(@Nullable Set<Integer> blockNums, String blockLetter)
	{
		for (int blockNum : Block.BLOCK_NUMS)
		{
			BlockImage blockNumImage = blockNumImageForNum.get(blockNum);
			blockNumImage.setText(blockLetter + blockNum);
			blockNumImage.setBackgroundFromBlockLetter(blockLetter);
			//fade all by default
			blockNumImage.setFadeColor(true);
		}

		if (blockNums == null)
			return;
		teacherBuilder.blockNums = new HashSet<>(blockNums);  //lists are pointers, so create a new one instead of copying
		//enable all those specified
		for (int blockNum : blockNums)
			blockNumImageForNum.get(blockNum).setFadeColor(false);
	}
	private void toggleLunch()
	{
		if (teacherBuilder.blockNums.size() == 0) {
			deleteLunch();
			return;
		}

		String blockLetter = teacherBuilder.blockLetter;
		for (int blockNum : teacherBuilder.blockNums)
		{
			String block = blockLetter + blockNum;
			if (!Block.LUNCH_BLOCK_GIVEN_DAY.values().contains(block))
				continue;
			//only set new lunch if new blockNum contains lunch & there wasn't a lunch
			if (teacherBuilder.lunch != 0)
				return;

			if (editedTeacher != null && editedTeacher.lunch != 0)
				setLunch(editedTeacher.lunch);
			else
				//default to 1st lunch
				setLunch(1);

			//exit function so lunch isn't deleted
			return;
		}
		deleteLunch();
	}
	private void setLunch(int lunch)
	{
		if (lunch == 0)
			return;

		lunchSelectionLayout.setVisibility(View.VISIBLE);
		teacherBuilder.lunch = lunch;
		for (BlockImage lunchNumImage : lunchNumImageForNum.values())
			lunchNumImage.setFadeColor(true);
		lunchNumImageForNum.get(lunch).setFadeColor(false);
	}
	private void deleteLunch()
	{
		lunchSelectionLayout.setVisibility(View.GONE);
		for (BlockImage lunchNumImage : lunchNumImageForNum.values())
			lunchNumImage.setFadeColor(true);
		teacherBuilder.lunch = 0;
	}

	/*
	UI INTERACTION
	 */
	private void showNoTeachersDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.dialog_title_no_teachers_list);
		builder.setMessage(R.string.dialog_message_no_teachers_list);
		builder.setCancelable(true);
		builder.show();
	}
	private void showBackPressCannotSaveDialog(@StringRes int stringResId)
	{
		showBackPressCannotSaveDialog(getString(stringResId));
	}
	private void showBackPressCannotSaveDialog(String error)
	{
		String message = error + "\n" + getString(R.string.dialog_message_cannot_save);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.dialog_title_cannot_save);
		builder.setMessage(message);
		builder.setNegativeButton(android.R.string.cancel, null);
		builder.setPositiveButton(android.R.string.ok, this);
		builder.show();
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				saveTeacherIfConditionsAllow();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	@Override
	public void onBackPressed()
	{
		saveTeacherIfConditionsAllow();
	}
	@Override
	public void onClick(View v)
	{
		int id = v.getId();
		switch (id)
		{
			//for the block image showing which block the teacherBuilder is currently set to, open up a dialog to choose another block
			case R.id.blockImage:
				ChooseBlockDialogFragment chooseBlockDialogFragment = ChooseBlockDialogFragment.newInstance(teacherBuilder.blockLetter);
				chooseBlockDialogFragment.onClickListener = this;
				chooseBlockDialogFragment.show(getFragmentManager(), "chooseBlockDialogFragment");
				break;
			//for the images numbered 1~4, fade if selected (or do the opposite if already faded)
			case R.id.block1Image:
			case R.id.block2Image:
			case R.id.block3Image:
			case R.id.block4Image:
				toggleBlockNum(blockNumImageForNum.inverse().get(v));
				toggleLunch();
				break;
			case R.id.lunch1Image:
			case R.id.lunch2Image:
			case R.id.lunch3Image:
				BlockImage selectedLunchNumImage = (BlockImage) findViewById(id);
				setLunch(lunchNumImageForNum.inverse().get(selectedLunchNumImage));
				break;
			case R.id.refreshTeachersButton:
				Internet.SINGLETON.queryTeachersList();
				Toast.makeText(this, R.string.toast_downloading_teachers_list, Toast.LENGTH_LONG).show();
				break;
		}
		userEditedSomething = true;
	}
	@Override
	public void onDismissChooseBlockDialog(String blockLetter)
	{
		setBlockLetter(blockLetter);
		setBlockNums(teacherBuilder.blockNums, blockLetter);
		toggleLunch();
	}
	//called when "OK" is clicked when the user is asked whether he would like to exit without saving a teacher
	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		super.onBackPressed();
	}
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
	{
		userEditedSomething = true;
		if (teachersList.isEmpty())
			showNoTeachersDialog();
		//save on enter click
		if (actionId == EditorInfo.IME_ACTION_DONE)
			saveTeacherIfConditionsAllow();
		return true;
	}
	@Override
	public void onDestroy()
	{
		MyEventBus.eventBus.unregister(this);
		changeColorsToDefault();
		super.onDestroy();
	}
	@Subscribe
	public void teachersListUpdated(EventTeachersList eventTeachersList)
	{
		if (!eventTeachersList.successful())
			return;

		teachersList = Settings.SINGLETON.getTeachersList();
		teacherNameInputField.setAdapter(new TeacherAutoCompleteAdapter(this, teachersList));
	}
}
