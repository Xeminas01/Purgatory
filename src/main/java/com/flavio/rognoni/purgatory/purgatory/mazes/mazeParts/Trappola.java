package com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts;

public class Trappola extends MazeCell {

    public final int danni;
    //aggiungere tipo di trappola?
    // (fare diversi tipi di trappola non solo quella danni, anche status, elementi e malus e (bonus?))
    private boolean activated;

    public Trappola(int x,int y,int danni,boolean activated){
        super(x,y);
        this.danni = danni;
        this.activated = activated;
    }

    public boolean isActivated() { return activated; }

    public boolean isNotActivated(){ return !activated; }

    public void activate(){
        if(!activated) activated = true;
    }

    //effect

    @Override
    public MazeCellType type() { return MazeCellType.TRAPPOLA; }

    @Override
    public boolean isWalkable() { return true; }

    @Override
    public String color() { return "Crimson"; }

    @Override
    public MazeCell copy() { return new Trappola(x,y,danni,activated); }

    @Override
    public String toString() {
        return super.toString()+"{"+((activated) ? "attivata" : "non attivata")+"}{"+danni+"}";
    }

}
