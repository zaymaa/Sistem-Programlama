import socket  # Socket kütüphanesini içe aktar
import json    # JSON kütüphanesini içe aktar
import matplotlib.pyplot as plt  # Matplotlib kütüphanesini içe aktar

# Sunucunun adresi ve portu
HOST = 'localhost'  # Sunucu adresi
PORT = 6000         # Sunucu portu

# Renk ve sunucu eşlemesi
server_colors = {
    'server1': 'blue',  # server1 için mavi renk
    'server2': 'green', # server2 için yeşil renk
    'server3': 'red'    # server3 için kırmızı renk
}

# Verileri saklamak için bir dict
data = {
    'server1': [],  # server1 için veri listesi
    'server2': [],  # server2 için veri listesi
    'server3': []   # server3 için veri listesi
}

def plot_data():
    plt.clf()  # Önceki grafiği temizle
    # Her sunucu için verileri çiz
    for server, color in server_colors.items():
        if data[server]:  # Eğer sunucunun verisi varsa
            plt.plot(data[server], label=server, color=color)  # Veriyi çiz
    plt.xlabel('Zaman (saniye)')  # X ekseni etiket
    plt.ylabel('Kapasite')         # Y ekseni etiket
    plt.title('Sunucu Kapasitesi Zaman Grafiği')  # Başlık
    plt.legend()  # Legend (açıklama) ekle
    plt.pause(0.1)  # Grafiği güncelle

def main():
    # TCP/IP socket oluştur
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.bind((HOST, PORT))  # Sunucu adresine ve portuna bağlan
        s.listen()  # Bağlantıları dinlemeye başla
        print(f'Plotter sunucusu {HOST}:{PORT} adresinde dinleniyor...')  # Bilgi yazdır

        while True:  # Sonsuz döngü
            conn, addr = s.accept()  # Bir istemciden bağlantı kabul et
            with conn:  # Bağlantı ile ilgili işlemleri yap
                print(f'{addr} bağlantı kurdu.')  # Bağlanan istemcinin adresini yazdır
                while True:  # Sonsuz döngü
                    data_received = conn.recv(1024)  # İstemciden veri al
                    if not data_received:  # Eğer veri yoksa döngüden çık
                        break
                    # Alınan veriyi JSON formatında ayrıştır
                    capacity_data = json.loads(data_received.decode())
                    server_key = capacity_data['server']  # Sunucu anahtarını al
                    capacity = capacity_data['capacity']    # Kapasiteyi al
                    data[server_key].append(capacity)  # Veriyi ilgili sunucunun listesine ekle
                    plot_data()  # Grafiği güncelle

if __name__ == '__main__':
    plt.ion()  # İnteraktif modu aç
    main()  # Ana fonksiyonu çalıştır
