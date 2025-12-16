package KundeArtikelLager.KundeArtikelLeager;

import java.sql.*;

public class Bestellung {

    public static void init(Connection con) {
        String sql =
                "CREATE TABLE IF NOT EXISTS BESTELLUNG (" +
                "id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                "kunden_id INTEGER NOT NULL, " +
                "artikel_id INTEGER NOT NULL, " +
                "menge INTEGER NOT NULL, " +
                "FOREIGN KEY (kunden_id) REFERENCES KUNDEN(id) ON DELETE RESTRICT, " +
                "FOREIGN KEY (artikel_id) REFERENCES ARTIKEL(id) ON DELETE RESTRICT" +
                ")";

        try (Statement st = con.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            System.out.println("Fehler beim Erstellen der Bestellung-Tabelle.");
        }
    }

    public static void create(Connection con, int kundenId, int artikelId, int menge) {

        if (!Lager.genug(con, artikelId, menge)) {
            System.out.println("Nicht genügend Lagerbestand.");
            return;
        }

        String sql =
                "INSERT INTO BESTELLUNG (kunden_id, artikel_id, menge) " +
                "VALUES (?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, kundenId);
            ps.setInt(2, artikelId);
            ps.setInt(3, menge);
            ps.executeUpdate();

            Lager.abbuchen(con, artikelId, menge);
        } catch (SQLException e) {
            System.out.println("Bestellung konnte nicht gespeichert werden.");
        }
    }

    public static void remove(Connection con, int bestellId) {
        String sql = "DELETE FROM BESTELLUNG WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bestellId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Bestellung konnte nicht gelöscht werden.");
        }
    }
}
