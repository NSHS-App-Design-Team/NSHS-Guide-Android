package com.nshsappdesignteam.nshsguide.tabs.yourTeachers;

import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.nshsappdesignteam.nshsguide.R;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.OnVHLongClickListener;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.VHClickable;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.VHLongClickable;
import com.nshsappdesignteam.nshsguide.util.TeachersManager;

import java.util.ArrayList;
import java.util.List;

public class YourTeachersRecyclerAdapter extends Adapter<ListYourTeacherNoSubject> implements OnVHLongClickListener
{
	private List<TeacherYours> teachers = new ArrayList<>();
	private final YourTeachersFragment parent;

	public YourTeachersRecyclerAdapter(YourTeachersFragment parent)
	{
		this.parent = parent;
	}
	@Override
	public ListYourTeacherNoSubject onCreateViewHolder(ViewGroup viewGroup, int viewType)
	{
		LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
		if (viewType == YourTeacherListType.YourTeacherNoSubject.value)
			return new ListYourTeacherNoSubject(inflater.inflate(R.layout.list_your_saved_teacher_no_subject, viewGroup, false), this);
		else if (viewType == YourTeacherListType.YourTeacher.value)
			return new ListYourTeacher(inflater.inflate(R.layout.list_your_saved_teacher, viewGroup, false), this);

		Log.wtf("YourTeachersRecycler", "item type unimplemented");
		return null;
	}
	@Override
	public void onBindViewHolder(ListYourTeacherNoSubject holder, int position)
	{
		holder.updateContent(teachers.get(position));
	}
	@Override
	public void onViewAttachedToWindow(ListYourTeacherNoSubject holder)
	{
		if (parent.isActionModeHidden())
			if (holder.isSelected())
				holder.setSelected(false);
	}
	@Override
	public int getItemViewType(int position)
	{
		return teachers.get(position).getType().value;
	}
	@Override
	public int getItemCount()
	{
		return teachers.size();
	}
	public TeacherYours getTeacherAtPosition(int position)
	{
		return teachers.get(position);
	}

	/*
	PUBLIC METHODS
	 */
	public void setTeachers(List<TeacherYours> teachers)
	{
		if (this.teachers.containsAll(teachers))
			return;
		this.teachers = teachers;
		notifyDataSetChanged();
	}
	public void removeTeacher(TeacherYours teacher)
	{
		int position = teachers.indexOf(teacher);
		teachers.remove(position);
		removeTeacherFromSharedPref(teacher);
		notifyItemRemoved(position);
	}
	private void removeTeacherFromSharedPref(TeacherYours teacher)
	{
		teacher.remove();
		TeachersManager.SINGLETON.saveNewYourAbsentTeachers();
	}

	/*
	TO BE CALLED BY VIEW HOLDERS
	 */
	@Override
	public void onItemClick(int position, VHClickable v)
	{
		parent.onItemClick(position, v);
	}
	@Override
	public void onItemLongClick(int position, VHLongClickable v)
	{
		parent.showActionMode();
		parent.onItemClick(position, v);
	}
}
