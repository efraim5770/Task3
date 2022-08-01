package restassured;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

public class CuisineTests extends basic.BaseTest {

    public CuisineTests() throws IOException {
    }

    String BuildUrl()
    {
        return  "https://api.spoonacular.com/recipes/cuisine?apiKey=" + ApiKey;
    }

    void DoBasicSuccessTests(Response response) {
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertTrue(response.getTime() < 2000, "Запрос выполнялся слишком долго");
    }

    @Test
    void BananaTest() {
        Response response = RestAssured.
                given().
                contentType("application/x-www-form-urlencoded; charset=utf-8").
                formParam("title", "banana").
                formParam("ingredientList", "banana with  chocolate").
                when().
                post(BuildUrl());

        DoBasicSuccessTests(response);
    }

    @Test
    void BorschTest() {
        Response response = RestAssured.
                given().
                contentType("application/x-www-form-urlencoded; charset=utf-8").
                formParam("title", "borsch").
                formParam("ingredientList", "svekla + morkov + kartoshka + kurica").
                when().
                post(BuildUrl());

        DoBasicSuccessTests(response);
    }

    @Test
    void PelmeniTest() {
        Response response = RestAssured.
                given().
                contentType("application/x-www-form-urlencoded; charset=utf-8").
                formParam("title", "pelmeni").
                formParam("ingredientList", "testo + myaso").
                when().
                post(BuildUrl());

        DoBasicSuccessTests(response);
    }

    @Test
    void MakaroniPoFlotskiTest() {
        Response response = RestAssured.
                given().
                contentType("application/x-www-form-urlencoded; charset=utf-8").
                formParam("title", "makaroni po flotski").
                formParam("ingredientList", "makaroni + tushenka").
                when().
                post(BuildUrl());

        DoBasicSuccessTests(response);
    }

    @Test
    void TvorozhnikiTest() {
        Response response = RestAssured.
                given().
                contentType("application/x-www-form-urlencoded; charset=utf-8").
                formParam("title", "blinchiki").
                formParam("ingredientList", "tvorog + yaico + muka").
                when().
                post(BuildUrl());

        DoBasicSuccessTests(response);
    }
}