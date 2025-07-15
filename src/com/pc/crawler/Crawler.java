package com.pc.crawler;

import com.pc.crawler.model.Fichero;
import com.pc.crawler.model.Ocurrencia;
import com.pc.crawler.model.Ranking;
import com.pc.crawler.model.helper.Labels;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.*;

/**
 * Creación de un Crawler para la asignatura de Recuperación de Información y
 * Búsqueda en la Web.
 * <p>
 * Implementación de la clase Crawler.java
 *
 * @author Sergio Fernández Rincón
 * @version 1.0 - 2019
 */
public class Crawler {

    /**
     * Instancia del crawler
     */
    private static Crawler MyCrawler = null;

    /**
     * Diccionario
     */
    private Map<String, Ocurrencia> diccionario = new TreeMap<>();

    /**
     * Diccionario de terminos
     */
    private Map<String, String> terminos;

    /**
     * Lista de ficheros
     */
    private ArrayList<Fichero> fat;

    /**
     * Origen de los ficheros
     */
    private String origen;

    /**
     * Lista de rankings
     */
    private ArrayList<Ranking> rankings;

    /**
     * Buffer de lectura    
     */
    private BufferedReader br;

    /**
     * Constructor por defecto privado
     */
    private Crawler() {
        rankings = new ArrayList<>();
    }

    /**
     * Crea una instancia del Crawler
     */
    private synchronized static void createInstance() {
        if (MyCrawler == null) {
            //asdasd
            MyCrawler = new Crawler();
        }
    }

    /**
     * Si no hay ina instancia creada, la crea y de cualquier modo la devuelve
     *
     * @return devuelve la instancia de la clase.
     */
    public static Crawler getInstance() {
        if (MyCrawler == null)
            createInstance();
        return MyCrawler;
    }

