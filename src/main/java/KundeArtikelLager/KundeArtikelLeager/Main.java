package KundeArtikelLager.KundeArtikelLeager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    private static String URL;
    private static String USER;
    private static String PASSWORD;

    private static void loadDatabaseConfig() {
        try (BufferedReader reader = new BufferedReader(new FileReader("config.txt"))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    switch (parts[0].trim()) {
                        case "URL":
                            URL = parts[1].trim();
                            break;
                        case "USER":
                            USER = parts[1].trim();
                            break;
                        case "PASSWORD":
                            PASSWORD = parts[1].trim();
                            break;
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        loadDatabaseConfig();

        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             Scanner sc = new Scanner(System.in)) {

            Kunde.init(con);
            Artikel.init(con);
            Lager.init(con);
            Bestellung.init(con);

            boolean running = true;

            while (running) {
                System.out.println("\n=== SHOP-MENÜ ===");
                System.out.println("1) Kunde löschen");
                System.out.println("2) Artikel löschen");
                System.out.println("3) Bestellung anlegen");
                System.out.println("4) Bestellung löschen");
                System.out.println("0) Beenden");
                System.out.print("Auswahl: ");

                try {
                    int choice = Integer.parseInt(sc.nextLine());

                    switch (choice) {
                        case 1:
                            System.out.print("Kunden-ID: ");
                            int kid = Integer.parseInt(sc.nextLine());
                            Kunde.remove(con, kid);
                            System.out.println("Kunde entfernt.");
                            break;

                        case 2:
                            System.out.print("Artikel-ID: ");
                            int aid = Integer.parseInt(sc.nextLine());
                            Artikel.remove(con, aid);
                            System.out.println("Artikel entfernt.");
                            break;

                        case 3:
                            System.out.print("Kunden-ID: ");
                            int kundenId = Integer.parseInt(sc.nextLine());

                            System.out.print("Artikel-ID: ");
                            int artikelId = Integer.parseInt(sc.nextLine());

                            System.out.print("Menge: ");
                            int menge = Integer.parseInt(sc.nextLine());

                            Bestellung.create(con, kundenId, artikelId, menge);
                            System.out.println("Bestellung erfolgreich gespeichert.");
                            break;

                        case 4:
                            System.out.print("Bestell-ID: ");
                            int bid = Integer.parseInt(sc.nextLine());
                            Bestellung.remove(con, bid);
                            System.out.println("Bestellung gelöscht.");
                            break;

                        case 0:
                            running = false;
                            System.out.println("Programm beendet.");
                            break;

                        default:
                            System.out.println("Ungültige Auswahl.");
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Bitte eine gültige Zahl eingeben.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Datenbankverbindung fehlgeschlagen.");
        }
    }
}
