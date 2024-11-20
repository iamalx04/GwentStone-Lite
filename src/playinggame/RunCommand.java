package playinggame;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.CardInput;

import java.util.ArrayList;
import java.util.Arrays;

public final class RunCommand {

    private RunCommand() {

    }

    /**
     *
     * @param printedDeck
     * @param mapper
     * @return
     */
    public static ArrayNode playerDeck(final ArrayList<CardInput> printedDeck,
                                       final ObjectMapper mapper) {
        ArrayNode result = mapper.createArrayNode();

        for (CardInput card : printedDeck) {
            ObjectNode cardtoprint = mapper.createObjectNode();
            cardtoprint = printCard(card, mapper);
            result.add(cardtoprint);
        }
        return result;
    }

    /**
     *
     * @param printedHero
     * @param mapper
     * @return
     */
    public static ObjectNode playerHero(final CardInput printedHero,
                                        final ObjectMapper mapper) {
        ObjectNode printHero = mapper.createObjectNode();
        printHero.put("mana", printedHero.getMana());
        printHero.put("description", printedHero.getDescription());

        ArrayNode colors = mapper.createArrayNode();
        for (String color : printedHero.getColors()) {
            colors.add(color);
        }

        printHero.set("colors", colors);
        printHero.put("name", printedHero.getName());
        printHero.put("health", printedHero.getHealth());
        return printHero;
    }

    /**
     *
     * @param printedHand
     * @param mapper
     * @return
     */
    public static ArrayNode playerHand(final ArrayList<CardInput> printedHand,
                                       final ObjectMapper mapper) {
        ArrayNode handcards = mapper.createArrayNode();

        for (CardInput card : printedHand) {
            ObjectNode printcard = mapper.createObjectNode();
            printcard = printCard(card, mapper);
            handcards.add(printcard);
        }
        return handcards;
    }

    /**
     *
     * @param table
     * @param mapper
     * @return
     */
    public static ArrayNode tableCards(final ArrayList<ArrayList<TableCard>> table,
                                       final ObjectMapper mapper) {
        ArrayNode output = mapper.createArrayNode();

        for (ArrayList<TableCard> row : table) {
            ArrayNode cards = mapper.createArrayNode();
            for (TableCard card : row) {
                ObjectNode cardToPrint = null;

                if (card != null) {
                    cardToPrint = printCard(card.getCard(), mapper);
                }

                if (cardToPrint != null && card.getCard().getName() != null) {
                    cards.add(cardToPrint);
                }
            }

            output.add(cards);
        }
        return output;
    }

    /**
     *
     * @param table
     * @param card
     * @param currPlayer
     * @return
     */
    public static int placeCard(final ArrayList<ArrayList<TableCard>> table,
                                final CardInput card,
                                final int currPlayer) {
        String name = card.getName();
        int hasAdded = 0;

        // Check if the card belongs to the front row
        if (Arrays.asList(Constants.FRONT_ROW).contains(name)) {
            if (currPlayer == 1) {
                ArrayList<TableCard> playerOneFrontRow = table.get(Constants.PLAYER_ONE_FRONT_ROW);
                if (playerOneFrontRow.size() < Constants.COLS) {
                TableCard newcard = new TableCard();
                newcard.setCard(card);
                playerOneFrontRow.add(newcard);
                hasAdded = 1;
                }
            } else {
                ArrayList<TableCard> playerTwoFrontRow = table.get(Constants.PLAYER_TWO_FRONT_ROW);
                if (playerTwoFrontRow.size() < Constants.COLS) {
                TableCard newcard = new TableCard();
                newcard.setCard(card);
                playerTwoFrontRow.add(newcard);
                hasAdded = 1;
                }
            }

            // Check if the card belongs to the back row
        } else if (Arrays.asList(Constants.BACK_ROW).contains(name)) {
            if (currPlayer == 1) {
                ArrayList<TableCard> playerOneBackRow = table.get(Constants.PLAYER_ONE_BACK_ROW);
                if (playerOneBackRow.size() < Constants.COLS) {
                TableCard newcard = new TableCard();
                newcard.setCard(card);
                playerOneBackRow.add(newcard);
                hasAdded = 1;
                }
            } else {
                ArrayList<TableCard> playerTwoBackRow = table.get(Constants.PLAYER_TWO_BACK_ROW);
                if (playerTwoBackRow.size() < Constants.COLS) {

                TableCard newcard = new TableCard();
                newcard.setCard(card);
                playerTwoBackRow.add(newcard);
                hasAdded = 1;
                }
            }
        }

        return hasAdded;
    }

