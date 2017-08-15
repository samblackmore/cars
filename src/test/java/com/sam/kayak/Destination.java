package com.sam.kayak;

public class Destination {
    private long id;
    private String name;
    private Integer tresa_id;
    private String apt_code;
    private String latitude;
    private String longitude;
    private long country_id;

    public Destination(int id, String name, int tresa_id, String apt_code, String latitude, String longitude, int country_id) {
        this.id = id;
        this.name = name;
        this.tresa_id = tresa_id;
        this.apt_code = apt_code;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country_id = country_id;
    }

    String getEscapedName() {
        if (name.contains(","))
            name = String.format("\"%s\"", name);
        return name;
    }

    long getCountryId() {
        return country_id;
    }
}
