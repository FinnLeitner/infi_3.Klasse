package KundeArtikelLager.KundeArtikelLeager;

import java.sql.*;

public class Kunde {

public static void init(Connection con) throws SQLException {
    String sql = """
            CREATE TABLE IF NOT EXISTS kunden (
                id INT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(100),
                email VARCHAR(100)
            )
        """;
    con.createStatement().execute(sql);
}

public static void add(Connection con, String name, String email)
        throws SQLException {
    
    String sql = "INSERT INTO kunden (name, email) VALUES (?, ?)";
    PreparedStatement ps = con.prepareStatement(sql);
    ps.setString(1, name);
    ps.setString(2, email);
    ps.executeUpdate();
}

public static void remove(Connection con, int id)
        throws SQLException {
    
    String sql = "DELETE FROM kunden WHERE id = ?";
    PreparedStatement ps = con.prepareStatement(sql);
    ps.setInt(1, id);
    ps.executeUpdate();
}
}
