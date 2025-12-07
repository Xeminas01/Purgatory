package com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts;

public class Teletrasporto extends MazeCell {

    public final Teletrasporto endPoint;

    public Teletrasporto(int x,int y,Teletrasporto endPoint){
        super(x,y);
        this.endPoint = endPoint;
    }

    public Teletrasporto(int x,int y){ this(x,y,null); }

    @Override
    public MazeCellType type() { return MazeCellType.TELETRASPORTO; }

    @Override
    public boolean isWalkable() { return true; }

    @Override
    public String color() { return "DeepSkyBlue"; }

    @Override
    public MazeCell copy() { return new Teletrasporto(x,y,endPoint); }

    @Override
    public String toString() {
        return super.toString()+"{endPoint:"+endPoint.x+","+endPoint.y+"}";
    }

}
