package program.format;

import lombok.Getter;

public class Merch {
    @Getter
    private String name;
    @Getter
    private int price;
    @Getter
    private String description;

    public Merch(String name, int price, String description) {
        this.name = name;
        this.price = price;
        this.description = description;
    }
    public boolean validMerch() {
        return price >= 0;
    }

    public String toString() {
        return name + " - " + price + ":\n\t" + description;
    }
}
