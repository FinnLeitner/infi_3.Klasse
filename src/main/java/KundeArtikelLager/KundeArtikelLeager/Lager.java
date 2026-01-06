package KundeArtikelLager.KundeArtikelLeager;

import java.sql.*;

public class Lager {

public static void init(Connection con) throws SQLException {
    
    String sql = """
            CREATE TABLE IF NOT EXISTS lager (
                artikel_id INT PRIMARY KEY,
                bestand INT NOT NULL,
                FOREIGN KEY (artikel_id) REFERENCES artikel(id)
            )
        """;
    con.createStatement().execute(sql);
    
    // Alle fehlenden Lager-Einträge automatisch anlegen
    String fix = """
            INSERT INTO lager (artikel_id, bestand)
            SELECT id, 100 FROM artikel
            WHERE id NOT IN (SELECT artikel_id FROM lager)
        """;
    con.createStatement().executeUpdate(fix);
}

public static void create(Connection con, int artikelId, int bestand)
        throws SQLException {
    
    String sql =
            "INSERT IGNORE INTO lager (artikel_id, bestand) VALUES (?, ?)";
    PreparedStatement ps = con.prepareStatement(sql);
    ps.setInt(1, artikelId);
    ps.setInt(2, bestand);
    ps.executeUpdate();
}

public static boolean genug(Connection con, int artikelId, int menge)
        throws SQLException {
    
    String sql = "SELECT bestand FROM lager WHERE artikel_id = ?";
    PreparedStatement ps = con.prepareStatement(sql);
    ps.setInt(1, artikelId);
    ResultSet rs = ps.executeQuery();
    
    return rs.next() && rs.getInt("bestand") >= menge;
}

public static void abbuchen(Connection con, int artikelId, int menge)
        throws SQLException {
    
    String sql =
            "UPDATE lager SET bestand = bestand - ? WHERE artikel_id = ?";
    PreparedStatement ps = con.prepareStatement(sql);
    ps.setInt(1, menge);
    ps.setInt(2, artikelId);
    ps.executeUpdate();
}
}
