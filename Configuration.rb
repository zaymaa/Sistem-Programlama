require 'socket'
require 'json'

# Configuration sınıfını temsil eden bir yapı
class Configuration
  attr_accessor :fault_tolerance_level, :method

  def initialize(fault_tolerance_level, method)
    @fault_tolerance_level = fault_tolerance_level
    @method = method
  end
end

# Message sınıfı
class Message
  attr_accessor :demand, :response

  def initialize(demand, response)
    @demand = demand
    @response = response
  end
end

# Kapasite sınıfı
class Capacity
  attr_accessor :server_status, :timestamp

  def initialize(server_status, timestamp)
    @server_status = server_status
    @timestamp = timestamp
  end
end

# dist_subs.conf dosyasını oku
def read_configuration(file_path)
  fault_tolerance_level = nil
  File.open(file_path, 'r') do |file|
    file.each_line do |line|
      if line =~ /fault_tolerance_level\s*=\s*(\d+)/
        fault_tolerance_level = $1.to_i
      end
    end
  end
  Configuration.new(fault_tolerance_level, 'STRT')
end

# Sunuculara bağlantı kur ve mesaj gönder
def send_message(server_address, server_port, message)
  begin
    socket = TCPSocket.new(server_address, server_port)
    socket.puts(message.to_json) # Mesajı JSON formatında gönder
    response = JSON.parse(socket.gets)
    socket.close
    return response
  rescue => e
    puts "Connection error with #{server_address}:#{server_port} - #{e.message}"
    return nil
  end
end

# Server'lara bağlantı kur
def start_servers(configuration)
  server_ports = {
    'Server1' => 5000,
    'Server2' => 5001,
    'Server3' => 5002
  }
  successful_servers = []

  # Sunuculara STRT komutunu gönder
  server_ports.each do |server_name, port|
    start_message = Message.new("STRT", nil)
    response = send_message('localhost', port, start_message)

    if response && response['response'] == 'YEP'
      successful_servers << server_name
      puts "#{server_name} responded with YEP."
    end
  end

  # Kapasite sorgularını gönder
  loop do
    successful_servers.each do |server_name|
      demand_message = Message.new("CPCTY", nil)
      response = send_message('localhost', server_ports[server_name], demand_message)

      if response
        puts "#{server_name} - Capacity: #{response['server_status']} Timestamp: #{response['timestamp']}"
      end
    end

    sleep(5)
  end
end

# Ana program akışı
config = read_configuration('dist_subs.conf')
start_servers(config)
