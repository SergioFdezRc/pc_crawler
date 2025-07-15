package com.pc.crawler.model.helper;

public class ModelTable {

    private String url;
    private int frecuencia;
    private Float ranking;

    public ModelTable(String url, int frecuencia, Float ranking) {
        this.url = url;
        this.frecuencia = frecuencia;
        this.ranking = ranking;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(int frecuencia) {
        this.frecuencia = frecuencia;
    }

    public Float getRanking() {
        return ranking;
    }

    public void setRanking(Float ranking) {
        this.ranking = ranking;
    }
}
