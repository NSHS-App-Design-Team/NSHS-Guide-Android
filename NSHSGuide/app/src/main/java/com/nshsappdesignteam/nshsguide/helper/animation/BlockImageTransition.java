package com.nshsappdesignteam.nshsguide.helper.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.nshsappdesignteam.nshsguide.helper.BlockImage;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BlockImageTransition extends Transition
{
	private static final String FONT_SIZE = "BlockImageTransition:fontSize";
	private static final String DATA = "BlockImageTransition:data";
	//only font size change will trigger animation
	private static final String[] PROPERTIES = new String[]{FONT_SIZE};

	/**
	 * Constructor used from XML.
	 */
	public BlockImageTransition(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		addTarget(BlockImage.class);
	}
	public BlockImageTransition()
	{
		addTarget(BlockImage.class);
	}
	@Override
	public String[] getTransitionProperties()
	{
		return PROPERTIES;
	}
	@Override
	public void captureStartValues(TransitionValues transitionValues)
	{
		captureValues(transitionValues);
	}
	@Override
	public void captureEndValues(TransitionValues transitionValues)
	{
		captureValues(transitionValues);
	}
	private void captureValues(TransitionValues transitionValues)
	{
		if (!(transitionValues.view instanceof BlockImage))
			return;
		BlockImage blockImage = (BlockImage) transitionValues.view;
		float fontSize = blockImage.getTextSize();
		transitionValues.values.put(FONT_SIZE, fontSize);
		BlockImageTransitionData data = new BlockImageTransitionData(blockImage);
		transitionValues.values.put(DATA, data);
	}
	@Override
	public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues)
	{
		if (startValues == null || endValues == null)
			return null;

		final BlockImage blockImage = (BlockImage) endValues.view;

		float startFontSize = (Float) startValues.values.get(FONT_SIZE);
		final BlockImageTransitionData startData = (BlockImageTransitionData) startValues.values.get(DATA);
		// Capture the start bitmap -- we need to set the values to the start values first
		setBlockImageData(blockImage, startData, startFontSize);
		float startWidth = blockImage.getPaint().measureText(blockImage.getText().toString());
		Bitmap startBitmap = captureBitmap(blockImage);
		if (startBitmap == null)
			startFontSize = 0;

		float endFontSize = (Float) endValues.values.get(FONT_SIZE);
		final BlockImageTransitionData endData = (BlockImageTransitionData) endValues.values.get(DATA);
		// Set the values to the end values
		setBlockImageData(blockImage, endData, endFontSize);
		float endWidth = blockImage.getPaint().measureText(blockImage.getText().toString());
		// Capture the end bitmap
		Bitmap endBitmap = captureBitmap(blockImage);
		if (endBitmap == null)
			endFontSize = 0;

		if (startFontSize == 0 && endFontSize == 0)
			return null; // Can't animate null bitmaps

		// Set the colors of the TextView so that nothing is drawn.
		// Only draw the bitmaps in the overlay.
		blockImage.setTextColor(Color.TRANSPARENT);

		// Create the drawable that will be animated in the TextView's overlay.
		// Ensure that it is showing the start state now.
		final SwitchBitmapDrawable drawable = new SwitchBitmapDrawable(blockImage, startData.gravity,
				startBitmap, startFontSize, startWidth, endBitmap, endFontSize, endWidth);
		blockImage.getOverlay().add(drawable);

		// Properties: left, top, font size, text color
		PropertyValuesHolder leftProp = PropertyValuesHolder.ofFloat("left", startData.paddingLeft, endData.paddingLeft);
		PropertyValuesHolder topProp = PropertyValuesHolder.ofFloat("top", startData.paddingTop, endData.paddingTop);
		PropertyValuesHolder rightProp = PropertyValuesHolder.ofFloat("right",
				startData.width - startData.paddingRight, endData.width - endData.paddingRight);
		PropertyValuesHolder bottomProp = PropertyValuesHolder.ofFloat("bottom",
				startData.height - startData.paddingBottom, endData.height - endData.paddingBottom);
		PropertyValuesHolder fontSizeProp = PropertyValuesHolder.ofFloat("fontSize", startFontSize, endFontSize);
		final ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(drawable, leftProp, topProp, rightProp, bottomProp, fontSizeProp);

		final float finalFontSize = endFontSize;
		AnimatorListenerAdapter listener = new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationPause(Animator animation)
			{
				blockImage.setTextSize(TypedValue.COMPLEX_UNIT_PX, drawable.getFontSize());
				int paddingLeft = Math.round(drawable.getLeft());
				int paddingTop = Math.round(drawable.getTop());
				float fraction = animator.getAnimatedFraction();
				int paddingRight = Math.round(TransitionFunctions.interpolate(startData.paddingRight, endData.paddingRight, fraction));
				int paddingBottom = Math.round(TransitionFunctions.interpolate(startData.paddingBottom, endData.paddingBottom, fraction));
				blockImage.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
			}
			@Override
			public void onAnimationResume(Animator animation)
			{
				blockImage.setTextSize(TypedValue.COMPLEX_UNIT_PX, finalFontSize);
				blockImage.setPadding(endData.paddingLeft, endData.paddingTop, endData.paddingRight, endData.paddingBottom);
			}
			@Override
			public void onAnimationEnd(Animator animation)
			{
				blockImage.setTextColor(Color.WHITE);
				blockImage.getOverlay().remove(drawable);
			}
		};
		animator.addListener(listener);
		animator.addPauseListener(listener);
		return animator;
	}
	private static void setBlockImageData(BlockImage view, BlockImageTransitionData data, float fontSize)
	{
		view.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
		view.setPadding(data.paddingLeft, data.paddingTop, data.paddingRight, data.paddingBottom);
		view.setRight(view.getLeft() + data.width);
		view.setBottom(view.getTop() + data.height);
		view.setTextColor(data.textColor);
		int widthSpec = View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY);
		int heightSpec = View.MeasureSpec.makeMeasureSpec(view.getHeight(), View.MeasureSpec.EXACTLY);
		view.measure(widthSpec, heightSpec);
		view.layout(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
	}
	private static Bitmap captureBitmap(BlockImage blockImage)
	{
		Drawable background = blockImage.getBackground();
		blockImage.setBackground(null);
		int width = blockImage.getWidth() - blockImage.getPaddingLeft() - blockImage.getPaddingRight();
		int height = blockImage.getHeight() - blockImage.getPaddingTop() - blockImage.getPaddingBottom();
		if (width == 0 || height == 0)
			return null;
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.translate(-blockImage.getPaddingLeft(), -blockImage.getPaddingTop());
		blockImage.draw(canvas);
		blockImage.setBackground(background);
		return bitmap;
	}
}
