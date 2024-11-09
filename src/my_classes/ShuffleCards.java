package my_classes;

import fileio.CardInput;

import java.util.ArrayList;
import java.util.Random;

import static java.util.Collections.shuffle;

public class ShuffleCards {

    public static void Shuffle_Cards( ArrayList<CardInput> deck1,
                                      ArrayList<CardInput> deck2,
                                      int seed) {
        Random randPlayer = new Random(seed);
        shuffle(deck1, randPlayer);

        randPlayer = new Random(seed);
        shuffle(deck2, randPlayer);
    }
}