    /**
     *
     * @param table
     * @param currPlayer
     * @param xAttacker
     * @param yAttacker
     * @param xAttacked
     * @param yAttacked
     * @return
     */
    public static int attackCard(final ArrayList<ArrayList<TableCard>> table,
                                 final int currPlayer,
                                 final int xAttacker,
                                 final int yAttacker,
                                 final int xAttacked,
                                 final int yAttacked) {
        if (currPlayer == 1) {
            if (xAttacked == Constants.PLAYER_ONE_BACK_ROW
                    || xAttacked == Constants.PLAYER_ONE_FRONT_ROW) {
                return 1;
            }

            int hastanks = 0;
            int tankattacked = 0;

            for (TableCard card : table.get(Constants.PLAYER_TWO_FRONT_ROW)) {
                if (card != null) {
                    if (Arrays.asList(Constants.TANKS).contains(card.getCard().getName())) {
                        hastanks = 1;
                        if (xAttacked < table.size() && yAttacked < table.get(xAttacked).size()
                                && card == table.get(xAttacked).get(yAttacked)) {
                            tankattacked = 1;
                        }
                    }
                }
            }

            if (hastanks == 1 && tankattacked == 0) {
                return Constants.ERROR_RET_FOUR;
            }

        } else {
            if (xAttacked == Constants.PLAYER_TWO_BACK_ROW
                    || xAttacked == Constants.PLAYER_TWO_FRONT_ROW) {
                return 1;
            }

            int hastanks = 0;
            int tankattacked = 0;

            for (TableCard card : table.get(Constants.PLAYER_ONE_FRONT_ROW)) {
                if (card != null) {
                    if (Arrays.asList(Constants.TANKS).contains(card.getCard().getName())) {
                        hastanks = 1;
                        if (xAttacked < table.size() && yAttacked < table.get(xAttacked).size()
                                && card == table.get(xAttacked).get(yAttacked)) {
                            tankattacked = 1;
                        }
                    }
                }
            }

            if (hastanks == 1 && tankattacked == 0) {
                return Constants.ERROR_RET_FOUR;
            }
        }

        if (xAttacker < table.size() && yAttacker < table.get(xAttacker).size()
                && table.get(xAttacker).get(yAttacker).isUsed()) {
            return 2;
        }

        if (xAttacker < table.size() && yAttacker < table.get(xAttacker).size()
                && table.get(xAttacker).get(yAttacker).isFrozen()) {
            return Constants.ERROR_RET_THREE;
        }

        if (xAttacker < table.size() && yAttacker < table.get(xAttacker).size()
                && xAttacked < table.size() && yAttacked < table.get(xAttacked).size()) {
            table.get(xAttacked).get(yAttacked).getCard().setHealth(table.get(xAttacked).
                    get(yAttacked).getCard().getHealth() - table.get(xAttacker).get(yAttacker).
                    getCard().getAttackDamage());
        }

        if (yAttacked < table.get(xAttacked).size()
                && table.get(xAttacked).get(yAttacked).getCard().getHealth() <= 0) {
            table.get(xAttacked).remove(yAttacked);
        }

        if (xAttacker < table.size() && yAttacker < table.get(xAttacker).size()) {
            table.get(xAttacker).get(yAttacker).setUsed(true);
        }

        return 0;
    }

