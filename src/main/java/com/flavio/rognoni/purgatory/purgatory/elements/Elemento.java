package com.flavio.rognoni.purgatory.purgatory.elements;

public enum Elemento {

    FUOCO("Fuoco"), GHIACCIO("Ghiaccio"), FULMINE("Fulmine"), NATURA("Natura"),
    ANIMA("Anima"), SABBIA("Sabbia"), LUCE("Luce"), OSCURITA("Oscurità"),
    ACQUA("Acqua"), TERRA("Terra"), ARIA("Aria"), METALLO("Metallo"),
    NEUTRO("");

    public final String nome;

    public final static Double[][] elementMatrix = { //todo: fatti ma da rivedere i livelli di forza
            {
                    1.0, /*Fuoco -> Fuoco*/ 2.0, /*Fuoco -> Ghiaccio*/ 1.0, /*Fuoco -> Fulmine*/ 4.0, /*Fuoco -> Natura*/
                    0.25, /*Fuoco -> Anima*/ 0.5, /*Fuoco -> Sabbia*/ 0.5, /*Fuoco -> Luce*/ 2.0, /*Fuoco -> Oscurità*/
                    0.0, /*Fuoco -> Acqua*/ 0.5, /*Fuoco -> Terra*/ 0.25, /*Fuoco -> Aria*/ 1.0, /*Fuoco -> Metallo*/
                    1.0 /*Fuoco -> Neutro*/
            }, /*Fuoco*/
            {
                    0.25, /*Ghiaccio -> Fuoco*/ 1.0, /*Ghiaccio -> Ghiaccio*/ 1.0, /*Ghiaccio -> Fulmine*/2.0, /*Ghiaccio -> Natura*/
                    0.0, /*Ghiaccio -> Anima*/0.25, /*Ghiaccio -> Sabbia*/0.5, /*Ghiaccio -> Luce*/2.0, /*Ghiaccio -> Oscurità*/
                    0.5, /*Ghiaccio -> Acqua*/0.5, /*Ghiaccio -> Terra*/1.0, /*Ghiaccio -> Aria*/4.0, /*Ghiaccio -> Metallo*/
                    1.0 /*Ghiaccio -> Neutro*/
            }, /*Ghiaccio*/
            {
                    1.0, /*Fulmine -> Fuoco*/1.0, /*Fulmine -> Ghiaccio*/1.0, /*Fulmine -> Fulmine*/2.0, /*Fulmine -> Natura*/
                    0.25, /*Fulmine -> Anima*/0.25, /*Fulmine -> Sabbia*/0.5, /*Fulmine -> Luce*/2.0, /*Fulmine -> Oscurità*/
                    4.0, /*Fulmine -> Acqua*/0.0, /*Fulmine -> Terra*/0.5, /*Fulmine -> Aria*/0.5, /*Fulmine -> Metallo*/
                    1.0 /*Fulmine -> Neutro*/
            }, /*Fulmine*/
            {
                    0.0, /*Natura -> Fuoco*/0.25, /*Natura -> Ghiaccio*/0.25, /*Natura -> Fulmine*/1.0, /*Natura -> Natura*/
                    0.5, /*Natura -> Anima*/0.5, /*Natura -> Sabbia*/1.0, /*Natura -> Luce*/0.5, /*Natura -> Oscurità*/
                    1.0, /*Natura -> Acqua*/4.0, /*Natura -> Terra*/2.0, /*Natura -> Aria*/2.0, /*Natura -> Metallo*/
                    1.0 /*Natura -> Neutro*/
            }, /*Natura*/
            {
                    2.0, /*Anima -> Fuoco*/4.0, /*Anima -> Ghiaccio*/2.0, /*Anima -> Fulmine*/1.0, /*Anima -> Natura*/
                    1.0, /*Anima -> Anima*/1.0, /*Anima -> Sabbia*/0.0, /*Anima -> Luce*/0.25, /*Anima -> Oscurità*/
                    0.25, /*Anima -> Acqua*/0.5, /*Anima -> Terra*/0.5, /*Anima -> Aria*/0.5, /*Anima -> Metallo*/
                    1.0 /*Anima -> Neutro*/
            }, /*Anima*/
            {
                    1.0, /*Sabbia -> Fuoco*/2.0, /*Sabbia -> Ghiaccio*/2.0, /*Sabbia -> Fulmine*/1.0, /*Sabbia -> Natura*/
                    0.5, /*Sabbia -> Anima*/1.0, /*Sabbia -> Sabbia*/0.25, /*Sabbia -> Luce*/0.5, /*Sabbia -> Oscurità*/
                    0.25, /*Sabbia -> Acqua*/2.0, /*Sabbia -> Terra*/0.5, /*Sabbia -> Aria*/4.0, /*Sabbia -> Metallo*/
                    1.0 /*Sabbia -> Neutro*/
            }, /*Sabbia*/
            {
                    1.0, /*Luce -> Fuoco*/1.0, /*Luce -> Ghiaccio*/1.0, /*Luce -> Fulmine*/0.5, /*Luce -> Natura*/
                    4.0, /*Luce -> Anima*/2.0, /*Luce -> Sabbia*/1.0, /*Luce -> Luce*/2.0, /*Luce -> Oscurità*/
                    0.25, /*Luce -> Acqua*/0.25, /*Luce -> Terra*/0.5, /*Luce -> Aria*/0.25, /*Luce -> Metallo*/
                    1.0 /*Luce -> Neutro*/
            }, /*Luce*/
            {
                    0.25, /*Oscurità -> Fuoco*/0.25, /*Oscurità -> Ghiaccio*/0.5, /*Oscurità -> Fulmine*/1.0, /*Oscurità -> Natura*/
                    2.0, /*Oscurità -> Anima*/1.0, /*Oscurità -> Sabbia*/2.0, /*Oscurità -> Luce*/1.0, /*Oscurità -> Oscurità*/
                    0.5, /*Oscurità -> Acqua*/0.5, /*Oscurità -> Terra*/4.0, /*Oscurità -> Aria*/0.0, /*Oscurità -> Metallo*/
                    1.0 /*Oscurità -> Neutro*/
            }, /*Oscurità*/
            {
                    4.0, /*Acqua -> Fuoco*/1.0, /*Acqua -> Ghiaccio*/0.0, /*Acqua -> Fulmine*/0.5, /*Acqua -> Natura*/
                    2.0, /*Acqua -> Anima*/2.0, /*Acqua -> Sabbia*/0.5, /*Acqua -> Luce*/1.0, /*Acqua -> Oscurità*/
                    1.0, /*Acqua -> Acqua*/0.25, /*Acqua -> Terra*/0.25, /*Acqua -> Aria*/0.5, /*Acqua -> Metallo*/
                    1.0 /*Acqua -> Neutro*/
            }, /*Acqua*/
            {
                    1.0, /*Terra -> Fuoco*/1.0, /*Terra -> Ghiaccio*/4.0, /*Terra -> Fulmine*/0.0, /*Terra -> Natura*/
                    0.5, /*Terra -> Anima*/0.25, /*Terra -> Sabbia*/2.0, /*Terra -> Luce*/0.5, /*Terra -> Oscurità*/
                    2.0, /*Terra -> Acqua*/1.0, /*Terra -> Terra*/0.25, /*Terra -> Aria*/0.5, /*Terra -> Metallo*/
                    1.0 /*Terra -> Neutro*/
            }, /*Terra*/
            {
                    2.0, /*Aria -> Fuoco*/0.5, /*Aria -> Ghiaccio*/1.0, /*Aria -> Fulmine*/0.25, /*Aria -> Natura*/
                    1.0, /*Aria -> Anima*/0.5, /*Aria -> Sabbia*/0.5, /*Aria -> Luce*/0.0, /*Aria -> Oscurità*/
                    2.0, /*Aria -> Acqua*/0.25, /*Aria -> Terra*/1.0, /*Aria -> Aria*/4.0, /*Aria -> Metallo*/
                    1.0 /*Aria -> Neutro*/
            }, /*Aria*/
            {
                    0.5, /*Metallo -> Fuoco*/0.25, /*Metallo -> Ghiaccio*/0.5, /*Metallo -> Fulmine*/0.25, /*Metallo -> Natura*/
                    0.5, /*Metallo -> Anima*/2.0, /*Metallo -> Sabbia*/2.0, /*Metallo -> Luce*/4.0, /*Metallo -> Oscurità*/
                    1.0, /*Metallo -> Acqua*/1.0, /*Metallo -> Terra*/0.0, /*Metallo -> Aria*/1.0, /*Metallo -> Metallo*/
                    1.0 /*Metallo -> Neutro*/
            }, /*Metallo*/
            {
                    1.0, /*Neutro -> Fuoco*/1.0, /*Neutro -> Ghiaccio*/1.0, /*Neutro -> Fulmine*/1.0, /*Neutro -> Natura*/
                    1.0, /*Neutro -> Anima*/1.0, /*Neutro -> Sabbia*/1.0, /*Neutro -> Luce*/1.0, /*Neutro -> Oscurità*/
                    1.0, /*Neutro -> Acqua*/1.0, /*Neutro -> Terra*/1.0, /*Neutro -> Aria*/1.0, /*Neutro -> Metallo*/
                    1.0 /*Neutro -> Neutro*/
            } /*Neutro*/
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

    public boolean isHyperGood(Elemento o){
        return elementMatrix[ordinal()][o.ordinal()] == 4.0;
    }

    public boolean isSuperGood(Elemento o){
        return elementMatrix[ordinal()][o.ordinal()] == 2.0;
    }

    public boolean isGood(Elemento o){
        return elementMatrix[ordinal()][o.ordinal()] == 1.0;
    }

    public boolean isBad(Elemento o){
        return elementMatrix[ordinal()][o.ordinal()] == 0.5;
    }

    public boolean isSuperBad(Elemento o){
        return elementMatrix[ordinal()][o.ordinal()] == 0.25;
    }

    public boolean isHyperBad(Elemento o){
        return elementMatrix[ordinal()][o.ordinal()] == 0.0;
    }

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
