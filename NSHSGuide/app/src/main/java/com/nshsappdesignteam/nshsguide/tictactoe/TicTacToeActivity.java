package com.nshsappdesignteam.nshsguide.tictactoe;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.nshsappdesignteam.nshsguide.R;

public class TicTacToeActivity extends AppCompatActivity implements View.OnClickListener
{
	private BiMap<ImageView, Coordinate> coordinateForSquare = HashBiMap.create(9);
	private FloatingActionButton fab;
	private TextView statusText;
	private Machine machine;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tictactoe);
		//statusText = (TextView) findViewById(R.id.nextPlayer);
		//fab = (FloatingActionButton) findViewById(R.id.fab);
		findTictactoeViews();
		setListeners();
		ImageView square = coordinateForSquare.inverse().get(getSavedCoordinate());
		square.setBackgroundResource(R.drawable.tictactoe_x_128dp);
		machine = new Machine(Symbol.X, this);
	}
	private void findTictactoeViews()
	{
		View row0Layout = findViewById(R.id.row0Layout);
		View row1Layout = findViewById(R.id.row1Layout);
		View row2Layout = findViewById(R.id.row2Layout);
		View[] rowLayouts = new View[]{row0Layout, row1Layout, row2Layout};
		for (int row = 2; row >= 0; row--)
		{
			ImageView column0Text = (ImageView) rowLayouts[row].findViewById(R.id.column0Image);
			ImageView column1Text = (ImageView) rowLayouts[row].findViewById(R.id.column1Image);
			ImageView column2Text = (ImageView) rowLayouts[row].findViewById(R.id.column2Image);
			coordinateForSquare.put(column0Text, new Coordinate(row, 0));
			coordinateForSquare.put(column1Text, new Coordinate(row, 1));
			coordinateForSquare.put(column2Text, new Coordinate(row, 2));
		}
	}
	private void setListeners()
	{
		for (ImageView square : coordinateForSquare.keySet())
			square.setOnClickListener(this);
	}
	@Override
	public void onClick(View v)
	{
		Coordinate coorClicked = coordinateForSquare.get(v);
		if (!machine.putMoveAsPlayer(coorClicked))
			return;

		v.setBackgroundResource(R.drawable.tictactoe_x_128dp);
		machine.tryMoves();
		Log.i("TicTacToeActivity", "View clicked");
		saveCoordinate(coorClicked);
	}
	public void showMachineMove(Coordinate machineMove)
	{
		ImageView square = coordinateForSquare.inverse().get(machineMove);
		square.setBackgroundResource(R.drawable.tictactoe_o_128dp);
		Log.i("TicTacToeActivity", "Machine moved");
	}
	private void saveCoordinate(Coordinate c)
	{
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt("row", c.row);
		editor.putInt("column", c.column);
		editor.apply();
	}
	private Coordinate getSavedCoordinate()
	{
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		int row = sharedPreferences.getInt("row", 1);
		int col = sharedPreferences.getInt("column", 1);
		return new Coordinate(row, col);
	}
}
