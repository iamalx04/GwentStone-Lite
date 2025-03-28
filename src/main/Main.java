package main;

import checker.Checker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import checker.CheckerConstants;
import fileio.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

import playinggame.Constants;
import playinggame.InitGame;
import playinggame.Player;
import playinggame.TableCard;

/**
 * The entry point to this homework. It runs the checker that tests your implementation.
 */
public final class Main {
    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * DO NOT MODIFY MAIN METHOD
     * Call the checker
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(CheckerConstants.TESTS_PATH);
        Path path = Paths.get(CheckerConstants.RESULT_PATH);

        if (Files.exists(path)) {
            File resultFile = new File(String.valueOf(path));
            for (File file : Objects.requireNonNull(resultFile.listFiles())) {
                file.delete();
            }
            resultFile.delete();
        }
        Files.createDirectories(path);

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            String filepath = CheckerConstants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getName(), filepath);
            }
        }

        Checker.calculateScore();
    }

    /**
     * @param filePath1 for input file
     * @param filePath2 for output file
     * @throws IOException in case of exceptions to reading / writing
     */

    public static void action(final String filePath1,
                              final String filePath2) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        Input inputData = objectMapper.readValue(new File(CheckerConstants.TESTS_PATH + filePath1),
                Input.class);
        ArrayNode output = objectMapper.createArrayNode();

        DecksInput playerOneDecks = inputData.getPlayerOneDecks();
        DecksInput playerTwoDecks = inputData.getPlayerTwoDecks();

        ArrayList<ArrayList<TableCard>> table = new ArrayList<>();

        for (int i = 0; i < Constants.ROWS; i++) {
            ArrayList<TableCard> row = new ArrayList<>(Constants.ROWS);
            table.add(row);
        }

        Player.setPlayerOneWins(0);
        Player.setPlayerTwoWins(0);

        for (GameInput game : inputData.getGames()) {
            DecksInput decks1copy = new DecksInput(playerOneDecks);
            DecksInput decks2copy = new DecksInput(playerTwoDecks);

            Player playerOne = new Player();
            Player playerTwo = new Player();

            InitGame.init(inputData, output, game, table, playerOne, playerTwo,
                    decks1copy, decks2copy);

            table = new ArrayList<>();

            for (int i = 0; i < Constants.ROWS; i++) {
                ArrayList<TableCard> row = new ArrayList<>(Constants.ROWS);
                table.add(row);
            }
        }

        /*
         * TODO Implement your function here
         *
         * How to add output to the output array?
         * There are multiple ways to do this, here is one example:*/

//         ObjectMapper mapper = new ObjectMapper();
//         ObjectNode objectNode = mapper.createObjectNode();
//         objectNode.put("field_name", "field_value");
//         ArrayNode arrayNode = mapper.createArrayNode();
//         arrayNode.add(objectNode);
//         output.add(arrayNode);
//         output.add(objectNode);


        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePath2), output);
    }
}

