package com.nshsappdesignteam.nshsguide.tabs.absentTeachers;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.nshsappdesignteam.nshsguide.R;
import com.nshsappdesignteam.nshsguide.helper.Block;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.HasInfo;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.HasType;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.OnVHClickListener;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.VHClickUpdatable;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.VHClickable;
import com.nshsappdesignteam.nshsguide.util.TeachersManager;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

public class AbsentTeachersRecyclerAdapter extends RecyclerView.Adapter<VHClickUpdatable<AbsentTeacherListType>> implements OnVHClickListener
{
	private List<HasType<AbsentTeacherListType>> listInfo = new ArrayList<>();
	private final List<Block> blocks;
	private final Context context;

	public AbsentTeachersRecyclerAdapter(Context context)
	{
		this.context = context;
		blocks = TeachersManager.SINGLETON.blocksForDay(LocalDate.now());
	}
	@Override
	public VHClickUpdatable<AbsentTeacherListType> onCreateViewHolder(ViewGroup parent, int viewType)
	{
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		//inflate correct layout based on what type of data this row holds (can't use switch as getValue() "isn't a constant")
		if (viewType == AbsentTeacherListType.Subheader.value)
			return new ListSubheader(inflater.inflate(R.layout.list_subheader, parent, false), this);
		else if (viewType == AbsentTeacherListType.YourTeacher.value)
			return new ListYourAbsentTeacher(inflater.inflate(R.layout.list_block_with_time, parent, false), this);
		else if (viewType == AbsentTeacherListType.YourTeacherNoInfo.value)
			return new ListYourAbsentTeacherNoInfo(inflater.inflate(R.layout.list_block_with_time_no_info, parent, false), this);
		else if (viewType == AbsentTeacherListType.OtherTeacher.value)
			return new ListOtherTeacher(inflater.inflate(R.layout.list_default_absent_teacher, parent, false), blocks, this, context);
		else if (viewType == AbsentTeacherListType.OtherTeacherNoInfo.value)
			return new ListOtherTeacherNoInfo(inflater.inflate(R.layout.list_default_absent_teacher_no_info, parent, false), blocks, this, context);
		else if (viewType == AbsentTeacherListType.Announcement.value)
			return new ListAnnouncement(inflater.inflate(R.layout.list_default, parent, false), this);

		Log.wtf("AbsentTeacherRecycler", "item type unimplemented");
		return null;
	}
	@Override
	public void onBindViewHolder(VHClickUpdatable<AbsentTeacherListType> holder, int position)
	{
		holder.updateContent(listInfo.get(position));
	}
	@Override
	public int getItemCount()
	{
		return listInfo.size();
	}
	@Override
	public int getItemViewType(int position)
	{
		return listInfo.get(position).getType().value;
	}
	@Override
	public void onItemClick(int position, VHClickable vhClickable)
	{
		HasType<AbsentTeacherListType> item = listInfo.get(position);
		if (item.getType() == AbsentTeacherListType.Subheader)
			return;
		//if what you clicked contains info, open a dialog
		HasInfo infoContainer = (HasInfo) item;
		if (infoContainer.getInfo().isEmpty())
			return;

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		dialogBuilder.setTitle(R.string.dialog_title_info);
		dialogBuilder.setMessage(infoContainer.getInfo());
		dialogBuilder.setCancelable(true);
		dialogBuilder.show();
	}

	/*
	PUBLIC METHODS
	 */
	public void setListInfo(List<HasType<AbsentTeacherListType>> listInfo)
	{
		if (this.listInfo.containsAll(listInfo))
			return;
		this.listInfo = listInfo;
		notifyDataSetChanged();
	}
}
