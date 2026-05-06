package Zug_Fahrkarten_Verwaltung;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Collection;

@DatabaseTable(tableName = "wagon")
public class Wagon {

public static final String FIELD_ID = "id";
public static final String FIELD_BEZEICHNUNG = "bezeichnung";
public static final String FIELD_PLAETZE = "plaetze";
public static final String FIELD_FAHRT = "fahrt_id";

@DatabaseField(generatedId = true)
private int id;

@DatabaseField(canBeNull = false, unique = true)
private String bezeichnung;

@DatabaseField(canBeNull = false)
private int plaetze;

@DatabaseField(foreign = true, canBeNull = false, foreignAutoRefresh = true)
private Fahrt fahrt;

@ForeignCollectionField(eager = true)
private Collection<Fahrgast> fahrgaeste;

Wagon() {
	// Pflicht für ORMLite
}

public Wagon(String bezeichnung, int plaetze, Fahrt fahrt) {
	this.bezeichnung = bezeichnung;
	this.plaetze = plaetze;
	this.fahrt = fahrt;
}

public boolean istVoll() {
	return fahrgaeste != null && fahrgaeste.size() >= plaetze;
}

public int freiePlaetze() {
	if (fahrgaeste == null) {
		return plaetze;
	}
	return plaetze - fahrgaeste.size();
}

public int getId() {
	return id;
}

public String getBezeichnung() {
	return bezeichnung;
}

public Fahrt getFahrt() {
	return fahrt;
}
}