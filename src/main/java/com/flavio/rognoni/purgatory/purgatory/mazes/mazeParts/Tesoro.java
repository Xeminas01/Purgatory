package com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts;

public class Tesoro extends MazeCell {

    private String oggetto;
    private boolean taken;

    public Tesoro(int x,int y,String oggetto,boolean taken){
        super(x,y);
        this.oggetto = oggetto;
        this.taken = taken;
    }

    public boolean isTaken() { return taken; }

    public boolean isNotTaken(){ return !taken; }

    public void take(){
        if(taken) return;
        taken = true;
    }

    public String getOggetto() {
        if(isTaken()) {
            String s = oggetto;
            this.oggetto = null;
            return s;
        }else return null;
    }

    @Override
    public MazeCellType type() { return MazeCellType.TESORO; }

    @Override
    public boolean isWalkable() { return true; }

    @Override
    public String color() { return (taken) ? "silver" : "gold"; }

    @Override
    public MazeCell copy() { return new Tesoro(x,y,oggetto,taken); }

    @Override
    public String toString() {
        return super.toString()+"{"+((taken) ? "preso" : "non preso")+"}{"+oggetto+"}";
    }

}
