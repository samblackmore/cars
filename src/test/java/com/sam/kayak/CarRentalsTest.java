package com.sam.kayak;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static io.restassured.RestAssured.get;
import static java.util.stream.Collectors.toList;


public class CarRentalsTest {

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "https://pia.services.carrentals.com";
        RestAssured.basePath = "/api/v2/";
    }

    @Test
    public void returnAllCountryNames() {

        JsonPath jsonPath = get("/destinations?language=us&limit=1000&q=san").jsonPath();

        List<Country> countries = jsonPath.getList("countries", Country.class);
        List<Destination> destinations = jsonPath.getList("destinations", Destination.class);

        countries.forEach(country -> country.setDestinations(destinations.stream()
                .filter(destination -> destination.getCountryId() == country.getId())
                .collect(toList())));

        countries.forEach(country -> System.out.println(country.toString()));
    }
}
