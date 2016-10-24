package ru.pastie.om;

import java.util.Calendar;
import java.util.Date;

public enum Expiration {
    NEVER("Never", null, 0),
    MIN_10("10 Min", Calendar.MINUTE, 10),
    HOUR_1("1 Hour", Calendar.HOUR_OF_DAY, 1),
    DAY_1("1 Day", Calendar.DAY_OF_YEAR, 1),
    WEEK("1 Week", Calendar.DAY_OF_YEAR, 7),
    MONTH("1 Month", Calendar.DAY_OF_MONTH, 1);
    
    private final String name;
    private final Integer period;
    private final int num;
    
    private Expiration(String name, Integer period, int num) {
        this.name = name;
        this.period = period;
        this.num = num;
        
    }
    
    public Date getExpirationDate(Date date) {
        if(period == null)
            return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(period, num);
        return cal.getTime();
    }
    
    public String getName() {
        return name;
    }
}
