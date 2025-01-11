package proje1;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class Server2 {

    private static final int PORT_SERVER_2 = 5002;
    private static final int MAX_CLIENTS = 100;

    private static List<Boolean> abonelerListesi = new ArrayList<>(MAX_CLIENTS);
    private static List<Boolean> girisYapanlarListesi = new ArrayList<>(MAX_CLIENTS);
    private static long lastUpdatedEpochMiliSeconds;

    public static void main(String[] args) throws IOException {
        for (int i = 0; i < MAX_CLIENTS; i++) {
            abonelerListesi.add(false);
            girisYapanlarListesi.add(false);
        }
        ServerSocket serverSocket = new ServerSocket(PORT_SERVER_2);
        System.out.println("Server3 is running on port " + PORT_SERVER_2);

        new Thread(() -> {
            try {
                listenForConnections(serverSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        
        new PingThread("localhost", 5001).start(); // Ping Server2
        new PingThread("localhost", 5003).start(); // Ping Server3
        new Thread(() -> receiveDataFromServer3(true)).start();
        new Thread(() -> receiveDataFromServer1(true)).start();


            
    }

    private static void listenForConnections(ServerSocket serverSocket1) throws IOException {
        while (true) {
            Socket clientSocket = serverSocket1.accept();
            new Thread(() -> handleClient(clientSocket)).start();
        }
    }

    public static void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String message = in.readLine();
            out.println("İstediğiniz mesaj sunucuya iletildi.");

            if (message != null) {
                if (message.startsWith("ABONOL")) {
                	SubscriberABONOL(message, clientSocket, out);
                } else if (message.startsWith("ABONIPTAL")) {
                	SubscriberABONIPTAL(message, clientSocket, out);
                } else if (message.startsWith("GIRIS")) {
                    handleGIRISMessage(message, clientSocket, out);
                } else if (message.startsWith("CIKIS")) {
                    handleCIKISMessage(message, clientSocket, out);
                } else if (message.equals("SERILESTIRILMIS_NESNE")) {

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void SubscriberABONOL(String message, Socket clientSocket, PrintWriter out) throws IOException {
        int clientNumber = Integer.parseInt(message.split(" ")[1]);

        if (clientNumber >= 0 && clientNumber < MAX_CLIENTS) {
            if (abonelerListesi.get(clientNumber)) {
                out.println("Bu client ID için Subscriber zaten var.");
                sendDataToServers();
            } else {
                abonelerListesi.set(clientNumber, true);
                out.println("Subscriber olundu.");
                sendDataToServers();
            }
        } else {
            out.println("50 HATA");
        }
    }

    private static void SubscriberABONIPTAL(String message, Socket clientSocket, PrintWriter out) throws IOException {
        int clientNumber = Integer.parseInt(message.split(" ")[1]);

        if (clientNumber >= 0 && clientNumber < MAX_CLIENTS) {
            if (!abonelerListesi.get(clientNumber)) {
                out.println("Bu client ID için Subscriber zaten yok.");
                sendDataToServers();
            } else {
                abonelerListesi.set(clientNumber, false);
                out.println("Subscriber iptal edildi.");
                sendDataToServers();
            }
        } else {
            out.println("50 HATA");
        }
    }

    private static void handleGIRISMessage(String message, Socket clientSocket, PrintWriter out) throws IOException {
        int clientNumber = Integer.parseInt(message.split(" ")[1]);

        if (clientNumber >= 0 && clientNumber < MAX_CLIENTS) {
            if (abonelerListesi.get(clientNumber)) {
                if (girisYapanlarListesi.get(clientNumber)) {
                    out.println("Bu Client zaten Online.");
                    sendDataToServers();
                } else {
                    girisYapanlarListesi.set(clientNumber, true);
                    out.println("Online.");
                    sendDataToServers();
                }
            } else {
                out.println("50 HATA");
            }
        } else {
            out.println("50 HATA");
        }
    }

    private static void handleCIKISMessage(String message, Socket clientSocket, PrintWriter out) throws IOException {
        int clientNumber = Integer.parseInt(message.split(" ")[1]);

        if (clientNumber >= 0 && clientNumber < MAX_CLIENTS) {
            if (!abonelerListesi.get(clientNumber) || !girisYapanlarListesi.get(clientNumber)) {
                out.println("Bu Client zaten Offline.");
                sendDataToServers();
            } else {
                girisYapanlarListesi.set(clientNumber, false);
                out.println("Offline.");
                sendDataToServers();
            }
        } else {
            out.println("50 HATA");
        }
    }
    
    
    private static void handleSerilestirilmisNesne(Socket clientSocket, PrintWriter out ,boolean condition) throws IOException {
        try {
            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
            MultiStringArrays receivedAboneler = (MultiStringArrays) ois.readObject();

            receiveDataFromServer1(condition);
            receiveDataFromServer3(condition);
            out.println("55 TAMM");


            if (receivedAboneler != null && receivedAboneler.getLastUpdatedEpochMiliSeconds() > lastUpdatedEpochMiliSeconds) {
                lastUpdatedEpochMiliSeconds = receivedAboneler.getLastUpdatedEpochMiliSeconds();
                out.println("55 TAMM");
            } else {
                out.println("99 HATA");
            }
        } catch (IOException | ClassNotFoundException e) {
            out.println("99 HATA");
        }
    }

    private static void sendDataToServers() {
        try {
            Socket socketServer1 = new Socket("localhost", 5007);
            Socket socketServer3 = new Socket("localhost", 5005);

            ObjectOutputStream outServer3 = new ObjectOutputStream(socketServer3.getOutputStream());
            ObjectOutputStream outServer1 = new ObjectOutputStream(socketServer1.getOutputStream());

            MultiStringArrays dataToSend = new MultiStringArrays();
            dataToSend.setLastUpdatedEpochMiliSeconds(System.currentTimeMillis());

            dataToSend.setAbonelerListesi(abonelerListesi);
            dataToSend.setGirisYapanlarListesi(girisYapanlarListesi);

            outServer3.writeObject(dataToSend);
            outServer1.writeObject(dataToSend);
            System.out.println("Data sent ");

            socketServer3.close();
            socketServer1.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private static void receiveDataFromServer3(boolean condition) {
        try (ServerSocket serverSocket = new ServerSocket(5009)) {
            while (condition) {
                Socket socket = serverSocket.accept();
                new Thread(() -> handleReceivedData(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void receiveDataFromServer1(boolean condition) {
        try (ServerSocket serverSocket = new ServerSocket(5004)) {
            while (condition) {
                Socket socket = serverSocket.accept();
                new Thread(() -> handleReceivedData(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleReceivedData(Socket socket) {
        try {
            int senderPort = socket.getPort();
            if (senderPort != PORT_SERVER_2) {
                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

                Object receivedObject = input.readObject();

                if (receivedObject instanceof MultiStringArrays) {
                    MultiStringArrays receivedAboneler = (MultiStringArrays) receivedObject;
                    System.out.println("Data received :");
                    
                    if (receivedAboneler != null && receivedAboneler.getLastUpdatedEpochMiliSeconds() > lastUpdatedEpochMiliSeconds) {
                        lastUpdatedEpochMiliSeconds = receivedAboneler.getLastUpdatedEpochMiliSeconds();
                        List<Boolean> receivedAbonelerList = receivedAboneler.getAbonelerListesi();
                        List<Boolean> receivedGirisYapanlarList = receivedAboneler.getGirisYapanlarListesi();

                        if (receivedAbonelerList != null && receivedGirisYapanlarList != null) {
                            abonelerListesi = new ArrayList<>(receivedAbonelerList);
                            girisYapanlarListesi = new ArrayList<>(receivedGirisYapanlarList);
                        }
                        System.out.println("55 Yerel liste güncellendi TAMM");
                    } else {
                        System.out.println("99 HATA");
                    }
                }
            } else {
                System.out.println("Data received from Server 1. Ignoring...");
            }

            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    private static class PingThread extends Thread {
        private String host;
        private int port;

        public PingThread(String host, int port) {
            this.host = host;
            this.port = port;
        }

        public void run() {

            try {
                while (true) {
                    try (Socket socket = new Socket(host, port)) {
                        System.out.println("Pinged " + host + " on port " + port);
                    } catch (IOException e) {
                        System.out.println("Ping to " + host + " on port " + port + " failed, retrying...");
                    }

                    try {
                        Thread.sleep(10000); // Wait for 10 seconds before retrying
                    } catch (InterruptedException ie) {
                        System.out.println("Ping thread interrupted: " + ie.getMessage());
                        break; // Optional: exit the loop if the thread is interrupted
                    }
                }
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }


        }
    }

}
