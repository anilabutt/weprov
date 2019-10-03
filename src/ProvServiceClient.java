

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
 
import org.json.JSONObject;
 
/**
 * @author anila
 * 
 */
 
public class ProvServiceClient {
	public static void main(String[] args) {
		String string = "";
		try {
 
			// Step1: Let's 1st read file from fileSystem
			// Change CrunchifyJSON.txt path here
			InputStream jsonInputStream = new FileInputStream("json.txt"); //"JSON_Template.txt"
			InputStreamReader jsonReader = new InputStreamReader(jsonInputStream);
			BufferedReader br = new BufferedReader(jsonReader);
			String line;
			while ((line = br.readLine()) != null) {
				string += line + "\n";
			}
 
			JSONObject jsonObject = new JSONObject(string);
			//System.out.println(jsonObject);
			String urlString = "http://localhost:8080/weprov/workflow";
			// Step2: Now pass JSON File Data to REST Service
			try {
				
				URL url = new URL(urlString);
				URLConnection connection = url.openConnection();
				connection.setDoOutput(true);
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setConnectTimeout(5000);
				connection.setReadTimeout(5000);
				OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
				out.write(jsonObject.toString());
				out.close();
 
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
 
				while (in.readLine() != null) {
				}
				System.out.println("\n Invoked Successfully.." + urlString);
				in.close();
			} catch (Exception e) {
				System.out.println("\nError while calling" + urlString);
				System.out.println(e);
			}
 
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}