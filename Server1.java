import java.io.*;
import java.net.*;
import java.util.*;

public class Server1 {
    private static final int PORT = 5000; // Sunucu port numarası
    private static Set<Subscriber> subscribers = Collections.synchronizedSet(new HashSet<>()); // Aboneleri saklamak için senkronize küme

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server1 is running on port " + PORT); // Sunucunun çalıştığı portu yazdır

            // Bağlantılar sürekli dinlenir
            while (true) {
                Socket socket = serverSocket.accept(); // Yeni bağlantı kabul et
                new Thread(new ConnectionHandler(socket)).start(); // Yeni bağlantıyı işlemek için yeni bir iş parçacığı başlat
            }
        } catch (IOException e) {
            e.printStackTrace(); // Hata durumunda stack trace yazdır
        }
    }

    private static class ConnectionHandler implements Runnable {
        private Socket socket; // Bağlantı soketi

        public ConnectionHandler(Socket socket) {
            this.socket = socket; // Soketi başlatıcıdan al
        }

        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                String inputLine;
                // Gelen mesajları dinle
                while ((inputLine = in.readLine()) != null) {
                    Message message = parseMessage(inputLine); // Mesajı ayrıştır
                    if (message.getDemand().equals("SUBS")) { // Eğer talep abone olma ise
                        handleSubscription(message); // Aboneliği işleme al
                        out.println("{\"response\": \"YEP\"}"); // Yanıt olarak YEP gönder
                    } else if (message.getDemand().equals("DEL")) { // Eğer talep abonelik iptali ise
                        handleUnsubscription(message); // Aboneliği iptal et
                        out.println("{\"response\": \"YEP\"}"); // Yanıt olarak YEP gönder
                    }
                }
            } catch (IOException e) {
                e.printStackTrace(); // Hata durumunda stack trace yazdır
            }
        }

        private Message parseMessage(String inputLine) {
            // Mesajı ayrıştırma (JSON formatında ayrıştırma yapmalısınız)
            return new Message("SUBS", null); // Örnek döndürme, gerçek ayrıştırmayı yapmalısınız
        }

        private void handleSubscription(Message message) {
            // Yeni bir abone oluştur ve aboneleri sakla
            Subscriber subscriber = new Subscriber(message.getId(), message.getNameSurname(), message.getStartDate(), message.getLastAccessed(), message.getInterests(), message.isOnline());
            subscribers.add(subscriber);
        }

        private void handleUnsubscription(Message message) {
            // Aboneliği iptal et
            subscribers.removeIf(subscriber -> subscriber.getId() == message.getId());
        }
    }
}
