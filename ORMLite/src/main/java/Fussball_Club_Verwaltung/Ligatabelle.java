package Fussball_Club_Verwaltung;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * M08: Automatische Berechnung der Ligatabelle aus Spielergebnissen.
 *
 * Verwendung:
 *   Ligatabelle tabelle = new Ligatabelle();
 *   tabelle.spielErfassen(spiel);      // für jedes gespielte Spiel
 *   tabelle.ausgeben();
 */
public class Ligatabelle {

/** Mannschaftsname → Tabelleneintrag */
private final Map<String, Tabelleneintrag> eintraege = new LinkedHashMap<>();

/**
 * Verarbeitet ein einzelnes Spiel.
 * Wird ignoriert, wenn das Spiel noch nicht gespielt wurde.
 *
 * @param spiel Ein Spiel mit eingetragenem Ergebnis (M06)
 */
public void spielErfassen(Spiel spiel) {
	if (!spiel.isGespielt()) return;
	
	String heimName = spiel.getHeimmannschaft() != null
			? spiel.getHeimmannschaft().getName()
			: (spiel.getGegner() != null ? "Arsenal" : "Heim");
	
	String gastName = spiel.getGastmannschaft() != null
			? spiel.getGastmannschaft().getName()
			: (spiel.getGegner() != null ? spiel.getGegner() : "Gast");
	
	int tH = spiel.getToreHeim();
	int tG = spiel.getToreGast();
	
	Tabelleneintrag heim = eintraege.computeIfAbsent(heimName, Tabelleneintrag::new);
	Tabelleneintrag gast = eintraege.computeIfAbsent(gastName, Tabelleneintrag::new);
	
	if (spiel.isUnentschieden()) {
		heim.addUnentschieden(tH, tG);
		gast.addUnentschieden(tG, tH);
	} else if (tH > tG) {          // Heimsieg
		heim.addSieg(tH, tG);
		gast.addNiederlage(tG, tH);
	} else {                        // Gastsieg
		gast.addSieg(tG, tH);
		heim.addNiederlage(tH, tG);
	}
}

/**
 * Gibt alle Einträge sortiert zurück (Punkte → Tordifferenz → Tore).
 */
public List<Tabelleneintrag> getSortierteTabelle() {
	List<Tabelleneintrag> liste = new ArrayList<>(eintraege.values());
	Collections.sort(liste);
	return liste;
}

/**
 * Gibt die Ligatabelle formatiert auf der Konsole aus.
 */
public void ausgeben() {
	List<Tabelleneintrag> sortiert = getSortierteTabelle();
	
	System.out.println("\n══════════════════════════════════════════════════════════════════════");
	System.out.println("  LIGATABELLE");
	System.out.println("  Platz  Mannschaft                | Sp  S  U  N | Tore      Diff | Pkt");
	System.out.println("  ──────────────────────────────────────────────────────────────────");
	
	int platz = 1;
	for (Tabelleneintrag e : sortiert) {
		System.out.printf("  %2d.    %s%n", platz++, e);
	}
	
	System.out.println("══════════════════════════════════════════════════════════════════════\n");
}
}