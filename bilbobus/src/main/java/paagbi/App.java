package paagbi;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.*;

/**
    (Langilea da, hau da)
    Aplikazioaren klase nagusia da hau. Bilboko OpenData ataritik Bilbobuseko geltokien CSV fitxategia 
    deskargatzeaz, prozesatzeaz eta JAXB erabiliz XML formatura bihurtzeaz (Marshall) arduratzen da. 
    Ondoren, sortutako XML fitxategitik datuak kargatzen ditu (Unmarshall) eta 
    erabiltzaile-menua abiarazten du informazioarekin elkarreragiteko.
 */

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

    // CSV-a deskargatu eta XML bihurtzeko metodoa, Path-arekin eta URL-arekin
    private static boolean deskargatuCSVetaBihurtuXML(String urlStr, String csvPath, String xmlPath) {
        try {
            URL url = URI.create(urlStr).toURL();
            HttpURLConnection konexioa = (HttpURLConnection) url.openConnection();
            konexioa.setRequestMethod("GET");

            if (konexioa.getResponseCode() == HttpURLConnection.HTTP_OK) {
                File dataDir = new File("data");
                if (!dataDir.exists())
                    dataDir.mkdir();

                List<String> lerroak = new ArrayList<>();
                Map<String, List<Map<String, String>>> zonaMapa = new LinkedHashMap<>();

                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(konexioa.getInputStream(), "ISO-8859-1"))) {
                    String lerroa;
                    boolean lehena = true;

                    while ((lerroa = br.readLine()) != null) {
                        lerroak.add(lerroa);
                        if (lehena) {
                            lehena = false;
                            continue;
                        }

                        // XML formatuan gordetzeko datuak separatu
                        String[] zutabeak = lerroa.split(";", -1);

                        if (zutabeak.length >= 5) {
                            String kodea = zutabeak[0].trim();
                            String izena = zutabeak[1].trim();
                            String zona = zutabeak[2].trim();
                            String korresp = zutabeak[3].trim();

                            // POINT barruan dagoena longitud eta latitud banandu
                            String longitud = "";
                            String latitud = "";
                            String geoPoint = zutabeak[4].trim();

                            if (geoPoint.startsWith("POINT")) {
                                String coords = geoPoint.replace("POINT (", "").replace(")", "");
                                String[] lonlat = coords.split(" ");
                                if (lonlat.length == 2) {
                                    longitud = lonlat[0].trim();
                                    latitud = lonlat[1].trim();
                                }
                            }

                            Map<String, String> paradaMap = new HashMap<>();
                            paradaMap.put("codigo_parada", kodea);
                            paradaMap.put("parada", izena);
                            paradaMap.put("correspondencias", korresp);
                            paradaMap.put("longitud", longitud);
                            paradaMap.put("latitud", latitud);

                            zonaMapa.putIfAbsent(zona, new ArrayList<>());
                            zonaMapa.get(zona).add(paradaMap);
                        }
                    }
                }

                try (BufferedWriter bw = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(csvPath), "ISO-8859-1"))) {
                    for (String l : lerroak) {
                        bw.write(l);
                        bw.newLine();
                    }
                }

                ParadasBilbobus paradasBilbobus = new ParadasBilbobus();
                List<Zona> zonasList = new ArrayList<>();

                for (Map.Entry<String, List<Map<String, String>>> entry : zonaMapa.entrySet()) {
                    Zona zonaObj = new Zona();
                    zonaObj.setNombre(entry.getKey());

                    List<Parada> paradasList = new ArrayList<>();
                    for (Map<String, String> pData : entry.getValue()) {
                        Parada p = new Parada();
                        p.setCodigo_parada(pData.get("codigo_parada"));
                        p.setParada(pData.get("parada"));
                        p.setCorrespondencias(pData.get("correspondencias"));
                        p.setLongitud(pData.get("longitud"));
                        p.setLatitud(pData.get("latitud"));
                        paradasList.add(p);
                    }
                    zonaObj.setParada(paradasList);
                    zonasList.add(zonaObj);
                }
                paradasBilbobus.setZona(zonasList);

                // MARSHALLER-a erabiliz XML-a sortu
                JAXBContext jaxbContext = JAXBContext.newInstance(ParadasBilbobus.class);
                Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
                jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

                jaxbMarshaller.marshal(paradasBilbobus, new File(xmlPath));

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // XML fitxategia kargatzeko metodoa
    private static BilbobusGeltokiak kargatuXML(String xmlPath) {
        try {
            File file = new File(xmlPath);
            JAXBContext jaxbContext = JAXBContext.newInstance(ParadasBilbobus.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            ParadasBilbobus paradasBilbobus = (ParadasBilbobus) jaxbUnmarshaller.unmarshal(file);

            BilbobusGeltokiak geltokiak = new BilbobusGeltokiak();
            geltokiak.setParadas_bilbobus(paradasBilbobus);
            return geltokiak;

        } catch (JAXBException e) {
            System.out.println("Errorea XML irakurtzean (Unmarshal): " + e.getMessage());
        }
        return null;
    }
}