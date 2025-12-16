package KundeArtikelLager.KundeArtikelLeager;

import java.sql.*;

public class Artikel {

    public static void init(Connection con) {
        String sql =
                "CREATE TABLE IF NOT EXISTS ARTIKEL (" +
                "id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                "name VARCHAR(100) NOT NULL, " +
                "preis DECIMAL(10,2) NOT NULL" +
                ")";

        try (Statement st = con.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            System.out.println("Fehler beim Erstellen der Artikel-Tabelle.");
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
