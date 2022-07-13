import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

public class MealTestPlannerTests extends BaseTest {

    public MealTestPlannerTests() throws IOException {
    }

    String BuildUrl(String url)
    {
        return url+ "?apiKey=" + ApiKey;
    }

    @Test
    void ChainTest()
    {
        // запрашиваю список публичных планов питания
        Response response = RestAssured.get(BuildUrl("https://api.spoonacular.com/mealplanner/public-templates"));
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertTrue(response.getTime() < 2000, "Запрос выполнялся слишком долго");
        JsonPath path = response.jsonPath();

        // беру первый попавшийся план по питанию
        String mealPlanId = path.getString("templates[0].id");
        Assert.assertNotNull(mealPlanId);

        System.out.println(mealPlanId);

        // коннекчу пользователя
        // POST https://api.spoonacular.com/users/connect
        path = RestAssured.
                given().
                contentType(ContentType.JSON).
                queryParam("apiKey", ConnectKey).
                body("{\n" +
                        "    \"username\": \"efraim5770\",\n" +
                        "    \"firstName\": \"\",\n" +
                        "    \"lastName\": \"\",\n" +
                        "    \"email\": \"efraim5770@gmail.com\"\n" +
                        "}").
                when().
                post("https://api.spoonacular.com/users/connect").
                then().
                statusCode(200).
                time(Matchers.lessThan(3000L)).extract().jsonPath();


        String hash = path.getString("hash");

        System.out.println("hash=" + hash);

        // делаю заказ
        RestAssured.
                given().
                contentType(ContentType.JSON).
                queryParam("apiKey", ConnectKey).
                body("{'username':'efraim5770', 'hash':'" + hash + "'}").
                when().
                post("https://api.spoonacular.com/mealplanner/efraim5770/shopping-list/2020-06-01/2030-06-01").
                then().
                time(Matchers.lessThan(3000L));
    }
}