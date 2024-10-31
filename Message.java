import java.util.List;

public class Message {
    private String demand; // Talep türü (SUBS, DEL, CPCTY, vb.)
    private String response; // Yanıt durumu (YEP, NOP, vb.)
    private Subscriber subscriber; // Abone bilgileri
    private List<Capacity> capacities; // Kapasite bilgileri (örneğin, sunucudan gelen yanıtlar)

    // Constructor for commands without a subscriber or capacities
    public Message(String demand, String response) {
        this.demand = demand; // Talebi ata
        this.response = response; // Yanıtı ata
    }

    // Constructor for commands with a subscriber
    public Message(String demand, String response, Subscriber subscriber) {
        this(demand, response); // Ana yapıcıyı çağır
        this.subscriber = subscriber; // Abone bilgilerini ata
    }

    // Constructor for commands with capacities
    public Message(String demand, String response, List<Capacity> capacities) {
        this(demand, response); // Ana yapıcıyı çağır
        this.capacities = capacities; // Kapasite bilgilerini ata
    }

    public String getDemand() {
        return demand; // Talebi döndür
    }

    public String getResponse() {
        return response; // Yanıtı döndür
    }

    public Subscriber getSubscriber() {
        return subscriber; // Abone bilgilerini döndür
    }

    public List<Capacity> getCapacities() {
        return capacities; // Kapasite bilgilerini döndür
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Message{")
          .append("demand='").append(demand).append('\'')
          .append(", response='").append(response).append('\'');

        if (subscriber != null) {
            sb.append(", subscriber=").append(subscriber); // Abone bilgilerini ekle
        }

        if (capacities != null && !capacities.isEmpty()) {
            sb.append(", capacities=").append(capacities); // Kapasite bilgilerini ekle
        }

        sb.append('}');
        return sb.toString(); // Mesajı döndür
    }
}
