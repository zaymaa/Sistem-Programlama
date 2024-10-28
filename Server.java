import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {
    private static final int PORT = 5001; // Her sunucu için port numaraları farklı olacak
    private static ConcurrentMap<String, List<String>> clientData = new ConcurrentHashMap<>();
    private static final ReentrantLock lock = new ReentrantLock(); // ReentrantLock oluşturma

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String command;
                while ((command = in.readLine()) != null) {
                    String response = processCommand(command);
                    out.println(response);
                }
            } catch (IOException e) {
                System.out.println("Error handling client: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Socket could not be closed: " + e.getMessage());
                }
            }
        }

        private String processCommand(String command) {
            lock.lock(); // Kilidi al
            try {
                switch (command.split(" ")[0]) { // Komutu analiz et
                    case "SUBSCRIBE":
                        return handleSubscribe(command);
                    case "UNSUBSCRIBE":
                        return handleUnsubscribe(command);
                    case "PUBLISH":
                        return handlePublish(command);
                    case "LIST":
                        return handleList();
                    case "STATUS":
                        return handleStatus();
                    default:
                        return "Geçersiz komut";
                }
            } finally {
                lock.unlock(); // Kilidi serbest bırak
            }
        }

        private String handleSubscribe(String command) {
            String clientId = command.split(" ")[1];
            // Abone olma işlemleri
            clientData.putIfAbsent(clientId, new ArrayList<>());
            return "Abone olundu: " + clientId;
        }

        private String handleUnsubscribe(String command) {
            String clientId = command.split(" ")[1];
            // Aboneliği iptal etme işlemleri
            clientData.remove(clientId);
            return "Abonelik iptal edildi: " + clientId;
        }

        private String handlePublish(String command) {
            String message = command.substring(command.indexOf(" ") + 1);
            // Mesajı yayınlama işlemleri
            return "Mesaj yayımlandı: " + message;
        }

        private String handleList() {
            return "Aboneler: " + clientData.keySet();
        }

        private String handleStatus() {
            return "Sunucu durumu: Çalışıyor";
        }
    }
}
