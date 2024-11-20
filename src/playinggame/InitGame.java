package playinggame;

import fileio.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;

public final class InitGame {
    private InitGame() {

    }

    /**
     *
     * @param input
     * @param output
     * @param game
     * @param table
     * @param playerOne
     * @param playerTwo
     * @param deckP1
     * @param deckP2
     */
    public static void init(final Input input,
                            final ArrayNode output,
                            final GameInput game,
                            final ArrayList<ArrayList<TableCard>> table,
                            final Player playerOne,
                            final Player playerTwo,
                            final DecksInput deckP1,
                            final DecksInput deckP2) {

        int noRounds = 1;
        int currPlayer = 0;
        int error = -1;
        // extrag deck-urile

        playerOne.setPlayerDeck(deckP1.getDecks().get(game.getStartGame().getPlayerOneDeckIdx()));
        playerTwo.setPlayerDeck(deckP2.getDecks().get(game.getStartGame().getPlayerTwoDeckIdx()));

        playerOne.setHero(game.getStartGame().getPlayerOneHero());
        playerTwo.setHero(game.getStartGame().getPlayerTwoHero());

        playerOne.getHero().setHealth(Constants.HERO_INIT_HEALTH);
        playerTwo.getHero().setHealth(Constants.HERO_INIT_HEALTH);

        int seed = game.getStartGame().getShuffleSeed();

        playerOne.getHand().clear();
        playerTwo.getHand().clear();
        ShuffleCards.shuffle(playerOne.getPlayerDeck(), playerTwo.getPlayerDeck(), seed);
        if (!playerOne.getPlayerDeck().isEmpty()) {
            playerOne.getHand().add(playerOne.getPlayerDeck().remove(0));
        }

        if (!playerTwo.getPlayerDeck().isEmpty()) {
            playerTwo.getHand().add(playerTwo.getPlayerDeck().remove(0));
        }

        playerOne.setMana(1);
        playerTwo.setMana(1);

        playerOne.setFinishedTurn(false);
        playerTwo.setFinishedTurn(false);

        currPlayer = game.getStartGame().getStartingPlayer();

        ObjectMapper mapper = new ObjectMapper();

        for (ActionsInput action : game.getActions()) {
            ObjectNode commandResult = mapper.createObjectNode();
            String command = action.getCommand();

            switch (command) {
                case Constants.PLAYER_DECK:
                    commandResult.put("command", command);
                    commandResult.put("playerIdx", action.getPlayerIdx());
                    ArrayNode deckResult;
                    if (action.getPlayerIdx() == 1) {
                        deckResult = RunCommand.playerDeck(playerOne.getPlayerDeck(), mapper);
                    } else {
                        deckResult = RunCommand.playerDeck(playerTwo.getPlayerDeck(), mapper);
                    }

                    commandResult.set("output", deckResult);

                    break;


                case Constants.PLAYER_HERO:
                    commandResult.put("command", command);
                    commandResult.put("playerIdx", action.getPlayerIdx());
                    ObjectNode heroResult;
                    if (action.getPlayerIdx() == 1) {
                        heroResult = RunCommand.playerHero(playerOne.getHero(), mapper);
                    } else {
                        heroResult = RunCommand.playerHero(playerTwo.getHero(), mapper);
                    }

                    commandResult.set("output", heroResult);
                    break;

                case Constants.PLAYER_TURN:
                    commandResult.put("command", command);
                    commandResult.put("output", currPlayer);

                    break;

                case Constants.PLAYER_MANA:
                    commandResult.put("command", command);
                    commandResult.put("playerIdx", action.getPlayerIdx());
                    if (action.getPlayerIdx() == 1) {
                        commandResult.put("output", playerOne.getMana());
                    } else {
                        commandResult.put("output", playerTwo.getMana());
                    }

                    break;

                case Constants.CARDS_IN_HAND:
                    commandResult.put("command", command);
                    commandResult.put("playerIdx", action.getPlayerIdx());
                    ArrayNode cardsResult;
                    if (action.getPlayerIdx() == 1) {
                        cardsResult = RunCommand.playerHand(playerOne.getHand(), mapper);
                    } else {
                        cardsResult = RunCommand.playerHand(playerTwo.getHand(), mapper);
                    }

                    commandResult.set("output", cardsResult);
                    break;


                case Constants.END_TURN:
                    if (currPlayer == 1) {
                        playerOne.setFinishedTurn(true);
                        for (TableCard card : table.get(Constants.PLAYER_ONE_FRONT_ROW)) {
                            if (card != null) {
                                card.setFrozen(false);
                            }
                        }

                        for (TableCard card : table.get(Constants.PLAYER_ONE_BACK_ROW)) {
                            if (card != null) {
                                card.setFrozen(false);
                            }
                        }

                        currPlayer = 2;
                    } else {
                        playerTwo.setFinishedTurn(true);
                        for (TableCard card : table.get(Constants.PLAYER_TWO_FRONT_ROW)) {
                            if (card != null) {
                                card.setFrozen(false);
                            }
                        }

                        for (TableCard card : table.get(Constants.PLAYER_TWO_BACK_ROW)) {
                            if (card != null) {
                                card.setFrozen(false);
                            }
                        }

                        currPlayer = 1;
                    }

                    if (playerOne.isFinishedTurn() && playerTwo.isFinishedTurn()) {
                        noRounds++;
                        RunCommand.initRound(playerOne, playerTwo, noRounds, table);
                    }
                    break;

                case Constants.CARDS_ON_TABLE:
                    commandResult.put("command", command);
                    ArrayNode tableResult = RunCommand.tableCards(table, mapper);
                    commandResult.set("output", tableResult);
                    break;

                case Constants.PLACE_CARD:
                    int idx = action.getHandIdx();
                    int status;
                    if (currPlayer == 1) {
                        if (idx < playerOne.getHand().size()) {
                            CardInput card = playerOne.getHand().get(idx);
                            if (playerOne.getMana() < card.getMana()) {
                                commandResult.put("command", command);
                                commandResult.put("handIdx", idx);
                                commandResult.put("error",
                                        "Not enough mana to place card on table.");
                            } else {
                                status = RunCommand.placeCard(table, card, currPlayer);
                                if (status == 1) {
                                    playerOne.getHand().remove(idx);
                                    playerOne.setMana(playerOne.getMana() - card.getMana());
                                } else {
                                    commandResult.put("command", command);
                                    commandResult.put("handIdx", idx);
                                    commandResult.put("error",
                                            "Cannot place card on table since row is full.");

                                }
                            }
                        }
                    } else {
                        if (idx < playerTwo.getHand().size()) {
                            CardInput card = playerTwo.getHand().get(idx);
                            if (playerTwo.getMana() < card.getMana()) {
                                commandResult.put("command", command);
                                commandResult.put("handIdx", idx);
                                commandResult.put("error",
                                        "Not enough mana to place card on table.");
                            } else {
                                status = RunCommand.placeCard(table, card, currPlayer);

                                if (status == 1) {
                                    playerTwo.getHand().remove(idx);
                                    playerTwo.setMana(playerTwo.getMana() - card.getMana());
                                } else {
                                    commandResult.put("command", command);
                                    commandResult.put("handIdx", idx);
                                    commandResult.put("error",
                                            "Cannot place card on table since row is full.");
                                }
                            }
                        }
                    }

                    break;

                case Constants.ATTACK_CARD:

                    int xAttacker = action.getCardAttacker().getX();
                    int yAttacker = action.getCardAttacker().getY();

                    int xAttacked = action.getCardAttacked().getX();
                    int yAttacked = action.getCardAttacked().getY();

                    error = RunCommand.attackCard(table, currPlayer, xAttacker, yAttacker,
                            xAttacked, yAttacked);

                    if (error > 0) {
                        ObjectNode coordinates = new ObjectMapper().createObjectNode();
                        coordinates = mapper.createObjectNode();
                        commandResult.put("command", command);
                        coordinates.put("x", xAttacker);
                        coordinates.put("y", yAttacker);
                        commandResult.set("cardAttacker", coordinates);
                        coordinates = mapper.createObjectNode();
                        coordinates.put("x", xAttacked);
                        coordinates.put("y", yAttacked);
                        commandResult.set("cardAttacked", coordinates);

                        if (error == 1) {
                            commandResult.put("error",
                                    "Attacked card does not belong to the enemy.");
                        } else if (error == 2) {
                            commandResult.put("error",
                                    "Attacker card has already attacked this turn.");
                        } else if (error == Constants.ERROR_RET_THREE) {
                            commandResult.put("error",
                                    "Attacker card is frozen.");
                        } else {
                            commandResult.put("error",
                                    "Attacked card is not of type 'Tank'.");
                        }
                    }

                    break;

                case Constants.CARD_AT_POSITION:
                    int x = action.getX();
                    int y = action.getY();
                    commandResult.put("command", command);
                    commandResult.put("x", x);
                    commandResult.put("y", y);
                    if (x < table.size() && y < table.get(x).size()
                            && table.get(x).get(y) != null) {
                        ObjectNode printcard = mapper.createObjectNode();
                        printcard = RunCommand.printCard(table.get(x).get(y).getCard(), mapper);
                        commandResult.set("output", printcard);
                    } else {
                        commandResult.put("output", "No card available at that position.");
                    }
                    break;

                case Constants.USES_ABILITY:
                    int xAttackerAbility;
                    xAttackerAbility = action.getCardAttacker().getX();
                    int yAttackerAbility;
                    yAttackerAbility = action.getCardAttacker().getY();

                    int xAttackedAbility;
                    xAttackedAbility = action.getCardAttacked().getX();
                    int yAttackedAbility;
                    yAttackedAbility = action.getCardAttacked().getY();

                    error = RunCommand.useAbility(table, currPlayer, xAttackerAbility,
                            yAttackerAbility, xAttackedAbility, yAttackedAbility);

                    if (error > 0) {
                        new ObjectMapper().createObjectNode();
                        ObjectNode coordinates = mapper.createObjectNode();
                        commandResult.put("command", command);
                        coordinates.put("x", xAttackerAbility);
                        coordinates.put("y", yAttackerAbility);
                        commandResult.set("cardAttacker", coordinates);
                        coordinates = mapper.createObjectNode();
                        coordinates.put("x", xAttackedAbility);
                        coordinates.put("y", yAttackedAbility);
                        commandResult.set("cardAttacked", coordinates);

                        if (error == 1) {
                            commandResult.put("error", "Attacker card is frozen.");
                        } else if (error == 2) {
                            commandResult.put("error",
                                    "Attacker card has already attacked this turn.");
                        } else if (error == Constants.ERROR_RET_THREE) {
                            commandResult.put("error",
                                    "Attacked card does not belong to the current player.");
                        } else if (error == Constants.ERROR_RET_FOUR) {
                            commandResult.put("error",
                                    "Attacked card does not belong to the enemy.");
                        } else {
                            commandResult.put("error", "Attacked card is not of type 'Tank'.");
                        }
                    }
                    break;

                case Constants.ATTACK_HERO:
                    int xAttackerhero = action.getCardAttacker().getX();
                    int yAttackerhero = action.getCardAttacker().getY();
                    int result;
                    if (currPlayer == 1) {
                        result = RunCommand.attackHero(table, currPlayer, xAttackerhero,
                                yAttackerhero, playerTwo.getHero());
                    } else {
                        result = RunCommand.attackHero(table, currPlayer, xAttackerhero,
                                yAttackerhero, playerOne.getHero());
                    }

                    if (result > 0) {
                        if (result == Constants.ERROR_RET_FOUR) {
                            if (currPlayer == 1) {
                                Player.setPlayerOneWins(Player.getPlayerOneWins() + 1);
                                commandResult.put("gameEnded", "Player one killed the enemy hero.");

                            } else {
                                Player.setPlayerTwoWins(Player.getPlayerTwoWins() + 1);
                                commandResult.put("gameEnded", "Player two killed the enemy hero.");
                            }
                        } else {
                            new ObjectMapper().createObjectNode();
                            ObjectNode coordinates = mapper.createObjectNode();
                            commandResult.put("command", command);
                            coordinates.put("x", xAttackerhero);
                            coordinates.put("y", yAttackerhero);
                            commandResult.set("cardAttacker", coordinates);

                            if (result == 1) {
                                commandResult.put("error", "Attacker card is frozen.");
                            } else if (result == 2) {
                                commandResult.put("error",
                                        "Attacker card has already attacked this turn.");
                            } else if (result == Constants.ERROR_RET_THREE) {
                                commandResult.put("error", "Attacked card is not of type 'Tank'.");
                            }
                        }
                    }

                    break;

                case Constants.USE_HERO:
                    int affectedRow = action.getAffectedRow();

                    if (currPlayer == 1) {
                        error = RunCommand.useHero(table, currPlayer, affectedRow, playerOne);
                    } else {
                        error = RunCommand.useHero(table, currPlayer, affectedRow, playerTwo);
                    }

                    if (error > 0) {
                        new ObjectMapper().createObjectNode();
                        commandResult.put("affectedRow", affectedRow);
                        commandResult.put("command", command);

                        if (error == 1) {
                            commandResult.put("error", "Not enough mana to use hero's ability.");
                        } else if (error == 2) {
                            commandResult.put("error", "Hero has already attacked this turn.");
                        } else if (error == Constants.ERROR_RET_THREE) {
                            commandResult.put("error",
                                    "Selected row does not belong to the enemy.");
                        } else {
                            commandResult.put("error",
                                    "Selected row does not belong to the current player.");
                        }
                    }

                    break;

                case Constants.FROZEN_CARDS:
                    commandResult.put("command", command);
                    ArrayNode frozenResult = RunCommand.frozenCards(table, mapper);
                    commandResult.set("output", frozenResult);
                    break;

                case Constants.P_ONE_WINS:
                    commandResult.put("command", command);
                    commandResult.put("output", Player.getPlayerOneWins());
                    break;

                case Constants.P_TWO_WINS:
                    commandResult.put("command", command);
                    commandResult.put("output", Player.getPlayerTwoWins());
                    break;

                case Constants.TOTAL_GAMES:
                    commandResult.put("command", command);
                    commandResult.put("output",
                            Player.getPlayerOneWins() + Player.getPlayerTwoWins());
                    break;

                default:
                    return;
            }

            if (commandResult.has("output")
                    || commandResult.has("error")
                    || commandResult.has("gameEnded")) {
                output.add(commandResult);
            }
        }
    }
}
