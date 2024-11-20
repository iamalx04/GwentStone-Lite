package playinggame;

import fileio.CardInput;
import lombok.Getter;
import lombok.Setter;

public class TableCard {

        @Getter @Setter private CardInput card;
        @Getter @Setter private boolean isFrozen;
        @Getter @Setter private boolean used;

        public TableCard() {
            this.card = new CardInput();
            this.isFrozen = false;
            this.used = false;
        }
}
