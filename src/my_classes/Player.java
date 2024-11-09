package my_classes;

import fileio.CardInput;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class Player{
    @Getter @Setter private ArrayList<CardInput> hand;
    @Getter @Setter private boolean finished_turn;
    @Getter @Setter private int mana = 0;
    @Getter @Setter private ArrayList<CardInput> player_deck;
    @Getter @Setter private CardInput hero;
    @Getter @Setter private boolean usedHero;

    public Player() {
        hand = new ArrayList<>();
        this.finished_turn = false;
        this.mana = 0;
        player_deck = new ArrayList<>();
        this.hero = new CardInput();
        this.usedHero = false;
    }
}
