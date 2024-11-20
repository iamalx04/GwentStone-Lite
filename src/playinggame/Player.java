package playinggame;

import fileio.CardInput;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class Player {
    @Getter @Setter private static int playerOneWins;
    @Getter @Setter private static int playerTwoWins;
    @Getter @Setter private ArrayList<CardInput> hand;
    @Getter @Setter private boolean finishedTurn;
    @Getter @Setter private int mana = 0;
    @Getter @Setter private ArrayList<CardInput> playerDeck;
    @Getter @Setter private CardInput hero;
    @Getter @Setter private boolean usedHero;

    public Player() {
        hand = new ArrayList<>();
        this.finishedTurn = false;
        this.mana = 0;
        playerDeck = new ArrayList<>();
        this.hero = new CardInput();
        this.usedHero = false;
    }
}
