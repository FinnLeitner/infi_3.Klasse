package Fussball_Club_Verwaltung;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * Arsenal FC – Vollständiges Verwaltungsbeispiel Saison 2025/26
 *
 * Umgesetzte Pflichtfunktionen:
 *   M01 Spielerverwaltung  – anlegen, bearbeiten, löschen (name, geburtsdatum, position, trikotnummer)
 *   M02 Mannschaftsverwaltung – anlegen, Spieler zuordnen (1:n)
 *   M03 Datenbankanbindung – ORMLite + H2 (in-memory, produktiv: SQLite)
 *   M04 Trainerverwaltung  – anlegen, Mannschaft zuweisen
 *   M05 Spielplanverwaltung – Spiel mit Datum, Uhrzeit, Heim- & Gastmannschaft
 *   M06 Ergebniserfassung  – Tore eintragen und speichern
 *   M08 Ligatabelle        – automatische Berechnung aus Ergebnissen
 *
 * Optionale Erweiterungen:
 *   K02 Transferverwaltung – Spielerwechsel mit Datum protokollieren
 */
public class Main {

private static final String DATABASE_URL = "jdbc:sqlite:arsenal.db";

private Dao<Spiel,        Integer> spielDao;
private Dao<Mannschaft,   Integer> mannschaftDao;
private Dao<Spieler,      Integer> spielerDao;
private Dao<Trikotnummer, Integer> trikotnummerDao;
private Dao<Trainer,      Integer> trainerDao;
private Dao<Transfer,     Integer> transferDao;   // K02

public static void main(String[] args) throws Exception {
	new Main().start();
}

public void start() throws Exception {
	
	// ── Datenbankverbindung & Tabellen ──────────────────────────────────
	ConnectionSource cs = new JdbcConnectionSource(DATABASE_URL);
	
	TableUtils.createTableIfNotExists(cs, Trainer.class);
	TableUtils.createTableIfNotExists(cs, Mannschaft.class);
	TableUtils.createTableIfNotExists(cs, Spiel.class);
	TableUtils.createTableIfNotExists(cs, Spieler.class);
	TableUtils.createTableIfNotExists(cs, Trikotnummer.class);
	TableUtils.createTableIfNotExists(cs, Transfer.class);   // K02
	
	trainerDao      = DaoManager.createDao(cs, Trainer.class);
	spielDao        = DaoManager.createDao(cs, Spiel.class);
	mannschaftDao   = DaoManager.createDao(cs, Mannschaft.class);
	spielerDao      = DaoManager.createDao(cs, Spieler.class);
	trikotnummerDao = DaoManager.createDao(cs, Trikotnummer.class);
	transferDao     = DaoManager.createDao(cs, Transfer.class);  // K02
	
	// ── M04: Trainer ────────────────────────────────────────────────────
	Trainer arteta = new Trainer("Mikel Arteta", "Spanisch");
	trainerDao.create(arteta);
	
	// ── M05: Mannschaften (Heim/Gast) ───────────────────────────────────
	// Mannschaften ohne Spielbezug – repräsentieren den Club / das Team
	Mannschaft arsenalStartelf1 = new Mannschaft("Arsenal Startelf – Chelsea",   11, true,  null, arteta);
	Mannschaft arsenalErsatz1   = new Mannschaft("Arsenal Ersatzbank – Chelsea",  7, true,  null, arteta);
	Mannschaft arsenalStartelf2 = new Mannschaft("Arsenal Startelf – Man City",  11, true,  null, arteta);
	Mannschaft arsenalErsatz2   = new Mannschaft("Arsenal Ersatzbank – Man City", 7, true,  null, arteta);
	mannschaftDao.create(arsenalStartelf1);
	mannschaftDao.create(arsenalErsatz1);
	mannschaftDao.create(arsenalStartelf2);
	mannschaftDao.create(arsenalErsatz2);
	
	// Gegnerische "Mannschaften" (nur als Platzhalter für Heim/Gast-Zuordnung)
	Mannschaft chelsea    = new Mannschaft("Chelsea FC",      11, false, null, null);
	Mannschaft manCity    = new Mannschaft("Manchester City", 11, false, null, null);
	mannschaftDao.create(chelsea);
	mannschaftDao.create(manCity);
	
	// ── M05: Spiele mit Datum, Uhrzeit, Heim- & Gastmannschaft ──────────
	Spiel spiel1 = new Spiel(LocalDate.of(2026, 5, 10), LocalTime.of(15, 30),
			arsenalStartelf1, chelsea);
	Spiel spiel2 = new Spiel(LocalDate.of(2026, 5, 17), LocalTime.of(17, 0),
			arsenalStartelf2, manCity);
	spielDao.create(spiel1);
	spielDao.create(spiel2);
	
	System.out.println("── M05 Spielplan ──────────────────────────────────");
	System.out.println("  " + spiel1);
	System.out.println("  " + spiel2);
	
	// ── M01: Spieler anlegen (mit Geburtsdatum) ─────────────────────────
	System.out.println("\n── Spiel 1 vs. Chelsea FC ─────────────────────────");
	meldeSpielerAn("David Raya",         LocalDate.of(1995, 9,  15), "Torwart",    1,  arsenalStartelf1);
	meldeSpielerAn("William Saliba",     LocalDate.of(2001, 3,  24), "Abwehr",     2,  arsenalStartelf1);
	meldeSpielerAn("Ben White",          LocalDate.of(1997, 10, 8),  "Abwehr",     4,  arsenalStartelf1);
	meldeSpielerAn("Gabriel Magalhaes",  LocalDate.of(1997, 12, 19), "Abwehr",     6,  arsenalStartelf1);
	meldeSpielerAn("Jurrien Timber",     LocalDate.of(2001, 6,  17), "Abwehr",     12, arsenalStartelf1);
	meldeSpielerAn("Declan Rice",        LocalDate.of(1999, 1,  14), "Mittelfeld", 41, arsenalStartelf1);
	meldeSpielerAn("Martin Zubimendi",   LocalDate.of(1999, 2,  2),  "Mittelfeld", 36, arsenalStartelf1);
	meldeSpielerAn("Martin Odegaard",    LocalDate.of(1998, 12, 17), "Mittelfeld", 8,  arsenalStartelf1);
	meldeSpielerAn("Bukayo Saka",        LocalDate.of(2001, 9,  5),  "Flügel",     7,  arsenalStartelf1);
	meldeSpielerAn("Viktor Gyokeres",    LocalDate.of(1998, 6,  4),  "Sturm",      14, arsenalStartelf1);
	meldeSpielerAn("Gabriel Martinelli", LocalDate.of(2001, 6,  18), "Flügel",     11, arsenalStartelf1);
	
	meldeSpielerAn("Kepa Arrizabalaga",  LocalDate.of(1994, 10, 3),  "Torwart",    13, arsenalErsatz1);
	meldeSpielerAn("Cristhian Mosquera", LocalDate.of(2004, 4,  17), "Abwehr",     3,  arsenalErsatz1);
	meldeSpielerAn("Riccardo Calafiori", LocalDate.of(2002, 5,  19), "Abwehr",     33, arsenalErsatz1);
	meldeSpielerAn("Mikel Merino",       LocalDate.of(1996, 6,  22), "Mittelfeld", 23, arsenalErsatz1);
	meldeSpielerAn("Kai Havertz",        LocalDate.of(1999, 6,  11), "Mittelfeld", 29, arsenalErsatz1);
	meldeSpielerAn("Leandro Trossard",   LocalDate.of(1994, 12, 4),  "Flügel",     19, arsenalErsatz1);
	meldeSpielerAn("Ethan Nwaneri",      LocalDate.of(2007, 3,  21), "Flügel",     22, arsenalErsatz1);
	
	// ── Überfüllungstest ─────────────────────────────────────────────────
	System.out.println("\n── Überfüllungstest: 8. Ersatzspieler für Spiel 1 ──");
	meldeSpielerAn("Noni Madueke", LocalDate.of(2001, 12, 14), "Flügel", 20, arsenalErsatz1); // abgelehnt
	
	// ── Spiel 2 Kader ────────────────────────────────────────────────────
	System.out.println("\n── Spiel 2 vs. Manchester City ─────────────────────");
	meldeSpielerAn("David Raya",          LocalDate.of(1995, 9,  15), "Torwart",    1,  arsenalStartelf2);
	meldeSpielerAn("William Saliba",      LocalDate.of(2001, 3,  24), "Abwehr",     2,  arsenalStartelf2);
	meldeSpielerAn("Riccardo Calafiori",  LocalDate.of(2002, 5,  19), "Abwehr",     33, arsenalStartelf2);
	meldeSpielerAn("Gabriel Magalhaes",   LocalDate.of(1997, 12, 19), "Abwehr",     6,  arsenalStartelf2);
	meldeSpielerAn("Myles Lewis-Skelly",  LocalDate.of(2006, 9,  26), "Abwehr",     49, arsenalStartelf2);
	meldeSpielerAn("Declan Rice",         LocalDate.of(1999, 1,  14), "Mittelfeld", 41, arsenalStartelf2);
	meldeSpielerAn("Martin Zubimendi",    LocalDate.of(1999, 2,  2),  "Mittelfeld", 36, arsenalStartelf2);
	meldeSpielerAn("Noni Madueke",        LocalDate.of(2001, 12, 14), "Flügel",     20, arsenalStartelf2);
	meldeSpielerAn("Bukayo Saka",         LocalDate.of(2001, 9,  5),  "Flügel",     7,  arsenalStartelf2);
	meldeSpielerAn("Viktor Gyokeres",     LocalDate.of(1998, 6,  4),  "Sturm",      14, arsenalStartelf2);
	meldeSpielerAn("Gabriel Martinelli",  LocalDate.of(2001, 6,  18), "Flügel",     11, arsenalStartelf2);
	
	meldeSpielerAn("Kepa Arrizabalaga",   LocalDate.of(1994, 10, 3),  "Torwart",    13, arsenalErsatz2);
	meldeSpielerAn("Ben White",           LocalDate.of(1997, 10, 8),  "Abwehr",     4,  arsenalErsatz2);
	meldeSpielerAn("Jakub Kiwior",        LocalDate.of(2000, 2,  26), "Abwehr",     15, arsenalErsatz2);
	meldeSpielerAn("Kai Havertz",         LocalDate.of(1999, 6,  11), "Mittelfeld", 29, arsenalErsatz2);
	meldeSpielerAn("Mikel Merino",        LocalDate.of(1996, 6,  22), "Mittelfeld", 23, arsenalErsatz2);
	meldeSpielerAn("Ethan Nwaneri",       LocalDate.of(2007, 3,  21), "Flügel",     22, arsenalErsatz2);
	meldeSpielerAn("Leandro Trossard",    LocalDate.of(1994, 12, 4),  "Flügel",     19, arsenalErsatz2);
	
	// ── M06: Ergebnisse eintragen ────────────────────────────────────────
	System.out.println("\n── M06 Ergebnisse eintragen ────────────────────────");
	ergebnisEintragen(spiel1, 3, 1);  // Arsenal 3:1 Chelsea
	ergebnisEintragen(spiel2, 2, 2);  // Arsenal 2:2 Man City
	
	// ── M01: Spieler bearbeiten (Positionswechsel als Beispiel) ──────────
	System.out.println("\n── M01 Spieler bearbeiten ──────────────────────────");
	spielerBearbeiten("Kai Havertz", "Sturm");
	
	// ── M01: Spieler löschen (Beispiel) ─────────────────────────────────
	System.out.println("\n── M01 Spieler löschen ─────────────────────────────");
	spielerLoeschen("Cristhian Mosquera");
	
	// ── K02: Transfer protokollieren ─────────────────────────────────────
	System.out.println("\n── K02 Transfer protokollieren ─────────────────────");
	transferProtokollieren("Noni Madueke", arsenalErsatz1, arsenalStartelf2,
			LocalDate.of(2026, 5, 14));
	
	// ── Kaderbericht ausgeben ────────────────────────────────────────────
	System.out.println("\n======================================================");
	System.out.println("  FC ARSENAL – KADERBERICHT SAISON 2025/26");
	System.out.println("======================================================");
	
	List<Mannschaft> mannschaften = mannschaftDao.queryForAll();
	for (Mannschaft m : mannschaften) {
		if (m.getKaderlimit() == 11 || m.getKaderlimit() == 7) {
			kaderAusgeben(m);
		}
	}
	
	// ── M08: Ligatabelle berechnen und ausgeben ──────────────────────────
	Ligatabelle tabelle = new Ligatabelle();
	for (Spiel s : spielDao.queryForAll()) {
		tabelle.spielErfassen(s);
	}
	tabelle.ausgeben();
	
	// ── K02: Transfer-Protokoll ausgeben ─────────────────────────────────
	System.out.println("── K02 Transfer-Protokoll ──────────────────────────");
	for (Transfer t : transferDao.queryForAll()) {
		System.out.println("  " + t);
	}
	System.out.println();
	
	cs.close();
}

// ═════════════════════════════════════════════════════════════════════════
//  Hilfsmethoden
// ═════════════════════════════════════════════════════════════════════════

/**
 * M01 + M02: Spieler anlegen und Mannschaft zuordnen.
 * Legt auch eine Trikotnummer an, falls noch nicht vorhanden.
 */
private void meldeSpielerAn(String name, LocalDate geburtsdatum,
                            String position, int trikotnr, Mannschaft mannschaft)
		throws Exception {
	
	Mannschaft frisch = mannschaftDao.queryForId(mannschaft.getId());
	
	if (frisch.istVoll()) {
		System.out.printf("  X %-25s -> %s ist voll (%d/%d)%n",
				name, frisch.getName(), frisch.getKaderlimit(), frisch.getKaderlimit());
		return;
	}
	
	// M01: Spieler anlegen (mit Geburtsdatum)
	Spieler sp = new Spieler(name, geburtsdatum, position, frisch);
	spielerDao.create(sp);
	
	// Trikotnummer anlegen, falls noch nicht vergeben
	List<Trikotnummer> vorhandene = trikotnummerDao.queryForEq(
			Trikotnummer.FIELD_NUMMER, trikotnr);
	if (vorhandene.isEmpty()) {
		trikotnummerDao.create(new Trikotnummer(trikotnr, sp));
	}
	
	System.out.printf("  + #%-3d %-25s (%s) -> %s%n",
			trikotnr, name, geburtsdatum, frisch.getName());
}

/**
 * M01: Spieler bearbeiten – Position aktualisieren (Beispiel).
 */
private void spielerBearbeiten(String name, String neuePosition) throws Exception {
	List<Spieler> gefunden = spielerDao.queryForEq(Spieler.FIELD_NAME, name);
	if (gefunden.isEmpty()) {
		System.out.println("  ! Spieler nicht gefunden: " + name);
		return;
	}
	Spieler sp = gefunden.get(0);
	String alt = sp.getPosition();
	sp.setPosition(neuePosition);
	spielerDao.update(sp);
	System.out.printf("  ~ %-25s: Position %s → %s%n", name, alt, neuePosition);
}

/**
 * M01: Spieler löschen (inkl. verknüpfte Trikotnummer).
 */
private void spielerLoeschen(String name) throws Exception {
	List<Spieler> gefunden = spielerDao.queryForEq(Spieler.FIELD_NAME, name);
	if (gefunden.isEmpty()) {
		System.out.println("  ! Spieler nicht gefunden: " + name);
		return;
	}
	Spieler sp = gefunden.get(0);
	// Trikotnummer des Spielers zuerst löschen (FK-Constraint)
	List<Trikotnummer> nrListe = trikotnummerDao.queryForEq(
			Trikotnummer.FIELD_SPIELER, sp.getId());
	for (Trikotnummer tn : nrListe) trikotnummerDao.delete(tn);
	spielerDao.delete(sp);
	System.out.printf("  - %-25s gelöscht%n", name);
}

/**
 * M06: Ergebnis eines Spiels eintragen und in DB speichern.
 */
private void ergebnisEintragen(Spiel spiel, int toreHeim, int toreGast) throws Exception {
	spiel.ergebnisEintragen(toreHeim, toreGast);
	spielDao.update(spiel);
	System.out.printf("  Ergebnis gespeichert: %s%n", spiel);
}

/**
 * K02: Transfer eines Spielers zwischen zwei Mannschaften protokollieren.
 * Aktualisiert gleichzeitig den Mannschaftseintrag des Spielers.
 */
private void transferProtokollieren(String spielerName,
                                    Mannschaft von, Mannschaft nach,
                                    LocalDate datum) throws Exception {
	List<Spieler> gefunden = spielerDao.queryForEq(Spieler.FIELD_NAME, spielerName);
	if (gefunden.isEmpty()) {
		System.out.println("  ! Spieler für Transfer nicht gefunden: " + spielerName);
		return;
	}
	Spieler sp = gefunden.get(0);
	
	// Transfer-Eintrag anlegen (K02)
	Transfer transfer = new Transfer(sp, von, nach, datum);
	transferDao.create(transfer);
	
	// Mannschaftszuweisung des Spielers aktualisieren
	sp.setMannschaft(nach);
	spielerDao.update(sp);
	
	System.out.println("  → Transfer: " + transfer);
}

/**
 * Gibt den Kader einer Mannschaft formatiert aus.
 */
private void kaderAusgeben(Mannschaft m) throws Exception {
	Mannschaft frisch = mannschaftDao.queryForId(m.getId());
	int belegt = frisch.getKaderlimit() - frisch.freieKaderplaetze();
	
	System.out.printf("%n  [%s] %s%n    Trainer: %s | %d/%d Spieler%n",
			frisch.isHeimspiel() ? "Heim" : "Gast",
			frisch.getName(),
			frisch.getTrainer() != null ? frisch.getTrainer().getName() : "–",
			belegt, frisch.getKaderlimit());
	System.out.println("    ─────────────────────────────────────────────────");
	
	List<Spieler> spielerListe = spielerDao.queryForEq(
			Spieler.FIELD_MANNSCHAFT, frisch.getId());
	
	for (Spieler sp : spielerListe) {
		List<Trikotnummer> nrListe = trikotnummerDao.queryForEq(
				Trikotnummer.FIELD_SPIELER, sp.getId());
		String nr = nrListe.isEmpty() ? " –" : String.format("%2d", nrListe.get(0).getNummer());
		String geb = sp.getGeburtsdatum() != null ? sp.getGeburtsdatum().toString() : "–";
		System.out.printf("    #%-3s %-25s  %-12s  *%s%n",
				nr, sp.getName(), sp.getPosition(), geb);
	}
}
}