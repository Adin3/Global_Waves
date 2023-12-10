package program.format;

import lombok.Getter;
import lombok.Setter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Date;

public class Event {
    @Getter
    private String name;
    @Getter
    private String description;
    @Getter
    private String date;

    public Event(String name, String description, String date) {
        this.name = name;
        this.description = description;
        this.date = date;
    }

    public boolean checkDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setLenient(false);

        try {
            Date parsedDate = dateFormat.parse(date);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parsedDate);

            int year = calendar.get(Calendar.YEAR);
            return (year >= 1900 && year <= 2023);
        } catch (ParseException e) {
            return false;
        }
    }

    public String toString() {
        return name + " - " + date + ":\n\t" + description;
    }

}
