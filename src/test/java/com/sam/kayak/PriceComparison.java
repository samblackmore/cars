package com.sam.kayak;

import org.apache.commons.lang3.text.StrBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PriceComparison {

    private String pickup;
    private String dropoff;
    private Date pickupDate;
    private Date dropoffDate;
    private int lowestPriceOneWay;
    private int lowestPriceReturn;

    PriceComparison(String pickup, String dropoff, Date pickupDate, Date dropoffDate,
                    int lowestPriceOneWay, int lowestPriceReturn) {

        this.pickup = pickup;
        this.dropoff = dropoff;
        this.pickupDate = pickupDate;
        this.dropoffDate = dropoffDate;
        this.lowestPriceOneWay = lowestPriceOneWay;
        this.lowestPriceReturn = lowestPriceReturn;
    }

    public int getLowestPriceOneWay() {
        return lowestPriceOneWay;
    }

    public int getLowestPriceReturn() {
        return lowestPriceReturn;
    }

    @Override
    public String toString() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        StrBuilder s = new StrBuilder();

        s.appendln(String.format("%s - %s", pickup, dropoff));
        s.appendln(String.format("%s - %s", dateFormat.format(pickupDate), dateFormat.format(dropoffDate)));
        s.appendln(String.format("Cheapest one-way: %s", lowestPriceOneWay));
        s.appendln(String.format("Cheapest return: %s", lowestPriceReturn));

        return s.toString();
    }
}