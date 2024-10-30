import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HASUPServer {
    private static final int PORT = 65432;
    private static Map<String, List<PrintWriter>> subscribers = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        System.out.println("HASUP Sunucusu başlatılıyor...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
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
            System.out.println("Yeni istemci bağlı: " + socket);
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                String message;
                while ((message = in.readLine()) != null) {
                    String[] command = message.split(" ", 2);
                    switch (command[0]) {
                        case "HELLO":
                            out.println("ACK");
                            break;
                        case "SUBSCRIBE":
                            subscribe(command[1]);
                            break;
                        case "PUBLISH":
                            String[] publishData = command[1].split(" ", 2);
                            publish(publishData[0], publishData[1]);
                            break;
                        case "GOODBYE":
                            out.println("BYE");
                            return;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("İstemci bağlantısı kapandı: " + socket);
            }
        }

        private void subscribe(String topic) {
            subscribers.putIfAbsent(topic, Collections.synchronizedList(new ArrayList<>()));
            subscribers.get(topic).add(out);
            out.println("OK " + topic);
        }

        private void publish(String topic, String message) {
            List<PrintWriter> subs = subscribers.get(topic);
            if (subs != null) {
                for (PrintWriter subscriber : subs) {
                    subscriber.println("RECEIVED " + message);
                }
            }
        }
    }
}
