package com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts;

public class Limite extends MazeCell {

    public Limite(int x,int y){ super(x,y); }

    @Override
    public MazeCellType type() { return MazeCellType.LIMITE; }

    @Override
    public boolean isWalkable() { return false; }

    @Override
    public String color() { return "brown"; }

    @Override
    public MazeCell copy() { return new Limite(x,y); }

    @Override
    public MazeCell copyOf(int x, int y) {
        return new Limite(x,y);
    }


}
