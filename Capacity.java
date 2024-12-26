// Remove package declaration for testing

public class Capacity {
    private int server_id; // Sunucu ID'si
    private int server_status; // Abone sayısı
    private long timestamp; // UNIX epoch time

    public Capacity(int server_id, int server_status, long timestamp) {
        this.server_id = server_id;
        this.server_status = server_status; // Ensure this is a valid number
        this.timestamp = timestamp;
    }

    public int getServerId() {
        return server_id; // Sunucu ID'sini döndür
    }

    public int getServerStatus() {
        return server_status; // Abone sayısını döndür
    }

    public long getTimestamp() {
        return timestamp; // UNIX epoch zamanını döndür
    }
}

