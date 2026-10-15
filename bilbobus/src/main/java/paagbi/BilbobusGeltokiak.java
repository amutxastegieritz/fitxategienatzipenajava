package paagbi;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.util.ArrayList;
import java.util.List;

public class BilbobusGeltokiak {
    private ParadasBilbobus paradas_bilbobus;

    public static BilbobusGeltokiak lortuXMLtik(Document doc) {
        BilbobusGeltokiak em = new BilbobusGeltokiak();
        ParadasBilbobus pb = new ParadasBilbobus();
        List<Zona> zonasList = new ArrayList<>();

        NodeList listaZonas = doc.getElementsByTagName("zona");
        for (int i = 0; i < listaZonas.getLength(); i++) {
            Element zonaElem = (Element) listaZonas.item(i);
            Zona zona = new Zona();
            zona.setNombre(zonaElem.getAttribute("nombre"));

            List<Parada> paradasList = new ArrayList<>();
            NodeList listaParadas = zonaElem.getElementsByTagName("parada");
            for (int j = 0; j < listaParadas.getLength(); j++) {
                Element paradaElem = (Element) listaParadas.item(j);
                Parada parada = new Parada();
                
                if (paradaElem.getElementsByTagName("codigo_parada").getLength() > 0)
                    parada.setCodigo_parada(paradaElem.getElementsByTagName("codigo_parada").item(0).getTextContent());
                
                if (paradaElem.getElementsByTagName("nombre_parada").getLength() > 0)
                    parada.setParada(paradaElem.getElementsByTagName("nombre_parada").item(0).getTextContent());
                
                if (paradaElem.getElementsByTagName("correspondencias").getLength() > 0)
                    parada.setCorrespondencias(paradaElem.getElementsByTagName("correspondencias").item(0).getTextContent());

                paradasList.add(parada);
            }
            zona.setParada(paradasList);
            zonasList.add(zona);
        }
        pb.setZona(zonasList);
        em.setParadas_bilbobus(pb);
        return em;
    }

    public ParadasBilbobus getParadas_bilbobus() { return paradas_bilbobus; }
    public void setParadas_bilbobus(ParadasBilbobus paradas_bilbobus) { this.paradas_bilbobus = paradas_bilbobus; }
}

class ParadasBilbobus {
    private List<Zona> zona;
    public List<Zona> getZona() { return zona; }
    public void setZona(List<Zona> zona) { this.zona = zona; }
}

class Zona {
    private String nombre;
    private List<Parada> parada;
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public List<Parada> getParada() { return parada; }
    public void setParada(List<Parada> parada) { this.parada = parada; }
}

class Parada {
    private String codigo_parada;
    private String parada;
    private String correspondencias;
    
    public String getCodigo_parada() { return codigo_parada; }
    public void setCodigo_parada(String codigo_parada) { this.codigo_parada = codigo_parada; }
    public String getParada() { return parada; }
    public void setParada(String parada) { this.parada = parada; }
    public String getCorrespondencias() { return correspondencias; }
    public void setCorrespondencias(String correspondencias) { this.correspondencias = correspondencias; }
}