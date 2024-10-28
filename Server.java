import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {
    private static final int PORT = 12345;  // Her sunucu için farklı bir port belirleyin
    private static ConcurrentMap<String, List<String>> clientData = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);

            // İstemci bağlantılarını kabul eder ve her bağlantı için bir iş parçacığı oluşturur.
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
            // Gelen komutlara göre işlemleri gerçekleştirin (örneğin SUBSCRIBE, PUBLISH, LIST)
            return "200 OK";  // Örnek yanıt
        }
    }
}
