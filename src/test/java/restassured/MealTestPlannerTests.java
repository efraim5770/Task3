package restassured;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

import java.io.IOException;

public class MealTestPlannerTests extends basic.BaseTest {

    public MealTestPlannerTests() throws IOException {
    }

    public Integer MealItemId =0;
    public String ConnectedUserName;
    public String Hash;

    @Test
    void ChainTest()
    {
        // 1. коннекчу пользователя
        // POST https://api.spoonacular.com/users/connect
        JsonPath path = RestAssured.
                given().
                contentType(ContentType.JSON).
                queryParam("apiKey", ConnectKey).
                body("{\n" +
                        "    \"username\": \"" + UserName + "\",\n" +
                        "    \"firstName\": \"\",\n" +
                        "    \"lastName\": \"\",\n" +
                        "    \"email\": \"efraim5770@gmail.com\"\n" +
                        "}").
                when().
                post("https://api.spoonacular.com/users/connect").
                then().
                statusCode(200).
                time(Matchers.lessThan(MaxResponseTime)).extract().jsonPath();


        Hash = path.getString("hash");
        ConnectedUserName = path.getString("username");
        Assert.assertNotNull(Hash);
        System.out.println("hash=" + Hash);
        System.out.println("username=" + ConnectedUserName);

        // 2. Запрашиваю текущий Shopping List
        path = RestAssured.
                given().
                queryParam("apiKey", ConnectKey).
                queryParam("hash", Hash).
                when().
                get("https://api.spoonacular.com/mealplanner/"+ ConnectedUserName + "/shopping-list").
                then().
                statusCode(200).
                time(Matchers.lessThan(MaxResponseTime)).extract().jsonPath();

        // должно быть 0!
        Double cost = path.getDouble("cost");
        Assert.assertEquals(cost, 0);

        // 3. Добавляю запись в план питания
        path = RestAssured.
                given().
                contentType(ContentType.JSON).
                queryParam("apiKey", ConnectKey).
                queryParam("hash", Hash).
                body("{\n" +
                        "    \"date\": 1589500800,\n" +
                        "    \"slot\": 1,\n" +
                        "    \"position\": 0,\n" +
                        "    \"type\": \"INGREDIENTS\",\n" +
                        "    \"value\": {\n" +
                        "        \"ingredients\": [\n" +
                        "            {\n" +
                        "                \"name\": \"1 banana\"\n" +
                        "            }\n" +
                        "        ]\n" +
                        "    }\n" +
                        "}").
                when().
                post("https://api.spoonacular.com/mealplanner/"+ ConnectedUserName +"/items").
                then().
                statusCode(200).
                time(Matchers.lessThan(MaxResponseTime)).extract().jsonPath();

        String status = path.getString("status");
        Assert.assertEquals(status, "success");

        // 4. Строю список
        path = RestAssured.
                given().
                contentType(ContentType.JSON).
                queryParam("apiKey", ConnectKey).
                queryParam("hash", Hash).
                when().
                post("https://api.spoonacular.com/mealplanner/" + ConnectedUserName + "/shopping-list/2018-01-01/2030-01-01").
                then().
                statusCode(200).
                time(Matchers.lessThan(MaxResponseTime)).extract().jsonPath();

        MealItemId = path.getInt("aisles[0].items[0].id");
        System.out.println("id=" + MealItemId);

        // 5. Проверяю список
        path = RestAssured.
                given().
                queryParam("apiKey", ConnectKey).
                queryParam("hash", Hash).
                when().
                get("https://api.spoonacular.com/mealplanner/"+ ConnectedUserName + "/shopping-list").
                then().
                statusCode(200).
                time(Matchers.lessThan(MaxResponseTime)).extract().jsonPath();

        // должно быть > 0!
        cost = path.getDouble("cost");
        Assert.assertTrue(cost > 0);

    }

    @AfterTest
    protected void tearDown() {
        System.out.println("tearing down");
        if (MealItemId != 0) {
            String path = "https://api.spoonacular.com/mealplanner/"+ ConnectedUserName +"/shopping-list/items/" + MealItemId;
            RestAssured.
                    given().
                    queryParam("apiKey", ConnectKey).
                    queryParam("hash", Hash).
                    when().
                    delete(path).
                    then().
                    statusCode(200).
                    time(Matchers.lessThan(MaxResponseTime)).extract().jsonPath();

            System.out.println("Item удален");
        }
    }
}