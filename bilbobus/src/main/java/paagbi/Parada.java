package paagbi;

import jakarta.xml.bind.annotation.*;

// XML-ko <parada> elementua adierazten du.
// @XmlType bidez, elementuen ordena zehatza behartzen dugu zuk eskatutako formatuarekin bat etortzeko.
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "codigo_parada", "parada", "correspondencias", "longitud", "latitud" })
public class Parada {
    
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