package KundeArtikelLager.KundeArtikelLeager;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

import org.json.simple.JSONObject;
public class WriteToJSON {
public static void writeToFile(Connection con) throws IOException, SQLException {
	//Creating a JSONObject object
	JSONObject jsonObject = new JSONObject();
	//Inserting key-value pairs into the json object
	Statement stmt = con.createStatement();
	String sql = "SELECT * FROM bestellung";
	
	
	ResultSet rs = stmt.executeQuery(sql);
	FileWriter file = new FileWriter("output.json");
	// 3. Alle Daten durchgehen
	while (rs.next()) {
		int id = rs.getInt("id");           // Spalte id
		int kid = rs.getInt("kunde_ID");
		int aid = rs.getInt("artikel_ID");
		int menge = rs.getInt("menge");
		Timestamp date = rs.getTimestamp("datum"); // Timestamp aus DB
		jsonObject.put("id", id);
		jsonObject.put("kundeID", kid);
		jsonObject.put("artikelID", aid);
		jsonObject.put("menge", menge);
		jsonObject.put("date", date.toString());
		
		try {
		
		file.write(jsonObject.toJSONString()+"\n");
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		}
	file.close();
	System.out.println("JSON file created: "+jsonObject);
}
}