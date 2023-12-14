package program.format;

import lombok.Getter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Event {
    @Getter
    private String name;
    @Getter
    private String description;
    @Getter
    private String date;

    private static final int MINIMUM_YEAR = 1900;

    private static final int MAXIMUM_YEAR = 2023;

    public Event(final String name, final String description, final String date) {
        this.name = name;
        this.description = description;
        this.date = date;
    }

    /**
     * checks if date is valid
     * @return true if date is valid, false otherwise
     */
    public boolean checkDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setLenient(false);

        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateFormat.parse(date));

            int year = calendar.get(Calendar.YEAR);
            return (year >= MINIMUM_YEAR && year <= MAXIMUM_YEAR);
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * override toString function for Event
     */
    public String toString() {
        return name + " - " + date + ":\n\t" + description;
    }

}
