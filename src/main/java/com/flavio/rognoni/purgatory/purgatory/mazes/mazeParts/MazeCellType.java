package com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts;

import java.util.Arrays;
import java.util.List;

public enum MazeCellType {

    LIMITE("Limite"), MURO("Muro"), PERCORSO("Percorso"),
    INIZIO_FINE("Inizio-Fine"), PORTA("Porta"),
    INTERRUTTORE("Interruttore"), TESORO("Tesoro"),
    TRAPPOLA("Trappola"), MURO_INVISIBILE("Muro Invisibile"),
    TELETRASPORTO("Teletrasporto");

    public final String nome;

    MazeCellType(String nome){
        this.nome = nome;
    }

    public boolean isLimite(){ return this == LIMITE; }
    public boolean isMuro(){ return this == MURO; }
    public boolean isPercorso(){ return this == PERCORSO; }
    public boolean isInizioFine(){ return this == INIZIO_FINE; }
    public boolean isPorta(){ return this == PORTA; }
    public boolean isInterruttore(){ return this == INTERRUTTORE; }
    public boolean isTesoro(){ return this == TESORO; }
    public boolean isTrappola(){ return this == TRAPPOLA; }
    public boolean isMuroInvisibile(){ return this == MURO_INVISIBILE; }
    public boolean isTeletrasporto(){ return this == TELETRASPORTO; }

    public static boolean isOneOfTheTypes(MazeCellType type, MazeCellType ...types){
        return Arrays.asList(types).contains(type);
    }

}
