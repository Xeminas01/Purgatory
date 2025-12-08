package com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts;

public class Muro extends MazeCell {

    public Muro(int x,int y){ super(x,y); }

    @Override
    public MazeCellType type() { return MazeCellType.MURO; }

    @Override
    public boolean isWalkable() { return false; }

    @Override
    public String color() { return "black"; }

    @Override
    public MazeCell copy() { return new Muro(x,y); }

    @Override
    public MazeCell copyOf(int x, int y) {
        return  new Muro(x,y);
    }

}
