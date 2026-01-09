package KundeArtikelLager.KundeArtikelLeager;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class JSONReadFromTheFileTest {


public static void jsonImport(Connection con) {
	
	JSONParser parser = new JSONParser();
	
	String sql = "INSERT IGNORE INTO artikel (name, preis) VALUES (?, ?)";
	
	try {
		JSONArray jsonArray = (JSONArray) parser.parse(
				new FileReader("ArtikelJSON.json")
		);
		
		PreparedStatement ps = con.prepareStatement(sql);
		
		for (Object obj : jsonArray) {
			JSONObject artikel = (JSONObject) obj;
			
			String name = (String) artikel.get("name");
			Number preisNumber = (Number) artikel.get("preis");
			double preis = preisNumber.doubleValue();
			
			ps.setString(1, name);
			ps.setDouble(2, preis);
			ps.executeUpdate();
		}
		
		ps.close();
		
	} catch (Exception e) {
		e.printStackTrace();
	}
}
}
