require 'socket' # Socket kütüphanesini içe aktar
require 'json'   # JSON kütüphanesini içe aktar

# Configuration sınıfını temsil eden bir yapı
class Configuration
  attr_accessor :fault_tolerance_level, :method

  # Yapıcı metod; hata toleransı seviyesini ve yöntemi alır
  def initialize(fault_tolerance_level, method)
    @fault_tolerance_level = fault_tolerance_level
    @method = method
  end
end

# Message sınıfı, talep ve yanıt bilgilerini tutar
class Message
  attr_accessor :demand, :response

  # Yapıcı metod; talep ve yanıt alır
  def initialize(demand, response)
    @demand = demand
    @response = response
  end
end

# Kapasite sınıfı, sunucunun durumunu ve zaman damgasını tutar
class Capacity
  attr_accessor :server_status, :timestamp

  # Yapıcı metod; sunucu durumu ve zaman damgası alır
  def initialize(server_status, timestamp)
    @server_status = server_status
    @timestamp = timestamp
  end
end

# dist_subs.conf dosyasını oku ve yapılandırmayı döndür
def read_configuration(file_path)
  fault_tolerance_level = nil
  File.open(file_path, 'r') do |file| # Dosyayı oku
    file.each_line do |line|
      if line =~ /fault_tolerance_level\s*=\s*(\d+)/ # Hata toleransı seviyesini kontrol et
        fault_tolerance_level = $1.to_i # Bulunan değeri tamsayıya çevir
      end
    end
  end
  Configuration.new(fault_tolerance_level, 'STRT') # Configuration nesnesini oluştur
end

# Sunuculara bağlantı kur ve mesaj gönder
def send_message(server_address, server_port, message)
  begin
    socket = TCPSocket.new(server_address, server_port) # Sunucuya bağlan
    socket.puts(message.to_json) # Mesajı JSON formatında gönder
    response = JSON.parse(socket.gets) # Yanıtı al ve JSON formatında ayrıştır
    socket.close # Bağlantıyı kapat
    return response # Yanıtı döndür
  rescue => e
    puts "Connection error with #{server_address}:#{server_port} - #{e.message}" # Hata mesajını yazdır
    return nil # Hata durumunda nil döndür
  end
end

# Sunucuları başlat
def start_servers(configuration)
  server_ports = {
    'Server1' => 5000,
    'Server2' => 5001,
    'Server3' => 5002
  }
  successful_servers = [] # Başarılı sunucuları saklamak için bir dizi

  # Sunuculara STRT komutunu gönder
  server_ports.each do |server_name, port|
    start_message = Message.new("STRT", nil) # STRT mesajı oluştur
    response = send_message('localhost', port, start_message) # Mesajı gönder

    if response && response['response'] == 'YEP' # Yanıtı kontrol et
      successful_servers << server_name # Başarılı sunucuyu listeye ekle
      puts "#{server_name} responded with YEP." # Yanıtı yazdır
    end
  end

  # Kapasite sorgularını gönder
  loop do
    successful_servers.each do |server_name|
      demand_message = Message.new("CPCTY", nil) # Kapasite sorgusu mesajı oluştur
      response = send_message('localhost', server_ports[server_name], demand_message) # Mesajı gönder

      if response
        puts "#{server_name} - Capacity: #{response['server_status']} Timestamp: #{response['timestamp']}" # Yanıtı yazdır
      end
    end

    sleep(5) # 5 saniye bekle
  end
end

# Ana program akışı
config = read_configuration('dist_subs.conf') # Yapılandırmayı oku
start_servers(config) # Sunucuları başlat
