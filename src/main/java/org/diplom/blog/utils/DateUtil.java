package org.diplom.blog.utils;

import java.sql.Timestamp;
import java.time.*;
import java.util.Date;

/**
 * @author Andrey.Kazakov
 * @date 02.09.2020
 */
public class DateUtil {
    public static final ZoneId TIME_ZONE = ZoneId.of("UTC");
    public static final ZoneOffset ZONE_OFFSET = ZoneOffset.UTC;

    public static long getTimestampFromLocalDateTime(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.toInstant(ZONE_OFFSET).getEpochSecond() : 0;
    }

    public static long getLongFromTimestamp(Timestamp timestamp) {
        LocalDateTime localDateTime = timestamp.toLocalDateTime();
        return localDateTime.toInstant(ZONE_OFFSET).getEpochSecond();
    }

    public static Long getTimestampFromLocalDate(LocalDate localDate) {
        return localDate != null ? getTimestampFromLocalDateTime(localDate.atStartOfDay()) : 0;
    }

    public static LocalDateTime getLocalDateTimeFromTimestamp(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), TIME_ZONE);
    }

    public static LocalDateTime getLocalDateTimeFromDate(Date date){
        return date.toInstant().atZone(TIME_ZONE).toLocalDateTime();
    }

    public static LocalDate getLocalDateFromDate(Date date) {
        return date.toInstant().atZone(TIME_ZONE).toLocalDate();
    }

    public static LocalDate getLocalDateFromTimestamp(long timestamp) {
        return LocalDate.ofInstant(Instant.ofEpochSecond(timestamp), TIME_ZONE);
    }
}
