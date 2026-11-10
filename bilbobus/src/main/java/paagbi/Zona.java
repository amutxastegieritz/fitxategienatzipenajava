package paagbi;

import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

// XML-ko <zona> elementua adierazten du
@XmlAccessorType(XmlAccessType.FIELD)
public class Zona {
    
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