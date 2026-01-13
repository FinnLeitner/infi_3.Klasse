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
        

        JSONReadFromTheFileTest.jsonImport(con);
        
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
                        Kunde.remove(con, Integer.parseInt(sc.nextLine()));
                    }
                    case 2 -> {
                        System.out.print("Artikel-ID: ");
                        Artikel.remove(con, Integer.parseInt(sc.nextLine()));
                    }
                    case 3 -> {
                        System.out.print("Kunden-ID: ");
                        int kundenId = Integer.parseInt(sc.nextLine());
                        System.out.print("Artikel-ID: ");
                        int artikelId = Integer.parseInt(sc.nextLine());
                        System.out.print("Menge: ");
                        int menge = Integer.parseInt(sc.nextLine());
                        
                        if (Bestellung.create(con, kundenId, artikelId, menge)) {
                            System.out.println(" Bestellung gespeichert");
                        }
                    }
                    case 5 -> {
                        System.out.print("Name: ");
                        String name = sc.nextLine();
                        System.out.print("Email: ");
                        String email = sc.nextLine();
                        Kunde.add(con, name, email);
                    }
                    case 6 -> {
                        System.out.print("Artikelname: ");
                        String name = sc.nextLine();
                        System.out.print("Preis: ");
                        double preis = Double.parseDouble(sc.nextLine());
                        Artikel.add(con, name, preis);
                    }
                    case 0 -> running = false;
                }
                
            } catch (NumberFormatException e) {
                System.out.println("Ungültige Eingabe.");
            }
        }
        WriteToJSON.writeToFile(con);
    } catch (SQLException e) {
        e.printStackTrace();
    } catch (IOException e) {
	    throw new RuntimeException(e);
    }
	
}

}