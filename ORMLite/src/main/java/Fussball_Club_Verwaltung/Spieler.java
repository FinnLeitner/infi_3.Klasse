package Fussball_Club_Verwaltung;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "spieler")
public class Spieler {

public static final String FIELD_ID = "id";
public static final String FIELD_NAME = "name";
public static final String FIELD_POSITION = "position";
public static final String FIELD_MANNSCHAFT = "mannschaft_id";

@DatabaseField(generatedId = true)
private int id;

@DatabaseField(canBeNull = false)
private String name;

@DatabaseField(canBeNull = false)
private String position;

@DatabaseField(foreign = true, canBeNull = false, foreignAutoRefresh = true)
private Mannschaft mannschaft;

Spieler() {
	// Pflicht für ORMLite
}

public Spieler(String name, String position, Mannschaft mannschaft) {
	this.name = name;
	this.position = position;
	this.mannschaft = mannschaft;
}

public int getId() {
	return id;
}

public String getName() {
	return name;
}

public String getPosition() {
	return position;
}

public Mannschaft getMannschaft() {
	return mannschaft;
}
}