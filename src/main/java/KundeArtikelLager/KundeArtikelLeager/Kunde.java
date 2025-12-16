package KundeArtikelLager.KundeArtikelLeager;

import java.sql.*;

public class Kunde {

    public static void init(Connection con) {
        String sql =
                "CREATE TABLE IF NOT EXISTS KUNDEN (" +
                "id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                "name VARCHAR(80) NOT NULL, " +
                "email VARCHAR(120)" +
                ")";

        try (Statement st = con.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            System.out.println("Fehler beim Erstellen der Kunden-Tabelle.");
        }
    }

    public static void remove(Connection con, int kundenId) {
        String sql = "DELETE FROM KUNDEN WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, kundenId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Kunde konnte nicht gelöscht werden.");
        }
    }
}
