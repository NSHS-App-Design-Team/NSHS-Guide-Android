package com.nshsappdesignteam.nshsguide.tabs.addTeacher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.nshsappdesignteam.nshsguide.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TeacherAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable
{
	//keeps context of an Activity so it can use getSystemService
	private final Context context;
	private final Set<String> allTeachers;
	//what AutoCompleteTextView uses to find the names we want
	private final Filter nameFilter = new Filter()
	{
		@Override
		protected FilterResults performFiltering(CharSequence constraint)
		{
			//things have to be returned as FilterResults, so put all the suggested allTeachers we have in here
			FilterResults results = new FilterResults();

			if (constraint != null)
			{
				List<String> suggestedTeachers = new ArrayList<>();
				for (String teacher : allTeachers)
				{
					//only suggest a teacher if the teacher's last name STARTS WITH whatever the user is typing
					if (teacher.toLowerCase().startsWith(constraint.toString().toLowerCase()))
						suggestedTeachers.add(teacher);
				}
				Collections.sort(suggestedTeachers);
				results.values = suggestedTeachers;
				results.count = suggestedTeachers.size();
			}
			return results;
		}
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results)
		{
			if (results != null && results.count > 0)
			{
				//clear the previous list, add to and show the new one
				clear();
				addAll((List<String>) results.values);
				notifyDataSetChanged();
			}
		}
	};

	public TeacherAutoCompleteAdapter(Context context, Set<String> teachers)
	{
		super(context, R.layout.list_default, new ArrayList<>(teachers));
		this.context = context;
		allTeachers = new HashSet<>(teachers);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView;
		//check to see if rowView can be reused (it isn't null) or we need to recreate it
		if (convertView != null)
			rowView = convertView;
		else
			rowView = inflater.inflate(R.layout.list_default, parent, false);

		TextView autoCompleteListTextView = (TextView) rowView.findViewById(R.id.someText);
		//use getItem instead of allTeachers.get because we want the SUGGESTED results
		autoCompleteListTextView.setText(getItem(position));

		return rowView;
	}
	@Override
	public Filter getFilter()
	{
		return nameFilter;
	}
}
