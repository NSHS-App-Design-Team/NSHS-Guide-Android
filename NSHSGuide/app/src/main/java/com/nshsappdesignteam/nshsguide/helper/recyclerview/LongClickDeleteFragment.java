package com.nshsappdesignteam.nshsguide.helper.recyclerview;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.nshsappdesignteam.nshsguide.R;

public abstract class LongClickDeleteFragment extends Fragment implements ActionMode.Callback
{
	protected boolean actionModeShowing = false;

	public void showActionMode()
	{
		if (actionModeShowing)
			return;
		((AppCompatActivity) getActivity()).startSupportActionMode(this);
	}
	public boolean isActionModeHidden()
	{
		return !actionModeShowing;
	}
	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu)
	{
		MenuInflater inflater = mode.getMenuInflater();
		inflater.inflate(R.menu.delete, menu);
		mode.setTitle(R.string.action_delete);
		actionModeShowing = true;
		return true;
	}
	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu)
	{
		return false;
	}
	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.delete:
				onDeleteClick();
				mode.finish();
				actionModeShowing = false;
				return true;
		}
		return false;
	}
	@Override
	public void onDestroyActionMode(ActionMode mode)
	{
		deselectAll();
		actionModeShowing = false;
	}
	protected abstract void onDeleteClick();
	protected abstract void deselectAll();
}
