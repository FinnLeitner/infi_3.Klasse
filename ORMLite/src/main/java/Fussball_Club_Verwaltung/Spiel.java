package Fussball_Club_Verwaltung;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.BaseDataType;
import com.j256.ormlite.support.DatabaseResults;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.SQLException;
import java.time.LocalDate;
//Zeit einbauen
@DatabaseTable(tableName = "spiel")
public class Spiel {

public static final String FIELD_ID = "id";
public static final String FIELD_GEGNER = "gegner";
public static final String FIELD_DATUM = "datum";

@DatabaseField(generatedId = true)
private int id;

@DatabaseField(canBeNull = false)
private String gegner;

@DatabaseField(canBeNull = false, persisterClass = LocalDatePersister.class)
private LocalDate datum;

Spiel() {
	// Pflicht für ORMLite
}

public Spiel(String gegner, LocalDate datum) {
	this.gegner = gegner;
	this.datum = datum;
}

public int getId() {
	return id;
}

public String getGegner() {
	return gegner;
}

public LocalDate getDatum() {
	return datum;
}

public static class LocalDatePersister extends BaseDataType {
	
	private static final LocalDatePersister INSTANCE = new LocalDatePersister();
	
	public static LocalDatePersister getSingleton() {
		return INSTANCE;
	}
	
	private LocalDatePersister() {
		super(SqlType.STRING, new Class<?>[]{ LocalDate.class });
	}
	
	@Override
	public Object parseDefaultString(FieldType fieldType, String defaultStr) {
		return LocalDate.parse(defaultStr).toString();
	}
	
	@Override
	public int getDefaultWidth() {
		return 10; // "2026-06-01" = 10 Zeichen
	}
	
	@Override
	public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos)
			throws SQLException {
		return results.getString(columnPos);
	}
	
	@Override
	public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) {
		return LocalDate.parse((String) sqlArg);
	}
	
	@Override
	public Object javaToSqlArg(FieldType fieldType, Object javaObject) {
		return javaObject.toString();
	}
}
}