package com.davidchu;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Printer
{
	public Printer(Map<String, List<Block>> blocksGivenDay)
	{
		System.out.println("Php: \n" + blocksGivenDayToPhp(blocksGivenDay) + "\n");
		System.out.println("Java: \n" + blocksGivenDayToJava(blocksGivenDay) + "\n");
		System.out.println("Swift: \n" + blocksGivenDayToSwift(blocksGivenDay));
	}
	/*
	PHP

	stored as such:
		$blocksOfDay = array();

		switch($dayOfWeek)
		{
			case 1:
				$blocksOfDay = array(
					array(
						'letter' => 'G',
						'num' => 3,
						'startTime' => '10:50',
						'endTime' => '12:35',
						'isLunch' => false,
						'customLunchTimes' => null,
						'customBlockName' => ''
					);
					array()
				);
				break;
		}
	 */
	private String blocksGivenDayToPhp(Map<String, List<Block>> blocksGivenDay)
	{
		String blocksOfDays =
				"$dayOfWeek = date('w', $timeStamp);\n" +
				"$blocksOfDay = array();\n\n" +
				"switch ($dayOfWeek)\n" +
				"{\n";
		blocksOfDays += IntStream.range(0, Main.DAYS_OF_WEEK.size())
				.mapToObj(a -> createBlocksOfDayForPhp(a + 1, blocksGivenDay))
				.collect(Collectors.joining("\n"));
		blocksOfDays +=
					"\ndefault:\n" +
						"$blocksOfDay = null;\n" +
				"}";
		return blocksOfDays;
	}
	private String createBlocksOfDayForPhp(int dayOfWeek, Map<String, List<Block>> blocksGivenDay)
	{
		String list =
			"case " + dayOfWeek + ":\n" +
				"$blocksOfDay = array(\n";
		list += blocksGivenDay.get(Main.DAYS_OF_WEEK.get(dayOfWeek - 1))
				.stream()
				.map(this::createBlockForPhp)
				.collect(Collectors.joining(",\n"));
		list +=
				");\n" +
				"break;";
		return list;
	}
	private String createBlockForPhp(Block block)
	{
		return "array(\n" +
					"'letter' => '" + block.letter + "',\n" +
					"'num' => " + block.num + ",\n" +
					"'startTime' => '" + block.startTime + "',\n" +
					"'endTime' => '" + block.endTime + "',\n" +
					"'isLunch' => " + block.isLunchBlock + ",\n" +
					"'customLunchTimes' => null,\n" +
					"'customBlockName' => ''\n" +
				")";
	}
	/*
	JAVA

	stored as such:
		public static final List<Block> BLOCKS_ON_MONDAY = ImmutableList
			.<Block>builder()
			.add(new Block(letter, num, start, end, isLunch))
			.build();

		public static final Map<Integer, List<Block>> BLOCKS_GIVEN_DAY = ImmutableMap
			.<Integer, List<Block>>builder()
			.put(Calendar.MONDAY, BLOCKS_ON_MONDAY)
			.build();
	 */
	private String blocksGivenDayToJava(Map<String, List<Block>> blocksGivenDay)
	{
		String blocksOfDaysAsLists = Main.DAYS_OF_WEEK
				.stream()
				.map(a -> createBlocksOfDayForJava(a, blocksGivenDay))
				.collect(Collectors.joining("\n"));
		blocksOfDaysAsLists += createBlocksGivenDayForJava();
		return blocksOfDaysAsLists;
	}
	private String createBlocksOfDayForJava(String dayOfWeek, Map<String, List<Block>> blocksGivenDay)
	{
		String list = "public static final List<Block> BLOCKS_ON_" + dayOfWeek.toUpperCase() + " = ImmutableList" +
				".<Block>builder()\n";
		list += blocksGivenDay.get(dayOfWeek)
				.stream()
				.map(this::createBlockForJava)
				.collect(Collectors.joining("\n"));
		list += "\n.build();";
		return list;
	}
	private String createBlockForJava(Block block)
	{
		return ".add(new Block(\"" + block.letter + "\", " + block.num + ", " + timeStringForJava(block.startTime) + ", " + timeStringForJava(block.endTime) + ", " + block.isLunchBlock + "))";
	}
	private String timeStringForJava(Time time)
	{
		return "new Time(" + time.hour + ", " + time.minute + ")";
	}
	private String createBlocksGivenDayForJava()
	{
		String blocksGivenDay = "\npublic static final Map<Integer, List<Block>> BLOCKS_GIVEN_DAY = ImmutableMap" +
				".<Integer, List<Block>>builder()\n";
		blocksGivenDay += Main.DAYS_OF_WEEK
				.stream()
				.map(a -> ".put(Calendar." + a.toUpperCase() + ", BLOCKS_ON_" + a.toUpperCase() + ")")
				.collect(Collectors.joining("\n"));
		blocksGivenDay += "\n.build();";
		return blocksGivenDay;
	}
	/*
	SWIFT

	stored as such (no aesthetic newLines allowed in Swift):
		static let BLOCKS_ON_MONDAY = [Block(letter: letter, num: num, startTime: start, endTime: end, isLunchBlock: isLunch)]
		static let BLOCKS_GIVEN_DAY:[DayOfWeek:[Block]] = [.Monday:Block.BLOCKS_ON_MONDAY]
	 */
	private String blocksGivenDayToSwift(Map<String, List<Block>> blocksGivenDay)
	{
		String blocksOfDaysAsLists = Main.DAYS_OF_WEEK
				.stream()
				.map(a -> createBlocksOfDayForSwift(a, blocksGivenDay))
				.collect(Collectors.joining("\n"));
		blocksOfDaysAsLists += createBlocksGivenDayForSwift();
		return blocksOfDaysAsLists;
	}
	private String createBlocksOfDayForSwift(String dayOfWeek, Map<String, List<Block>> blocksGivenDay)
	{
		String list = "static let BLOCKS_ON_" + dayOfWeek.toUpperCase() + " = [";
		list += blocksGivenDay.get(dayOfWeek)
				.stream()
				.map(this::createBlockForSwift)
				.collect(Collectors.joining(", "));
		list += "]";
		return list;
	}
	private String createBlockForSwift(Block block)
	{
		return "Block(letter: \"" + block.letter + "\", num: " + block.num + ", startTime: " + timeStringForSwift(block.startTime) + ", endTime: " + timeStringForSwift(block.endTime) + ", isLunchBlock: " + block.isLunchBlock + ")";
	}
	private String timeStringForSwift(Time time)
	{
		return "Time(hour: " + time.hour + ", minute: " + time.minute + ")";
	}
	private String createBlocksGivenDayForSwift()
	{
		String blocksGivenDay = "\nstatic let BLOCKS_GIVEN_DAY:[DayOfWeek:[Block]] = [";
		blocksGivenDay += Main.DAYS_OF_WEEK
				.stream()
				.map(a -> "." + a + ":Block.BLOCKS_ON_" + a.toUpperCase())
				.collect(Collectors.joining(", "));
		blocksGivenDay += "]";
		return blocksGivenDay;
	}

}
