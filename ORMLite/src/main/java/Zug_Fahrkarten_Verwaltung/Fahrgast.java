package Zug_Fahrkarten_Verwaltung;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "fahrgast")
public class Fahrgast {

public static final String FIELD_ID = "id";
public static final String FIELD_NAME = "name";
public static final String FIELD_WAGON = "wagon_id";

@DatabaseField(generatedId = true)
private int id;

@DatabaseField(canBeNull = false)
private String name;

@DatabaseField(foreign = true, canBeNull = false, foreignAutoRefresh = true)
private Wagon wagon;

Fahrgast() {
	// Pflicht für ORMLite
}

public Fahrgast(String name, Wagon wagon) {
	this.name = name;
	this.wagon = wagon;
}

public int getId() {
	return id;
}

public String getName() {
	return name;
}

public Wagon getWagon() {
	return wagon;
}
}