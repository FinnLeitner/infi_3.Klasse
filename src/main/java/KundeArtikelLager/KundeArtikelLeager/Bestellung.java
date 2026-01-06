package KundeArtikelLager.KundeArtikelLeager;

import java.sql.*;

public class Bestellung {

public static void init(Connection con) throws SQLException {
    String sql = """
    CREATE TABLE IF NOT EXISTS bestellung (
        id INT AUTO_INCREMENT PRIMARY KEY,
        kunde_id INT,
        artikel_id INT,
        menge INT,
        datum TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (kunde_id) REFERENCES kunden(id),
        FOREIGN KEY (artikel_id) REFERENCES artikel(id)
    )
""";
    
    con.createStatement().execute(sql);
}

public static boolean create(Connection con,
                             int kundeId,
                             int artikelId,
                             int menge) throws SQLException {
    
    if (!Lager.genug(con, artikelId, menge)) {
        System.out.println("❌ Nicht genug Lagerbestand!");
        return false;
    }
    
    // Kein Datum nötig, DB setzt automatisch CURRENT_TIMESTAMP
    String sql = """
        INSERT INTO bestellung (kunde_id, artikel_id, menge)
        VALUES (?, ?, ?)
    """;
    
    PreparedStatement ps = con.prepareStatement(sql);
    ps.setInt(1, kundeId);
    ps.setInt(2, artikelId);
    ps.setInt(3, menge);
    ps.executeUpdate();
    
    Lager.abbuchen(con, artikelId, menge);
    return true;
}



public static void remove(Connection con, int id)
        throws SQLException {
    
    String sql = "DELETE FROM bestellung WHERE id = ?";
    PreparedStatement ps = con.prepareStatement(sql);
    ps.setInt(1, id);
    ps.executeUpdate();
}
}
