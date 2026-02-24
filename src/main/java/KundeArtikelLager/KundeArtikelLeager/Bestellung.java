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
        System.out.println(" Nicht genug Lagerbestand!");
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

public static void printKundenBestellungen(Connection con) throws SQLException {
    String sql = """
            SELECT kunden.id, kunden.name, bestellung.artikel_id, bestellung.menge
            FROM kunden
            INNER JOIN bestellung ON kunden.id = bestellung.kunde_id
            """;
    
    try (Statement stmt = con.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        
        while (rs.next()) {
            System.out.println("ID: " + rs.getInt("id") +
                    ", Name: " + rs.getString("name") +
                    ", Artikel-ID: " + rs.getInt("artikel_id") +
                    ", Menge: " + rs.getInt("menge"));
        }
    }
}

public static void unionWith(Connection con) throws SQLException {
    String sql = """
        WITH viel_bestellt AS (
            SELECT kunde_id, artikel_id, menge FROM bestellung WHERE menge > 20
        ),
        wenig_bestellt AS (
            SELECT kunde_id, artikel_id, menge FROM bestellung WHERE menge <= 10
        )
        SELECT kunde_id, artikel_id, menge  FROM viel_bestellt
        UNION
        SELECT kunde_id, artikel_id, menge FROM wenig_bestellt
        ORDER BY menge DESC
        """;
    
    try (Statement stmt = con.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        while (rs.next()) {
            System.out.println(
                    "Kunde-ID: "   + rs.getInt("kunde_id")    +
                            ", Artikel-ID: " + rs.getInt("artikel_id") +
                            ", Menge: "    + rs.getInt("menge"));
                           
        }
    }
}

public static void groupByHaving(Connection con) throws SQLException {
    
    String sql = """
        SELECT artikel_id, COUNT(id) AS anzahl
        FROM bestellung
        GROUP BY artikel_id
        HAVING COUNT(id) > 1
        ORDER BY anzahl DESC
        """;
    
    try (Statement stmt = con.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        
        while (rs.next()) {
            System.out.println(
                    "Artikel-ID: " + rs.getInt("artikel_id") +
                            ", Bestellungen: " + rs.getInt("anzahl"));
        }
    }
}

public static void subSelect(Connection con) throws SQLException {
    
    String sql = """
            SELECT *
            FROM bestellung
            WHERE menge > (
                SELECT AVG(menge)
                FROM bestellung
            )
            """;
    
    try (Statement stmt = con.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        
        while (rs.next()) {
            System.out.println("Bestellung-ID: " + rs.getInt("id") +
                    ", Kunde-ID: " + rs.getInt("kunde_id") +
                    ", Artikel-ID: " + rs.getInt("artikel_id") +
                    ", Menge: " + rs.getInt("menge"));
        }
    }
}

public static void remove(Connection con, int id)
        throws SQLException {
    
    String sql = "DELETE FROM bestellung WHERE id = ?";
    PreparedStatement ps = con.prepareStatement(sql);
    ps.setInt(1, id);
    ps.executeUpdate();
}
}
