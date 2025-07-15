package com.pc.crawler.model;

/**
 * Definición de la clase Ranking que evalúa las palabras que aparecen en los ficheros, cuántas veces y dónde
 * aparecen.
 *
 * @author Sergio Fernández Rincón.
 * @version 1.0
 */
public class Ranking {

    /**
     * URL del fichero
     */
    private int url;

    /**
     * Frecuencia del término
     */
    private int FT;

    /**
     * Ranking de las ocurrencias
     */
    private Float ranking;

    /**
     * Constructor parametrizado de la clase.
     * Crea un nuevo valor de ranking
     *
     * @param url: URL del fichero
     * @param FT: Frecuencia del término
     * @param palabras: Palabras del fichero
     * @param total: Total de palabras
     * @param pos: Posicion de la palabra
     */
    public Ranking(int url, int FT, int palabras, int total, int pos) {
        this.url = url;
        this.FT = FT;
        this.ranking = new Float(FT * 100 * FT) / (palabras * total * pos);
    }

    public int getUrl() {
        return url;
    }

    public void setUrl(int url) {
        this.url = url;
    }

    public int getFT() {
        return FT;
    }

    public void setFT(int FT) {
        this.FT = FT;
    }

    public Float getRanking() {
        return ranking;
    }

    public void setRanking(Float ranking) {
        this.ranking = ranking;
    }
}
