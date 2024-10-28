public class Synchronizer extends Thread {
    private String serverAddress;

    public Synchronizer(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(5000);  // 5 saniyede bir veri senkronizasyonu yapılır
                syncWithServer();
            } catch (InterruptedException e) {
                System.out.println("Sync interrupted: " + e.getMessage());
            }
        }
    }

    private void syncWithServer() {
        // Diğer sunucudan veri alınarak senkronizasyon yapılır
        System.out.println("Senkronize ediliyor: " + serverAddress);
        // İstemci verilerini diğer sunuculardan alıp, clientData yapısına ekleyin
    }
}
