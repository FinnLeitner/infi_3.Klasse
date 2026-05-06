package Zug_Fahrkarten_Verwaltung;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.BaseDataType;
import com.j256.ormlite.support.DatabaseResults;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.SQLException;
import java.time.LocalDate;

@DatabaseTable(tableName = "fahrt")
public class Fahrt {

public static final String FIELD_ID = "id";
public static final String FIELD_ZUG = "zug";
public static final String FIELD_DATUM = "datum";

@DatabaseField(generatedId = true)
private int id;

@DatabaseField(canBeNull = false)
private String zug;

@DatabaseField(canBeNull = false, persisterClass = LocalDatePersister.class)
private LocalDate datum;

Fahrt() {
	// Pflicht für ORMLite
}

public Fahrt(String zug, LocalDate datum) {
	this.zug = zug;
	this.datum = datum;
}

public int getId() {
	return id;
}

public String getZug() {
	return zug;
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