package Fussball_Club_Verwaltung;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Repräsentiert die Trikotnummer eines Spielers im Club.
 * Eine Trikotnummer ist clubweit eindeutig (unabhängig vom Spiel).
 */
@DatabaseTable(tableName = "trikotnummer")
public class Trikotnummer {

public static final String FIELD_ID = "id";
public static final String FIELD_NUMMER = "nummer";
public static final String FIELD_SPIELER = "spieler_id";

@DatabaseField(generatedId = true)
private int id;

@DatabaseField(canBeNull = false, unique = true)
private int nummer;

@DatabaseField(foreign = true, canBeNull = false, foreignAutoRefresh = true, unique = true)
private Spieler spieler;

Trikotnummer() {
	// Pflicht für ORMLite
}

public Trikotnummer(int nummer, Spieler spieler) {
	this.nummer = nummer;
	this.spieler = spieler;
}

public int getId() {
	return id;
}

public int getNummer() {
	return nummer;
}

public Spieler getSpieler() {
	return spieler;
}

@Override
public String toString() {
	return "#" + nummer + " " + spieler.getName();
}
}