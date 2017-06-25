package com.nshsappdesignteam.nshsguide.tabs.addTeacher;

import android.content.Context;
import android.support.v4.app.SharedElementCallback;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.nshsappdesignteam.nshsguide.R;
import com.nshsappdesignteam.nshsguide.helper.BlockImage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddTeacherTransitionCallback extends SharedElementCallback
{
	private final Context context;

	public AddTeacherTransitionCallback(Context context)
	{
		this.context = context;
	}
	@Override
	public void onSharedElementStart(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots)
	{
		onSharedElementForStart(true, sharedElementNames, sharedElements);
	}
	@Override
	public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots)
	{
		onSharedElementForStart(false, sharedElementNames, sharedElements);
	}
	private void onSharedElementForStart(boolean start, List<String> sharedElementNames, List<View> sharedElements)
	{
		BlockImage blockImage = getBlockImage(sharedElementNames, sharedElements);
		blockImage.setTextSize(TypedValue.COMPLEX_UNIT_PX, getBlockImageTextSizeForStart(start));

		float blockNumImageTextSize = getBlockNumImageTextSizeForStart(start);
		Set<BlockImage> blockNumImages = getBlockNumImages(sharedElementNames, sharedElements);
		for (BlockImage blockNumImage : blockNumImages)
			blockNumImage.setTextSize(TypedValue.COMPLEX_UNIT_PX, blockNumImageTextSize);
	}
	private BlockImage getBlockImage(List<String> sharedElementNames, List<View> sharedElements)
	{
		String transitionName = context.getString(R.string.transition_target_block_image);
		int index = sharedElementNames.indexOf(transitionName);
		return (BlockImage) sharedElements.get(index);
	}
	private Set<BlockImage> getBlockNumImages(List<String> sharedElementNames, List<View> sharedElements)
	{
		Set<BlockImage> blockNumImages = new HashSet<>(4);
		for (int i = 1; i <= 4; i++)
			blockNumImages.add(getBlockNumImageForNum(i, sharedElementNames, sharedElements));
		return blockNumImages;
	}
	private BlockImage getBlockNumImageForNum(int num, List<String> sharedElementNames, List<View> sharedElements)
	{
		int transitionNameResId;
		switch (num)
		{
			case 1:
				transitionNameResId = R.string.transition_target_block_1_image;
				break;
			case 2:
				transitionNameResId = R.string.transition_target_block_2_image;
				break;
			case 3:
				transitionNameResId = R.string.transition_target_block_3_image;
				break;
			case 4:
				transitionNameResId = R.string.transition_target_block_4_image;
				break;
			default:
				transitionNameResId = 0;
				Log.wtf("AddTeacherTransition", "getBlockNumImageForNum() called with odd num: " + num);
		}

		String transitionName = context.getString(transitionNameResId);
		int index = sharedElementNames.indexOf(transitionName);
		return (BlockImage) sharedElements.get(index);
	}
	private float getBlockImageTextSizeForStart(boolean start)
	{
		int dimenId = start ? R.dimen.list_primary_text_in_image_size : R.dimen.add_teacher_block_text_size;
		return context.getResources().getDimensionPixelSize(dimenId);
	}
	private float getBlockNumImageTextSizeForStart(boolean start)
	{
		int dimenId = start ? R.dimen.list_small_block_text_size : R.dimen.add_teacher_block_num_text_size;
		return context.getResources().getDimensionPixelSize(dimenId);
	}
}
