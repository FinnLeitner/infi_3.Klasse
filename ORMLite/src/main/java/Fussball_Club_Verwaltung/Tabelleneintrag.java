package Fussball_Club_Verwaltung;


public class Tabelleneintrag implements Comparable<Tabelleneintrag> {

private final String mannschaftsname;
private int spiele       = 0;
private int siege        = 0;
private int unentschieden = 0;
private int niederlagen  = 0;
private int toreGeschossen = 0;
private int toreKassiert  = 0;

public Tabelleneintrag(String mannschaftsname) {
	this.mannschaftsname = mannschaftsname;
}

// ── Ergebnis verarbeiten ─────────────────────────────────────────────────

public void addSieg(int geschossen, int kassiert) {
	spiele++;
	siege++;
	toreGeschossen += geschossen;
	toreKassiert   += kassiert;
}

public void addUnentschieden(int geschossen, int kassiert) {
	spiele++;
	unentschieden++;
	toreGeschossen += geschossen;
	toreKassiert   += kassiert;
}

public void addNiederlage(int geschossen, int kassiert) {
	spiele++;
	niederlagen++;
	toreGeschossen += geschossen;
	toreKassiert   += kassiert;
}

// ── Berechnungen ─────────────────────────────────────────────────────────

public int getPunkte()        { return siege * 3 + unentschieden; }
public int getTordifferenz()  { return toreGeschossen - toreKassiert; }

// ── Sortierung: Punkte → Tordifferenz → Tore ────────────────────────────

@Override
public int compareTo(Tabelleneintrag o) {
	if (this.getPunkte() != o.getPunkte())
		return Integer.compare(o.getPunkte(), this.getPunkte());
	if (this.getTordifferenz() != o.getTordifferenz())
		return Integer.compare(o.getTordifferenz(), this.getTordifferenz());
	return Integer.compare(o.toreGeschossen, this.toreGeschossen);
}

// ── Getter ────────────────────────────────────────────────────────────────

public String getMannschaftsname() { return mannschaftsname; }
public int getSpiele()             { return spiele; }
public int getSiege()              { return siege; }
public int getUnentschieden()      { return unentschieden; }
public int getNiederlagen()        { return niederlagen; }
public int getToreGeschossen()     { return toreGeschossen; }
public int getToreKassiert()       { return toreKassiert; }

// ── Ausgabe ───────────────────────────────────────────────────────────────

@Override
public String toString() {
	return String.format("%-25s | Sp:%2d | S:%2d U:%2d N:%2d | %2d:%2d (%+3d) | Pkt: %2d",
			mannschaftsname, spiele, siege, unentschieden, niederlagen,
			toreGeschossen, toreKassiert, getTordifferenz(), getPunkte());
}
}