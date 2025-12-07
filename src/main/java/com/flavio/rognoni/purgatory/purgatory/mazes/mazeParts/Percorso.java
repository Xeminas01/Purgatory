package com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts;

public class Percorso extends MazeCell{

    public Percorso(int x,int y){ super(x,y); }

    @Override
    public MazeCellType type() { return MazeCellType.PERCORSO; }

    @Override
    public boolean isWalkable() { return true; }

    @Override
    public String color() { return "rgb(128,128,128)"; }

    @Override
    public MazeCell copy() { return new Percorso(x,y); }

}
