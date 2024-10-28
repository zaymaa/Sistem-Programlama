import java.io.*;
import java.net.*;

public class Client {
    private static String serverAddress = "localhost"; // Sunucu adresi
    private static int serverPort = 5001; // Sunucu portu

    public static void main(String[] args) {
        try (Socket socket = new Socket(serverAddress, serverPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Abone olma
            out.println("SUBSCRIBE Client_1");
            System.out.println("Sunucudan gelen yanıt: " + in.readLine());

            // Mesaj gönderme
            out.println("PUBLISH Merhaba dünya!");
            System.out.println("Sunucudan gelen yanıt: " + in.readLine());

            // Aboneleri listeleme
            out.println("LIST");
            System.out.println("Sunucudan gelen yanıt: " + in.readLine());

            // Aboneliği iptal etme
            out.println("UNSUBSCRIBE Client_1");
            System.out.println("Sunucudan gelen yanıt: " + in.readLine());

        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
        }
    }
}
