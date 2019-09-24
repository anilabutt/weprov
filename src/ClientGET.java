import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import com.csiro.webservices.app.RDFModel_to_JSON;

public class ClientGET {
    public static void main(String[] args) {
        try {

            URL url = new URL("http://localhost:8080/weprov/workflow/abc");//your url i.e fetch data from .
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "rdf/xml");
            
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP Error code : "
                        + conn.getResponseCode());
            }
            
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(in);
            
            //Parse the JSON object
    		        
            String output="", line="";
            while ((line = br.readLine()) != null) {
                output += line + "\n";
            }
            
                
           System.out.println(output);
            
            
            conn.disconnect();

        } catch (Exception e) {
            System.out.println("Exception in NetClientGet:- " + e);
        }
    }
}