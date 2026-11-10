package paagbi;

import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
    Klase honek Bilbobuseko geltokien datuak gordetzen ditu (XML-aren egitura sortzeko da).
    Anotazio hauei esker (@Xml...), Javak badaki datu hauek nola bihurtu 
    XML fitxategi automatikoki, guk eskatutako ordena eta formatu zehatzarekin.
    "@XmlAccessorType" baimena emateko
    "@XmlRootElement" erroko etiketa, "@XmlAttribute" atributuarena, "@XmlElement" elementuarena, eta "@XmlType" elementuen ordena zehazteko.
 */

// XML-aren erroko etiketa nagusia adierazten du: <paradas_bilbobus>
@XmlRootElement(name = "paradas_bilbobus")
@XmlAccessorType(XmlAccessType.FIELD) // JAXB-ri eremuetara (aldagaietara) zuzenean sartzeko baimena ematen dio
public class BilbobusGeltokiak {
    
    // <paradas_bilbobus> barruan egongo diren <zona> elementuen zerrenda
    @XmlElement(name = "zona")
    private List<Zona> zona = new ArrayList<>();
    
    public List<Zona> getZona() { return zona; }
    public void setZona(List<Zona> zona) { this.zona = zona; }
}