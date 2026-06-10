package Fussball_Club_Verwaltung;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.BaseDataType;
import com.j256.ormlite.support.DatabaseResults;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;


@DatabaseTable(tableName = "spiel")
public class Spiel {

public static final String FIELD_ID              = "id";
public static final String FIELD_DATUM           = "datum";
public static final String FIELD_UHRZEIT         = "uhrzeit";
public static final String FIELD_HEIMMANNSCHAFT  = "heimmannschaft_id";
public static final String FIELD_GASTMANNSCHAFT  = "gastmannschaft_id";
public static final String FIELD_TORE_HEIM       = "tore_heim";
public static final String FIELD_TORE_GAST       = "tore_gast";
public static final String FIELD_GESPIELT        = "gespielt";

@DatabaseField(generatedId = true)
private int id;

// Datum des Spiels
@DatabaseField(canBeNull = false, persisterClass = LocalDatePersister.class)
private LocalDate datum;

// Uhrzeit des Spiels
@DatabaseField(canBeNull = true, persisterClass = LocalTimePersister.class)
private LocalTime uhrzeit;

// Heimmannschaft (Foreign Key → Mannschaft)
@DatabaseField(foreign = true, canBeNull = true, foreignAutoRefresh = true,
		columnName = FIELD_HEIMMANNSCHAFT)
private Mannschaft heimmannschaft;

// Gastmannschaft (Foreign Key → Mannschaft)
@DatabaseField(foreign = true, canBeNull = true, foreignAutoRefresh = true,
		columnName = FIELD_GASTMANNSCHAFT)
private Mannschaft gastmannschaft;

// Tore der Heimmannschaft
@DatabaseField(canBeNull = false)
private int toreHeim = -1;  // -1 = noch nicht erfasst

//: Tore der Gastmannschaft
@DatabaseField(canBeNull = false)
private int toreGast = -1;  // -1 = noch nicht erfasst

// Hilfsflag: wurde das Ergebnis schon eingetragen?
@DatabaseField(canBeNull = false)
private boolean gespielt = false;

// ── Für Rückwärtskompatibilität: einfaches Gegner-Feld ────────────────────
// (Wird nicht mehr primär genutzt, Heimmannschaft/Gastmannschaft ist der Standard)
@DatabaseField(canBeNull = true)
private String gegner;

Spiel() {
	// Pflicht für ORMLite
}

// Einfacher Konstruktor: Gegner als String + Datum
public Spiel(String gegner, LocalDate datum) {
	this.gegner = gegner;
	this.datum  = datum;
}

// Vollständiger Konstruktor
public Spiel(LocalDate datum, LocalTime uhrzeit, Mannschaft heimmannschaft, Mannschaft gastmannschaft) {
	this.datum           = datum;
	this.uhrzeit         = uhrzeit;
	this.heimmannschaft  = heimmannschaft;
	this.gastmannschaft  = gastmannschaft;
}


/**
 * Trägt das Spielergebnis ein.
 * @param toreHeim Tore der Heimmannschaft
 * @param toreGast Tore der Gastmannschaft
 */
public void ergebnisEintragen(int toreHeim, int toreGast) {
	this.toreHeim  = toreHeim;
	this.toreGast  = toreGast;
	this.gespielt  = true;
}

// Liefert true, wenn das Ergebnis bereits erfasst wurde.
public boolean isGespielt() { return gespielt; }
/*
 Gibt den Namen der Siegermannschaft zurück,
 oder null bei Unentschieden / noch nicht gespielt.
 */
public String getSieger() {
	if (!gespielt) return null;
	if (toreHeim > toreGast) return heimmannschaft != null ? heimmannschaft.getName() : gegner;
	if (toreGast > toreHeim) return gastmannschaft != null ? gastmannschaft.getName() : "Gast";
	return null; // Unentschieden
}

public boolean isUnentschieden() {
	
	return gespielt && toreHeim == toreGast;
}

// ── Getter & Setter ───────────────────────────────────────────────────────

public int getId()                       { return id; }
public LocalDate getDatum()              { return datum; }
public LocalTime getUhrzeit()            { return uhrzeit; }
public Mannschaft getHeimmannschaft()    { return heimmannschaft; }
public Mannschaft getGastmannschaft()    { return gastmannschaft; }
public int getToreHeim()                 { return toreHeim; }
public int getToreGast()                 { return toreGast; }
public String getGegner()                { return gegner; }

public void setDatum(LocalDate datum)                     { this.datum = datum; }
public void setUhrzeit(LocalTime uhrzeit)                 { this.uhrzeit = uhrzeit; }
public void setHeimmannschaft(Mannschaft heimmannschaft)  { this.heimmannschaft = heimmannschaft; }
public void setGastmannschaft(Mannschaft gastmannschaft)  { this.gastmannschaft = gastmannschaft; }

@Override
public String toString() {
	String heim = heimmannschaft != null ? heimmannschaft.getName()
			: (gegner != null ? "Arsenal" : "Heim");
	String gast = gastmannschaft != null ? gastmannschaft.getName()
			: (gegner != null ? gegner : "Gast");
	String ergebnis = gespielt ? toreHeim + ":" + toreGast : "–:–";
	String zeit = uhrzeit != null ? " " + uhrzeit : "";
	return datum + zeit + "  " + heim + " vs. " + gast + "  " + ergebnis;
}



//ersistiert LocalDate als ISO-String in SQLite
public static class LocalDatePersister extends BaseDataType {
	
	private static final LocalDatePersister INSTANCE = new LocalDatePersister();
	public static LocalDatePersister getSingleton() { return INSTANCE; }
	
	private LocalDatePersister() {
		super(SqlType.STRING, new Class<?>[]{ LocalDate.class });
	}
	
	@Override public Object parseDefaultString(FieldType ft, String s) { return s; }
	@Override public int    getDefaultWidth()                           { return 10; }
	
	@Override
	public Object resultToSqlArg(FieldType ft, DatabaseResults r, int col) throws SQLException {
		return r.getString(col);
	}
	@Override
	public Object sqlArgToJava(FieldType ft, Object sqlArg, int col) {
		return LocalDate.parse((String) sqlArg);
	}
	@Override
	public Object javaToSqlArg(FieldType ft, Object javaObj) {
		return javaObj.toString();
	}
}

// Persistiert LocalTime als HH:mm-String in SQLite
public static class LocalTimePersister extends BaseDataType {
	
	private static final LocalTimePersister INSTANCE = new LocalTimePersister();
	public static LocalTimePersister getSingleton() { return INSTANCE; }
	
	private LocalTimePersister() {
		super(SqlType.STRING, new Class<?>[]{ LocalTime.class });
	}
	
	@Override public Object parseDefaultString(FieldType ft, String s) { return s; }
	@Override public int    getDefaultWidth()                           { return 5; }
	
	@Override
	public Object resultToSqlArg(FieldType ft, DatabaseResults r, int col) throws SQLException {
		return r.getString(col);
	}
	@Override
	public Object sqlArgToJava(FieldType ft, Object sqlArg, int col) {
		return LocalTime.parse((String) sqlArg);
	}
	@Override
	public Object javaToSqlArg(FieldType ft, Object javaObj) {
		return javaObj.toString();
	}
}
}