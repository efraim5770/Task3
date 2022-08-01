package restassured;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

public class ComplexSearchTests extends basic.BaseTest {

    public ComplexSearchTests() throws IOException {
    }

    String BuildUrl(String query)
    {
        return  "https://api.spoonacular.com/recipes/complexSearch?query=" + query + "&apiKey=" + ApiKey;
    }

    // базовые проверки получения данных
    //
    void DoBasicSuccessTests(Response response, Boolean expectedAnyResults) {
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertTrue(response.getTime() < 2000, "Запрос выполнялся слишком долго");
        JsonPath path = response.jsonPath();
        List<Object> results = path.getList("results");
        Assert.assertNotNull(results);

        if (expectedAnyResults)
        {
            Assert.assertTrue(results.size() > 0, "Данные не были получены");
        } else {
            Assert.assertEquals(results.size(), 0, "Данные были получены когда не ожидались");
        }
    }

    @Test
    void PastaTest() {
        Response response = RestAssured.get(BuildUrl("pasta"));
        DoBasicSuccessTests(response, true);
    }

    @Test
    void BearTest() {
        Response response = RestAssured.get(BuildUrl("bear"));
        DoBasicSuccessTests(response, false);
    }

    @Test
    void SoupTest() {
        Response response = RestAssured.get(BuildUrl("soup"));
        DoBasicSuccessTests(response, true);
    }

    @Test
    void WineTest() {
        Response response = RestAssured.get(BuildUrl("wine"));
        DoBasicSuccessTests(response, true);
    }

    @Test
    void SteakTest() {
        Response response = RestAssured.get(BuildUrl("steak"));
        DoBasicSuccessTests(response, true);
    }

}