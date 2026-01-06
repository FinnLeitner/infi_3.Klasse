package KundeArtikelLager.KundeArtikelLeager;

import java.sql.*;

public class Artikel {

public static void init(Connection con) throws SQLException {
    String sql = """
            CREATE TABLE IF NOT EXISTS artikel (
                id INT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(100),
                preis DECIMAL(10,2),
                datumuhrzeit TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;
    con.createStatement().execute(sql);
}

public static void add(Connection con, String name, double preis)
        throws SQLException {
    
    String sql = "INSERT INTO artikel (name, preis) VALUES (?, ?)";
    PreparedStatement ps = con.prepareStatement(sql);
    ps.setString(1, name);
    ps.setDouble(2, preis);
    ps.executeUpdate();
    
    // Lager-Eintrag automatisch anlegen
    ResultSet rs = con.createStatement()
            .executeQuery("SELECT LAST_INSERT_ID()");
    if (rs.next()) {
        int artikelId = rs.getInt(1);
        Lager.create(con, artikelId, 100); // fixer Lagerbestand
    }
}

public static void remove(Connection con, int id)
        throws SQLException {
    
    String sql = "DELETE FROM artikel WHERE id = ?";
    PreparedStatement ps = con.prepareStatement(sql);
    ps.setInt(1, id);
    ps.executeUpdate();
}
}
