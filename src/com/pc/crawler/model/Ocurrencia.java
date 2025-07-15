package com.pc.crawler.model;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Sergio Fernández Rincón
 */
public class Ocurrencia implements Serializable {

    /**
     *
     */
    private int FT;

    /**
     * El primer parámetro es la url, el índice al array/vector de documentos
     * y el segundo es la frecuencia.
     */
    private Map<Integer, Integer> oc;

    /**
     * Posición del documento en la que se ha encontrado la ocurrencia.
     */
    private int pos;

    /**
     * Constructor parametrizado
     *
     * @param ft
     * @param ruta
     * @param pos
     */
    public Ocurrencia(int ft, int ruta, int pos) {
        this.FT = ft;
        oc = new TreeMap<Integer, Integer>();
        this.oc.put(ruta, 1);
        this.pos = pos;
    }

    /**
     * Actualiza la ocurrencia
     * @param ruta: Ruta del documento
     */
    public void actualizarOcurrencia(Integer ruta) {
        this.FT += 1;
        Object o = oc.get(ruta);
        if (o == null) {
            oc.put(ruta, 1);
        } else {
            Integer cont = oc.get(ruta);
            oc.put(ruta, new Integer(cont.intValue() + 1));
        }
    }

    public int getFT() {
        return FT;
    }

    public void setFT(int FT) {
        this.FT = FT;
    }

    public Map<Integer, Integer> getOc() {
        return oc;
    }

    public void setOc(Map<Integer, Integer> oc) {
        this.oc = oc;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    @Override
    public String toString() {
        return "-> FT: " + FT +
                "-> Ocurrencia: " + oc;
    }
}
