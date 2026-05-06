package Fussball_Club_Verwaltung;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Collection;

@DatabaseTable(tableName = "mannschaft")
public class Mannschaft {

public static final String FIELD_ID = "id";
public static final String FIELD_NAME = "name";
public static final String FIELD_KADERLIMIT = "kaderlimit";
public static final String FIELD_SPIEL = "spiel_id";

@DatabaseField(generatedId = true)
private int id;

@DatabaseField(canBeNull = false, unique = true)
private String name;

@DatabaseField(canBeNull = false)
private int kaderlimit;

@DatabaseField(foreign = true, canBeNull = false, foreignAutoRefresh = true)
private Spiel spiel;

@ForeignCollectionField(eager = true)
private Collection<Spieler> spieler;

Mannschaft() {
	// Pflicht für ORMLite
}

public Mannschaft(String name, int kaderlimit, Spiel spiel) {
	this.name = name;
	this.kaderlimit = kaderlimit;
	this.spiel = spiel;
}

public boolean istVoll() {
	return spieler != null && spieler.size() >= kaderlimit;
}

public int freieKaderplaetze() {
	if (spieler == null) {
		return kaderlimit;
	}
	return kaderlimit - spieler.size();
}

public int getId() {
	return id;
}

public String getName() {
	return name;
}

public Spiel getSpiel() {
	return spiel;
}

public int getKaderlimit() {
	return kaderlimit;
}
}