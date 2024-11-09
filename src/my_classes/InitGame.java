package my_classes;

import my_classes.RunCommand;
import my_classes.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.ActionsInput;
import fileio.CardInput;
import fileio.GameInput;
import fileio.Input;
import java.util.ArrayList;

public class InitGame {

    public static void init_game(Input input, ArrayNode output, GameInput game, int playerOneWins, int playerTwoWins, ArrayList<ArrayList<TableCard>> table, Player playerOne, Player playerTwo) {
        //am initiat cei doi playeri

        /*Player playerOne = new Player();
        Player playerTwo = new Player();*/
        /*table = new ArrayList<>();

        for (int i = 0; i < Constants.rows; i++) {
            ArrayList<TableCard> row = new ArrayList<>();
            table.add(row);
        }*/


        int no_rounds = 1;
        int current_player = 0;
        // extrag deck-urile

        playerOne.setPlayer_deck(input.getPlayerOneDecks().getDecks().get(game.getStartGame().getPlayerOneDeckIdx()));
        playerTwo.setPlayer_deck(input.getPlayerTwoDecks().getDecks().get(game.getStartGame().getPlayerTwoDeckIdx()));

        playerOne.setHero(game.getStartGame().getPlayerOneHero());
        playerTwo.setHero(game.getStartGame().getPlayerTwoHero());

        playerOne.getHero().setHealth(30);
        playerTwo.getHero().setHealth(30);

        int seed = game.getStartGame().getShuffleSeed();

        ShuffleCards.Shuffle_Cards(playerOne.getPlayer_deck(), playerTwo.getPlayer_deck(), seed);
        if(!playerOne.getPlayer_deck().isEmpty())
            playerOne.getHand().add(playerOne.getPlayer_deck().remove(0));

        if(!playerTwo.getPlayer_deck().isEmpty())
            playerTwo.getHand().add(playerTwo.getPlayer_deck().remove(0));

        playerOne.setMana(1);
        playerTwo.setMana(1);

        playerOne.setFinished_turn(false);
        playerTwo.setFinished_turn(false);

        current_player = game.getStartGame().getStartingPlayer();

        ObjectMapper mapper = new ObjectMapper();

        for(ActionsInput action : game.getActions()) {
            ObjectNode commandResult = mapper.createObjectNode();
            String command = action.getCommand();

            switch (command) {
                case Constants.PlayerDeck: {
                    commandResult.put("command", command);
                    commandResult.put("playerIdx", action.getPlayerIdx());
                    ArrayNode DeckResult;
                    if(action.getPlayerIdx() == 1)
                        DeckResult = RunCommand.RunPlayerDeck(playerOne.getPlayer_deck(), mapper);
                    else
                        DeckResult = RunCommand.RunPlayerDeck(playerTwo.getPlayer_deck(), mapper);

                    commandResult.set("output", DeckResult);

                    break;
                }

                case Constants.PlayerHero: {
                    commandResult.put("command", command);
                    commandResult.put("playerIdx", action.getPlayerIdx());
                    ObjectNode HeroResult;
                    if(action.getPlayerIdx() == 1)
                        HeroResult = RunCommand.RunPlayerHero(playerOne.getHero(),mapper);
                    else
                        HeroResult = RunCommand.RunPlayerHero(playerTwo.getHero(),mapper);

                    commandResult.set("output", HeroResult);
                    break;
                }
                case Constants.PlayerTurn: {
                    commandResult.put("command", command);
                    commandResult.put("output", current_player);

                    break;
                }
                case Constants.PlayerMana: {
                    commandResult.put("command", command);
                    commandResult.put("playerIdx", action.getPlayerIdx());
                    if(action.getPlayerIdx() == 1)
                        commandResult.put("output", playerOne.getMana());
                    else
                        commandResult.put("output", playerTwo.getMana());

                    break;
                }

                case Constants.CardsInHand: {
                    commandResult.put("command", command);
                    commandResult.put("playerIdx", action.getPlayerIdx());
                    ArrayNode CardsResult;
                    if(action.getPlayerIdx() == 1)
                        CardsResult = RunCommand.RunPlayerHand(playerOne.getHand(), mapper);
                    else
                        CardsResult = RunCommand.RunPlayerHand(playerTwo.getHand(), mapper);

                    commandResult.set("output", CardsResult);
                    break;
                }

                case Constants.EndTurn: {
                    if(current_player == 1) {
                        playerOne.setFinished_turn(true);
                        for(TableCard card : table.get(Constants.playerOneFrontRow))
                            if(card != null)
                                card.set_frozen(false);

                        for(TableCard card : table.get(Constants.playerOneBackRow))
                            if(card != null)
                                card.set_frozen(false);

                        current_player = 2;
                    } else {
                        playerTwo.setFinished_turn(true);
                        for(TableCard card : table.get(Constants.playerTwoFrontRow))
                            if(card != null)
                                card.set_frozen(false);

                        for(TableCard card : table.get(Constants.playerTwoBackRow))
                            if(card != null)
                                card.set_frozen(false);
                        current_player = 1;
                    }

                    if(playerOne.isFinished_turn() && playerTwo.isFinished_turn()) {
                        no_rounds++;
                        RunCommand.InitRound(playerOne, playerTwo, no_rounds, table);
                    }
                    break;
                }

                case Constants.CardsOnTable: {
                    commandResult.put("command", command);
                    ArrayNode TableResult = RunCommand.RunTableCards(table, mapper);
                    commandResult.set("output", TableResult);
                    break;
                }

                case Constants.placeCard: {
                    int idx = action.getHandIdx();
                    int status;
                    if(current_player == 1) {
                        if(idx < playerOne.getHand().size()) {
                            CardInput card = playerOne.getHand().get(idx);
                            if (playerOne.getMana() < card.getMana()) {
                                commandResult.put("command", command);
                                commandResult.put("handIdx", idx);
                                commandResult.put("error", "Not enough mana to place card on table.");
                            } else {
                                status = RunCommand.RunPlaceCard(table, card, current_player);

                                if (status == 1) {
                                    playerOne.getHand().remove(idx);
                                    playerOne.setMana(playerOne.getMana() - card.getMana());
                                } else {
                                    commandResult.put("command", command);
                                    commandResult.put("handIdx", idx);
                                    commandResult.put("error", "Cannot place card on table since row is full.");

                                }
                            }
                        }
                    } else {
                        if (idx < playerTwo.getHand().size()) {
                            CardInput card = playerTwo.getHand().get(idx);
                            if (playerTwo.getMana() < card.getMana()) {
                                commandResult.put("command", command);
                                commandResult.put("handIdx", idx);
                                commandResult.put("error", "Not enough mana to place card on table.");
                            } else {
                                status = RunCommand.RunPlaceCard(table, card, current_player);

                                if (status == 1) {
                                    playerTwo.getHand().remove(idx);
                                    playerTwo.setMana(playerTwo.getMana() - card.getMana());
                                } else {
                                    commandResult.put("command", command);
                                    commandResult.put("handIdx", idx);
                                    commandResult.put("error", "Cannot place card on table since row is full.");
                                }
                            }
                        }
                    }

                    break;
                }

                case Constants.AttackCard: {

                    int x_attacker = action.getCardAttacker().getX();
                    int y_attacker = action.getCardAttacker().getY();

                    int x_attacked = action.getCardAttacked().getX();
                    int y_attacked = action.getCardAttacked().getY();

                    int error = RunCommand.RunAttackCard(table, current_player, x_attacker, y_attacker, x_attacked, y_attacked);

                    if(error > 0) {
                        ObjectNode coordinates = new ObjectMapper().createObjectNode();
                        coordinates = mapper.createObjectNode();
                        commandResult.put("command", command);
                        coordinates.put("x", x_attacker);
                        coordinates.put("y", y_attacker);
                        commandResult.set("cardAttacker", coordinates);
                        coordinates = mapper.createObjectNode();
                        coordinates.put("x", x_attacked);
                        coordinates.put("y", y_attacked);
                        commandResult.set("cardAttacked", coordinates);

                        if (error == 1)
                            commandResult.put("error", "Attacked card does not belong to the enemy.");
                        else if (error == 2)
                            commandResult.put("error", "Attacker card has already attacked this turn.");
                        else if (error == 3)
                            commandResult.put("error", "Attacker card is frozen.");
                        else
                            commandResult.put("error", "Attacked card is not of type 'Tank'.");
                    }

                    break;
                }

                case Constants.CardAtPosition: {
                    int x = action.getX();
                    int y = action.getY();
                    commandResult.put("command", command);
                    commandResult.put("x", x);
                    commandResult.put("y", y);
                    if(x<table.size() && y < table.get(x).size() && table.get(x).get(y) != null) {
                        ObjectNode CardToPrint = mapper.createObjectNode();
                        CardToPrint = RunCommand.PrintCard(table.get(x).get(y).getCard(), mapper);
                        commandResult.set("output", CardToPrint);
                    } else {
                        commandResult.put("output", "No card available at that position.");
                    }
                    break;
                }

                case Constants.UseAbility: {
                    int x_attackerAbility;
                    x_attackerAbility = action.getCardAttacker().getX();
                    int y_attackerAbility;
                    y_attackerAbility = action.getCardAttacker().getY();

                    int x_attackedAbility;
                    x_attackedAbility = action.getCardAttacked().getX();
                    int y_attackedAbility;
                    y_attackedAbility = action.getCardAttacked().getY();

                    int error = RunCommand.RunUseAbility(table, current_player, x_attackerAbility, y_attackerAbility, x_attackedAbility, y_attackedAbility);

                    if(error > 0) {
                        new ObjectMapper().createObjectNode();
                        ObjectNode coordinates = mapper.createObjectNode();
                        commandResult.put("command", command);
                        coordinates.put("x", x_attackerAbility);
                        coordinates.put("y", y_attackerAbility);
                        commandResult.set("cardAttacker", coordinates);
                        coordinates = mapper.createObjectNode();
                        coordinates.put("x", x_attackedAbility);
                        coordinates.put("y", y_attackedAbility);
                        commandResult.set("cardAttacked", coordinates);

                        if (error == 1)
                            commandResult.put("error", "Attacker card is frozen.");
                        else if (error == 2)
                            commandResult.put("error", "Attacker card has already attacked this turn.");
                        else if (error == 3)
                            commandResult.put("error", "Attacked card does not belong to the current player.");
                        else if(error == 4)
                            commandResult.put("error", "Attacked card does not belong to the enemy.");
                        else
                            commandResult.put("error", "Attacked card is not of type 'Tank'.");
                    }
                    break;
                }

                case Constants.AttackHero: {
                    int x_attacker = action.getCardAttacker().getX();
                    int y_attacker = action.getCardAttacker().getY();
                    int result;
                    if(current_player == 1)
                        result = RunCommand.RunAttackHero(table, current_player, x_attacker, y_attacker, playerTwo.getHero());
                    else
                        result = RunCommand.RunAttackHero(table, current_player, x_attacker, y_attacker, playerOne.getHero());

                    if(result > 0) {
                        if(result == 4) {
                            if(current_player == 1) {
                                commandResult.put("gameEnded", "Player one killed the enemy hero.");
                                playerOneWins ++;
                            }
                            else{
                                commandResult.put("gameEnded", "Player two killed the enemy hero.");
                                playerTwoWins ++;
                            }
                        } else {
                            new ObjectMapper().createObjectNode();
                            ObjectNode coordinates = mapper.createObjectNode();
                            commandResult.put("command", command);
                            coordinates.put("x", x_attacker);
                            coordinates.put("y", y_attacker);
                            commandResult.set("cardAttacker", coordinates);

                            if (result == 1)
                                commandResult.put("error", "Attacker card is frozen.");
                            else if (result == 2)
                                commandResult.put("error", "Attacker card has already attacked this turn.");
                            else if (result == 3)
                                commandResult.put("error", "Attacked card is not of type 'Tank'.");
                        }
                    }

                    break;
                }

                case Constants.UseHero:{
                    int affected_row = action.getAffectedRow();
                    int error;

                    if(current_player == 1)
                        error = RunCommand.RunUseHero(table, current_player, affected_row, playerOne);
                    else
                        error = RunCommand.RunUseHero(table, current_player, affected_row, playerTwo);

                    if(error > 0) {
                        new ObjectMapper().createObjectNode();
                        commandResult.put("affectedRow", affected_row);
                        commandResult.put("command", command);

                        if (error == 1)
                            commandResult.put("error", "Not enough mana to use hero's ability.");
                        else if (error == 2)
                            commandResult.put("error", "Hero has already attacked this turn.");
                        else if (error == 3)
                            commandResult.put("error", "Selected row does not belong to the enemy.");
                        else
                            commandResult.put("error", "Selected row does not belong to the current player.");
                    }

                    break;
                }

                case Constants.FrozenCards: {
                    commandResult.put("command", command);
                    ArrayNode FrozenResult = RunCommand.RunFrozenCards(table, mapper);
                    commandResult.set("output", FrozenResult);
                    break;
                }

                case Constants.PlayerOneWins: {
                    commandResult.put("command", command);
                    commandResult.put("output", playerOneWins);
                    break;
                }

                case Constants.PlayerTwoWins: {
                    commandResult.put("command", command);
                    commandResult.put("output", playerTwoWins);
                    break;
                }

                case Constants.TotalGames: {
                    commandResult.put("command", command);
                    commandResult.put("output", playerOneWins + playerTwoWins);
                    break;
                }
            }

            if(commandResult.has("output") || commandResult.has("error") || commandResult.has("gameEnded"))
                output.add(commandResult);
        }
    }
}
