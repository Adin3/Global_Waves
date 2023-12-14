package program.format;

import lombok.Getter;

public class Merch {
    @Getter
    private String name;
    @Getter
    private int price;
    @Getter
    private String description;

    public Merch(final String name, final int price, final String description) {
        this.name = name;
        this.price = price;
        this.description = description;
    }

    /**
     * checks if merch is valid
     * @return true if merch is valid, false otherwise
     */
    public boolean validMerch() {
        return price >= 0;
    }


    /**
     * override toString function for Merch
     */
    public String toString() {
        return name + " - " + price + ":\n\t" + description;
    }
}
