import com.google.gson.Gson;
import java.io.FileReader;
import java.io.IOException;

public class App {
    public static void main(String[] args) {
        // Fitxategiaren bidea (aldatu zure fitxategiaren izenagatik edo kokapenagatik behar izanez gero)
        String fitxategiRuta = "paradas_bilbobus.json";
        
        System.out.println("Bilbobus datuak kargatzen...");
        ParadasBilbobusResponse datuak = kargatuJSON(fitxategiRuta);
        
        if (datuak != null) {
            System.out.println("Datuak ondo kargatu dira!");
            // Menua abiarazi datuekin
            MenuBilbobus.abiaraziMenua(datuak);
        } else {
            System.out.println("Ezin izan dira datuak kargatu. Egiaztatu fitxategiaren bidea.");
        }
    }

    // JSON fitxategia Gson bidez irakurtzeko metodo laguntzailea
    private static ParadasBilbobusResponse kargatuJSON(String ruta) {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(ruta)) {
            return gson.fromJson(reader, ParadasBilbobusResponse.class);
        } catch (IOException e) {
            System.out.println("Errorea fitxategia irakurtzean: " + e.getMessage());
            return null;
        }
    }
}