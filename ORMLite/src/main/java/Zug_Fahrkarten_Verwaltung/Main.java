package Zug_Fahrkarten_Verwaltung;

import java.time.LocalDate;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class Main {

private static final String DATABASE_URL = "jdbc:h2:mem:zug";

private Dao<Fahrt, Integer> fahrtDao;
private Dao<Wagon, Integer> wagonDao;
private Dao<Fahrgast, Integer> fahrgastDao;

public static void main(String[] args) throws Exception {
	new Main().start();
}

public void start() throws Exception {
	
	ConnectionSource connectionSource =
			new JdbcConnectionSource(DATABASE_URL);
	
	// Tabellen erstellen
	TableUtils.createTable(connectionSource, Fahrt.class);
	TableUtils.createTable(connectionSource, Wagon.class);
	TableUtils.createTable(connectionSource, Fahrgast.class);
	
	// DAOs erstellen
	fahrtDao = DaoManager.createDao(connectionSource, Fahrt.class);
	wagonDao = DaoManager.createDao(connectionSource, Wagon.class);
	fahrgastDao = DaoManager.createDao(connectionSource, Fahrgast.class);
	
	// Fahrten erstellen
	Fahrt f1 = new Fahrt("RailJet 100", LocalDate.of(2026, 6, 1));
	Fahrt f2 = new Fahrt("ICE 200", LocalDate.of(2026, 6, 2));
	fahrtDao.create(f1);
	fahrtDao.create(f2);
	
	// Waggons erstellen
	Wagon w1 = new Wagon("A", 2, f1);
	Wagon w2 = new Wagon("B", 3, f1);
	Wagon w3 = new Wagon("C", 3, f2);
	wagonDao.create(w1);
	wagonDao.create(w2);
	wagonDao.create(w3);
	
	// Fahrgäste buchen
	bucheFahrgast("Max", w1);
	bucheFahrgast("Anna", w1);
	bucheFahrgast("Tom", w1);   // Wagon A ist voll → wird abgelehnt
	
	bucheFahrgast("Lisa", w2);
	bucheFahrgast("Lisa", w3);
	
	// Alle Fahrgäste anzeigen
	List<Fahrgast> alle = fahrgastDao.queryForAll();
	
	System.out.println("\nGebuchte Fahrgäste:");
	for (Fahrgast f : alle) {
		System.out.println(
				f.getId() + " "
						+ f.getName()
						+ " -> Wagon " + f.getWagon().getBezeichnung()
						+ " -> Zug " + f.getWagon().getFahrt().getZug()
						+ " am " + f.getWagon().getFahrt().getDatum()
		);
	}
	
	connectionSource.close();
}

private void bucheFahrgast(String name, Wagon wagon) throws Exception {
	
	// Wagon neu aus DB laden → aktuellen Belegungsstand holen
	Wagon frischerWagon = wagonDao.queryForId(wagon.getId());
	
	if (frischerWagon.istVoll()) {
		System.out.println("Wagon "
				+ frischerWagon.getBezeichnung()
				+ " ist voll!");
		return;
	}
	
	Fahrgast fahrgast = new Fahrgast(name, frischerWagon);
	fahrgastDao.create(fahrgast);
	
	System.out.println(name
			+ " hat eine Karte für Wagon "
			+ frischerWagon.getBezeichnung()
			+ " gebucht.");
}
}