package KundeArtikelLager.KundeArtikelLeager;

import java.sql.*;

public class Lager {

    public static void init(Connection con) {
        String sql =
                "CREATE TABLE IF NOT EXISTS LAGER (" +
                "artikel_id INTEGER PRIMARY KEY, " +
                "bestand INTEGER NOT NULL, " +
                "FOREIGN KEY (artikel_id) REFERENCES ARTIKEL(id) " +
                "ON DELETE RESTRICT" +
                ")";

        try (Statement st = con.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            System.out.println("Fehler beim Erstellen der Lager-Tabelle.");
        }
    }

    public static boolean genug(Connection con, int artikelId, int menge) {
        String sql = "SELECT bestand FROM LAGER WHERE artikel_id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, artikelId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("bestand") >= menge;
            }
        } catch (SQLException e) {
            System.out.println("Fehler bei der Lagerprüfung.");
        }
        return false;
    }

    public static void abbuchen(Connection con, int artikelId, int menge) {
        String sql = "UPDATE LAGER SET bestand = bestand - ? WHERE artikel_id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, menge);
            ps.setInt(2, artikelId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Bestand konnte nicht reduziert werden.");
        }
    }
}
