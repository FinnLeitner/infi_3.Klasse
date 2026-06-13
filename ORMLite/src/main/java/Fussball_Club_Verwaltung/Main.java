package Fussball_Club_Verwaltung;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.Level;

public class Main {

private static final String DATABASE_URL = "jdbc:sqlite:arsenal.db";

private Dao<Spiel,        Integer> spielDao;
private Dao<Mannschaft,   Integer> mannschaftDao;
private Dao<Spieler,      Integer> spielerDao;
private Dao<Trikotnummer, Integer> trikotnummerDao;
private Dao<Trainer,      Integer> trainerDao;

private final Scanner scanner = new Scanner(System.in);

public static void main(String[] args) throws Exception {
	Logger.setGlobalLogLevel(Level.OFF);
	new Main().start();
}

public void start() throws Exception {
	ConnectionSource cs = new JdbcConnectionSource(DATABASE_URL);
	
	TableUtils.createTableIfNotExists(cs, Trainer.class);
	TableUtils.createTableIfNotExists(cs, Mannschaft.class);
	TableUtils.createTableIfNotExists(cs, Spiel.class);
	TableUtils.createTableIfNotExists(cs, Spieler.class);
	TableUtils.createTableIfNotExists(cs, Trikotnummer.class);
	
	trainerDao      = DaoManager.createDao(cs, Trainer.class);
	spielDao        = DaoManager.createDao(cs, Spiel.class);
	mannschaftDao   = DaoManager.createDao(cs, Mannschaft.class);
	spielerDao      = DaoManager.createDao(cs, Spieler.class);
	trikotnummerDao = DaoManager.createDao(cs, Trikotnummer.class);
	
	mainMenu();
	cs.close();
}

// ═══════════════════════════════════════════════════════════════════════
//  Menüs
// ═══════════════════════════════════════════════════════════════════════

private void mainMenu() throws Exception {
	while (true) {
		System.out.println("\n╔══════════════════════════════════╗");
		System.out.println("║   FC ARSENAL – VERWALTUNG        ║");
		System.out.println("╠══════════════════════════════════╣");
		System.out.println("║  1  Spieler verwalten            ║");
		System.out.println("║  2  Mannschaften verwalten       ║");
		System.out.println("║  3  Spiele verwalten             ║");
		System.out.println("║  4  Ligatabelle anzeigen         ║");
		System.out.println("║  0  Beenden                      ║");
		System.out.println("╚══════════════════════════════════╝");
		System.out.print("  Auswahl: ");
		
		switch (readInt()) {
			case 1 -> spielerMenu();
			case 2 -> mannschaftMenu();
			case 3 -> spieleMenu();
			case 4 -> ligatabelle();
			case 0 -> { System.out.println("Auf Wiedersehen!"); return; }
			default -> System.out.println("  ! Ungültige Eingabe.");
		}
	}
}

// ── Spieler ──────────────────────────────────────────────────────────

private void spielerMenu() throws Exception {
	while (true) {
		System.out.println("\n── Spieler ─────────────────────────");
		System.out.println("  1  Alle Spieler anzeigen");
		System.out.println("  2  Spieler anlegen");
		System.out.println("  3  Spieler bearbeiten");
		System.out.println("  4  Spieler löschen");
		System.out.println("  0  Zurück");
		System.out.print("  Auswahl: ");
		
		switch (readInt()) {
			case 1 -> alleSpielerAnzeigen();
			case 2 -> spielerAnlegen();
			case 3 -> spielerBearbeiten();
			case 4 -> spielerLoeschen();
			case 0 -> { return; }
			default -> System.out.println("  ! Ungültige Eingabe.");
		}
	}
}

private void alleSpielerAnzeigen() throws Exception {
	List<Spieler> liste = spielerDao.queryForAll();
	if (liste.isEmpty()) { System.out.println("  Keine Spieler vorhanden."); return; }
	
	System.out.println("\n  #    Name                       Position      Geburtsdatum  Mannschaft");
	System.out.println("  ─────────────────────────────────────────────────────────────────────────");
	for (Spieler sp : liste) {
		List<Trikotnummer> nrListe = trikotnummerDao.queryForEq(Trikotnummer.FIELD_SPIELER, sp.getId());
		String nr  = nrListe.isEmpty() ? " –" : String.format("%2d", nrListe.get(0).getNummer());
		String geb = sp.getGeburtsdatum() != null ? sp.getGeburtsdatum().toString() : "–";
		String mann = sp.getMannschaft() != null ? sp.getMannschaft().getName() : "–";
		System.out.printf("  #%-3s %-27s %-13s %-13s %s%n",
				nr, sp.getName(), sp.getPosition(), geb, mann);
	}
}

private void spielerAnlegen() throws Exception {
	System.out.println("\n── Spieler anlegen ─────────────────");
	
	System.out.print("  Name: ");
	String name = scanner.nextLine().trim();
	if (name.isEmpty()) { System.out.println("  ! Name darf nicht leer sein."); return; }
	
	System.out.print("  Geburtsdatum (YYYY-MM-DD, leer lassen = überspringen): ");
	String gebStr = scanner.nextLine().trim();
	LocalDate geb = null;
	if (!gebStr.isEmpty()) {
		try { geb = LocalDate.parse(gebStr); }
		catch (Exception e) { System.out.println("  ! Ungültiges Datum, wird übersprungen."); }
	}
	
	System.out.println("  Position:");
	System.out.println("    1 Torwart  2 Abwehr  3 Mittelfeld  4 Sturm  5 Flügel");
	System.out.print("  Auswahl: ");
	String position = switch (readInt()) {
		case 1 -> "Torwart";
		case 2 -> "Abwehr";
		case 3 -> "Mittelfeld";
		case 4 -> "Sturm";
		case 5 -> "Flügel";
		default -> null;
	};
	if (position == null) { System.out.println("  ! Ungültige Position."); return; }
	
	System.out.print("  Trikotnummer: ");
	int nr = readInt();
	if (!trikotnummerDao.queryForEq(Trikotnummer.FIELD_NUMMER, nr).isEmpty()) {
		System.out.println("  ! Trikotnummer " + nr + " ist bereits vergeben.");
		return;
	}
	
	Mannschaft mannschaft = mannschaftAuswaehlen();
	if (mannschaft == null) return;
	
	Mannschaft frisch = mannschaftDao.queryForId(mannschaft.getId());
	if (frisch.istVoll()) {
		System.out.printf("  ! %s ist voll (%d/%d).%n",
				frisch.getName(), frisch.getKaderlimit(), frisch.getKaderlimit());
		return;
	}
	
	Spieler sp = new Spieler(name, geb, position, frisch);
	spielerDao.create(sp);
	trikotnummerDao.create(new Trikotnummer(nr, sp));
	System.out.printf("  + #%d %s (%s) wurde angelegt.%n", nr, name, position);
}

private void spielerBearbeiten() throws Exception {
	System.out.println("\n── Spieler bearbeiten ──────────────");
	alleSpielerAnzeigen();
	
	System.out.print("\n  Name des Spielers: ");
	String name = scanner.nextLine().trim();
	List<Spieler> gefunden = spielerDao.queryForEq(Spieler.FIELD_NAME, name);
	if (gefunden.isEmpty()) { System.out.println("  ! Spieler nicht gefunden."); return; }
	
	Spieler sp = gefunden.get(0);
	System.out.println("  Was bearbeiten?");
	System.out.println("    1 Position  2 Mannschaft  3 Geburtsdatum");
	System.out.print("  Auswahl: ");
	
	switch (readInt()) {
		case 1 -> {
			System.out.println("  Neue Position:");
			System.out.println("    1 Torwart  2 Abwehr  3 Mittelfeld  4 Sturm  5 Flügel");
			System.out.print("  Auswahl: ");
			String pos = switch (readInt()) {
				case 1 -> "Torwart";
				case 2 -> "Abwehr";
				case 3 -> "Mittelfeld";
				case 4 -> "Sturm";
				case 5 -> "Flügel";
				default -> null;
			};
			if (pos == null) { System.out.println("  ! Ungültig."); return; }
			String alt = sp.getPosition();
			sp.setPosition(pos);
			spielerDao.update(sp);
			System.out.printf("  ~ Position: %s → %s%n", alt, pos);
		}
		case 2 -> {
			Mannschaft neu = mannschaftAuswaehlen();
			if (neu == null) return;
			sp.setMannschaft(neu);
			spielerDao.update(sp);
			System.out.println("  ~ Mannschaft aktualisiert.");
		}
		case 3 -> {
			System.out.print("  Neues Geburtsdatum (YYYY-MM-DD): ");
			String s = scanner.nextLine().trim();
			try {
				sp.setGeburtsdatum(LocalDate.parse(s));
				spielerDao.update(sp);
				System.out.println("  ~ Geburtsdatum aktualisiert.");
			} catch (Exception e) { System.out.println("  ! Ungültiges Datum."); }
		}
		default -> System.out.println("  ! Ungültig.");
	}
}

private void spielerLoeschen() throws Exception {
	System.out.println("\n── Spieler löschen ─────────────────");
	alleSpielerAnzeigen();
	
	System.out.print("\n  Name des Spielers: ");
	String name = scanner.nextLine().trim();
	List<Spieler> gefunden = spielerDao.queryForEq(Spieler.FIELD_NAME, name);
	if (gefunden.isEmpty()) { System.out.println("  ! Spieler nicht gefunden."); return; }
	
	Spieler sp = gefunden.get(0);
	System.out.print("  Wirklich löschen? (j/n): ");
	if (!scanner.nextLine().trim().equalsIgnoreCase("j")) { System.out.println("  Abgebrochen."); return; }
	
	List<Trikotnummer> nrListe = trikotnummerDao.queryForEq(Trikotnummer.FIELD_SPIELER, sp.getId());
	for (Trikotnummer tn : nrListe) trikotnummerDao.delete(tn);
	spielerDao.delete(sp);
	System.out.printf("  - %s wurde gelöscht.%n", name);
}

// ── Mannschaften ─────────────────────────────────────────────────────

private void mannschaftMenu() throws Exception {
	while (true) {
		System.out.println("\n── Mannschaften ────────────────────");
		System.out.println("  1  Alle Mannschaften anzeigen");
		System.out.println("  2  Mannschaft anlegen");
		System.out.println("  3  Kader einer Mannschaft anzeigen");
		System.out.println("  0  Zurück");
		System.out.print("  Auswahl: ");
		
		switch (readInt()) {
			case 1 -> alleMannschaftenAnzeigen();
			case 2 -> mannschaftAnlegen();
			case 3 -> kaderAnzeigen();
			case 0 -> { return; }
			default -> System.out.println("  ! Ungültige Eingabe.");
		}
	}
}

private void alleMannschaftenAnzeigen() throws Exception {
	List<Mannschaft> liste = mannschaftDao.queryForAll();
	if (liste.isEmpty()) { System.out.println("  Keine Mannschaften vorhanden."); return; }
	
	System.out.println("\n  Name                            Limit  Belegt  Typ    Trainer");
	System.out.println("  ──────────────────────────────────────────────────────────────────");
	for (Mannschaft m : liste) {
		int belegt = spielerDao.queryForEq(Spieler.FIELD_MANNSCHAFT, m.getId()).size();
		System.out.printf("  %-32s %5d  %6d  %-6s %s%n",
				m.getName(), m.getKaderlimit(), belegt,
				m.isHeimspiel() ? "Heim" : "Gast",
				m.getTrainer() != null ? m.getTrainer().getName() : "–");
	}
}

private void mannschaftAnlegen() throws Exception {
	System.out.println("\n── Mannschaft anlegen ──────────────");
	
	System.out.print("  Name: ");
	String name = scanner.nextLine().trim();
	if (name.isEmpty()) { System.out.println("  ! Name darf nicht leer sein."); return; }
	if (!mannschaftDao.queryForEq(Mannschaft.FIELD_NAME, name).isEmpty()) {
		System.out.println("  ! Eine Mannschaft mit diesem Namen existiert bereits.");
		return;
	}
	
	System.out.print("  Kaderlimit: ");
	int limit = readInt();
	
	System.out.print("  Heimmannschaft? (j/n): ");
	boolean heim = scanner.nextLine().trim().equalsIgnoreCase("j");
	
	System.out.print("  Trainername (leer = kein Trainer): ");
	String trainerName = scanner.nextLine().trim();
	Trainer trainer = null;
	if (!trainerName.isEmpty()) {
		List<Trainer> vorhandene = trainerDao.queryForEq(Trainer.FIELD_NAME, trainerName);
		if (!vorhandene.isEmpty()) {
			trainer = vorhandene.get(0);
		} else {
			System.out.print("  Trainer nicht gefunden. Nationalität: ");
			String nat = scanner.nextLine().trim();
			trainer = new Trainer(trainerName, nat.isEmpty() ? "unbekannt" : nat);
			trainerDao.create(trainer);
		}
	}
	
	Mannschaft m = new Mannschaft(name, limit, heim, null, trainer);
	mannschaftDao.create(m);
	System.out.printf("  + Mannschaft \"%s\" angelegt.%n", name);
}

private void kaderAnzeigen() throws Exception {
	alleMannschaftenAnzeigen();
	System.out.print("\n  Mannschaftsname: ");
	String name = scanner.nextLine().trim();
	List<Mannschaft> gefunden = mannschaftDao.queryForEq(Mannschaft.FIELD_NAME, name);
	if (gefunden.isEmpty()) { System.out.println("  ! Mannschaft nicht gefunden."); return; }
	
	Mannschaft m = mannschaftDao.queryForId(gefunden.get(0).getId());
	List<Spieler> spielerListe = spielerDao.queryForEq(Spieler.FIELD_MANNSCHAFT, m.getId());
	int belegt = spielerListe.size();
	
	System.out.printf("%n  [%s] %s | Trainer: %s | %d/%d Spieler%n",
			m.isHeimspiel() ? "Heim" : "Gast", m.getName(),
			m.getTrainer() != null ? m.getTrainer().getName() : "–",
			belegt, m.getKaderlimit());
	System.out.println("  ─────────────────────────────────────────────────");
	
	for (Spieler sp : spielerListe) {
		List<Trikotnummer> nrListe = trikotnummerDao.queryForEq(Trikotnummer.FIELD_SPIELER, sp.getId());
		String nr  = nrListe.isEmpty() ? " –" : String.format("%2d", nrListe.get(0).getNummer());
		String geb = sp.getGeburtsdatum() != null ? sp.getGeburtsdatum().toString() : "–";
		System.out.printf("  #%-3s %-25s %-13s *%s%n", nr, sp.getName(), sp.getPosition(), geb);
	}
}

// ── Spiele ───────────────────────────────────────────────────────────

private void spieleMenu() throws Exception {
	while (true) {
		System.out.println("\n── Spiele ──────────────────────────");
		System.out.println("  1  Alle Spiele anzeigen");
		System.out.println("  2  Spiel anlegen");
		System.out.println("  3  Ergebnis eintragen");
		System.out.println("  0  Zurück");
		System.out.print("  Auswahl: ");
		
		switch (readInt()) {
			case 1 -> alleSpieleanzeigen();
			case 2 -> spielAnlegen();
			case 3 -> ergebnisEintragen();
			case 0 -> { return; }
			default -> System.out.println("  ! Ungültige Eingabe.");
		}
	}
}

private void alleSpieleanzeigen() throws Exception {
	List<Spiel> liste = spielDao.queryForAll();
	if (liste.isEmpty()) { System.out.println("  Keine Spiele vorhanden."); return; }
	
	System.out.println("\n  ID  Datum        Uhrzeit  Heim                           Gast                    Ergebnis");
	System.out.println("  ─────────────────────────────────────────────────────────────────────────────────────────");
	for (Spiel s : liste) {
		String heim = s.getHeimmannschaft() != null ? s.getHeimmannschaft().getName() : "–";
		String gast = s.getGastmannschaft() != null ? s.getGastmannschaft().getName() : "–";
		String ergebnis = s.isGespielt() ? s.getToreHeim() + ":" + s.getToreGast() : "–:–";
		String zeit = s.getUhrzeit() != null ? s.getUhrzeit().toString() : "–";
		System.out.printf("  %-3d %-12s %-8s %-30s %-23s %s%n",
				s.getId(), s.getDatum(), zeit, heim, gast, ergebnis);
	}
}

private void spielAnlegen() throws Exception {
	System.out.println("\n── Spiel anlegen ───────────────────");
	
	System.out.print("  Datum (YYYY-MM-DD): ");
	LocalDate datum;
	try { datum = LocalDate.parse(scanner.nextLine().trim()); }
	catch (Exception e) { System.out.println("  ! Ungültiges Datum."); return; }
	
	System.out.print("  Uhrzeit (HH:MM, leer = keine): ");
	String zeitStr = scanner.nextLine().trim();
	LocalTime uhrzeit = null;
	if (!zeitStr.isEmpty()) {
		try { uhrzeit = LocalTime.parse(zeitStr); }
		catch (Exception e) { System.out.println("  ! Ungültige Uhrzeit, wird übersprungen."); }
	}
	
	System.out.println("  Heimmannschaft auswählen:");
	Mannschaft heim = mannschaftAuswaehlen();
	if (heim == null) return;
	
	System.out.println("  Gastmannschaft auswählen:");
	Mannschaft gast = mannschaftAuswaehlen();
	if (gast == null) return;
	
	if (heim.getId() == gast.getId()) {
		System.out.println("  ! Heim- und Gastmannschaft müssen verschieden sein.");
		return;
	}
	
	Spiel spiel = new Spiel(datum, uhrzeit, heim, gast);
	spielDao.create(spiel);
	System.out.printf("  + Spiel %s vs. %s am %s angelegt.%n",
			heim.getName(), gast.getName(), datum);
}

private void ergebnisEintragen() throws Exception {
	alleSpieleanzeigen();
	System.out.print("\n  Spiel-ID: ");
	int id = readInt();
	Spiel spiel = spielDao.queryForId(id);
	if (spiel == null) { System.out.println("  ! Spiel nicht gefunden."); return; }
	if (spiel.isGespielt()) { System.out.println("  ! Ergebnis wurde bereits eingetragen."); return; }
	
	System.out.print("  Tore Heim: ");
	int tH = readInt();
	System.out.print("  Tore Gast: ");
	int tG = readInt();
	
	spiel.ergebnisEintragen(tH, tG);
	spielDao.update(spiel);
	System.out.printf("  + Ergebnis gespeichert: %s%n", spiel);
}

// ── Ligatabelle ──────────────────────────────────────────────────────

private void ligatabelle() throws Exception {
	Ligatabelle tabelle = new Ligatabelle();
	for (Spiel s : spielDao.queryForAll()) {
		tabelle.spielErfassen(s);
	}
	tabelle.ausgeben();
}

// ═══════════════════════════════════════════════════════════════════════
//  Hilfsmethoden
// ═══════════════════════════════════════════════════════════════════════

private Mannschaft mannschaftAuswaehlen() throws Exception {
	List<Mannschaft> alle = mannschaftDao.queryForAll();
	if (alle.isEmpty()) { System.out.println("  ! Keine Mannschaften vorhanden."); return null; }
	
	for (int i = 0; i < alle.size(); i++) {
		System.out.printf("    %2d  %s%n", i + 1, alle.get(i).getName());
	}
	System.out.print("  Auswahl (Nummer): ");
	int idx = readInt() - 1;
	if (idx < 0 || idx >= alle.size()) { System.out.println("  ! Ungültige Auswahl."); return null; }
	return alle.get(idx);
}

private int readInt() {
	try {
		int val = Integer.parseInt(scanner.nextLine().trim());
		return val;
	} catch (NumberFormatException e) {
		return -1;
	}
}
}