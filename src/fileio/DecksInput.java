package fileio;

import java.util.ArrayList;

public final class DecksInput {
    private int nrCardsInDeck;
    private int nrDecks;
    private ArrayList<ArrayList<CardInput>> decks;

    public DecksInput() {
    }

    public DecksInput(final DecksInput deck) {
        this.nrCardsInDeck = deck.nrCardsInDeck;
        this.nrDecks = deck.nrDecks;
        this.decks = new ArrayList<>();

        for (ArrayList<CardInput> row : deck.decks) {
            ArrayList<CardInput> newRow = new ArrayList<>();
            for (CardInput card : row) {
                newRow.add(new CardInput(card));
            }
            this.decks.add(newRow);
        }
    }

    public int getNrCardsInDeck() {
        return nrCardsInDeck;
    }

    public void setNrCardsInDeck(final int nrCardsInDeck) {
        this.nrCardsInDeck = nrCardsInDeck;
    }

    public int getNrDecks() {
        return nrDecks;
    }

    public void setNrDecks(final int nrDecks) {
        this.nrDecks = nrDecks;
    }

    public ArrayList<ArrayList<CardInput>> getDecks() {
        return decks;
    }

    public void setDecks(final ArrayList<ArrayList<CardInput>> decks) {
        this.decks = decks;
    }

    @Override
    public String toString() {
        return "InfoInput{"
                + "nr_cards_in_deck="
                + nrCardsInDeck
                +  ", nr_decks="
                + nrDecks
                + ", decks="
                + decks
                + '}';
    }
}
