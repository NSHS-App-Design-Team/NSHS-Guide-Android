package com.nshsappdesignteam.nshsguide.tabs.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.util.AttributeSet;

/**
 * A {@link Preference} that displays a list of entries as
 * a dialog.
 * <p/>
 * This preference will store an int into the SharedPreferences. This string will be the value
 * from the {@link #setEntryValues(int[])} array.
 *
 * @attr ref android.R.styleable#ListPreference_entries
 * @attr ref android.R.styleable#ListPreference_entryValues
 */

public class IntListPreference extends DialogPreference
{
	private final static String ANDROID_NS = "http://schemas.android.com/apk/res/android";

	private CharSequence[] mEntries;
	private int[] mEntryValues;
	private int mValue;
	private String mSummary;
	int mClickedDialogEntryIndex;

	public IntListPreference(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		Resources res = context.getResources();

		int entriesResId = attrs.getAttributeResourceValue(ANDROID_NS, "entries", 0);
		mEntries = res.getTextArray(entriesResId);
		int valuesResId = attrs.getAttributeResourceValue(ANDROID_NS, "entryValues", 0);
		mEntryValues = res.getIntArray(valuesResId);
		int summaryResId = attrs.getAttributeResourceValue(ANDROID_NS, "summary", 0);
		if (summaryResId != 0)
		{
			mSummary = res.getString(summaryResId);
		}
	}
	public IntListPreference(Context context)
	{
		this(context, null);
	}

	/*
	ENTRIES & ENTRY VALUES (text displayed on list & numbers that will be saved, both arrays in identical order)
	 */
	public void setEntries(CharSequence[] entries)
	{
		mEntries = entries;
	}
	public void setEntries(int entriesResId)
	{
		setEntries(getContext().getResources().getTextArray(entriesResId));
	}
	public CharSequence[] getEntries()
	{
		return mEntries;
	}
	public void setEntryValues(int[] entryValues)
	{
		mEntryValues = entryValues;
	}
	public void setEntryValues(int entryValuesResId)
	{
		setEntryValues(getContext().getResources().getIntArray(entryValuesResId));
	}
	public int[] getEntryValues()
	{
		return mEntryValues;
	}

	/**
	 * Returns the summary of this ListPreference. If the summary
	 * has a {@linkplain java.lang.String#format String formatting}
	 * marker in it (i.e. "%s" or "%1$s"), then the current entry
	 * value will be substituted in its place.
	 *
	 * @return the summary with appropriate string substitution
	 */
	@Override
	public CharSequence getSummary()
	{
		final CharSequence entry = getEntry();
		if (mSummary == null || entry == null)
		{
			return super.getSummary();
		}
		else
		{
			return String.format(mSummary, entry);
		}
	}

	/**
	 * Sets the summary for this Preference with a CharSequence.
	 * If the summary has a
	 * {@linkplain java.lang.String#format String formatting}
	 * marker in it (i.e. "%s" or "%1$s"), then the current entry
	 * value will be substituted in its place when it's retrieved.
	 *
	 * @param summary The summary for the preference.
	 */
	@Override
	public void setSummary(CharSequence summary)
	{
		super.setSummary(summary);
		if (summary == null && mSummary != null)
		{
			mSummary = null;
		}
		else if (summary != null && !summary.equals(mSummary))
		{
			mSummary = summary.toString();
		}
	}

	public void setValue(int value)
	{
		mValue = value;
		persistInt(value);
	}
	public int getValue()
	{
		return mValue;
	}
	/**
	 * Sets the value to the given index from the entry values.
	 *
	 * @param index The index of the value to set.
	 */
	public void setValueIndex(int index)
	{
		if (mEntryValues != null)
		{
			setValue(mEntryValues[index]);
		}
	}
	private int getValueIndex()
	{
		return getValueIndex(mValue);
	}
	/**
	 * Returns the index of the given value (in the entry values array).
	 *
	 * @param value The value whose index should be returned.
	 * @return The index of the value, or -1 if not found.
	 */
	public int getValueIndex(int value)
	{
		if (mEntryValues != null)
		{
			for (int i = mEntryValues.length - 1; i >= 0; i--)
			{
				if (mEntryValues[i] == value)
				{
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * Returns the entry corresponding to the current value.
	 *
	 * @return The entry corresponding to the current value, or null.
	 */
	public CharSequence getEntry()
	{
		int index = getValueIndex();
		return index >= 0 && mEntries != null ? mEntries[index] : null;
	}

	@Override
	protected void onPrepareDialogBuilder(AlertDialog.Builder builder)
	{
		super.onPrepareDialogBuilder(builder);

		if (mEntries == null || mEntryValues == null)
		{
			throw new IllegalStateException("ListPreference requires an entries array and an entryValues array.");
		}

		mClickedDialogEntryIndex = getValueIndex();
		builder.setSingleChoiceItems(mEntries, mClickedDialogEntryIndex, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						mClickedDialogEntryIndex = which;

                        /*
                         * Clicking on an item simulates the positive button
                         * click, and dismisses the dialog.
                         */
						IntListPreference.this.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
						dialog.dismiss();
					}
				});

        /*
         * The typical interaction for list-based dialogs is to have
         * click-on-an-item dismiss the dialog instead of the user having to
         * press 'Ok'.
         */
		builder.setPositiveButton(null, null);
	}
	@Override
	protected void onDialogClosed(boolean positiveResult)
	{
		super.onDialogClosed(positiveResult);

		if (positiveResult && mClickedDialogEntryIndex >= 0 && mEntryValues != null)
		{
			int value = mEntryValues[mClickedDialogEntryIndex];
			if (callChangeListener(value))
			{
				setValue(value);
			}
		}
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index)
	{
		return a.getInteger(index, -1);
	}
	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue)
	{
		setValue(restoreValue ? getPersistedInt(mValue) : (Integer) defaultValue);
	}

	@Override
	protected Parcelable onSaveInstanceState()
	{
		final Parcelable superState = super.onSaveInstanceState();
		if (isPersistent())
		{
			// No need to save instance state since it's persistent
			return superState;
		}

		final SavedState myState = new SavedState(superState);
		myState.value = getValue();
		return myState;
	}
	@Override
	protected void onRestoreInstanceState(Parcelable state)
	{
		if (state == null || !state.getClass().equals(SavedState.class))
		{
			// Didn't save state for us in onSaveInstanceState
			super.onRestoreInstanceState(state);
			return;
		}

		SavedState myState = (SavedState) state;
		super.onRestoreInstanceState(myState.getSuperState());
		setValue(myState.value);
	}
	private static class SavedState extends BaseSavedState
	{
		int value;

		public SavedState(Parcel source)
		{
			super(source);
			value = source.readInt();
		}
		@Override
		public void writeToParcel(Parcel dest, int flags)
		{
			super.writeToParcel(dest, flags);
			dest.writeInt(value);
		}
		public SavedState(Parcelable superState)
		{
			super(superState);
		}
		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>()
		{
			@Override
			public SavedState createFromParcel(Parcel in)
			{
				return new SavedState(in);
			}

			@Override
			public SavedState[] newArray(int size)
			{
				return new SavedState[size];
			}
		};
	}
}