package KundeArtikelLager.KundeArtikelLeager;

import java.io.*;
import java.sql.*;
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
                    case "URL" -> URL = parts[1].trim();
                    case "USER" -> USER = parts[1].trim();
                    case "PASSWORD" -> PASSWORD = parts[1].trim();
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
            System.out.println("5) Kunde hinzufügen");
            System.out.println("6) Artikel hinzufügen");
            System.out.println("0) Beenden");
            System.out.print("Auswahl: ");
            
            try {
                int choice = Integer.parseInt(sc.nextLine());
                
                switch (choice) {
                    case 1 -> {
                        System.out.print("Kunden-ID: ");
                        int kid = Integer.parseInt(sc.nextLine());
                        Kunde.remove(con, kid);
                        System.out.println("Kunde entfernt.");
                    }
                    case 2 -> {
                        System.out.print("Artikel-ID: ");
                        int aid = Integer.parseInt(sc.nextLine());
                        Artikel.remove(con, aid);
                        System.out.println("Artikel entfernt.");
                    }
                    case 3 -> {
                        System.out.print("Kunden-ID: ");
                        int kundenId = Integer.parseInt(sc.nextLine());
                        
                        System.out.print("Artikel-ID: ");
                        int artikelId = Integer.parseInt(sc.nextLine());
                        
                        System.out.print("Menge: ");
                        int menge = Integer.parseInt(sc.nextLine());
                        
                        try {
                            boolean ok = Bestellung.create(con, kundenId, artikelId, menge);
                            if (ok) {
                                System.out.println("✅ Bestellung erfolgreich gespeichert.");
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    case 4 -> {
                        System.out.print("Bestell-ID: ");
                        int bid = Integer.parseInt(sc.nextLine());
                        Bestellung.remove(con, bid);
                        System.out.println("Bestellung gelöscht.");
                    }
                    case 5 -> {
                        System.out.print("Name: ");
                        String name = sc.nextLine();
                        System.out.print("Email: ");
                        String email = sc.nextLine();
                        Kunde.add(con, name, email);
                        System.out.println("Kunde erfolgreich gespeichert.");
                    }
                    case 6 -> {
                        System.out.print("Artikelname: ");
                        String aName = sc.nextLine();
                        System.out.print("Preis: ");
                        double preis = Double.parseDouble(sc.nextLine());
                        Artikel.add(con, aName, preis);
                        System.out.println("Artikel erfolgreich gespeichert.");
                    }
                    case 0 -> {
                        running = false;
                        System.out.println("Programm beendet.");
                    }
                    default -> System.out.println("Ungültige Auswahl.");
                }
                
            } catch (NumberFormatException e) {
                System.out.println("Bitte eine gültige Zahl eingeben.");
            }
        }
        
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
}
