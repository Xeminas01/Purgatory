package com.flavio.rognoni.purgatory.purgatory.elements;

public enum Elemento {

    FUOCO("Fuoco"), GHIACCIO("Ghiaccio"), FULMINE("Fulmine"), NATURA("Natura"),
    ANIMA("Anima"), SABBIA("Sabbia"), LUCE("Luce"), OSCURITA("Oscurità"),
    ACQUA("Acqua"), TERRA("Terra"), ARIA("Aria"), METALLO("Metallo"),
    NEUTRO("");

    public final String nome;

    public final static Double[][] elementMatrix = {
            {
                    1.0, //Fuoco -> Fuoco
                    2.0, //Fuoco -> Ghiaccio
                    1.0, //Fuoco -> Fulmine
                    4.0, //Fuoco -> Natura
                    0.25, //Fuoco -> Anima
                    0.5, //Fuoco -> Sabbia
                    0.5, //Fuoco -> Luce
                    2.0, //Fuoco -> Oscurità
                    0.0, //Fuoco -> Acqua
                    0.5, //Fuoco -> Terra
                    0.25, //Fuoco -> Aria
                    1.0, //Fuoco -> Metallo
                    1.0 //Fuoco -> Neutro
            }, //Fuoco
            {
                    0.25, //Ghiaccio -> Fuoco
                    1.0, //Ghiaccio -> Ghiaccio
                    1.0, //Ghiaccio -> Fulmine
                    2.0, //Ghiaccio -> Natura
                    0.0, //Ghiaccio -> Anima
                    0.25, //Ghiaccio -> Sabbia
                    0.5, //Ghiaccio -> Luce
                    2.0, //Ghiaccio -> Oscurità
                    0.5, //Ghiaccio -> Acqua
                    0.5, //Ghiaccio -> Terra
                    1.0, //Ghiaccio -> Aria
                    4.0, //Ghiaccio -> Metallo
                    1.0 //Ghiaccio -> Neutro
            }, //Ghiaccio
            {
                    1.0, //Fulmine -> Fuoco
                    1.0, //Fulmine -> Ghiaccio
                    1.0, //Fulmine -> Fulmine
                    2.0, //Fulmine -> Natura
                    0.25, //Fulmine -> Anima
                    0.25, //Fulmine -> Sabbia
                    0.5, //Fulmine -> Luce
                    2.0, //Fulmine -> Oscurità
                    4.0, //Fulmine -> Acqua
                    0.0, //Fulmine -> Terra
                    0.5, //Fulmine -> Aria
                    0.5, //Fulmine -> Metallo
                    1.0 //Fulmine -> Neutro
            }, //Fulmine
            {}, //Natura
            {}, //Anima
            {}, //Sabbia
            {}, //Luce
            {}, //Oscurità
            {}, //Acqua
            {}, //Terra
            {}, //Aria
            {}, //Metallo
            {} //Neutro
    };

    Elemento(String nome){
        this.nome = nome;
    }

    public boolean isFuoco(Elemento el){ return el == FUOCO; }
    public boolean isGhiaccio(Elemento el){ return el == GHIACCIO; }
    public boolean isFulmine(Elemento el){ return el == FULMINE; }
    public boolean isNatura(Elemento el){ return el ==  NATURA; }
    public boolean isAnima(Elemento el){ return el == ANIMA; }
    public boolean isSabbia(Elemento el){ return el == SABBIA; }
    public boolean isLuce(Elemento el){ return el == LUCE; }
    public boolean isOscurita(Elemento el){ return el == OSCURITA; }
    public boolean isAcqua(Elemento el){ return el == ACQUA; }
    public boolean isTerra(Elemento el){ return el == TERRA; }
    public boolean isAria(Elemento el){ return el == ARIA; }
    public boolean isMetallo(Elemento el){ return el == METALLO; }
    public boolean isNeutro(Elemento el){ return el == NEUTRO; }

    public static int danni(Elemento att,Elemento dif,int d){
        return (int) (elementMatrix[att.ordinal()][dif.ordinal()] * (double) d);
    }

    public static String color(Elemento el){
        switch(el){
            case FUOCO -> { return "red"; }
            case GHIACCIO -> { return "blue"; }
            case FULMINE -> { return "yellow"; }
            case NATURA -> { return "green"; }
            case ANIMA -> { return "purple"; }
            case SABBIA -> { return "orange"; }
            case LUCE -> { return "white"; }
            case OSCURITA -> { return "black"; }
            case ACQUA -> { return "cyan"; }
            case TERRA -> { return "brown"; }
            case ARIA -> { return "turquoise"; }
            case METALLO -> { return "darkgray"; }
            default -> { return "pink"; }
        }
    }

}
