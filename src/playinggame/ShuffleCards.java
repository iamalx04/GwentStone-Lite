package playinggame;

import fileio.CardInput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public final class ShuffleCards {

    private ShuffleCards() {

    }

    /**
     *
     * @param deck1
     * @param deck2
     * @param seed
     */
    public static void shuffle(final ArrayList<CardInput> deck1,
                        final ArrayList<CardInput> deck2,
                        final int seed) {
        Random randPlayer1 = new Random(seed);
        Collections.shuffle(deck1, randPlayer1);

        Random randPlayer2 = new Random(seed);
        Collections.shuffle(deck2, randPlayer2);
    }
}
