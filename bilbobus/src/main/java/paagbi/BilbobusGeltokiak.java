package paagbi;

import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Bilbobuseko geltokien datuak gordetzen dituen klasea da, XML fitxategiaren erroa.
 * JAXB-ren anotazioei esker, Javak datu hauek XML bihurtzen ditu (Marshalling)
 * eta XMLtik Javara irakurtzen ditu (Unmarshalling).
 *
 * Erabilitako anotazioak:
 * - @XmlRootElement: erroko etiketa definitzen du (<paradas_bilbobus>).
 * - @XmlAccessorType: JAXB-ri eremuetara zuzenean sartzeko baimena ematen dio.
 * - @XmlElement: zerrendako elementuen etiketa definitzen du (<zona>).
 */

@XmlRootElement(name = "paradas_bilbobus")
@XmlAccessorType(XmlAccessType.FIELD) // JAXB-ri eremuetara (aldagaietara) zuzenean sartzeko baimena ematen dio
public class BilbobusGeltokiak {
    
    // <paradas_bilbobus> barruan egongo diren <zona> elementuen zerrenda
    @XmlElement(name = "zona")
    private List<Zona> zona = new ArrayList<>();
    
    public List<Zona> getZona() { return zona; }
    public void setZona(List<Zona> zona) { this.zona = zona; }
}