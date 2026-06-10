package Fussball_Club_Verwaltung;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Collection;

// Ein Trainer leitet genau eine Mannschaft pro Spiel.

@DatabaseTable(tableName = "trainer")
public class Trainer {

public static final String FIELD_ID = "id";
public static final String FIELD_NAME = "name";
public static final String FIELD_NATIONALITAET = "nationalitaet";

@DatabaseField(generatedId = true)
private int id;

@DatabaseField(canBeNull = false)
private String name;

@DatabaseField(canBeNull = false)
private String nationalitaet;

@ForeignCollectionField(eager = false)
private Collection<Mannschaft> mannschaften;

Trainer() {
	// Pflicht für ORMLite
}

public Trainer(String name, String nationalitaet) {
	this.name = name;
	this.nationalitaet = nationalitaet;
}

public int getId() {
	return id;
}

public String getName() {
	return name;
}

public String getNationalitaet() {
	return nationalitaet;
}

@Override
public String toString() {
	return name + " (" + nationalitaet + ")";
}
}