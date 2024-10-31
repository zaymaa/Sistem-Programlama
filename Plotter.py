import socket
import json
import matplotlib.pyplot as plt

# Sunucunun adresi ve portu
HOST = 'localhost'
PORT = 6000

# Renk ve sunucu eşlemesi
server_colors = {
    'server1': 'blue',
    'server2': 'green',
    'server3': 'red'
}

# Verileri saklamak için bir dict
data = {
    'server1': [],
    'server2': [],
    'server3': []
}

def plot_data():
    plt.clf()  # Önceki grafiği temizle
    for server, color in server_colors.items():
        if data[server]:
            plt.plot(data[server], label=server, color=color)
    plt.xlabel('Zaman (saniye)')
    plt.ylabel('Kapasite')
    plt.title('Sunucu Kapasitesi Zaman Grafiği')
    plt.legend()
    plt.pause(0.1)  # Grafiği güncelle

def main():
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.bind((HOST, PORT))
        s.listen()
        print(f'Plotter sunucusu {HOST}:{PORT} adresinde dinleniyor...')

        while True:
            conn, addr = s.accept()
            with conn:
                print(f'{addr} bağlantı kurdu.')
                while True:
                    data_received = conn.recv(1024)
                    if not data_received:
                        break
                    capacity_data = json.loads(data_received.decode())
                    server_key = capacity_data['server']
                    capacity = capacity_data['capacity']
                    data[server_key].append(capacity)
                    plot_data()  # Grafiği güncelle

if __name__ == '__main__':
    plt.ion()  # İnteraktif modu aç
    main()
