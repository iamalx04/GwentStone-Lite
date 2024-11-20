package playinggame;

public final class Constants {
    private Constants() {

    }
    public static final int ROWS = 4;
    public static final int COLS = 5;
    public static final int MAX_INC = 10;
    public static final int PLAYER_TWO_BACK_ROW = 0;
    public static final int PLAYER_TWO_FRONT_ROW = 1;
    public static final int PLAYER_ONE_FRONT_ROW = 2;
    public static final int PLAYER_ONE_BACK_ROW = 3;
    public static final int ERROR_RET_THREE = 3;
    public static final int ERROR_RET_FOUR = 4;
    public static final int ERROR_RET_FIVE = 5;
    public static final int HERO_INIT_HEALTH = 30;
    public static final String PLAYER_DECK = "getPlayerDeck";
    public static final String PLAYER_HERO = "getPlayerHero";
    public static final String PLAYER_TURN = "getPlayerTurn";
    public static final String END_TURN = "endPlayerTurn";
    public static final String PLACE_CARD = "placeCard";
    public static final String CARDS_IN_HAND = "getCardsInHand";
    public static final String PLAYER_MANA = "getPlayerMana";
    public static final String CARDS_ON_TABLE = "getCardsOnTable";
    public static final String ATTACK_CARD = "cardUsesAttack";
    public static final String CARD_AT_POSITION = "getCardAtPosition";
    public static final String USES_ABILITY = "cardUsesAbility";
    public static final String ATTACK_HERO = "useAttackHero";
    public static final String USE_HERO = "useHeroAbility";
    public static final String FROZEN_CARDS = "getFrozenCardsOnTable";
    public static final String TOTAL_GAMES = "getTotalGamesPlayed";
    public static final String P_ONE_WINS = "getPlayerOneWins";
    public static final String P_TWO_WINS = "getPlayerTwoWins";
    public static final String[] FRONT_ROW = {"The Ripper", "Miraj", "Goliath", "Warden"};
    public static final String[] BACK_ROW = {"Sentinel", "Berserker", "The Cursed One", "Disciple"};
    public static final String[] TANKS = {"Goliath", "Warden"};
    public static final String[] AF_CURR_PLAYER = {"King Mudface", "General Kocioraw"};
    public static final String[] AF_OPP_PLAYER = {"Lord Royce", "Empress Thorina"};

}