    /**
     *
     * @param table
     * @param currPlayer
     * @param xAttacker
     * @param yAttacker
     * @param xAttacked
     * @param yAttacked
     * @return
     */
    public static int useAbility(final ArrayList<ArrayList<TableCard>> table,
                                 final int currPlayer,
                                 final int xAttacker,
                                 final int yAttacker,
                                 final int xAttacked,
                                 final int yAttacked) {
        if (xAttacker < table.size() && yAttacker < table.get(xAttacker).size()
                && xAttacked < table.size() && yAttacked < table.get(xAttacked).size()) {
            if (table.get(xAttacker).get(yAttacker).isFrozen()) {
                return 1;
            }

            if (table.get(xAttacker).get(yAttacker).isUsed()) {
                return 2;
            }

            if (table.get(xAttacker).get(yAttacker).getCard().getName().equals("Disciple")) {
                if ((currPlayer == 1 && (xAttacked == Constants.PLAYER_TWO_BACK_ROW
                        || xAttacked == Constants.PLAYER_TWO_FRONT_ROW)) || currPlayer == 2
                        && (xAttacked == Constants.PLAYER_ONE_BACK_ROW
                        || xAttacked == Constants.PLAYER_ONE_FRONT_ROW)) {
                    return Constants.ERROR_RET_THREE;
                }

            } else if (table.get(xAttacker).get(yAttacker).getCard().getName().equals("The Ripper")
                    || table.get(xAttacker).get(yAttacker).getCard().getName().equals("Miraj")
                    || table.get(xAttacker).get(yAttacker).getCard().getName().
                    equals("The Cursed One")) {
                if ((currPlayer == 1 && (xAttacked == Constants.PLAYER_ONE_BACK_ROW
                        || xAttacked == Constants.PLAYER_ONE_FRONT_ROW))
                        || currPlayer == 2 && (xAttacked == Constants.PLAYER_TWO_BACK_ROW
                        || xAttacked == Constants.PLAYER_TWO_FRONT_ROW)) {
                    return Constants.ERROR_RET_FOUR;
                }

                int hastanks = 0;
                int tankattacked = 0;
                if (currPlayer == 1) {
                    for (TableCard card : table.get(Constants.PLAYER_TWO_FRONT_ROW)) {
                        if (card != null) {
                            if (Arrays.asList(Constants.TANKS).contains(card.getCard().getName())) {
                                hastanks = 1;
                                if (card == table.get(xAttacked).get(yAttacked)) {
                                    tankattacked = 1;
                                }
                            }
                        }
                    }

                } else {
                    for (TableCard card : table.get(Constants.PLAYER_ONE_FRONT_ROW)) {
                        if (card != null) {
                            if (Arrays.asList(Constants.TANKS).contains(card.getCard().getName())) {
                                hastanks = 1;
                                if (yAttacked < table.get(xAttacked).size()
                                        && card == table.get(xAttacked).get(yAttacked)) {
                                    tankattacked = 1;
                                }
                            }
                        }
                    }

                }
                if (hastanks == 1 && tankattacked == 0) {
                    return Constants.ERROR_RET_FIVE;
                }
            }

            if (table.get(xAttacker).get(yAttacker).getCard().getName().equals("The Ripper")) {

                table.get(xAttacker).get(yAttacker).setUsed(true);

                table.get(xAttacked).get(yAttacked).getCard().setAttackDamage(table.get(xAttacked).
                        get(yAttacked).getCard().getAttackDamage() - 2);

                if (table.get(xAttacked).get(yAttacked).getCard().getAttackDamage() <= 0) {
                    table.get(xAttacked).get(yAttacked).getCard().setAttackDamage(0);
                }

            } else if (table.get(xAttacker).get(yAttacker).getCard().getName().equals("Miraj")) {

                table.get(xAttacker).get(yAttacker).setUsed(true);

                int healthAttacker = table.get(xAttacker).get(yAttacker).getCard().getHealth();

                int healthAttacked = table.get(xAttacked).get(yAttacked).getCard().getHealth();

                table.get(xAttacked).get(yAttacked).getCard().setHealth(healthAttacker);

                table.get(xAttacker).get(yAttacker).getCard().setHealth(healthAttacked);

            } else if (table.get(xAttacker).get(yAttacker).getCard().
                    getName().equals("The Cursed One")) {

                table.get(xAttacker).get(yAttacker).setUsed(true);

                int healthAttacked = table.get(xAttacked).get(yAttacked).getCard().getHealth();

                int attackAttacked = table.get(xAttacked).get(yAttacked).
                        getCard().getAttackDamage();

                table.get(xAttacked).get(yAttacked).getCard().setHealth(attackAttacked);

                table.get(xAttacked).get(yAttacked).getCard().setAttackDamage(healthAttacked);

                if (table.get(xAttacked).get(yAttacked).getCard().getHealth() <= 0) {
                    table.get(xAttacked).remove(yAttacked);
                }

            } else if (table.get(xAttacker).get(yAttacker).getCard().
                    getName().equals("Disciple")) {

                table.get(xAttacker).get(yAttacker).setUsed(true);

                table.get(xAttacked).get(yAttacked).getCard().setHealth(table.get(xAttacked).
                        get(yAttacked).getCard().getHealth() + 2);
            }
            return 0;
        }
        return -1;
    }

