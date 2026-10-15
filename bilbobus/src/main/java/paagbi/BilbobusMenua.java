import java.util.List;
import java.util.Scanner;

public class BilbobusMenua {

    public static void abiaraziMenua(ParadasBilbobusResponse datuak) {
        Scanner scanner = new Scanner(System.in);
        int aukera = 0;

        do {
            System.out.println("\n=======================================================");
            System.out.println("             BILBOBUS GELTOKIAK KUDEATZEKO             ");
            System.out.println("=======================================================");
            System.out.println("1. Zenbat autobus geldialdi daude guztira?");
            System.out.println("2. Zenbat eta zein autobus pasatzen dira gune/kale batetik?");
            System.out.println("3. Bilatu geldialdiak autobus-linea zehatz baten arabera");
            System.out.println("4. Bilatu geldialdi bat bere kodearen edo izenaren arabera");
            System.out.println("5. Ikusi estatistikak (Geldialdi gehien eta gutxien dituzten guneak)");
            System.out.println("6. Irten");
            System.out.print("Aukeratu zenbaki bat: ");

            if (scanner.hasNextInt()) {
                aukera = scanner.nextInt();
                scanner.nextLine(); // Bufferra garbitu

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
                        bilatuGeldialdiaTestuarenArabera(datuak, scanner);
                        break;
                    case 5:
                        erakutsiGuneEstatistikak(datuak);
                        break;
                    case 6:
                        System.out.println("Ondo izan!");
                        break;
                    default:
                        System.out.println("Aukera okerra. Saiatu berriro.");
                }
            } else {
                System.out.println("Mesedez, sartu zenbaki baliodun bat.");
                scanner.nextLine();
            }
        } while (aukera != 6);

        scanner.close();
    }

    // --- PROGRAMAREN METODOAK ---

    // 1. aukera: Geldialdi guztiak kalkulatu
    private static void kalkulatuGeldialdiGuztiak(ParadasBilbobusResponse datuak) {
        int totala = 0;
        if (datuak != null && datuak.getParadas_bilbobus() != null && datuak.getParadas_bilbobus().getZona() != null) {
            for (Zona zona : datuak.getParadas_bilbobus().getZona()) {
                if (zona.getParada() != null) {
                    totala += zona.getParada().size();
                }
            }
        }
        System.out.println("\n>>> Bilbon guztira dauden autobus geldialdiak: " + totala);
    }

    // 2. aukera: Autobusak kontsultatu gune/kalearen arabera (Azpimenu dinamikoa)
    private static void erakutsiAukerakEtaKontsultatuAutobusak(ParadasBilbobusResponse datuak, Scanner scanner) {
        if (datuak == null || datuak.getParadas_bilbobus() == null || datuak.getParadas_bilbobus().getZona() == null) {
            System.out.println("Ez dago daturik kargatuta.");
            return;
        }

        List<Zona> guneak = datuak.getParadas_bilbobus().getZona();
        
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

            } else {
                System.out.println("Gunearen zenbaki hori ez dago barrutian.");
            }
        } else {
            System.out.println("Sarrera baliogabea.");
            scanner.nextLine();
        }
    }

    // 3. aukera: Geldialdiak linea zehatz baten arabera bilatu
    private static void bilatuGeldialdiakLinearenArabera(ParadasBilbobusResponse datuak, Scanner scanner) {
        System.out.print("\nSartu bilatu nahi duzun linea (adib. 28, A3): ");
        String bilatutakoLinea = scanner.nextLine().trim().toUpperCase();
        
        int aurkituta = 0;
        System.out.println("\n--- " + bilatutakoLinea + " lineako geldialdiak ---");
        
        for (Zona zona : datuak.getParadas_bilbobus().getZona()) {
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

    // 4. aukera: Geldialdia kodearen edo izenaren arabera bilatu
    private static void bilatuGeldialdiaTestuarenArabera(ParadasBilbobusResponse datuak, Scanner scanner) {
        System.out.print("\nSartu bilatu nahi duzun geldialdiaren kodea edo izena: ");
        String bilaketa = scanner.nextLine().trim().toLowerCase();
        
        boolean aurkitua = false;
        System.out.println("\n--- Bilaketaren emaitzak ---");
        
        for (Zona zona : datuak.getParadas_bilbobus().getZona()) {
            if (zona.getParada() != null) {
                for (Parada p : zona.getParada()) {
                    if (p.getCodigo_parada().toLowerCase().contains(bilaketa) || 
                        p.getParada().toLowerCase().contains(bilaketa)) {
                        System.out.println(" - Kodea: " + p.getCodigo_parada());
                        System.out.println("   Izena: " + p.getParada());
                        System.out.println("   Gunea: " + zona.getNombre());
                        System.out.println("   Lineak: " + p.getCorrespondencias());
                        System.out.println("--------------------------------------------------");
                        aurkitua = true;
                    }
                }
            }
        }
        if (!aurkitua) {
            System.out.println("Ez da irizpide horrekin bat datorren geldialdirik aurkitu.");
        }
    }

    // 5. aukera: Estatistikak guneen arabera
    private static void erakutsiGuneEstatistikak(ParadasBilbobusResponse datuak) {
        System.out.println("\n--- GUNEEN ESTATISTIKAK ---");
        Zona maxZona = null;
        Zona minZona = null;
        int maxGeldialdiak = -1;
        int minGeldialdiak = Integer.MAX_VALUE;
        
        for (Zona zona : datuak.getParadas_bilbobus().getZona()) {
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
            System.out.println("\n🏆 Geldialdi gehien dituen gunea: " + maxZona.getNombre() + " (" + maxGeldialdiak + " geldialdi)");
            System.out.println("📉 Geldialdi gutxien dituen gunea: " + minZona.getNombre() + " (" + minGeldialdiak + " geldialdi)");
        }
    }
}