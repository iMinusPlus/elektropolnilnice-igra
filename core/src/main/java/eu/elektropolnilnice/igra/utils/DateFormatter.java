package eu.elektropolnilnice.igra.utils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateFormatter {
    public static String formatDate(String isoDate) {
        try {
            ZonedDateTime dateTime = ZonedDateTime.parse(isoDate);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd. MMMM yyyy");

            return dateTime.format(formatter);
        } catch (Exception e) {
            return isoDate;
        }
    }
}