    /**
     * Carga un objeto serializable en memoria previamente salvado en un fichero
     */
    private void cargarObjeto(int valor, File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            switch (valor) {
                case 1:
                    this.diccionario = (TreeMap) ois.readObject();
                    break;
                case 2:
                    this.terminos = (TreeMap) ois.readObject();
                    break;
                case 3:
                    this.fat = (ArrayList<Fichero>) ois.readObject();
                    break;
            }
            ois.close();
            fis.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

   
    /**
     * Carga el tesauro desde el fichero de texto
     * @throws IOException
     * @throws FileNotFoundException
     */
    private void cargarTesauro() {
        try {
            FileReader fr = new FileReader(Labels.nombre_fichero_tessauro);
            BufferedReader br = new BufferedReader(fr);
            String linea;
            try {
                // Lee el fichero linea por linea
                while ((linea = br.readLine()) != null) {
                    if (linea.charAt(0) != '#') {
                        String[] terminos = linea.split(";");
                        String aux = linea;

                        // Cuenta el numero de terminos
                        int cont = 1;
                        while (aux.indexOf(";") > -1) {
                            cont++;
                            aux = aux.substring(aux.indexOf(";") + 1);
                        }

                        // Añade los terminos al diccionario
                        for (int i = 0; i < cont; i++) {
                            if (!terminos[i].contains(" ")) {
                                this.terminos.put(terminos[i], terminos[i]);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            System.out.println("Fichero desaparecido en combate  ;-)");
        }
    }

    /**
     * Salva un objeto serializable en un fichero
     * @param o: Objeto a salvar
     * @param nombre: Nombre del fichero
     */
    private void salvarObjeto(Object o, String nombre) {
        try {
            FileOutputStream fos = new FileOutputStream(nombre);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(o);
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Lista el contenido de los directorios de una ruta dada por parámetro.
     * @param ruta: Ruta del directorio
     */
    public void listIt(String ruta) {
        File fichero = new File(ruta);
        if (!fichero.exists() || !fichero.canRead()) {
            System.out.println("No puedo leer " + fichero);
            return;
        }
        if (fichero.isDirectory()) {
            String[] listaFicheros = fichero.list();
            if (listaFicheros != null)
                for (String listaFichero : listaFicheros) {
                    listIt(ruta + "/" + listaFichero);
                }
        } else try {
            String result;
            try {
                result = this.makeTika(fichero);
                StringTokenizer st = this.tokenizeIt(result, ".!#·$%&/()=?¿ \t\n");
                fat.add(new Fichero(st.countTokens(), fichero.getAbsolutePath()));
                int pos = 0;
                while (st.hasMoreTokens()) {
                    pos++;
                    String s = st.nextToken().toLowerCase();
                    Object o = diccionario.get(s);
                    if (o == null) {
                        Object ob = this.terminos.get(s);
                        if (ob != null)
                            diccionario.put(s, new Ocurrencia(1, fat.size() - 1, pos));
                    } else {
                        Ocurrencia ocur = (Ocurrencia) o;
                        ocur.actualizarOcurrencia(fat.size() - 1);
                    }
                }
            } catch (TikaException e) {
                System.err.println("ATENCION: El documento " + fichero.getAbsolutePath() + " no puede ser reconocido");
            } catch (SAXException e) {
                System.err.println("ATENCION: El documento " + fichero.getAbsolutePath() + " tiene mas de 100k de caracateres");
            }
        } catch (FileNotFoundException fnfe) {
            System.out.println("Fichero desaparecido en combate  ;-)");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tokeniza el contenido de un fichero
     * @param contenido: Contenido del fichero
     * @param expresion: Expresion regular
     * @return: Tokenizador
     */
    private StringTokenizer tokenizeIt(String contenido, String expresion) {
        return new StringTokenizer(contenido, expresion);
    }

    /**
     * @param file: Fichero a procesar
     * @return: Contenido del fichero
     * @throws IOException
     * @throws SAXException
     * @throws TikaException
     */
    private String makeTika(File file) throws IOException, SAXException, TikaException {
        Parser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler();

        Metadata metadata = new Metadata();
        FileInputStream inputstream = new FileInputStream(file);
        ParseContext context = new ParseContext();
        parser.parse(inputstream, handler, metadata, context);
        return handler.toString();
    }

    /**
     * Genera el ranking de las ocurrencias
     * @param o: Objeto a procesar
     * @param busqueda: Busqueda a procesar
     * @return: Ranking de las ocurrencias
     */
    public void generaMuestraRanking(Object o, String busqueda) {
        Ocurrencia resultados = (Ocurrencia) o;

        for (Integer key : resultados.getOc().keySet()) {
            Integer value = resultados.getOc().get(key);
            rankings.add(new Ranking(key, value, this.fat.get(key).getPalabras(), resultados.getFT(), resultados.getPos()));
        }

        Comparator<Ranking> comparator = (r1, r2) -> {
            int resultado = Float.compare(r2.getRanking(), r1.getRanking());
            if (resultado != 0) {
                return resultado;
            }
            return resultado;
        };

        //Comprobar con lo que me pone esto
        Collections.sort(rankings, comparator);
        System.out.println(busqueda + " aparece " + resultados.getFT() + " veces en el diccionario.");
        System.out.println("Listado de ocurrencias:");

        for (Ranking r : rankings) {
            System.out.println(this.fat.get(r.getUrl()).getUrl() +
                    " aparece " + r.getFT() +
                    " veces en el documento y tiene un valor de ranking "
                    + r.getRanking());
        }
    }

    /**
     * Configura el tesauro
     * @return: True si se ha configurado correctamente, false en caso contrario
     */
    private boolean configurarThesauro() {
        File f2 = new File(Labels.nombre_fichero_tes);
        if (!f2.exists()) {
            terminos = new TreeMap<String, String>();
            this.cargarTesauro();
            this.salvarObjeto(this.terminos, Labels.nombre_fichero_tes);
            return false;
        } else {
            this.cargarObjeto(2, f2);
            return true;
        }
    }

    /**
     * Configura el diccionario
     * @return: True si se ha configurado correctamente, false en caso contrario
     */
    private boolean configurarDiccionario() {
        File f = new File(Labels.nombre_fichero_map);
        if (!f.exists()) {
            fat = new ArrayList<Fichero>();
            diccionario = new TreeMap<String, Ocurrencia>();
            this.listIt(origen);
            this.salvarObjeto(this.diccionario, Labels.nombre_fichero_map);
            this.salvarObjeto(this.fat, Labels.nombre_fichero_fat);
            return false;
        } else {
            this.cargarObjeto(1, f);
            this.cargarObjeto(3, new File(Labels.nombre_fichero_fat));
            return true;
        }
    }

    /**
     * Configura el crawler
     */
    public void configurar() {

        if (configurarThesauro())
            System.out.println("Tesauro cargado con éxito.");
        else
            System.out.println("El tesauro se ha procesado y cargado correctamente");

        if (configurarDiccionario())
            System.out.println("Diccionario y FAT cargados correctamente.");
        else
            System.out.println("Diccionario y FAT procesados y cargados correctamente.");
    }

    public void resetRanking() {
        this.rankings.clear();
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public Map<String, Ocurrencia> getDiccionario() {
        return diccionario;
    }

    public Fichero getFat(int i) {
        return fat.get(i);
    }

    public void setFat(ArrayList<Fichero> fat) {
        this.fat = fat;
    }

    public Ranking getRanking(int i) {
        return rankings.get(i);
    }

    public int getRankingSize() {
        return rankings.size();
    }

    public int getFatSize() {
        if (this.fat != null)
            return this.fat.size();
        else return 0;
    }
}
