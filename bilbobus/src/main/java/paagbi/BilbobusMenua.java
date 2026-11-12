package paagbi;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonWriter;
import jakarta.json.JsonWriterFactory;
import jakarta.json.stream.JsonGenerator;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/*
 * Zer egiten du?
 *   Klase honek erabiltzaile-menua kudeatzen du, erabiltzaileak aukerak egin
 *   ditzan eta datuak kontsultatu ditzan. Datuak memorian daude (XMLtik kargatuak
 *   izan dira App klasean) eta hemen irakurri, bilatu eta aztertzen dira.
 *
 * Menuaren aukerak honako hauek dira:
 *   1. Zenbat autobus geldialdi daude guztira?
 *   2. Zenbat eta zein autobus pasatzen dira gune/kale batetik? (Gorde aukera)
 *   3. Bilatu geldialdiak autobus-linea zehatz baten arabera
 *   4. Ikusi estatistikak (Geldialdi gehien eta gutxien dituzten guneak)
 *   5. Irten
 *
 * Metodoak:
 *   - abiaraziMenua                              : menuaren begizta nagusia (publikoa).
 *   - kalkulatuGeldialdiGuztiak                  : 1. aukera, geldialdi kopuru osoa.
 *   - erakutsiAukerakEtaKontsultatuAutobusak     : 2. aukera, gune baten geldialdiak eta lineak.
 *   - gordeZonaJSON                              : 2. aukerako JSON esportazioa (Jakarta JSON).
 *   - testua                                     : null balioak kate huts bihurtzen ditu.
 *   - bilatuGeldialdiakLinearenArabera           : 3. aukera, linea baten geldialdiak.
 *   - erakutsiGuneEstatistikak                   : 4. aukera, gune handiena eta txikiena.
 *
 * Sarrera baliogabeak (zenbakirik ez dena, barrutitik kanpokoa...) kudeatzen dira
 * programa ez gelditzeko.
 */

public class BilbobusMenua {

    public static void abiaraziMenua(BilbobusGeltokiak datuak) {
        Scanner scanner = new Scanner(System.in);
        int aukera = 0;

        do {
            System.out.println("\n=======================================================");
            System.out.println("             BILBOBUS GELTOKIAK KUDEATZEKO             ");
            System.out.println("=======================================================");
            System.out.println("1. Zenbat autobus geldialdi daude guztira?");
            System.out.println("2. Zenbat eta zein autobus pasatzen dira kale batetik?");
            System.out.println("3. Bilatu geldialdiak autobus-linea zehatz baten arabera");
            System.out.println("4. Ikusi estatistikak (Geldialdi gehien eta gutxien dituzten guneak)");
            System.out.println("5. Irten");
            System.out.print("Aukeratu zenbaki bat: ");

            if (scanner.hasNextInt()) {
                aukera = scanner.nextInt();
                scanner.nextLine();

                switch (aukera) {
                    case 1:
                        kalkulatuGeldialdiGuztiak(datuak);
                        break;
                    case 2:
                        erakutsiAukerakEtaKontsultatuAutobusak(datuak, scanner);
                        break;
                    case 3:
                        bilatuGeldialdiakLinearenArabera(datuak, scanner);
                        break;
                    case 4:
                        erakutsiGuneEstatistikak(datuak);
                        break;
                    case 5:
                        System.out.println("Ondo izan!");
                        break;
                    default:
                        System.out.println("Aukera okerra. Saiatu berriro.");
                }
            } else {
                System.out.println("Mesedez, sartu zenbaki baliodun bat.");
                scanner.nextLine();
            }
        } while (aukera != 5);

        scanner.close();
    }

    private static void kalkulatuGeldialdiGuztiak(BilbobusGeltokiak datuak) {
        int totala = 0;
        if (datuak != null && datuak.getZona() != null) {
            for (Zona zona : datuak.getZona()) {
                if (zona.getParada() != null) {
                    totala += zona.getParada().size();
                }
            }
        }
        System.out.println("\n>>> Bilbon guztira dauden autobus geldialdiak: " + totala);
    }

    private static void erakutsiAukerakEtaKontsultatuAutobusak(BilbobusGeltokiak datuak, Scanner scanner) {
        if (datuak == null || datuak.getZona() == null) {
            System.out.println("Ez dago daturik kargatuta.");
            return;
        }

        List<Zona> guneak = datuak.getZona();
        
        System.out.println("\n--- ESKURAGO DAUDEN GUNEAK / KALEAK ---");
        for (int i = 0; i < guneak.size(); i++) {
            System.out.println((i + 1) + ". " + guneak.get(i).getNombre());
        }
        System.out.print("Hautatu gune zenbaki bat: ");

        if (scanner.hasNextInt()) {
            int hautapena = scanner.nextInt();
            scanner.nextLine();

            if (hautapena > 0 && hautapena <= guneak.size()) {
                Zona aukeratutakoZona = guneak.get(hautapena - 1);
                System.out.println("\nHautatutako gunea: " + aukeratutakoZona.getNombre());
                System.out.println("Geldialdiak eta dagozkien lineak:");
                
                int gunekoGeldialdiKopurua = 0;
                if (aukeratutakoZona.getParada() != null) {
                    gunekoGeldialdiKopurua = aukeratutakoZona.getParada().size();
                    for (Parada p : aukeratutakoZona.getParada()) {
                        System.out.println(" * Geldialdia [" + p.getCodigo_parada() + "] " + p.getParada() + 
                                           " --> Lineak: " + p.getCorrespondencias());
                    }
                }
                System.out.println("\nGeldialdi kopurua gune honetan: " + gunekoGeldialdiKopurua);

                // ERABILTZAILEARI GALDETU EA JSON FORMATUAN GORDI NAHI DUEN
                System.out.print("\nNahi duzu gune honen emaitza .json formatuan gordetzea? (bai/ez): ");
                String erantzuna = scanner.nextLine().trim().toLowerCase();

                if (erantzuna.equals("bai") || erantzuna.equals("b")) {
                    gordeZonaJSON(aukeratutakoZona);
                }

            } else {
                System.out.println("Gunearen zenbaki hori ez dago barrutian.");
            }
        } else {
            System.out.println("Sarrera baliogabea.");
            scanner.nextLine();
        }
    }

