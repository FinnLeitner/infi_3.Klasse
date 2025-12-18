package KundeArtikelLager.KundeArtikelLeager;

import java.sql.*;
import java.time.LocalDateTime;

public class Artikel {

    public static void init(Connection con) {
        String sql =
                "CREATE TABLE IF NOT EXISTS ARTIKEL (" +
                "id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                "name VARCHAR(100) NOT NULL, " +
                "preis DECIMAL(10,2) NOT NULL,"+
                "datumuhrzeit DATETIME NOT NULL"+
                ")";

        try (Statement st = con.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            System.out.println("Fehler beim Erstellen der Artikel-Tabelle.");
        }
    }
public static void add(Connection con, String name, Double preis){
    String sql="Insert Into ARTIKEL (name,preis, datumuhrzeit)Values (?,?,?)";
    try (PreparedStatement ps = con.prepareStatement(sql)) {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        ps.setString(1,name);
        ps.setDouble(2,preis);
        ps.setObject(3,now);
        ps.executeUpdate();
    } catch (SQLException e) {
        System.out.println("Artikel konnte nicht hinzugefügt werden."+e.getMessage());
        }
    }
    public static void remove(Connection con, int artikelId) {
        String sql = "DELETE FROM ARTIKEL WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, artikelId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Artikel konnte nicht gelöscht werden.");
        }
    }
}
