import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class LoadBalancer {
    private List<String> serverAddresses;
    private AtomicInteger currentIndex = new AtomicInteger(0);

    public LoadBalancer(List<String> serverAddresses) {
        this.serverAddresses = serverAddresses;
    }

    public String getNextServer() {
        int index = currentIndex.getAndUpdate(i -> (i + 1) % serverAddresses.size());
        return serverAddresses.get(index);
    }

    public static void main(String[] args) {
        // Sunucu IP adreslerini ekleyin
        List<String> servers = List.of("127.0.0.1:12345", "127.0.0.1:12346");
        LoadBalancer lb = new LoadBalancer(servers);

        // Örnek kullanım: İstemciyi sıradaki sunucuya yönlendirme
        String selectedServer = lb.getNextServer();
        System.out.println("Yönlendirilen Sunucu: " + selectedServer);
    }
}
