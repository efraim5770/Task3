package retrofit;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import retrofit.http.*;

public interface SpoonacularServiceAPI {

    @Headers("Accept: application/json")
    @POST("users/connect")
    Call<ConnectResult> connect(@Query("apiKey") String apiKey, @Body ConnectRequest login);

    @Headers("Accept: application/json")
    @GET("mealplanner/{connectedUserName}/shopping-list")
    Call<ShoppingListResult> shoppingList(
            @Path("connectedUserName") String connectedUserName,
            @Query("apiKey") String apiKey,
            @Query("hash") String hash
            );

    @Headers("Accept: application/json")
    @POST("mealplanner/{connectedUserName}/items")
    Call<ItemsResult> addItems(
            @Path("connectedUserName") String connectedUserName,
            @Query("apiKey") String apiKey,
            @Query("hash") String hash,
            @Body ItemsRequest items);

    @Headers("Accept: application/json")
    @POST("mealplanner/{connectedUserName}/shopping-list/2018-01-01/2030-01-01")
    Call<ShoppingListResult> shoppingListPost(
            @Path("connectedUserName") String connectedUserName,
            @Query("apiKey") String apiKey,
            @Query("hash") String hash);

    @DELETE("mealplanner/{connectedUserName}/shopping-list/items/{itemId}")
    Call<DeleteResult> deleteItem(
                    @Path("connectedUserName") String connectedUserName,
                    @Path("itemId") Integer itemId,
                    @Query("apiKey") String apiKey,
                    @Query("hash") String hash);

}
