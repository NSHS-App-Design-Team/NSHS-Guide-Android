package com.davidchu;

import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSNumber;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListParser;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.util.Pair;

public class Main
{
	public final static List<String> DAYS_OF_WEEK = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday");

	private static int normalBlock;
	private static int longBlock;
	private static int lunchBlock;
	private static int normalHR;
	private static int longHR;
	private static int normalJBlock;
	private static int shortJBlock;
	private static int passing;
	private static Time startTime;
	private static Map<String, List<Block>> blocksGivenDay = new HashMap<>(5);

    public static void main(String[] args)
    {
	    getBlocks();
	    new Printer(blocksGivenDay);
    }
	private static void getBlocks()
	{
		File file = new File("blocks.plist");
		try
		{
			NSDictionary rootDictionary = (NSDictionary) PropertyListParser.parse(file);
			parseRootDictionary(rootDictionary);
		}
		catch (Exception e)
		{
			System.out.println("Something broke: " + e);
		}
	}
	private static void parseRootDictionary(NSDictionary rootDictionary)
	{
		parseTimesFromRootDictionary(rootDictionary);
		NSDictionary blocksInfoGivenDay = (NSDictionary) rootDictionary.objectForKey("blocks");
		DAYS_OF_WEEK.stream()
				.forEach(day -> parseBlocksOfDay(day, blocksInfoGivenDay));
	}
	private static void parseTimesFromRootDictionary(NSDictionary rootDictionary)
	{
		NSDictionary timeInfo = (NSDictionary) rootDictionary.objectForKey("times");
		normalBlock = ((NSNumber) timeInfo.objectForKey("normalBlock")).intValue();
		longBlock = ((NSNumber) timeInfo.objectForKey("longBlock")).intValue();
		lunchBlock = ((NSNumber) timeInfo.objectForKey("lunchBlock")).intValue();
		normalHR = ((NSNumber) timeInfo.objectForKey("normalHR")).intValue();
		longHR = ((NSNumber) timeInfo.objectForKey("longHR")).intValue();
		normalJBlock = ((NSNumber) timeInfo.objectForKey("normalJBlock")).intValue();
		shortJBlock = ((NSNumber) timeInfo.objectForKey("shortJBlock")).intValue();
		passing = ((NSNumber) timeInfo.objectForKey("passing")).intValue();
		int startHour = ((NSNumber) timeInfo.objectForKey("startHour")).intValue();
		int startMinute = ((NSNumber) timeInfo.objectForKey("startMinute")).intValue();
		startTime = new Time(startHour, startMinute);
	}
	private static void parseBlocksOfDay(String dayOfWeek, NSDictionary blocksInfoGivenDay)
	{
		NSArray blocksOfDayNSArray = (NSArray) blocksInfoGivenDay.objectForKey(dayOfWeek);
		NSDictionary[] blocksInfo = castBlocksOfDayToNSDictionaryArray(blocksOfDayNSArray);

		List<Block> blocksOfDay = new ArrayList<>(blocksInfo.length);
		Time blockStartTime = startTime;
		for (NSDictionary blockInfo : blocksInfo)
		{
			Pair<Time, Block> timeAndBlock = parseBlockInfo(blockInfo, blockStartTime);
			blockStartTime = timeAndBlock.getKey();
			blocksOfDay.add(timeAndBlock.getValue());
		}
		blocksGivenDay.put(dayOfWeek, blocksOfDay);
	}
	private static NSDictionary[] castBlocksOfDayToNSDictionaryArray(NSArray blocksOfDay)
	{
		NSObject[] blocksOfDayNSObjectArray = blocksOfDay.getArray();
		NSDictionary[] blocksInfo = new NSDictionary[blocksOfDayNSObjectArray.length];
		return Arrays.asList(blocksOfDayNSObjectArray).toArray(blocksInfo);
	}
	private static Pair<Time, Block> parseBlockInfo(NSDictionary blockInfo, Time blockStartTime)
	{
		String blockLetter = blockInfo.objectForKey("blockLetter").toString();
		int blockNum = ((NSNumber) blockInfo.objectForKey("blockNum")).intValue();
		String type = blockInfo.objectForKey("type").toString();
		boolean isLunch = type.equals("lunchBlock");
		int blockLength = blockLengthForType(type);

		Block block = new Block(blockLetter, blockNum, blockStartTime, blockLength, isLunch);
		Time nextBlockStartTime = Time.add(block.endTime, passing);

		return new Pair<>(nextBlockStartTime, block);
	}
	private static int blockLengthForType(String type)
	{
		switch (type)
		{
			case "normalBlock":
				return normalBlock;
			case "longBlock":
				return longBlock;
			case "lunchBlock":
				return lunchBlock;
			case "normalHR":
				return normalHR;
			case "longHR":
				return longHR;
			case "normalJBlock":
				return normalJBlock;
			case "shortJBlock":
				return shortJBlock;
			default:
				System.out.println("timeLengthType undefined: " + type);
				return 0;
		}
	}
}
