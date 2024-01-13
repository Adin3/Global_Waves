package program.format;

import lombok.Getter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public final class Event {
    @Getter
    private String name;
    @Getter
    private String description;
    @Getter
    private String date;

    private static final int MINIMUM_YEAR = 1900;

    private static final int MAXIMUM_YEAR = 2023;

    private Event() { }

    public static class EventBuilder {
        private Event event;

        public EventBuilder() {
            event = new Event();
        }

        /**
         * takes the name of event
         * @param name name of the event
         */
        public EventBuilder withName(final String name) {
            event.name = name;
            return this;
        }

        /**
         * takes the description of event
         * @param description description of the event
         */
        public EventBuilder withDescription(final String description) {
            event.description = description;
            return this;
        }
        /**
         * takes the date of event
         * @param date date of the event
         */
        public EventBuilder withDate(final String date) {
            event.date = date;
            return this;
        }
        /**
         * build the builder
         */
        public Event build() {
            return event;
        }
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
