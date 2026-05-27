package Fussball_Club_Verwaltung;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.time.LocalDate;

/**
 * K02: Transferverwaltung – protokolliert Spielerwechsel zwischen Mannschaften.
 *
 * Jeder Transfer hält fest:
 *  - welcher Spieler gewechselt hat
 *  - von welcher Mannschaft (kann null sein = Neuzugang)
 *  - zu welcher Mannschaft
 *  - am welchem Datum
 */
@DatabaseTable(tableName = "transfer")
public class Transfer {

public static final String FIELD_ID          = "id";
public static final String FIELD_SPIELER     = "spieler_id";
public static final String FIELD_VON         = "von_mannschaft_id";
public static final String FIELD_NACH        = "nach_mannschaft_id";
public static final String FIELD_DATUM       = "datum";

@DatabaseField(generatedId = true)
private int id;

/** Der transferierte Spieler */
@DatabaseField(foreign = true, canBeNull = false, foreignAutoRefresh = true)
private Spieler spieler;

/** Abgebende Mannschaft (null = externer Neuzugang) */
@DatabaseField(foreign = true, canBeNull = true, foreignAutoRefresh = true,
		columnName = FIELD_VON)
private Mannschaft vonMannschaft;

/** Aufnehmende Mannschaft */
@DatabaseField(foreign = true, canBeNull = false, foreignAutoRefresh = true,
		columnName = FIELD_NACH)
private Mannschaft nachMannschaft;

/** Datum des Transfers (K02: "mit Datum protokollieren") */
@DatabaseField(canBeNull = false, persisterClass = Spiel.LocalDatePersister.class)
private LocalDate datum;

Transfer() {
	// Pflicht für ORMLite
}

/**
 * Erstellt einen neuen Transfereintrag.
 *
 * @param spieler        Der wechselnde Spieler
 * @param vonMannschaft  Bisherige Mannschaft (null = Neuzugang)
 * @param nachMannschaft Neue Mannschaft
 * @param datum          Datum des Wechsels
 */
public Transfer(Spieler spieler, Mannschaft vonMannschaft,
                Mannschaft nachMannschaft, LocalDate datum) {
	this.spieler         = spieler;
	this.vonMannschaft   = vonMannschaft;
	this.nachMannschaft  = nachMannschaft;
	this.datum           = datum;
}

// ── Getter ────────────────────────────────────────────────────────────────

public int getId()                    { return id; }
public Spieler getSpieler()           { return spieler; }
public Mannschaft getVonMannschaft()  { return vonMannschaft; }
public Mannschaft getNachMannschaft() { return nachMannschaft; }
public LocalDate getDatum()           { return datum; }

@Override
public String toString() {
	String von  = vonMannschaft  != null ? vonMannschaft.getName()  : "extern";
	String nach = nachMannschaft != null ? nachMannschaft.getName() : "unbekannt";
	return datum + "  " + spieler.getName() + "  " + von + " → " + nach;
}
}