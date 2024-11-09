package my_classes;

import fileio.CardInput;
import lombok.Getter;
import lombok.Setter;

public class TableCard {

        @Getter @Setter private CardInput card;
        @Getter @Setter private boolean is_frozen;
        @Getter @Setter private boolean used;

        public TableCard() {
            this.card = new CardInput();
            this.is_frozen = false;
            this.used = false;
        }
}
