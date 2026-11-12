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
 * ============================================================================
 * BILBOBUS GELTOKIEN KUDEATZAILEA
 * ============================================================================
 * Bilboko autobusen (Bilbobus) geltokiei buruzko informazioa prozesatu 
 * eta kudeatzeko balio du.
 * 
 * Funtzionamendu nagusia:
 * 1. Datuen deskarga: Bilboko Udalaren Open Data ataritik CSV fitxategia automatikoki 
 *    deskargatzen du.
 * 2. XML bihurketa: JAXB liburutegia erabiliz, datu horiek egituratu eta XML formatuan 
 *    gordetzen ditu datuen irakurketa errazteko.
 * 3. Interakzioa: Datuak memoriara kargatu ostean, erabiltzaileari menu interaktibo bat
 *    eskaintzen dio informazioa kontsultatu eta aztertzeko.
 * 
 * Funtzionalitate nabarmenak:
 * - Bilbon dauden geltoki guztien kontaketa.
 * - Gune edo kale zehatz bateko geltokien kontsulta eta JSON formatuan esportatzea.
 * - Autobus-linea jakin baten (adib. 28, A3) geltokien bilaketa.
 * - Geltoki gehien eta gutxien dituzten guneen estatistikak.
 * ============================================================================
 */

public class App {
    public static void main(String[] args) {
        // OpenData URL-a
        String urlHelbidea = "https://www.bilbao.eus/aytoonline/jsp/opendata/bilbobus/od_recorridos.jsp?idioma=c&formato=csv&tipo=paradas";

        // CSV-a gorde behar duen path-a eta XML fitxategiaren path-a
        String csvPath = "data/paradas_bilbobus.csv";
        String xmlFitxategia = "data/paradas_bilbobus.xml";

        System.out.println("Bilbobus datuak Internetetik deskargatzen...");

        // CSV-a deskargatu eta XML bihurtzeko metodoa deitu eta boolean balioa jaso
        boolean eguneratuta = deskargatuCSVetaBihurtuXML(urlHelbidea, csvPath, xmlFitxategia);

        // Aurretik jasotako boolean-a zuzena bada:
        if (eguneratuta) {
            System.out.println("Fitxategiak behar bezala gorde eta prestatu dira 'data' karpetan.");

            //XML fitxategia kargatu (Unmarshal) eta BilbobusGeltokiak objektu bat sortu
            BilbobusGeltokiak datuak = kargatuXML(xmlFitxategia);

            //Datuak ondo kargatu badira, menu interaktiboa abiarazi
            if (datuak != null) {
                System.out.println("Datuak XML-tik ondo kargatu dira!");
                BilbobusMenua.abiaraziMenua(datuak);
            } else { // Bestela errorea adierazi
                System.out.println("ERROREA: Ezin izan da XML fitxategia irakurri.");
            }
        } else { //Aurretik jasotako boolean-a okerra bada, errorea adierazi
            System.out.println("ERROREA: Ezin izan da Interneteko CSV-a deskargatu.");
        }
    }

    // CSV-a deskargatu eta XML bihurtzeko metodoa, Path-arekin eta URL-arekin
    private static boolean deskargatuCSVetaBihurtuXML(String urlStr, String csvPath, String xmlPath) {
        try {
            // URL-a sortu eta HTTP konexioa ireki
            URL url = URI.create(urlStr).toURL();
            HttpURLConnection konexioa = (HttpURLConnection) url.openConnection();
            konexioa.setRequestMethod("GET"); //GET motatakoa, datuak jeisteko

            // HTTP erantzun kodea egokia bada (200 OK), prozesua jarraitu
            if (konexioa.getResponseCode() == HttpURLConnection.HTTP_OK) {
                File dataDir = new File("data");

                // "data" karpeta existitzen ez bada, sortu
                if (!dataDir.exists())
                    dataDir.mkdir();

                // CSV-aren lerro guztiak gordetzeko zerrenda
                List<String> lerroak = new ArrayList<>();

                // Gune bakoitzeko geltokien datuak gordetzeko mapa: Gune bakoitzeko gakoa izango da, eta balioa geltokien zerrenda izango da Abando ....
                Map<String, List<Map<String, String>>> zonaMapa = new LinkedHashMap<>();

                // Karaktere espezialak irakurtzeko prest egon dezaketen BufferedReader bat sortu, ISO-8859-1 karaktere multzoarekin
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(konexioa.getInputStream(), "ISO-8859-1"))) {
                    String lerroa;
                    boolean lehena = true;

                    // Lerro bakoitza irakurri eta lerroa aldagaian idatzi (br.readLine() metodoarekin, hurrengo lerroa irakurtzeko null ez den bitartean)
                    while ((lerroa = br.readLine()) != null) {
                        lerroak.add(lerroa);

                        // Lehenengo lerroa ez irakurtzeko, CSV-aren zutabeen izenak baitira
                        if (lehena) {
                            lehena = false;
                            continue;
                        }

                        // XML formatuan gordetzeko datuak separatu zutabeka array batean, eta zutabeak banatu ";" karakterearekin
                        String[] zutabeak = lerroa.split(";", -1);

                        // Zutabeak behar bezala banatu badira (gutxienez 5 zutabe), datuak prozesatu
                        if (zutabeak.length >= 5) {
                            // .trim() metodoarekin espazioak kentzen ditu
                            String kodea = zutabeak[0].trim();
                            String izena = zutabeak[1].trim();
                            String zona = zutabeak[2].trim();
                            String korresp = zutabeak[3].trim();

                            // POINT barruan dagoena longitud eta latitud banandu
                            String longitud = "";
                            String latitud = "";
                            String geoPoint = zutabeak[4].trim();

                            if (geoPoint.startsWith("POINT")) {
                                // "POINT (longitud latitud)" formatua kendu eta longitude eta latitude banandu
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

                            // Zona horren "karpeta" sortuta badago bertan sartu
                            zonaMapa.putIfAbsent(zona, new ArrayList<>());

                            // Ez badago sortuta "zona" sortu eta bertan sartu
                            zonaMapa.get(zona).add(paradaMap);
                        }
                    }
                }

                // CSV fitxategia gorde ISO-8859-1 karaktere multzoarekin
                try (BufferedWriter bw = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(csvPath), "ISO-8859-1"))) {
                    for (String l : lerroak) {
                        bw.write(l);
                        bw.newLine();
                    }
                }

                // BilbobusGeltokiak objektua sortu eta zonaMapa erabiliz XML fitxategia sortzeko
                BilbobusGeltokiak paradasBilbobus = new BilbobusGeltokiak();
                List<Zona> zonasList = new ArrayList<>();

                // Aurretik sortutako zonaMapa bakoitzetik (gakoa izena eta balioa geldialdia)
                for (Map.Entry<String, List<Map<String, String>>> entry : zonaMapa.entrySet()) {
                    // Zona objektua sortu eta izena ezarri
                    Zona zonaObj = new Zona();
                    zonaObj.setNombre(entry.getKey()); // izena jasotako gakoa da

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
                JAXBContext jaxbContext = JAXBContext.newInstance(BilbobusGeltokiak.class);
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
            JAXBContext jaxbContext = JAXBContext.newInstance(BilbobusGeltokiak.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return (BilbobusGeltokiak) jaxbUnmarshaller.unmarshal(file);

        } catch (JAXBException e) {
            System.out.println("Errorea XML irakurtzean (Unmarshal): " + e.getMessage());
        }
        return null;
    }
}