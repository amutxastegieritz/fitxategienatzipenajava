package paagbi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class App {

    public static void main(String[] args) {
        String fitxategiCsv = "paradas_bilbobus.csv";
        String fitxategiXml = "paradas_bilbobus.xml";

        try (
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fitxategiCsv), "ISO-8859-1"));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fitxategiXml), "UTF-8"))
        ) {
            String lerroa = br.readLine();
            if (lerroa == null) {
                System.out.println("CSV fitxategia hutsik dago.");
                return;
            }

            // CSV fitxategiaren goiburuak irakurri
            String[] goiburuak = lerroa.split(";");
            
            // Goiburuak garbitu XML etiketa baliagarriak izateko
            int indexZona = -1;
            int indexGeometria = -1;
            
            for (int i = 0; i < goiburuak.length; i++) {
                goiburuak[i] = goiburuak[i].trim().toLowerCase()
                        .replace(" ", "_")
                        .replace("á", "a")
                        .replace("é", "e")
                        .replace("í", "i")
                        .replace("ó", "o")
                        .replace("ú", "u");
                        
                // Guardamos la posición de las columnas clave
                if (goiburuak[i].equals("zona")) {
                    indexZona = i;
                } else if (goiburuak[i].equals("geometria")) {
                    indexGeometria = i;
                }
            }

            // Mapa para agrupar las paradas por su zona
            // Usamos LinkedHashMap para mantener el orden de inserción original
            Map<String, List<String[]>> paradasPorZona = new LinkedHashMap<>();

            // Leer archivo CSV y agrupar los datos en memoria
            while ((lerroa = br.readLine()) != null) {
                String[] balioak = lerroa.split(";");
                
                String zona = "Zona Desconocida";
                if (indexZona != -1 && indexZona < balioak.length) {
                    zona = balioak[indexZona].trim();
                }
                
                // Si la zona no existe en el mapa, la creamos
                paradasPorZona.putIfAbsent(zona, new ArrayList<>());
                // Añadimos los datos de la parada a su zona correspondiente
                paradasPorZona.get(zona).add(balioak);
            }

            // Escribir el documento XML
            bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            bw.write("<paradas_bilbobus>\n");

            // Recorrer el mapa (zona a zona)
            for (Map.Entry<String, List<String[]>> entry : paradasPorZona.entrySet()) {
                String nombreZona = entry.getKey();
                
                // Abrimos la etiqueta de la rama Zona
                bw.write("    <zona nombre=\"" + ihesEginXml(nombreZona) + "\">\n");

                // Recorremos cada parada de esa zona
                for (String[] balioak : entry.getValue()) {
                    bw.write("        <parada>\n");
                    
                    for (int i = 0; i < goiburuak.length; i++) {
                        // Saltamos la etiqueta <zona> interna porque ya está en el padre
                        if (i == indexZona) continue;
                        
                        String balioa = (i < balioak.length) ? balioak[i].trim() : "";
                        
                        // Si es la columna "geometria", separamos las coordenadas
                        if (i == indexGeometria && balioa.startsWith("POINT (") && balioa.endsWith(")")) {
                            // Extraemos lo que hay dentro de "POINT ( ... )"
                            String coordenadas = balioa.substring(7, balioa.length() - 1);
                            String[] partes = coordenadas.split(" "); // separamos por el espacio
                            
                            if (partes.length >= 2) {
                                String longitud = partes[0];
                                String latitud = partes[1];
                                
                                bw.write("            <longitud>" + ihesEginXml(longitud) + "</longitud>\n");
                                bw.write("            <latitud>" + ihesEginXml(latitud) + "</latitud>\n");
                            }
                        } else {
                            // Imprimir los datos normales (codigo_parada, parada, correspondencias, etc)
                            bw.write("            <" + goiburuak[i] + ">" + ihesEginXml(balioa) + "</" + goiburuak[i] + ">\n");
                        }
                    }
                    bw.write("        </parada>\n");
                }
                // Cerramos la etiqueta de la zona
                bw.write("    </zona>\n");
            }

            bw.write("</paradas_bilbobus>\n");
            System.out.println("XML fitxategia arrakastaz sortu da formatu berriarekin: " + fitxategiXml + "!");

        } catch (Exception e) {
            System.err.println("Errorea bihurtze-prozesuan: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Testuak XML-ko karaktere bereziak baditu akatsak saihesteko laguntzaile-metodoa
    private static String ihesEginXml(String testua) {
        return testua.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("'", "&apos;")
                    .replace("\"", "&quot;");
    }
}