    private static void gordeZonaJSON(Zona zona) {
        try {
            // Gunearen geldialdi guztiak JSON zerrenda batean
            JsonArrayBuilder paradakJson = Json.createArrayBuilder();
            for (Parada p : zona.getParada()) {
                paradakJson.add(Json.createObjectBuilder()
                        .add("codigo_parada", testua(p.getCodigo_parada()))
                        .add("parada", testua(p.getParada()))
                        .add("correspondencias", testua(p.getCorrespondencias()))
                        .add("longitud", testua(p.getLongitud()))
                        .add("latitud", testua(p.getLatitud())));
            }

            // Zona objektua, bere geldialdien zerrenda barruan duela
            JsonObject zonaJson = Json.createObjectBuilder()
                    .add("nombre", testua(zona.getNombre()))
                    .add("parada", paradakJson)
                    .build();

            // Garbitu karaktere bereziak eta espazioak fitxategi izenetik egokia izateko
            String izenGarbia = zona.getNombre().replaceAll("[\\\\/:*?\"<>|]", "_").replace(" ", "_");

            // Izena dinamikoa izango da
            String fitxategiIzena = "data/" + izenGarbia + ".json";

            // JSON-a formatu irakurgarriarekin idatzi
            JsonWriterFactory fabrika = Json.createWriterFactory(Map.of(JsonGenerator.PRETTY_PRINTING, true));
            try (JsonWriter writer = fabrika.createWriter(new FileWriter(fitxategiIzena))) {
                writer.writeObject(zonaJson);
                System.out.println("Datuak arrakastaz gordeta dira hemen: " + fitxategiIzena);
            }
        } catch (Exception e) {
            System.out.println("Errorea JSON-a gordetzean: " + e.getMessage());
        }
    }

    // null ez sartzeko JSON-ean (testua hutsik badago)
    private static String testua(String balioa) {
        return balioa == null ? "" : balioa;
    }

    private static void bilatuGeldialdiakLinearenArabera(BilbobusGeltokiak datuak, Scanner scanner) {
        System.out.print("\nSartu bilatu nahi duzun linea (adib. 28, A3): ");
        String bilatutakoLinea = scanner.nextLine().trim().toUpperCase();
        
        int aurkituta = 0;
        System.out.println("\n--- " + bilatutakoLinea + " lineako geldialdiak ---");
        
        for (Zona zona : datuak.getZona()) {
            if (zona.getParada() != null) {
                for (Parada p : zona.getParada()) {
                    if (p.getCorrespondencias() != null && p.getCorrespondencias().toUpperCase().contains(bilatutakoLinea)) {
                        System.out.println(" - [" + p.getCodigo_parada() + "] " + p.getParada() + " (Gunea: " + zona.getNombre() + ")");
                        aurkituta++;
                    }
                }
            }
        }
        System.out.println("\nAurkitutako geldialdi kopurua " + bilatutakoLinea + " linearentzat: " + aurkituta);
    }

    private static void erakutsiGuneEstatistikak(BilbobusGeltokiak datuak) {
        System.out.println("\n--- GUNEEN ESTATISTIKAK ---");
        Zona maxZona = null;
        Zona minZona = null;
        int maxGeldialdiak = -1;
        int minGeldialdiak = Integer.MAX_VALUE;
        
        for (Zona zona : datuak.getZona()) {
            int geldialdiKop = (zona.getParada() != null) ? zona.getParada().size() : 0;
            System.out.println(" * " + zona.getNombre() + ": " + geldialdiKop + " geldialdi");
            
            if (geldialdiKop > maxGeldialdiak) {
                maxGeldialdiak = geldialdiKop;
                maxZona = zona;
            }
            if (geldialdiKop < minGeldialdiak) {
                minGeldialdiak = geldialdiKop;
                minZona = zona;
            }
        }
        
        if (maxZona != null && minZona != null) {
            System.out.println("\n Geldialdi gehien dituen gunea: " + maxZona.getNombre() + " (" + maxGeldialdiak + " geldialdi)");
            System.out.println("Geldialdi gutxien dituen gunea: " + minZona.getNombre() + " (" + minGeldialdiak + " geldialdi)");
        }
    }
}