    /**
     *
     * @param table
     * @param currPlayer
     * @param xAttacker
     * @param yAttacker
     * @param hero
     * @return
     */
    public static int attackHero(final ArrayList<ArrayList<TableCard>> table,
                                 final int currPlayer,
                                 final int xAttacker,
                                 final int yAttacker,
                                 final CardInput hero) {
        if (xAttacker < table.size() && yAttacker < table.get(xAttacker).size()) {
            if (table.get(xAttacker).get(yAttacker).isFrozen()) {
                return 1;
            }

            if (table.get(xAttacker).get(yAttacker).isUsed()) {
                return 2;
            }

            int hastanks = 0;
            if (currPlayer == 1) {
                for (TableCard card : table.get(Constants.PLAYER_TWO_FRONT_ROW)) {
                    if (card != null) {
                        if (Arrays.asList(Constants.TANKS).contains(card.getCard().getName())) {
                            hastanks = 1;
                        }
                    }
                }

            } else {
                for (TableCard card : table.get(Constants.PLAYER_ONE_FRONT_ROW)) {
                    if (card != null) {
                        if (Arrays.asList(Constants.TANKS).contains(card.getCard().getName())) {
                            hastanks = 1;
                        }
                    }
                }

            }
            if (hastanks == 1) {
                return Constants.ERROR_RET_THREE;
            }

            int damage = table.get(xAttacker).get(yAttacker).getCard().getAttackDamage();
            hero.setHealth(hero.getHealth() - damage);
            table.get(xAttacker).get(yAttacker).setUsed(true);
            if (hero.getHealth() <= 0) {
                return Constants.ERROR_RET_FOUR;
            }

            return 0;
        }
        return Constants.ERROR_RET_FOUR;
    }

    /**
     *
     * @param table
     * @param currPlayer
     * @param row
     * @param player
     * @return
     */
    public static int useHero(final ArrayList<ArrayList<TableCard>> table,
                              final int currPlayer,
                              final int row,
                              final Player player) {
        if (row < table.size()) {
            if (player.getMana() < player.getHero().getMana()) {
                return 1;
            }

            if (player.isUsedHero()) {
                return 2;
            }

            if (currPlayer == 1) {
                if (Arrays.asList(Constants.AF_OPP_PLAYER).contains(player.getHero().
                        getName()) && (row == Constants.PLAYER_ONE_FRONT_ROW
                        || row == Constants.PLAYER_ONE_BACK_ROW)) {
                    return Constants.ERROR_RET_THREE;
                }

                if (Arrays.asList(Constants.AF_CURR_PLAYER).contains(player.getHero().
                        getName()) && (row == Constants.PLAYER_TWO_FRONT_ROW
                        || row == Constants.PLAYER_TWO_BACK_ROW)) {
                    return Constants.ERROR_RET_FOUR;
                }

            } else {
                if (Arrays.asList(Constants.AF_OPP_PLAYER).contains(player.getHero().
                        getName()) && (row == Constants.PLAYER_TWO_FRONT_ROW
                        || row == Constants.PLAYER_TWO_BACK_ROW)) {
                    return Constants.ERROR_RET_THREE;
                }

                if (Arrays.asList(Constants.AF_CURR_PLAYER).contains(player.getHero().
                        getName()) && (row == Constants.PLAYER_ONE_FRONT_ROW
                        || row == Constants.PLAYER_ONE_BACK_ROW)) {
                    return Constants.ERROR_RET_FOUR;
                }
            }

            if (player.getHero().getName().equals("Lord Royce")) {
                for (TableCard card : table.get(row)) {
                    card.setFrozen(true);
                }
            } else if (player.getHero().getName().equals("Empress Thorina")) {
                int destroyidx = 0;
                for (int i = 0; i < table.get(row).size(); i++) {
                    if (table.get(row).get(i) != null && table.get(row).get(i).getCard().
                            getHealth() > table.get(row).get(destroyidx).getCard().getHealth()) {
                        destroyidx = i;
                    }
                }

                if (destroyidx < table.get(row).size()) {
                    table.get(row).remove(destroyidx);
                }

            } else if (player.getHero().getName().equals("King Mudface")) {
                for (TableCard card : table.get(row)) {
                    if (card != null) {
                        card.getCard().setHealth(card.getCard().getHealth() + 1);
                    }
                }
            } else {
                for (TableCard card : table.get(row)) {
                    if (card != null) {
                        card.getCard().setAttackDamage(card.getCard().getAttackDamage() + 1);
                    }
                }
            }

            player.setMana(player.getMana() - player.getHero().getMana());
            player.setUsedHero(true);

            return 0;
        }

        return -1;
    }

