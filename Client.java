import java.io.*;
import java.net.*;

public class Client {
    private String serverAddress; // Sunucu adresi
    private int serverPort; // Sunucu portu
    private Socket socket; // Socket bağlantısı
    private PrintWriter out; // Çıkış akışı
    private BufferedReader in; // Giriş akışı

    public Client(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    // Sunucuya bağlan
    public void connect() {
        try {
            socket = new Socket(serverAddress, serverPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Sunucuya bağlandı: " + serverAddress + ":" + serverPort);
        } catch (IOException e) {
            System.out.println("Bağlantı hatası: " + e.getMessage());
        }
    }

    // Komut gönder ve yanıt al
    public String sendCommand(String command) {
        try {
            out.println(command); // Komutu sunucuya gönder
            return in.readLine(); // Sunucudan yanıt al
        } catch (IOException e) {
            System.out.println("Komut gönderme hatası: " + e.getMessage());
            return null;
        }
    }

    // Bağlantıyı kapat
    public void disconnect() {
        try {
            in.close();
            out.close();
            socket.close();
            System.out.println("Bağlantı kapatıldı.");
        } catch (IOException e) {
            System.out.println("Bağlantı kapatma hatası: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Örnek istemci kullanımı
        Client client = new Client("localhost", 5001); // Sunucu adresi ve portu
        client.connect(); // Sunucuya bağlan

        // Örnek komut gönderimleri
        System.out.println(client.sendCommand("SUBSCRIBE Client_1")); // Abone olma
        System.out.println(client.sendCommand("PUBLISH Merhaba Dünya!")); // Mesaj yayınlama
        System.out.println(client.sendCommand("LIST")); // Aboneleri listeleme
        System.out.println(client.sendCommand("STATUS")); // Sunucu durumu sorgulama
        System.out.println(client.sendCommand("UNSUBSCRIBE Client_1")); // Abonelik iptali

        client.disconnect(); // Bağlantıyı kapat
    }
}
