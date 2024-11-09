package my_classes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.CardInput;
import fileio.DecksInput;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class RunCommand {

    public static ArrayNode RunPlayerDeck(ArrayList<CardInput> printedDeck, ObjectMapper mapper) {
        ArrayNode result = mapper.createArrayNode();

        for (CardInput card : printedDeck) {
            ObjectNode CardToPrint = mapper.createObjectNode();
            CardToPrint = PrintCard(card, mapper);
            result.add(CardToPrint);
        }
        return result;
    }

    public static ObjectNode RunPlayerHero(CardInput printedHero, ObjectMapper mapper) {
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

    public static ArrayNode RunPlayerHand(ArrayList<CardInput> printedHand, ObjectMapper mapper) {
        ArrayNode CardsInHand = mapper.createArrayNode();

        for (CardInput card : printedHand) {
            ObjectNode CardToPrint = mapper.createObjectNode();
            CardToPrint = PrintCard(card, mapper);
            CardsInHand.add(CardToPrint);
        }
        return CardsInHand;
    }

    public static ArrayNode RunTableCards(ArrayList<ArrayList<TableCard>> table, ObjectMapper mapper) {
        ArrayNode output = mapper.createArrayNode();

        for (ArrayList<TableCard> row : table) {
            ArrayNode cards = mapper.createArrayNode();
            for (TableCard card : row) {
                ObjectNode cardToPrint = null;

                if (card != null) {
                    cardToPrint = PrintCard(card.getCard(), mapper);
                }

                if (cardToPrint != null && card.getCard().getName() != null) {
                    cards.add(cardToPrint);
                }
            }

            output.add(cards);
        }
        return output;
    }

    public static int RunPlaceCard(ArrayList<ArrayList<TableCard>> table, CardInput card, int current_player) {
        String name = card.getName();
        int has_added = 0;

        // Check if the card belongs to the front row
        if (Arrays.asList(Constants.frontRow).contains(name)) {
            if (current_player == 1) {
                ArrayList<TableCard> playerOneFrontRow = table.get(Constants.playerOneFrontRow);
                if (playerOneFrontRow.size() < 5) {
                TableCard new_card = new TableCard();
                new_card.setCard(card);
                playerOneFrontRow.add(new_card);
                has_added = 1;
                }
            } else {
                ArrayList<TableCard> playerTwoFrontRow = table.get(Constants.playerTwoFrontRow);
                if (playerTwoFrontRow.size() < 5) {
                TableCard new_card = new TableCard();
                new_card.setCard(card);
                playerTwoFrontRow.add(new_card);
                has_added = 1;
                }
            }

            // Check if the card belongs to the back row
        } else if (Arrays.asList(Constants.backRow).contains(name)) {
            if (current_player == 1) {
                ArrayList<TableCard> playerOneBackRow = table.get(Constants.playerOneBackRow);
                if (playerOneBackRow.size() < 5) {
                TableCard new_card = new TableCard();
                new_card.setCard(card);
                playerOneBackRow.add(new_card);
                has_added = 1;
                }
            } else {
                ArrayList<TableCard> playerTwoBackRow = table.get(Constants.playerTwoBackRow);
                if (playerTwoBackRow.size() < 5) {

                TableCard new_card = new TableCard();
                new_card.setCard(card);
                playerTwoBackRow.add(new_card);
                has_added = 1;
                }
            }
        }

        return has_added;
    }

    public static int RunAttackCard(ArrayList<ArrayList<TableCard>> table, int current_player, int x_attacker, int y_attacker, int x_attacked, int y_attacked) {

        if(current_player == 1) {
            if(x_attacked == Constants.playerOneBackRow || x_attacked == Constants.playerOneFrontRow) {
                return 1;
            }

            int has_tanks = 0;
            int tankIsAttacked = 0;

            for(TableCard card : table.get(Constants.playerTwoFrontRow)) {
                if(card != null) {
                    if(Arrays.asList(Constants.Tanks).contains(card.getCard().getName())) {
                        has_tanks = 1;
                        if(x_attacked < table.size() && y_attacked < table.get(x_attacked).size() && card == table.get(x_attacked).get(y_attacked)){
                            tankIsAttacked = 1;
                        }
                    }
                }
            }

            if(has_tanks == 1 && tankIsAttacked == 0) {
                return 4;
            }

        } else {
            if(x_attacked == Constants.playerTwoBackRow || x_attacked == Constants.playerTwoFrontRow) {
                return 1;
            }

            int has_tanks = 0;
            int tankIsAttacked = 0;

            for(TableCard card : table.get(Constants.playerOneFrontRow)) {
                if(card != null) {
                    if(Arrays.asList(Constants.Tanks).contains(card.getCard().getName())) {
                        has_tanks = 1;
                        if(x_attacked < table.size() && y_attacked < table.get(x_attacked).size() && card == table.get(x_attacked).get(y_attacked)){
                            tankIsAttacked = 1;
                        }
                    }
                }
            }

            if(has_tanks == 1 && tankIsAttacked == 0) {
                return 4;
            }
        }

        if(x_attacker < table.size() && y_attacker < table.get(x_attacker).size() && table.get(x_attacker).get(y_attacker).isUsed())
            return 2;

        if(x_attacker < table.size() && y_attacker < table.get(x_attacker).size() && table.get(x_attacker).get(y_attacker).is_frozen())
            return 3;

        if(x_attacker < table.size() && y_attacker < table.get(x_attacker).size() && x_attacked < table.size() && y_attacked < table.get(x_attacked).size())
            table.get(x_attacked).get(y_attacked).getCard().setHealth(table.get(x_attacked).get(y_attacked).getCard().getHealth() - table.get(x_attacker).get(y_attacker).getCard().getAttackDamage());

        if(y_attacked < table.get(x_attacked).size() && table.get(x_attacked).get(y_attacked).getCard().getHealth() <= 0)
            table.get(x_attacked).remove(y_attacked);

        if(x_attacker < table.size() && y_attacker < table.get(x_attacker).size())
            table.get(x_attacker).get(y_attacker).setUsed(true);

        return 0;
    }

    public static int RunUseAbility(ArrayList<ArrayList<TableCard>> table, int current_player, int x_attackerAbility, int y_attackerAbility, int x_attackedAbility, int y_attackedAbility) {
        if(x_attackerAbility < table.size() && y_attackerAbility < table.get(x_attackerAbility).size() && x_attackedAbility < table.size() && y_attackedAbility < table.get(x_attackedAbility).size()) {
            if (table.get(x_attackerAbility).get(y_attackerAbility).is_frozen())
                return 1;

            if (table.get(x_attackerAbility).get(y_attackerAbility).isUsed())
                return 2;

            if(table.get(x_attackerAbility).get(y_attackerAbility).getCard().getName().equals("Disciple")) {
                if((current_player == 1 && (x_attackedAbility == Constants.playerTwoBackRow || x_attackedAbility == Constants.playerTwoFrontRow))
                        || current_player == 2 && (x_attackedAbility == Constants.playerOneBackRow || x_attackedAbility == Constants.playerOneFrontRow)){
                    return 3;
                }

            } else if (table.get(x_attackerAbility).get(y_attackerAbility).getCard().getName().equals("The Ripper") ||
                    table.get(x_attackerAbility).get(y_attackerAbility).getCard().getName().equals("Miraj") ||
                    table.get(x_attackerAbility).get(y_attackerAbility).getCard().getName().equals("The Cursed One")) {
                if((current_player == 1 && (x_attackedAbility == Constants.playerOneBackRow || x_attackedAbility == Constants.playerOneFrontRow))
                        || current_player == 2 && (x_attackedAbility == Constants.playerTwoBackRow || x_attackedAbility == Constants.playerTwoFrontRow)){
                    return 4;
                }

                int has_tanks = 0;
                int tankIsAttacked = 0;
                if(current_player == 1) {

                    for (TableCard card : table.get(Constants.playerTwoFrontRow)) {
                        if (card != null) {
                            if (Arrays.asList(Constants.Tanks).contains(card.getCard().getName())) {
                                has_tanks = 1;
                                if (card == table.get(x_attackedAbility).get(y_attackedAbility)) {
                                    tankIsAttacked = 1;
                                }
                            }
                        }
                    }

                } else {

                    for (TableCard card : table.get(Constants.playerOneFrontRow)) {
                        if (card != null) {
                            if (Arrays.asList(Constants.Tanks).contains(card.getCard().getName())) {
                                has_tanks = 1;
                                if (y_attackedAbility < table.get(x_attackedAbility).size() && card == table.get(x_attackedAbility).get(y_attackedAbility)) {
                                    tankIsAttacked = 1;
                                }
                            }
                        }
                    }

                }
                if (has_tanks == 1 && tankIsAttacked == 0) {
                    return 5;
                }
            }

            if(table.get(x_attackerAbility).get(y_attackerAbility).getCard().getName().equals("The Ripper")) {

                table.get(x_attackerAbility).get(y_attackerAbility).setUsed(true);

                table.get(x_attackedAbility).get(y_attackedAbility).getCard().setAttackDamage(table.get(x_attackedAbility).get(y_attackedAbility).getCard().getAttackDamage() - 2);

                if(table.get(x_attackedAbility).get(y_attackedAbility).getCard().getAttackDamage() <= 0)
                    table.get(x_attackedAbility).get(y_attackedAbility).getCard().setAttackDamage(0);

            } else if(table.get(x_attackerAbility).get(y_attackerAbility).getCard().getName().equals("Miraj")) {

                table.get(x_attackerAbility).get(y_attackerAbility).setUsed(true);

                int health_attacker = table.get(x_attackerAbility).get(y_attackerAbility).getCard().getHealth();

                int health_attacked = table.get(x_attackedAbility).get(y_attackedAbility).getCard().getHealth();

                table.get(x_attackedAbility).get(y_attackedAbility).getCard().setHealth(health_attacker);

                table.get(x_attackerAbility).get(y_attackerAbility).getCard().setHealth(health_attacked);

            } else if(table.get(x_attackerAbility).get(y_attackerAbility).getCard().getName().equals("The Cursed One")) {

                table.get(x_attackerAbility).get(y_attackerAbility).setUsed(true);

                int health_attacked = table.get(x_attackedAbility).get(y_attackedAbility).getCard().getHealth();

                int attack_attacked = table.get(x_attackedAbility).get(y_attackedAbility).getCard().getAttackDamage();

                table.get(x_attackedAbility).get(y_attackedAbility).getCard().setHealth(attack_attacked);

                table.get(x_attackedAbility).get(y_attackedAbility).getCard().setAttackDamage(health_attacked);

                if(table.get(x_attackedAbility).get(y_attackedAbility).getCard().getHealth() <= 0)
                    table.get(x_attackedAbility).remove(y_attackedAbility);

            } else if(table.get(x_attackerAbility).get(y_attackerAbility).getCard().getName().equals("Disciple")) {

                table.get(x_attackerAbility).get(y_attackerAbility).setUsed(true);

                table.get(x_attackedAbility).get(y_attackedAbility).getCard().setHealth(table.get(x_attackedAbility).get(y_attackedAbility).getCard().getHealth() + 2);
            }

            return 0;
        }

        return -1;
    }

    public static int RunAttackHero(ArrayList<ArrayList<TableCard>> table, int current_player, int x_attacker, int y_attacker, CardInput hero) {
        if(x_attacker < table.size() && y_attacker < table.get(x_attacker).size()) {
            if (table.get(x_attacker).get(y_attacker).is_frozen())
                return 1;

            if (table.get(x_attacker).get(y_attacker).isUsed())
                return 2;

            int has_tanks = 0;
            if(current_player == 1) {
                for (TableCard card : table.get(Constants.playerTwoFrontRow)) {
                    if (card != null) {
                        if (Arrays.asList(Constants.Tanks).contains(card.getCard().getName())) {
                            has_tanks = 1;
                        }
                    }
                }

            } else {
                for (TableCard card : table.get(Constants.playerOneFrontRow)) {
                    if (card != null) {
                        if (Arrays.asList(Constants.Tanks).contains(card.getCard().getName())) {
                            has_tanks = 1;
                        }
                    }
                }

            }
            if (has_tanks == 1) {
                return 3;
            }

            int damage = table.get(x_attacker).get(y_attacker).getCard().getAttackDamage();
            hero.setHealth(hero.getHealth() - damage);
            table.get(x_attacker).get(y_attacker).setUsed(true);
            if(hero.getHealth() <= 0)
                return 4;

            return 0;
        }
        return -1;
    }

    public static int RunUseHero(ArrayList<ArrayList<TableCard>> table, int current_player, int affected_row, Player player) {
        if(affected_row < table.size()){
            if(player.getMana() < player.getHero().getMana())
                return 1;

            if(player.isUsedHero())
                return 2;

            if(current_player == 1){
                if(Arrays.asList(Constants.AffectsOpositePlayer).contains(player.getHero().getName()) && (affected_row == Constants.playerOneFrontRow || affected_row == Constants.playerOneBackRow)) {
                    return 3;
                }

                if(Arrays.asList(Constants.AffectsCurrentPLayer).contains(player.getHero().getName()) && (affected_row == Constants.playerTwoFrontRow || affected_row == Constants.playerTwoBackRow)) {
                    return 4;
                }

            } else {
                if(Arrays.asList(Constants.AffectsOpositePlayer).contains(player.getHero().getName()) && (affected_row == Constants.playerTwoFrontRow || affected_row == Constants.playerTwoBackRow)) {
                    return 3;
                }

                if(Arrays.asList(Constants.AffectsCurrentPLayer).contains(player.getHero().getName()) && (affected_row == Constants.playerOneFrontRow || affected_row == Constants.playerOneBackRow)) {
                    return 4;
                }
            }

            if(player.getHero().getName().equals("Lord Royce")){
                for(TableCard card : table.get(affected_row)){
                    card.set_frozen(true);
                }
            } else if(player.getHero().getName().equals("Empress Thorina")){
                int destroy_idx = 0;
                for(int i = 0; i < table.get(affected_row).size(); i++){
                    if(table.get(affected_row).get(i) != null && table.get(affected_row).get(i).getCard().getHealth() > table.get(affected_row).get(destroy_idx).getCard().getHealth())
                        destroy_idx = i;
                }

                if(destroy_idx < table.get(affected_row).size())
                    table.get(affected_row).remove(destroy_idx);

            } else if(player.getHero().getName().equals("King Mudface")){
                for(TableCard card : table.get(affected_row))
                    if(card != null)
                        card.getCard().setHealth(card.getCard().getHealth() + 1);
            } else {
                for(TableCard card : table.get(affected_row))
                    if(card != null)
                        card.getCard().setAttackDamage(card.getCard().getAttackDamage() + 1);
            }

            player.setMana(player.getMana() - player.getHero().getMana());
            player.setUsedHero(true);

            return 0;
        }

        return -1;
    }

    public static ArrayNode RunFrozenCards(ArrayList<ArrayList<TableCard>> table, ObjectMapper mapper) {
        ArrayNode output = mapper.createArrayNode();

        for (ArrayList<TableCard> row : table) {
            for (TableCard card : row) {
                if (card != null && card.is_frozen()) {
                    ObjectNode cardToPrint = PrintCard(card.getCard(), mapper);
                    output.add(cardToPrint);
                }
            }
        }

        return output;
    }


    public static void InitRound(Player playerOne, Player playerTwo, int no_round, ArrayList<ArrayList<TableCard>> table) {
        if(!playerOne.getPlayer_deck().isEmpty()) {
            CardInput firstCard = playerOne.getPlayer_deck().remove(0);
            playerOne.getHand().add(firstCard);
        }

        if(!playerTwo.getPlayer_deck().isEmpty()) {
            CardInput firstCard = playerTwo.getPlayer_deck().remove(0);
            playerTwo.getHand().add(firstCard);
        }

        playerOne.setFinished_turn(false);
        playerOne.setUsedHero(false);

        playerTwo.setFinished_turn(false);
        playerTwo.setUsedHero(false);

        if(no_round <= Constants.max_inc){
            playerOne.setMana(playerOne.getMana() + no_round);
            playerTwo.setMana(playerTwo.getMana() + no_round);
        } else {
            playerOne.setMana(playerOne.getMana() + Constants.max_inc);
            playerTwo.setMana(playerTwo.getMana() + Constants.max_inc);
        }

        for(ArrayList<TableCard> row : table)
            for(TableCard card : row) {
                if(card != null) {
                    card.setUsed(false);
                }
            }

    }

    public static ObjectNode PrintCard(CardInput printedCard, ObjectMapper mapper) {
        ObjectNode print = mapper.createObjectNode();
        print.put("mana", printedCard.getMana());
        print.put("attackDamage", printedCard.getAttackDamage());
        print.put("health", printedCard.getHealth());
        print.put("description", printedCard.getDescription());

        ArrayNode colors = mapper.createArrayNode();
        if(printedCard.getColors() != null) {
        for (String color : printedCard.getColors()) {
            colors.add(color);
        }
        }

        print.set("colors", colors);
        print.put("name", printedCard.getName());
        return print;
    }
}
