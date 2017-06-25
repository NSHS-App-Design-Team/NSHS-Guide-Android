package com.nshsappdesignteam.nshsguide.tabs.addTeacher;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;

import com.google.common.collect.ImmutableMap;
import com.nshsappdesignteam.nshsguide.R;
import com.nshsappdesignteam.nshsguide.helper.Block;
import com.nshsappdesignteam.nshsguide.helper.BlockImage;

public class ChooseBlockDialogFragment extends DialogFragment implements View.OnClickListener
{
	//MUST be set by parent
	public OnClickListener onClickListener;
	private static final ImmutableMap<Integer, String> BLOCK_LETTER_FOR_ID = ImmutableMap.<Integer, String>builder()
			.put(R.id.aBlockImage, Block.BLOCK_LETTERS.get(0))
			.put(R.id.bBlockImage, Block.BLOCK_LETTERS.get(1))
			.put(R.id.cBlockImage, Block.BLOCK_LETTERS.get(2))
			.put(R.id.dBlockImage, Block.BLOCK_LETTERS.get(3))
			.put(R.id.eBlockImage, Block.BLOCK_LETTERS.get(4))
			.put(R.id.fBlockImage, Block.BLOCK_LETTERS.get(5))
			.put(R.id.gBlockImage, Block.BLOCK_LETTERS.get(6))
			.put(R.id.hrBlockImage, Block.BLOCK_LETTERS.get(7))
			.put(R.id.jBlockImage, Block.BLOCK_LETTERS.get(8))
			.build();    //clickable letters in dialog
	private static final String NEW_INSTANCE_CURRENT_BLOCK_LETTER_KEY = "currentBlockLetter";

	public static ChooseBlockDialogFragment newInstance(String currentBlockLetter)
	{
		Bundle args = new Bundle();
		args.putString(NEW_INSTANCE_CURRENT_BLOCK_LETTER_KEY, currentBlockLetter);
		ChooseBlockDialogFragment fragment = new ChooseBlockDialogFragment();
		fragment.setArguments(args);
		return fragment;
	}
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		String currentBlockLetter = getArguments().getString(NEW_INSTANCE_CURRENT_BLOCK_LETTER_KEY);
		//let our dialog show dialog_pick_block layout
		View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_pick_block, null);

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
		dialogBuilder.setTitle(R.string.dialog_title_pick_block);
		dialogBuilder.setView(dialogView);

		for (ImmutableMap.Entry<Integer, String> entry : BLOCK_LETTER_FOR_ID.entrySet())
		{
			int blockSelectableID = entry.getKey();
			String blockLetter = entry.getValue();

			//give every single button in the dialog access to our blockClickListener
			BlockImage blockSelectable = (BlockImage) dialogView.findViewById(blockSelectableID);
			blockSelectable.setOnClickListener(this);

			//fade the bg of the blockSelectable that is currently chosen & disable it
			if (currentBlockLetter.equals(blockLetter))
			{
				blockSelectable.setFadeColor(true);
				blockSelectable.setEnabled(false);
			}
		}

		return dialogBuilder.create();
	}
	@Override
	public void onClick(View v)
	{
		int id = v.getId();

		switch (id)
		{
			case R.id.aBlockImage:
			case R.id.bBlockImage:
			case R.id.cBlockImage:
			case R.id.dBlockImage:
			case R.id.eBlockImage:
			case R.id.fBlockImage:
			case R.id.gBlockImage:
			case R.id.hrBlockImage:
			case R.id.jBlockImage:
				String blockLetter = BLOCK_LETTER_FOR_ID.get(id);
				dismiss();
				onClickListener.onDismissChooseBlockDialog(blockLetter);
				break;
		}
	}

	public interface OnClickListener
	{
		void onDismissChooseBlockDialog(String blockLetter);
	}
}
