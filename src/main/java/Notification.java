import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Notification implements Runnable{
    private static String BASE_URL = "https://fcm.googleapis.com/fcm/send";
    private static String SERVER_KEY = "YOUR_SERVER_KEY_HERE_TO_SEND_NOTIFICATIONS"; //you can find instructions of it via google
    private static HttpURLConnection con;

    private String client;
    private String title;
    private String body;
    public Notification(String client, String title, String body){
        this.client = client;
        this.title = title;
        this.body = body;
    }

    @Override
    public void run() {
        try {
            URL url = new URL(BASE_URL);
            con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "key="+SERVER_KEY);
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            String jsonInputString = "{\"to\":\""+client+"\",\"notification\":{\"title\":\""+title+"\",\"body\":\""+body+"\",\"icon\":\"ic_stat_alert\",\"color\":\"red\",\"android_channel_id\":\"cnid\"}}";
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("Notification sent");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
