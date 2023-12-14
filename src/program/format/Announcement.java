package program.format;

import lombok.Getter;

public class Announcement {
    @Getter
    private String name;
    @Getter
    private String description;

    public Announcement(final String name, final String description) {
        this.name = name;
        this.description = description;
    }
    /**
     * override toString function for Announcement
     */
    public String toString() {
        return name + ":\n\t" + description + "\n";
    }
}
