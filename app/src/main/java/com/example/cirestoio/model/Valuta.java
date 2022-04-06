package com.example.cirestoio.model;

import java.util.Objects;

public class Valuta {
    private String nome;
    private String nazione;
    private double eurRate;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNazione() {
        return nazione;
    }

    public void setNazione(String nazione) {
        this.nazione = nazione;
    }

    public double getEurRate() {
        return eurRate;
    }

    public void setEurRate(double eurRate) {
        this.eurRate = eurRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Valuta valuta = (Valuta) o;
        return Double.compare(valuta.eurRate, eurRate) == 0 && Objects.equals(nome, valuta.nome) && Objects.equals(nazione, valuta.nazione);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, nazione, eurRate);
    }

    public Valuta(String nazione,String nome ,double eurRate) {
        this.nome = nome;
        this.nazione = nazione;
        this.eurRate = eurRate;
    }
}
