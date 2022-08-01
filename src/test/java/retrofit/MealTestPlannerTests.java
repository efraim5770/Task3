package retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;


import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class MealTestPlannerTests extends basic.BaseTest {

    static final String BASE_URL = "https://api.spoonacular.com/";

    public Integer MealItemId =0;
    public String ConnectedUserName;
    public String Hash;
    SpoonacularServiceAPI Api;

    public MealTestPlannerTests() throws IOException {
    }

    @Test
    void ChainTest() throws IOException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Api = retrofit.create(SpoonacularServiceAPI.class);

        // 1. коннекчу пользователя
        // POST https://api.spoonacular.com/users/connect
        ConnectRequest login = new ConnectRequest();
        login.username = UserName;
        login.firstName = "";
        login.lastName = "";
        login.email = "efraim5770@gmail.com";

        LocalDateTime t1 = LocalDateTime.now();
        Response<ConnectResult> connectResult = Api.connect(ConnectKey, login).execute();
        LocalDateTime t2 = LocalDateTime.now();
        Assert.assertTrue(ChronoUnit.SECONDS.between(t2, t1) < MaxResponseTime);
        Assert.assertEquals(HttpStatus.SC_OK, connectResult.code());

        Hash = connectResult.body().hash;
        ConnectedUserName = connectResult.body().username;
        Assert.assertNotNull(Hash);
        System.out.println("hash=" + Hash);
        System.out.println("username=" + ConnectedUserName);

        // 2. Запрашиваю текущий Shopping List
        t1 = LocalDateTime.now();
        Response<ShoppingListResult> shopResult = Api.shoppingList(ConnectedUserName, ConnectKey, Hash).execute();
        t2 = LocalDateTime.now();
        Assert.assertTrue(ChronoUnit.SECONDS.between(t2, t1) < MaxResponseTime);
        Assert.assertEquals(HttpStatus.SC_OK, connectResult.code());

        // должно быть 0!
        double cost = shopResult.body().cost;
        Assert.assertEquals(cost, 0);

        // 3. Добавляю запись в план питания
        t1 = LocalDateTime.now();
        ItemsIngredientRequest ingredient = new ItemsIngredientRequest();
        ingredient.name = "1 banana";

        ItemsRequest request = new ItemsRequest();
        request.date = 1589500800L;
        request.slot = 1L;
        request.position = 0L;
        request.type = "INGREDIENTS";
        request.value = new ItemsValueRequest();
        request.value.ingredients = new ArrayList<ItemsIngredientRequest>();
        request.value.ingredients.add(ingredient);


        t1 = LocalDateTime.now();
        Response<ItemsResult> itemResult = Api.addItems(ConnectedUserName, ConnectKey, Hash, request).execute();
        t2 = LocalDateTime.now();
        Assert.assertTrue(ChronoUnit.SECONDS.between(t2, t1) < MaxResponseTime);
        Assert.assertEquals(HttpStatus.SC_OK, itemResult.code());
        Assert.assertEquals(itemResult.body().status, "success");

        // 4. Строю список
        t1 = LocalDateTime.now();
        Response<ShoppingListResult> listResult2 = Api.shoppingListPost(ConnectedUserName, ConnectKey, Hash).execute();
        t2 = LocalDateTime.now();
        Assert.assertTrue(ChronoUnit.SECONDS.between(t2, t1) < MaxResponseTime);
        Assert.assertEquals(HttpStatus.SC_OK, itemResult.code());
        MealItemId = listResult2.body().aisles.get(0).items.get(0).id;
        System.out.println("id=" + MealItemId);

        // 5. Запрашиваю текущий Shopping List
        t1 = LocalDateTime.now();
        shopResult = Api.shoppingList(ConnectedUserName, ConnectKey, Hash).execute();
        t2 = LocalDateTime.now();
        Assert.assertTrue(ChronoUnit.SECONDS.between(t2, t1) < MaxResponseTime);
        Assert.assertEquals(HttpStatus.SC_OK, connectResult.code());

        // должно быть > 0!
        cost = shopResult.body().cost;
        Assert.assertTrue(cost > 0);
    }

    @AfterTest
    protected void tearDown() throws IOException {
        System.out.println("tearing down");
        if (MealItemId != 0) {
            Response<DeleteResult> r = Api.deleteItem(ConnectedUserName, MealItemId, ConnectKey, Hash).execute();
            Assert.assertEquals(HttpStatus.SC_OK, r.code());
            System.out.println("Item удален");
        }

    }
}