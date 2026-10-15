# Bilbobus Geltokien Kudeatzailea

Proiektu hau Java erabiliz garatutako aplikazio bat da, Bilboko autobus geltokien (Bilbobus) OpenData datuak deskargatu, egituratu eta aztertzeko aukera ematen duena. 

Aplikazioak datuak Internetetik zuzenean irakurtzen ditu, egitura hierarkiko batean antolatzen ditu XML fitxategian eta erabiltzaileari kontsulta eta esportazio ezberdinak egiteko menu interaktibo bat du.

## 1. Datu-iturria
Aplikazioak Bilboko Udalaren OpenData ataritik hartzen ditu datuak CSV formatuan, zuzenean sareko konexio bat erabiliz.
* **Jatorrizko URLa:** [Bilbobus - Ibilbideak eta Geltokiak (CSV)](https://www.bilbao.eus/aytoonline/jsp/opendata/bilbobus/od_recorridos.jsp?idioma=c&formato=csv&tipo=paradas)

## 2. Proiektua

Aplikazio honek eskatutako irizpide eta baldintza guztiak betetzen ditu:

### 2.1. Hiru formatuetako fitxategien erabilera
Programak datuak hiru formatu ezberdinetan maneiatzen ditu:
1. **CSV:** Internetetik formatu honetan deskargatu eta prozesatzen dira hasierako datuak (`data/paradas_bilbobus.csv`).
2. **XML:** CSVko datuak memoria egitura batean kargatu ondoren, JAXB erabiliz XML formatuan gordetzen dira. Fitxategi hau aplikazioaren datu-base nagusia bezala funtzionatzen du (`data/paradas_bilbobus.xml`).
3. **JSON:** Erabiltzaileak menuaren bidez gune (zona) zehatz bat kontsultatzen duenean, emaitza hori fitxategi formatu honetan esportatzeko aukera ematen da (**Gson** liburutegia erabiliz).

### 2.2. Anidazioa eta Atributuak (XML eta JSON)
* **XML fitxategia:** Hiru mailako anidazioa dauka:
  * Erroa: `<paradas_bilbobus>`
  * 1. Maila: `<zona>`
  * 2. Maila: `<parada>`
* **Atributuen erabilera XMLan:** `<zona>` etiketak `nombre` izeneko atributua erabiltzen du bere izena definitzeko (Adibidez: `<zona nombre="1 DEUSTU - 101 SAN IGNACIO">`).
* **JSON fitxategia:** Objektuen anidazioa mantentzen du (Zona objektuak Parada objektuen zerrenda bat dauka bere barruan).

### 2.3. Erabilitako Teknologiak
* **JAXB (Java Architecture for XML Binding):** XML fitxategiak kudeatzeko erabili da. Klaseetan anotazioak erabili dira (`@XmlRootElement`, `@XmlElement`, `@XmlAttribute`, `@XmlType`) eta **Marshalling** (Javatik XMLra) zein **Unmarshalling** (XMLtik Javara) prozesuak inplementatu dira.
* **Gson:** JSON fitxategiak sortzeko eta esportatzeko erabili da.

## 3. Proiektuaren egitura

* **Paketeen antolaketa:** Kode guztia `paagbi` izeneko paketean egituratu da, klase bakoitzak erantzukizun bakarra (Single Responsibility) izan dezan:
  * `App.java`: Aplikazioaren abiarazlea eta exekuzioaren fluxu nagusia.
  * `BilbobusMenua.java` (eta bere azpiklaseak): Menua printeatzeko funtzioa eta aukera bakoitzaren funtzioak.
  * `BilbobusGeltokiak.java` (eta bere azpiklaseak): JAXB-ren datu-eredua eta XML anotazioak.