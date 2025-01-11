ServerX.java özellikleri

   ✅ hata toleransı 1 ve 2 prensibiyle çalışma

 Dağıtık abone istemiyle Abonelik ve Giriş-çıkış için 2 liste oluşturuldu. Belirlenen Client id için Abone varsa 1 yoksa 0 şeklinde listeye veri girişi oluyor. Giriş varsa 1 yoksa 0 değeri de diğer listeye kaydediliyor. Lastepocmills second ile listelere zaman damgası atanıyor, belirlirli zaman aralıklarıyla 3 Server birbirine bu listeleri gönderiyor. Örneğin Server 3, Server 2 den aldığı zaman damgasına bakıyor eğer en güncel liste Server 2 den gelen liste  ise Server 3 kendi listesini güncelliyor. Güncel liste Server3'ün listesi ise, Server 1  ve Server 2 ye güncel liste gönderilir ve onlar kendi listelerini günceller. 

plotter.py özellikleri

   ❌ no

admin.rb özellikleri

   ❌ no

Ekip üyeleri# Sistem-Programlama

21060124-Zayma Rümeysa KARKİ
19060397-Mustafa KILIÇ
20061163-Moustapha Naouchi