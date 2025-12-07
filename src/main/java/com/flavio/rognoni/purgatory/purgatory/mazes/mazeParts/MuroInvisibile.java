package com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts;

public class MuroInvisibile extends MazeCell{

    public MuroInvisibile(int x,int y){ super(x,y); }

    @Override
    public MazeCellType type() { return MazeCellType.MURO_INVISIBILE; }

    @Override
    public boolean isWalkable() { return true; }

    @Override
    public String color() { return "white"; }

    @Override
    public MazeCell copy() { return new MuroInvisibile(x,y); }

}
