package com.nshsappdesignteam.nshsguide.helper.animation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.widget.TextView;

/**
 * This Drawable is used to scale the start and end bitmaps and switch between them
 * at the appropriate progress.
 */
public class SwitchBitmapDrawable extends Drawable
{
	private final TextView view;
	private final int horizontalGravity;
	private final int verticalGravity;
	private final Bitmap startBitmap;
	private final Bitmap endBitmap;
	private final Paint paint = new Paint();
	private final float startFontSize;
	private final float endFontSize;
	private final float startWidth;
	private final float endWidth;
	private float fontSize;
	private float left;
	private float top;
	private float right;
	private float bottom;
	private int textColor;

	public SwitchBitmapDrawable(TextView view, int gravity, Bitmap startBitmap, float startFontSize, float startWidth, Bitmap endBitmap, float endFontSize, float endWidth)
	{
		this.view = view;
		this.horizontalGravity = gravity & Gravity.HORIZONTAL_GRAVITY_MASK;
		this.verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;
		this.startBitmap = startBitmap;
		this.endBitmap = endBitmap;
		this.startFontSize = startFontSize;
		this.endFontSize = endFontSize;
		this.startWidth = startWidth;
		this.endWidth = endWidth;
	}

	@Override
	public void invalidateSelf()
	{
		super.invalidateSelf();
		view.invalidate();
	}

	/**
	 * Sets the font size that the text should be displayed at.
	 *
	 * @param fontSize The font size in pixels of the scaled bitmap text.
	 */
	public void setFontSize(float fontSize)
	{
		this.fontSize = fontSize;
		invalidateSelf();
	}

	/**
	 * Sets the color of the text to be displayed.
	 *
	 * @param textColor The color of the text to be displayed.
	 */
	public void setTextColor(int textColor)
	{
		this.textColor = textColor;
		setColorFilter(textColor, PorterDuff.Mode.SRC_IN);
		invalidateSelf();
	}

	/**
	 * Sets the left side of the text. This should be the same as the left padding.
	 *
	 * @param left The left side of the text in pixels.
	 */
	public void setLeft(float left)
	{
		this.left = left;
		invalidateSelf();
	}

	/**
	 * Sets the top of the text. This should be the same as the top padding.
	 *
	 * @param top The top of the text in pixels.
	 */
	public void setTop(float top)
	{
		this.top = top;
		invalidateSelf();
	}

	/**
	 * Sets the right of the drawable.
	 *
	 * @param right The right pixel of the drawn area.
	 */
	public void setRight(float right)
	{
		this.right = right;
		invalidateSelf();
	}

	/**
	 * Sets the bottom of the drawable.
	 *
	 * @param bottom The bottom pixel of the drawn area.
	 */
	public void setBottom(float bottom)
	{
		this.bottom = bottom;
		invalidateSelf();
	}

	/**
	 * @return The left side of the text.
	 */
	public float getLeft()
	{
		return left;
	}

	/**
	 * @return The top of the text.
	 */
	public float getTop()
	{
		return top;
	}

	/**
	 * @return The right side of the text.
	 */
	public float getRight()
	{
		return right;
	}

	/**
	 * @return The bottom of the text.
	 */
	public float getBottom()
	{
		return bottom;
	}

	/**
	 * @return The font size of the text in the displayed bitmap.
	 */
	public float getFontSize()
	{
		return fontSize;
	}

	/**
	 * @return The color of the text being displayed.
	 */
	public int getTextColor()
	{
		return textColor;
	}

	@Override
	public void draw(Canvas canvas)
	{
		int saveCount = canvas.save();
		// The threshold changes depending on the target font sizes. Because scaled-up
		// fonts look bad, we want to switch when closer to the smaller font size. This
		// algorithm ensures that null bitmaps (font size = 0) are never used.
		final float threshold = startFontSize / (startFontSize + endFontSize);
		final float fontSize = getFontSize();
		final float progress = (fontSize - startFontSize) / (endFontSize - startFontSize);

		// The drawn text width is a more accurate scale than font size. This avoids
		// jump when switching bitmaps.
		final float expectedWidth = TransitionFunctions.interpolate(startWidth, endWidth, progress);
		if (progress < threshold)
		{
			// draw start bitmap
			final float scale = expectedWidth / startWidth;
			float tx = getTranslationPoint(horizontalGravity, left, right, startBitmap.getWidth(), scale);
			float ty = getTranslationPoint(verticalGravity, top, bottom, startBitmap.getHeight(), scale);
			canvas.translate(tx, ty);
			canvas.scale(scale, scale);
			canvas.drawBitmap(startBitmap, 0, 0, paint);
		}
		else
		{
			// draw end bitmap
			final float scale = expectedWidth / endWidth;
			float tx = getTranslationPoint(horizontalGravity, left, right, endBitmap.getWidth(), scale);
			float ty = getTranslationPoint(verticalGravity, top, bottom, endBitmap.getHeight(), scale);
			canvas.translate(tx, ty);
			canvas.scale(scale, scale);
			canvas.drawBitmap(endBitmap, 0, 0, paint);
		}
		canvas.restoreToCount(saveCount);
	}

	@Override
	public void setAlpha(int alpha)
	{
	}

	@Override
	public void setColorFilter(ColorFilter colorFilter)
	{
		paint.setColorFilter(colorFilter);
	}

	@Override
	public int getOpacity()
	{
		return PixelFormat.TRANSLUCENT;
	}

	private float getTranslationPoint(int gravity, float start, float end, float dim, float scale)
	{
		switch (gravity)
		{
			case Gravity.CENTER_HORIZONTAL:
			case Gravity.CENTER_VERTICAL:
				return ((start + end) - (dim * scale)) / 2f;
			case Gravity.RIGHT:
			case Gravity.BOTTOM:
				return end - (dim * scale);
			case Gravity.LEFT:
			case Gravity.TOP:
			default:
				return start;
		}
	}
}