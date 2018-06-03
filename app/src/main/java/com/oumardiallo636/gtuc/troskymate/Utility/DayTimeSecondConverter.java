package com.oumardiallo636.gtuc.troskymate.Utility;

import java.util.concurrent.TimeUnit;

public class DayTimeSecondConverter {

    long seconds;
    long min;
    long hour;
    int day;

    public DayTimeSecondConverter(long seconds) {

        convert(seconds);
    }

    private void convert(long seconds){

        int day = (int) TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24);
        long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60);
        long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) * 60);

        setDay(day);
        setHour(hours);
        setMin(minute);
        setSeconds(second);
    }

    public long getSeconds() {
        return seconds;
    }

    public void setSeconds(long seconds) {
        this.seconds = seconds;
    }

    public long getMin() {
        return min;
    }

    public void setMin(long min) {
        this.min = min;
    }

    public long getHour() {
        return hour;
    }

    public void setHour(long hour) {
        this.hour = hour;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}
