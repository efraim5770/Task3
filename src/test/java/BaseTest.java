import org.ini4j.Ini;
import java.io.File;
import java.io.IOException;

// базовый класс для всех тестов
public class BaseTest {
    public String ApiKey;
    public String ConnectKey;
    public String UserName;
    public Long MaxResponseTime;

    public BaseTest() throws IOException {
        Ini ini = new Ini(new File("test.ini"));
        ApiKey = ini.get("params", "apiKey");
        ConnectKey = ini.get("params", "connectKey");
        UserName = ini.get("params", "userName");
        MaxResponseTime = Long.parseLong(ini.get("params", "maxResponseTime"));
    }

}
