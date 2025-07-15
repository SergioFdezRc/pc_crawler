package com.pc.crawler.model;

import java.io.Serializable;

/**
 * Definición de la clase Fichero
 *
 * @author Sergio Fernández Rincón
 * @version 1.0
 */
public class Fichero implements Serializable {

    /**
     * Numero de palabras
     */
    private int palabras;

    /**
     * URL del fichero
     */
    private String url;

    /**
     * Constructor parametrizado
     *
     * @param palabras: Numero de palabras
     * @param url: URL del fichero
     */
    public Fichero(int palabras, String url) {
        this.palabras = palabras;
        this.url = url;
    }

    public int getPalabras() {
        return palabras;
    }

    public void setPalabras(int palabras) {
        this.palabras = palabras;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Fichero{" +
                "palabras=" + palabras +
                ", url='" + url + '\'' +
                '}';
    }
}
