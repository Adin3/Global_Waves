package program.format;

import lombok.Getter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Announcement {
    @Getter
    private String name;
    @Getter
    private String description;

    public Announcement(String name, String description) {
        this.name = name;
        this.description = description;
    }
    public String toString() {
        return name + "\n\t" + description + "\n";
    }
}
