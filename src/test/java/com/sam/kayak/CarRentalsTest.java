package com.sam.kayak;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.when;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;


public class CarRentalsTest {

    static String query;

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "https://pia.services.carrentals.com";
        RestAssured.basePath = "/api/v2/";

        query = "/destinations?language=us&limit=1000&q=san";
    }

    @Test
    public void verifyResponse() {
        when()
                .get(query)
        .then()
                .statusCode(200)
                .body("destinations", hasSize(greaterThan(2)),
                        "countries", hasSize(greaterThan(2)));
    }

    @Test
    public void returnAllCountryNames() {

        JsonPath jsonPath = get(query).jsonPath();

        List<Country> countries = jsonPath.getList("countries", Country.class);
        List<Destination> destinations = jsonPath.getList("destinations", Destination.class);

        countries.forEach(country -> country.setDestinations(destinations.stream()
                .filter(destination -> destination.getCountryId() == country.getId())
                .collect(toList())));

        countries.forEach(country -> System.out.println(country.toString()));
    }
}
