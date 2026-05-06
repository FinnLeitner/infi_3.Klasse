package Fussball_Club_Verwaltung;

import java.time.LocalDate;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class Main {

private static final String DATABASE_URL = "jdbc:h2:mem:fussballclub";

private Dao<Spiel, Integer> spielDao;
private Dao<Mannschaft, Integer> mannschaftDao;
private Dao<Spieler, Integer> spielerDao;

public static void main(String[] args) throws Exception {
	new Main().start();
}

public void start() throws Exception {
	
	ConnectionSource connectionSource =
			new JdbcConnectionSource(DATABASE_URL);
	
	// Tabellen erstellen
	TableUtils.createTable(connectionSource, Spiel.class);
	TableUtils.createTable(connectionSource, Mannschaft.class);
	TableUtils.createTable(connectionSource, Spieler.class);
	
	// DAOs erstellen
	spielDao = DaoManager.createDao(connectionSource, Spiel.class);
	mannschaftDao = DaoManager.createDao(connectionSource, Mannschaft.class);
	spielerDao = DaoManager.createDao(connectionSource, Spieler.class);
	
	// Spiele erstellen
	Spiel s1 = new Spiel("FC Bayern München", LocalDate.of(2026, 8, 15));
	Spiel s2 = new Spiel("Borussia Dortmund", LocalDate.of(2026, 8, 22));
	spielDao.create(s1);
	spielDao.create(s2);
	
	// Mannschaften erstellen (mit Kaderlimit)
	Mannschaft m1 = new Mannschaft("Startelf A", 2, s1);
	Mannschaft m2 = new Mannschaft("Ersatzbank A", 3, s1);
	Mannschaft m3 = new Mannschaft("Startelf B", 3, s2);
	mannschaftDao.create(m1);
	mannschaftDao.create(m2);
	mannschaftDao.create(m3);
	
	// Spieler anmelden
	meldeSpielerAn("Thomas Müller", "Stürmer", m1);
	meldeSpielerAn("Manuel Neuer", "Torwart", m1);
	meldeSpielerAn("Leon Goretzka", "Mittelfeld", m1);   // Startelf A ist voll → wird abgelehnt
	
	meldeSpielerAn("Serge Gnabry", "Flügel", m2);
	meldeSpielerAn("Leroy Sané", "Flügel", m3);
	
	// Alle Spieler anzeigen
	List<Spieler> alle = spielerDao.queryForAll();
	
	System.out.println("\nAngemeldete Spieler:");
	for (Spieler sp : alle) {
		System.out.println(
				sp.getId() + " "
						+ sp.getName()
						+ " (" + sp.getPosition() + ")"
						+ " -> Mannschaft: " + sp.getMannschaft().getName()
						+ " -> Gegner: " + sp.getMannschaft().getSpiel().getGegner()
						+ " am " + sp.getMannschaft().getSpiel().getDatum()
		);
	}
	
	connectionSource.close();
}

private void meldeSpielerAn(String name, String position, Mannschaft mannschaft) throws Exception {
	
	// Mannschaft neu aus DB laden → aktuellen Kaderstand holen
	Mannschaft frischeMannschaft = mannschaftDao.queryForId(mannschaft.getId());
	
	if (frischeMannschaft.istVoll()) {
		System.out.println("Mannschaft "
				+ frischeMannschaft.getName()
				+ " ist voll! "
				+ name
				+ " kann nicht angemeldet werden.");
		return;
	}
	
	Spieler spieler = new Spieler(name, position, frischeMannschaft);
	spielerDao.create(spieler);
	
	System.out.println(name
			+ " (" + position + ") wurde für Mannschaft "
			+ frischeMannschaft.getName()
			+ " angemeldet. ("
			+ (frischeMannschaft.getKaderlimit() - frischeMannschaft.freieKaderplaetze() + 1)
			+ "/" + frischeMannschaft.getKaderlimit() + ")");
}
}