package com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts;

public class Interruttore extends MazeCell {

    private boolean on;

    public Interruttore(int x,int y,boolean on){
        super(x,y);
        this.on = on;
    }

    public boolean isOn() { return on; }

    public boolean isOff() { return !on; }

    private void setOn(boolean on) { this.on = on; }

    public void turnOn(){ if(isOff()) setOn(true); }

    public void turnOff(){ if(isOn()) setOn(false); }

    @Override
    public MazeCellType type() { return MazeCellType.INTERRUTTORE; }

    @Override
    public boolean isWalkable() { return true; }

    @Override
    public String color() { return (on) ? "Green" : "Red"; }

    @Override
    public MazeCell copy() { return new Interruttore(x,y,on); }

}
