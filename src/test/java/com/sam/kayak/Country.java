package com.sam.kayak;

import org.apache.commons.lang3.text.StrBuilder;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class Country {
    private long id;
    private String name;
    private String code;
    private List<Destination> destinations;

    public Country(int id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

    long getId() {
        return id;
    }

    void setDestinations(List<Destination> destinations) {
        this.destinations = destinations;
    }

    @Override
    public String toString() {
        StrBuilder s = new StrBuilder();
        s.appendln(this.name);
        s.appendln(this.destinations.size() + " cities:");
        s.appendln(String.join(", ", this.destinations.stream().map(Destination::getEscapedName).collect(toList())));
        return s.toString();
    }
}
