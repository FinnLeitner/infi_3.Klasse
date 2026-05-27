package Fussball_Club_Verwaltung;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Collection;

/**
 * Repräsentiert eine Mannschaft (Startelf oder Ersatzbank) für ein Spiel.
 *
 * M02: Mannschaft anlegen, Spieler zuordnen (1:n)
 * M04: Trainer ist zugewiesen
 */
@DatabaseTable(tableName = "mannschaft")
public class Mannschaft {

public static final String FIELD_ID         = "id";
public static final String FIELD_NAME       = "name";
public static final String FIELD_KADERLIMIT = "kaderlimit";
public static final String FIELD_SPIEL      = "spiel_id";
public static final String FIELD_TRAINER    = "trainer_id";
public static final String FIELD_HEIMSPIEL  = "heimspiel";

@DatabaseField(generatedId = true)
private int id;

@DatabaseField(canBeNull = false, unique = true)
private String name;

@DatabaseField(canBeNull = false)
private int kaderlimit;

/** true = Heimmannschaft, false = Gastmannschaft */
@DatabaseField(canBeNull = false)
private boolean heimspiel;

/**
 * M05: Optionale Verknüpfung zu einem Spiel.
 * Kann null sein, wenn die Mannschaft noch keinem Spiel zugeordnet ist.
 */
@DatabaseField(foreign = true, canBeNull = true, foreignAutoRefresh = true)
private Spiel spiel;

/** M04: Trainer der Mannschaft (kann null sein bei Gastteams) */
@DatabaseField(foreign = true, canBeNull = true, foreignAutoRefresh = true)
private Trainer trainer;

@ForeignCollectionField(eager = true)
private Collection<Spieler> spieler;

Mannschaft() {
	// Pflicht für ORMLite
}

/** Konstruktor mit optionalem Spiel (null erlaubt) */
public Mannschaft(String name, int kaderlimit, boolean heimspiel, Spiel spiel, Trainer trainer) {
	this.name       = name;
	this.kaderlimit = kaderlimit;
	this.heimspiel  = heimspiel;
	this.spiel      = spiel;
	this.trainer    = trainer;
}

// ── Kaderverwaltung ───────────────────────────────────────────────────────

public boolean istVoll() {
	return spieler != null && spieler.size() >= kaderlimit;
}

public int freieKaderplaetze() {
	if (spieler == null) return kaderlimit;
	return kaderlimit - spieler.size();
}

// ── Getter ────────────────────────────────────────────────────────────────

public int getId()                       { return id; }
public String getName()                  { return name; }
public int getKaderlimit()               { return kaderlimit; }
public boolean isHeimspiel()             { return heimspiel; }
public Spiel getSpiel()                  { return spiel; }
public Trainer getTrainer()              { return trainer; }
public Collection<Spieler> getSpieler()  { return spieler; }

// ── Setter ────────────────────────────────────────────────────────────────

public void setName(String name)         { this.name = name; }
public void setSpiel(Spiel spiel)        { this.spiel = spiel; }
public void setTrainer(Trainer trainer)  { this.trainer = trainer; }

@Override
public String toString() {
	return name + (trainer != null ? " [" + trainer.getName() + "]" : "");
}
}