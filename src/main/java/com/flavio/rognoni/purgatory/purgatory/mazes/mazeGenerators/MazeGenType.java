package com.flavio.rognoni.purgatory.purgatory.mazes.mazeGenerators;

public enum MazeGenType {

    DFS_GEN("DFS"),
    FRACTAL_GEN("Fractal"),
    CELLULAR_GEN("Cellular"),
    I_R_KRUSKAL_GEN("IRK"),
    I_R_PRIM_GEM("IRP"),
    WILSON_GEN("Wilson");

    private final String nome;

    MazeGenType(String nome){ this.nome = nome; }

    public String getNome() { return nome; }

    public int millis(){
        switch(this){
            case DFS_GEN,I_R_KRUSKAL_GEN,I_R_PRIM_GEM -> { return 10; }
            case FRACTAL_GEN -> { return 1000; }
            default -> { return 100; }
        }
    }

}
