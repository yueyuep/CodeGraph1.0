package data;

import java.time.*;

/**
 * Create by lp on 2020/1/4
 */
public interface A {
    void setTime(int hour, int minute, int second);

    void setDate(int day, int month, int year);

    void setDateAndTime(int day, int month, int year,
                        int hour, int minute, int second);

    LocalDateTime getLocalDateTime();
}
