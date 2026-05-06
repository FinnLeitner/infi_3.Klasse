package Fussball_Club_Verwaltung;

import java.time.LocalDate;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * Arsenal FC – Verwaltungsbeispiel Saison 2025/26
 *
 * Datenbankstruktur:
 *   trainer  <── mannschaft ──> spiel
 *                    ^
 *                 spieler ──> trikotnummer
 */
public class Main {

private static final String DATABASE_URL = "jdbc:h2:mem:arsenal";

private Dao<Spiel,        Integer> spielDao;
private Dao<Mannschaft,   Integer> mannschaftDao;
private Dao<Spieler,      Integer> spielerDao;
private Dao<Trikotnummer, Integer> trikotnummerDao;
private Dao<Trainer,      Integer> trainerDao;

public static void main(String[] args) throws Exception {
	new Main().start();
}

public void start() throws Exception {
	
	ConnectionSource cs = new JdbcConnectionSource(DATABASE_URL);
	
	TableUtils.createTable(cs, Trainer.class);
	TableUtils.createTable(cs, Spiel.class);
	TableUtils.createTable(cs, Mannschaft.class);
	TableUtils.createTable(cs, Spieler.class);
	TableUtils.createTable(cs, Trikotnummer.class);
	
	trainerDao      = DaoManager.createDao(cs, Trainer.class);
	spielDao        = DaoManager.createDao(cs, Spiel.class);
	mannschaftDao   = DaoManager.createDao(cs, Mannschaft.class);
	spielerDao      = DaoManager.createDao(cs, Spieler.class);
	trikotnummerDao = DaoManager.createDao(cs, Trikotnummer.class);
	
	// Trainer
	Trainer arteta = new Trainer("Mikel Arteta", "Spanisch");
	trainerDao.create(arteta);
	
	// Spiele
	Spiel spiel1 = new Spiel("Chelsea FC",      LocalDate.of(2026, 5, 10));
	Spiel spiel2 = new Spiel("Manchester City", LocalDate.of(2026, 5, 17));
	spielDao.create(spiel1);
	spielDao.create(spiel2);
	
	// Mannschaften
	Mannschaft startelf1 = new Mannschaft("Arsenal Startelf – Chelsea",   11, true, spiel1, arteta);
	Mannschaft ersatz1   = new Mannschaft("Arsenal Ersatzbank – Chelsea",  7, true, spiel1, arteta);
	Mannschaft startelf2 = new Mannschaft("Arsenal Startelf – Man City",  11, true, spiel2, arteta);
	Mannschaft ersatz2   = new Mannschaft("Arsenal Ersatzbank – Man City", 7, true, spiel2, arteta);
	mannschaftDao.create(startelf1);
	mannschaftDao.create(ersatz1);
	mannschaftDao.create(startelf2);
	mannschaftDao.create(ersatz2);
	
	// Spiel 1 Startelf
	meldeSpielerAn("David Raya",          "Torwart",     1,  startelf1);
	meldeSpielerAn("William Saliba",       "Abwehr",      2,  startelf1);
	meldeSpielerAn("Ben White",            "Abwehr",      4,  startelf1);
	meldeSpielerAn("Gabriel Magalhaes",    "Abwehr",      6,  startelf1);
	meldeSpielerAn("Jurrien Timber",       "Abwehr",      12, startelf1);
	meldeSpielerAn("Declan Rice",          "Mittelfeld",  41, startelf1);
	meldeSpielerAn("Martin Zubimendi",     "Mittelfeld",  36, startelf1);
	meldeSpielerAn("Martin Odegaard",      "Mittelfeld",  8,  startelf1);
	meldeSpielerAn("Bukayo Saka",          "Flügel",      7,  startelf1);
	meldeSpielerAn("Viktor Gyokeres",      "Sturm",       14, startelf1);
	meldeSpielerAn("Gabriel Martinelli",   "Flügel",      11, startelf1);
	
	// Spiel 1 Ersatzbank
	meldeSpielerAn("Kepa Arrizabalaga",   "Torwart",     13, ersatz1);
	meldeSpielerAn("Cristhian Mosquera",  "Abwehr",      3,  ersatz1);
	meldeSpielerAn("Riccardo Calafiori",  "Abwehr",      33, ersatz1);
	meldeSpielerAn("Mikel Merino",        "Mittelfeld",  23, ersatz1);
	meldeSpielerAn("Kai Havertz",         "Mittelfeld",  29, ersatz1);
	meldeSpielerAn("Leandro Trossard",    "Flügel",      19, ersatz1);
	meldeSpielerAn("Ethan Nwaneri",       "Flügel",      22, ersatz1);
	
	// Überfüllungstest
	System.out.println("\n── Überfüllungstest: 8. Ersatzspieler für Spiel 1 ──");
	meldeSpielerAn("Noni Madueke",        "Flügel",      20, ersatz1);  // wird abgelehnt
	
	// Spiel 2 Startelf (andere Aufstellung)
	System.out.println("\n── Spiel 2 vs. Manchester City ──");
	meldeSpielerAn("David Raya",          "Torwart",     1,  startelf2);  // selber Spieler, neue Mannschaft
	meldeSpielerAn("William Saliba",      "Abwehr",      2,  startelf2);
	meldeSpielerAn("Riccardo Calafiori",  "Abwehr",      33, startelf2);
	meldeSpielerAn("Gabriel Magalhaes",   "Abwehr",      6,  startelf2);
	meldeSpielerAn("Myles Lewis-Skelly",  "Abwehr",      49, startelf2);
	meldeSpielerAn("Declan Rice",         "Mittelfeld",  41, startelf2);
	meldeSpielerAn("Martin Zubimendi",    "Mittelfeld",  36, startelf2);
	meldeSpielerAn("Noni Madueke",        "Flügel",      20, startelf2);  // jetzt erfolgreich
	meldeSpielerAn("Bukayo Saka",         "Flügel",      7,  startelf2);
	meldeSpielerAn("Viktor Gyokeres",     "Sturm",       14, startelf2);
	meldeSpielerAn("Gabriel Martinelli",  "Flügel",      11, startelf2);
	
	// Ersatzbank Spiel 2
	meldeSpielerAn("Kepa Arrizabalaga",   "Torwart",     13, ersatz2);
	meldeSpielerAn("Ben White",           "Abwehr",      4,  ersatz2);
	meldeSpielerAn("Jakub Kiwior",        "Abwehr",      15, ersatz2);
	meldeSpielerAn("Kai Havertz",         "Mittelfeld",  29, ersatz2);
	meldeSpielerAn("Mikel Merino",        "Mittelfeld",  23, ersatz2);
	meldeSpielerAn("Ethan Nwaneri",       "Flügel",      22, ersatz2);
	meldeSpielerAn("Leandro Trossard",    "Flügel",      19, ersatz2);
	
	// Ausgabe
	System.out.println("\n======================================================");
	System.out.println("  FC ARSENAL – KADERBERICHT SAISON 2025/26");
	System.out.println("======================================================");
	
	List<Mannschaft> mannschaften = mannschaftDao.queryForAll();
	
	for (Mannschaft m : mannschaften) {
		// Mannschaft frisch aus DB laden (damit ForeignCollection befüllt ist)
		Mannschaft frisch = mannschaftDao.queryForId(m.getId());
		int belegt = frisch.getKaderlimit() - frisch.freieKaderplaetze();
		
		System.out.printf("%n  [%s] %s%n    Trainer: %s | %s | %d/%d Spieler%n",
				frisch.isHeimspiel() ? "Heim" : "Gast",
				frisch.getName(),
				frisch.getTrainer() != null ? frisch.getTrainer().getName() : "–",
				frisch.getSpiel().getDatum(),
				belegt, frisch.getKaderlimit());
		System.out.println("    ─────────────────────────────────────────");
		
		List<Spieler> spielerListe = spielerDao.queryForEq(
				Spieler.FIELD_MANNSCHAFT + "_id", frisch.getId());
		
		for (Spieler sp : spielerListe) {
			List<Trikotnummer> nrListe = trikotnummerDao.queryForEq(
					Trikotnummer.FIELD_SPIELER + "_id", sp.getId());
			String nr = nrListe.isEmpty() ? " –" : String.format("%2d", nrListe.get(0).getNummer());
			System.out.printf("    #%-3s %-25s  %s%n", nr, sp.getName(), sp.getPosition());
		}
	}
	
	System.out.println("\n======================================================\n");
	cs.close();
}

private void meldeSpielerAn(String name, String position, int trikotnr, Mannschaft mannschaft)
		throws Exception {
	
	Mannschaft frisch = mannschaftDao.queryForId(mannschaft.getId());
	
	if (frisch.istVoll()) {
		System.out.printf("  X %-25s -> %s ist voll (%d/%d)%n",
				name, frisch.getName(), frisch.getKaderlimit(), frisch.getKaderlimit());
		return;
	}
	
	// Prüfen, ob Trikotnummer bereits vergeben
	List<Trikotnummer> vorhandene = trikotnummerDao.queryForEq(
			Trikotnummer.FIELD_NUMMER, trikotnr);
	
	Spieler sp = new Spieler(name, position, frisch);
	spielerDao.create(sp);
	
	if (vorhandene.isEmpty()) {
		// Neue Trikotnummer anlegen
		trikotnummerDao.create(new Trikotnummer(trikotnr, sp));
	}
	// (sonst: Trikotnummer existiert bereits – Spieler trägt dieselbe Nummer in anderem Spiel)
	
	System.out.printf("  + #%-3d %-25s -> %s%n", trikotnr, name, frisch.getName());
}
}