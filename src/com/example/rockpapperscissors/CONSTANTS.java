package com.example.rockpapperscissors;

public final class CONSTANTS {
	public static final int EMPTY = 0;
	public static final int FRIENDLY_KNIGHT = 1;
	public static final int FRIENDLY_DUCK = 2;
	public static final int FRIENDLY_WITCH = 3;
	public static final int ENEMY_KNIGHT = 4;
	public static final int ENEMY_DUCK = 5;
	public static final int ENEMY_WITCH = 6;
	public static final int IMPASSABLE = 7;
	public static final int COINS = 8;

	public static final int GAMEFIELD_COLUMNS = 8;
	public static final int GAMEFIELD_ROWS = 5;

	public final static float scale = 1.150f;
	public final static int squareSize = 64;
	public final static float startY = 480 - GAMEFIELD_ROWS
			* (squareSize * scale) + 6 * scale;
	public final static float startX = (800 - (GAMEFIELD_COLUMNS * squareSize - squareSize / 2)
			* scale) / 2;
	public final static float increment = squareSize * scale;
	public static final int PLAYER = 0;
	public static final int ENEMY = 1;
	public static final int KNIGHT_PRICE = 4;
	public static final int DUCK_PRICE = 4;
	public static final int WITCH_PRICE = 3;
	public static final int PLANTABLE_COLUMNS = 1;
	public static final int START_PLANTABLE_ROW = 1;
	public static final int END_PLANTABLE_ROW = 3;
	public static final int VANQUISH_REWARD = 0;
	public static final int BASE_TURN_INCOME = 0;

	public static final int MOVE_TYPE_MOVE = 0;
	public static final int MOVE_TYPE_BUY = 1;
	public static final int MOVE_TYPE_COLLECT = 2;
	public static final int MOVE_TYPE_ATTACK = 3;
	public static final double NEGATIVE_INFINITY = Double.NEGATIVE_INFINITY;
	public static final int KNIGHT_STARTING_HEALTH = 4;
	public static final int KNIGHT_STARTING_MOVES = 1;
	public static final int WITCH_STARTING_HEALTH = 3;
	public static final int WITCH_STARTING_MOVES = 1;
	public static final int DUCK_STARTING_HEALTH = 2;
	public static final int DUCK_STARTING_MOVES = 2;
	public static final int WIN_SCORE_BONUS = 5000;
	public static final int INITIAL_PHASE_TURN_NUM = 3;

	public static final int MAX_FIELD_DISTANCE = GAMEFIELD_COLUMNS
			+ GAMEFIELD_ROWS;
	public static final int MIN_HOP_SIZE = Math.min(
			Math.min(KNIGHT_STARTING_MOVES, WITCH_STARTING_MOVES),
			DUCK_STARTING_MOVES);
	public static final int MAX_HOP_SIZE = Math.max(
			Math.max(KNIGHT_STARTING_MOVES, WITCH_STARTING_MOVES),
			DUCK_STARTING_MOVES);
	public static final double MAX_CHASE_PICKUPS_CHANCE = 2d / 3d;
	public static final int BASE_MAX_DEPTH = 4;
	public static final int MIN_AB_INTERFIGURE_DISTANCE = BASE_MAX_DEPTH
			* MIN_HOP_SIZE;
	public static final double CHECK_FOR_BUYING_CHANCE = 1d / 2d;
	public static final int MAX_UNIT_COUNT = 3;
	public static final int MAX_HEALTH = Math.max(
			Math.max(DUCK_STARTING_HEALTH, KNIGHT_STARTING_HEALTH),
			WITCH_STARTING_HEALTH);
	public static final int MAX_COIN_COUNT = 11;
	public static final int MAX_COIN_SIZE = 3;
	public static final int NEUTRAL_AGRESSION = 0;
	public static final int AGRESSIVE_AGRESSION = 1;
	public static final int DEFENSIVE_AGRESSION = -1;
	public static final float GREEDINES_INCREASE_CHANCE_FACTOR = 0.5f;
	public static final String KNIGHT_DESCRIPTION = "+Health\n-Movement\nHigh Price";
	public static final String DUCK_DESCRITPION = "-Health\n+Movement\nHigh Price";
	public static final String WITCH_DESCRIPTION = "avg Health\navg Movement\nLower Price";
	public static final int TUTORIAL_PIC_NUM = 14;
}
