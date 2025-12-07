package com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts;

public class InizioFine extends MazeCell{

    public final boolean isStart;

    public InizioFine(int x,int y,boolean isStart){
        super(x,y);
        this.isStart = isStart;
    }

    @Override
    public MazeCellType type() { return MazeCellType.INIZIO_FINE; }

    @Override
    public boolean isWalkable() { return true; }

    @Override
    public String color() { return (isStart) ? "yellow" : "cyan"; }

    @Override
    public MazeCell copy() { return new InizioFine(x,y,isStart); }

    public MazeCell invertInizioFine(){ return new InizioFine(x,y,!isStart); }

    @Override
    public String toString() {
        return super.toString()+"{"+((isStart) ? "inizio" : "fine")+"}";
    }

}
