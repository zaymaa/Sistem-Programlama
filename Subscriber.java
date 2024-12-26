// Remove package declaration for testing

public class Subscriber {
    private int ID; // Abone ID'si
    private String name_surname; // Abonenin adı ve soyadı
    private long start_date; // Abonelik başlangıç tarihi
    private long last_accessed; // Son erişim tarihi
    private String[] interests; // Abonenin ilgi alanları
    private boolean isOnline; // Abonenin çevrimiçi durumu

    // Constructor for SUBS
    public Subscriber(int ID, String name_surname, long start_date, long last_accessed, String[] interests, boolean isOnline) {
        this.ID = ID; // Abone ID'sini ata
        this.name_surname = name_surname; // Abonenin adını ve soyadını ata
        this.start_date = start_date; // Başlangıç tarihini ata
        this.last_accessed = last_accessed; // Son erişim tarihini ata
        this.interests = interests; // İlgi alanlarını ata
        this.isOnline = isOnline; // Çevrimiçi durumunu ata
    }

    // Constructor for DEL
    public Subscriber(int ID) {
        this.ID = ID; // Abone ID'sini ata
        this.name_surname = null; // Adı ve soyadı boş bırak
        this.start_date = 0; // Başlangıç tarihini sıfırla
        this.last_accessed = 0; // Son erişim tarihini sıfırla
        this.interests = null; // İlgi alanlarını boş bırak
        this.isOnline = false; // Çevrimdışı olarak ayarla
    }

    public int getId() {
        return ID; // Abone ID'sini döndür
    }

    public String getNameSurname() {
        return name_surname; // Abonenin adı ve soyadını döndür
    }

    public long getStartDate() {
        return start_date; // Abonelik başlangıç tarihini döndür
    }

    public long getLastAccessed() {
        return last_accessed; // Son erişim tarihini döndür
    }

    public String[] getInterests() {
        return interests; // İlgi alanlarını döndür
    }

    public boolean isOnline() {
        return isOnline; // Çevrimiçi durumunu döndür
    }
}