    /**
     *
     * @param table
     * @param mapper
     * @return
     */
    public static ArrayNode frozenCards(final ArrayList<ArrayList<TableCard>> table,
                                        final ObjectMapper mapper) {
        ArrayNode output = mapper.createArrayNode();

        for (ArrayList<TableCard> row : table) {
            for (TableCard card : row) {
                if (card != null && card.isFrozen()) {
                    ObjectNode cardToPrint = printCard(card.getCard(), mapper);
                    output.add(cardToPrint);
                }
            }
        }

        return output;
    }

    /**
     *
     * @param playerOne
     * @param playerTwo
     * @param noRound
     * @param table
     */
    public static void initRound(final Player playerOne,
                                 final Player playerTwo,
                                 final int noRound,
                                 final ArrayList<ArrayList<TableCard>> table) {
        if (!playerOne.getPlayerDeck().isEmpty()) {
            CardInput firstCard = playerOne.getPlayerDeck().remove(0);
            playerOne.getHand().add(firstCard);
        }

        if (!playerTwo.getPlayerDeck().isEmpty()) {
            CardInput firstCard = playerTwo.getPlayerDeck().remove(0);
            playerTwo.getHand().add(firstCard);
        }

        playerOne.setFinishedTurn(false);
        playerOne.setUsedHero(false);

        playerTwo.setFinishedTurn(false);
        playerTwo.setUsedHero(false);

        if (noRound <= Constants.MAX_INC) {
            playerOne.setMana(playerOne.getMana() + noRound);
            playerTwo.setMana(playerTwo.getMana() + noRound);
        } else {
            playerOne.setMana(playerOne.getMana() + Constants.MAX_INC);
            playerTwo.setMana(playerTwo.getMana() + Constants.MAX_INC);
        }

        for (ArrayList<TableCard> row : table) {
            for (TableCard card : row) {
                if (card != null) {
                    card.setUsed(false);
                }
            }
        }
    }

    /**
     *
     * @param printedCard
     * @param mapper
     * @return
     */
    public static ObjectNode printCard(final CardInput printedCard,
                                       final ObjectMapper mapper) {
        ObjectNode print = mapper.createObjectNode();
        print.put("mana", printedCard.getMana());
        print.put("attackDamage", printedCard.getAttackDamage());
        print.put("health", printedCard.getHealth());
        print.put("description", printedCard.getDescription());

        ArrayNode colors = mapper.createArrayNode();
        if (printedCard.getColors() != null) {
        for (String color : printedCard.getColors()) {
            colors.add(color);
        }
        }

        print.set("colors", colors);
        print.put("name", printedCard.getName());
        return print;
    }
}
