package paagbi;

import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Zer egiten du?
 *   Bilbobuseko geltokien datuak gordetzen dituen klasea da, XML fitxategiaren
 *   erroa. JAXB-ren anotazioei esker, Javak datu hauek XML bihurtzen ditu
 *   (Marshalling) eta XMLtik Javara irakurtzen ditu (Unmarshalling).
 *
 * Egitura hierarkikoa (3 mailako anidazioa):
 *   <paradas_bilbobus>            <-- klase hau (erroa)
 *       <zona nombre="...">       <-- Zona klasea (1. maila)
 *           <parada>...</parada>  <-- Parada klasea (2. maila)
 *
 * Erabilitako anotazioak:
 *   - @XmlRootElement: erroko etiketa definitzen du (<paradas_bilbobus>).
 *   - @XmlAccessorType: JAXB-ri eremuetara zuzenean sartzeko baimena ematen dio.
 *   - @XmlElement: zerrendako elementuen etiketa definitzen du (<zona>).
 */

// Klase hau XML dokumentuaren erro-elementua da, eta "paradas_bilbobus" izena izango du
@XmlRootElement(name = "paradas_bilbobus")
// JAXB-ri eremuetara (aldagaietara) zuzenean sartzeko baimena ematen dio (getter/setter-ak ez dira beharrezkoak XMLrako)
@XmlAccessorType(XmlAccessType.FIELD)
// Klase publikoa: App eta BilbobusMenua klaseek erabiltzen dute
public class BilbobusGeltokiak {

    // <paradas_bilbobus> barruan egongo diren <zona> elementuen zerrenda
    // Zerrendako elementu bakoitza <zona> etiketa bezala idatziko da XMLan
    @XmlElement(name = "zona")
    // Gune guztien zerrenda (hasieran hutsik); pribatua da, getter/setter-en bidez erabiltzen da
    private List<Zona> zona = new ArrayList<>();

    // Getter-a: guneen zerrenda itzultzen du
    public List<Zona> getZona() { return zona; }
    // Setter-a: guneen zerrenda berri bat esleitzen du
    public void setZona(List<Zona> zona) { this.zona = zona; }
} // BilbobusGeltokiak klasearen amaiera