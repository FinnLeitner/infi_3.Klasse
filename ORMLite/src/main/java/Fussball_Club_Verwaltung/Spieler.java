package Fussball_Club_Verwaltung;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.time.LocalDate;

/**
 * Repräsentiert einen Spieler im Fußballclub.
 * M01: name, geburtsdatum, position, trikotnummer, mannschaft_id
 */
@DatabaseTable(tableName = "spieler")
public class Spieler {

public static final String FIELD_ID          = "id";
public static final String FIELD_NAME        = "name";
public static final String FIELD_GEBURTSDATUM = "geburtsdatum";
public static final String FIELD_POSITION    = "position";
public static final String FIELD_MANNSCHAFT  = "mannschaft_id";

@DatabaseField(generatedId = true)
private int id;

@DatabaseField(canBeNull = false)
private String name;

/** M01: Geburtsdatum des Spielers */
@DatabaseField(canBeNull = true, persisterClass = Spiel.LocalDatePersister.class)
private LocalDate geburtsdatum;

@DatabaseField(canBeNull = false)
private String position;

@DatabaseField(foreign = true, canBeNull = false, foreignAutoRefresh = true)
private Mannschaft mannschaft;

Spieler() {
	// Pflicht für ORMLite
}


public Spieler(String name, String position, Mannschaft mannschaft) {
	this(name, null, position, mannschaft);
}


public Spieler(String name, LocalDate geburtsdatum, String position, Mannschaft mannschaft) {
	this.name          = name;
	this.geburtsdatum  = geburtsdatum;
	this.position      = position;
	this.mannschaft    = mannschaft;
}



public int getId()               { return id; }
public String getName()          { return name; }
public LocalDate getGeburtsdatum() { return geburtsdatum; }
public String getPosition()      { return position; }
public Mannschaft getMannschaft(){ return mannschaft; }


public void setName(String name)                   { this.name = name; }
public void setGeburtsdatum(LocalDate geburtsdatum){ this.geburtsdatum = geburtsdatum; }
public void setPosition(String position)           { this.position = position; }
public void setMannschaft(Mannschaft mannschaft)   { this.mannschaft = mannschaft; }

@Override
public String toString() {
	return name + " (" + position + ")" +
			(geburtsdatum != null ? ", *" + geburtsdatum : "");
}
}