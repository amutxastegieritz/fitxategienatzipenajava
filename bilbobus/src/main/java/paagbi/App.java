package paagbi;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.*;

public class App {
    public static void main(String[] args) {
        String urlHelbidea = "https://www.bilbao.eus/aytoonline/jsp/opendata/bilbobus/od_recorridos.jsp?idioma=c&formato=csv&tipo=paradas";
        String csvPath = "data/paradas_bilbobus.csv";
        String xmlFitxategia = "data/paradas_bilbobus.xml";

        System.out.println("Bilbobus datuak Internetetik deskargatzen...");
        boolean eguneratuta = deskargatuCSVetaBihurtuXML(urlHelbidea, csvPath, xmlFitxategia);

        if (eguneratuta) {
            System.out.println("Fitxategiak behar bezala gorde eta prestatu dira 'data' karpetan.");
            BilbobusGeltokiak datuak = kargatuXML(xmlFitxategia);

            if (datuak != null) {
                System.out.println("Datuak XML-tik ondo kargatu dira!");
                BilbobusMenua.abiaraziMenua(datuak);
            } else {
                System.out.println("ERROREA: Ezin izan da XML fitxategia irakurri.");
            }
        } else {
            System.out.println("ERROREA: Ezin izan da Interneteko CSV-a deskargatu.");
        }
    }

    private static boolean deskargatuCSVetaBihurtuXML(String urlStr, String csvPath, String xmlPath) {
        try {
            URL url = URI.create(urlStr).toURL();
            HttpURLConnection konexioa = (HttpURLConnection) url.openConnection();
            konexioa.setRequestMethod("GET");

            if (konexioa.getResponseCode() == HttpURLConnection.HTTP_OK) {
                File dataDir = new File("data");
                if (!dataDir.exists()) dataDir.mkdir();

                List<String> lerroak = new ArrayList<>();
                Map<String, List<Map<String, String>>> zonaMapa = new LinkedHashMap<>();

                try (BufferedReader br = new BufferedReader(new InputStreamReader(konexioa.getInputStream(), "ISO-8859-1"))) {
                    String lerroa;
                    boolean lehena = true;

                    while ((lerroa = br.readLine()) != null) {
                        lerroak.add(lerroa);

                        if (lehena) { lehena = false; continue; } // Goiburua saltatu

                        // CSV banaketa. Zutabeen orden egokia Bilboko OpenData-n:
                        // 0: Kodea, 1: Izena, 2: Korespondentziak, 3: Zona/Gunea, ...
                        String[] zutabeak = lerroa.split(",");
                        if (zutabeak.length >= 4) {
                            String kodea = zutabeak[0].trim();
                            String izena = zutabeak[1].trim();
                            String korresp = zutabeak[2].trim();
                            String zona = zutabeak[3].trim(); // Hemen dago benetako gunea!

                            Map<String, String> paradaMap = new HashMap<>();
                            paradaMap.put("codigo_parada", kodea);
                            paradaMap.put("nombre_parada", izena);
                            paradaMap.put("correspondencias", korresp);

                            zonaMapa.putIfAbsent(zona, new ArrayList<>());
                            zonaMapa.get(zona).add(paradaMap);
                        }
                    }
                }

                // Gorde CSV fitxategia
                try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvPath), "ISO-8859-1"))) {
                    for (String l : lerroak) {
                        bw.write(l);
                        bw.newLine();
                    }
                }

                // Sortu XML dokumentua
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.newDocument();

                Element rootElement = doc.createElement("paradas_bilbobus");
                doc.appendChild(rootElement);

                Element zonasElement = doc.createElement("zonas");
                rootElement.appendChild(zonasElement);

                for (Map.Entry<String, List<Map<String, String>>> entry : zonaMapa.entrySet()) {
                    Element zonaElement = doc.createElement("zona");
                    zonaElement.setAttribute("nombre", entry.getKey());
                    zonasElement.appendChild(zonaElement);

                    for (Map<String, String> pData : entry.getValue()) {
                        Element paradaElement = doc.createElement("parada");

                        Element kodeaEl = doc.createElement("codigo_parada");
                        kodeaEl.setTextContent(pData.get("codigo_parada"));
                        paradaElement.appendChild(kodeaEl);

                        Element izenaEl = doc.createElement("nombre_parada");
                        izenaEl.setTextContent(pData.get("nombre_parada"));
                        paradaElement.appendChild(izenaEl);

                        Element korrEl = doc.createElement("correspondencias");
                        korrEl.setTextContent(pData.get("correspondencias"));
                        paradaElement.appendChild(korrEl);

                        zonaElement.appendChild(paradaElement);
                    }
                }

                // Gorde XML fitxategia
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(new File(xmlPath));
                transformer.transform(source, result);

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static BilbobusGeltokiak kargatuXML(String xmlPath) {
        try {
            File fXmlFile = new File(xmlPath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();

            return BilbobusGeltokiak.lortuXMLtik(doc);

        } catch (Exception e) {
            System.out.println("Errorea XML irakurtzean: " + e.getMessage());
        }
        return null;
    }
}