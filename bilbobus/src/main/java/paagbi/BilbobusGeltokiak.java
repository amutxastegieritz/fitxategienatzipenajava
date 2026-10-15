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

// Klase nagusia, jatorrizko menuak arazorik gabe funtzionatzen jarraitzeko (Klase enboltorioa)
public class BilbobusGeltokiak {
    private ParadasBilbobus paradas_bilbobus;

    public ParadasBilbobus getParadas_bilbobus() { return paradas_bilbobus; }
    public void setParadas_bilbobus(ParadasBilbobus paradas_bilbobus) { this.paradas_bilbobus = paradas_bilbobus; }
}

// XML-aren erroko etiketa nagusia adierazten du: <paradas_bilbobus>
@XmlRootElement(name = "paradas_bilbobus")
@XmlAccessorType(XmlAccessType.FIELD) // JAXB-ri eremuetara (aldagaietara) zuzenean sartzeko baimena ematen dio
class ParadasBilbobus {
    
    // <paradas_bilbobus> barruan egongo diren <zona> elementuen zerrenda
    @XmlElement(name = "zona")
    private List<Zona> zona = new ArrayList<>();
    
    public List<Zona> getZona() { return zona; }
    public void setZona(List<Zona> zona) { this.zona = zona; }
}

// XML-ko <zona> elementua adierazten du
@XmlAccessorType(XmlAccessType.FIELD)
class Zona {
    
    // <zona nombre="..."> atributua definitzen du
    @XmlAttribute(name = "nombre") 
    private String nombre;
    
    // Zona bakoitzaren barruan egongo diren <parada> elementuen zerrenda
    @XmlElement(name = "parada")
    private List<Parada> parada = new ArrayList<>();
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public List<Parada> getParada() { return parada; }
    public void setParada(List<Parada> parada) { this.parada = parada; }
}

// XML-ko <parada> elementua adierazten du.
// @XmlType bidez, elementuen ordena zehatza behartzen dugu zuk eskatutako formatuarekin bat etortzeko.
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "codigo_parada", "parada", "correspondencias", "longitud", "latitud" })
class Parada {
    
    // Parada bakoitzaren datuak (hauek guztiak XML etiketa bihurtuko dira barruan)
    private String codigo_parada;
    private String parada; 
    private String correspondencias;
    private String longitud;
    private String latitud;
    
    // Getter eta Setter metodoak datuak irakurri eta idazteko
    public String getCodigo_parada() { return codigo_parada; }
    public void setCodigo_parada(String codigo_parada) { this.codigo_parada = codigo_parada; }
    
    public String getParada() { return parada; }
    public void setParada(String parada) { this.parada = parada; }
    
    public String getCorrespondencias() { return correspondencias; }
    public void setCorrespondencias(String correspondencias) { this.correspondencias = correspondencias; }

    public String getLongitud() { return longitud; }
    public void setLongitud(String longitud) { this.longitud = longitud; }

    public String getLatitud() { return latitud; }
    public void setLatitud(String latitud) { this.latitud = latitud; }
}