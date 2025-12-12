package com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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

    @Override
    public MazeCell copyOf(int x, int y) {
        return new Interruttore(x,y,on);
    }

    @Override
    public Element toXMLElement(Document doc) {
        Element el = doc.createElement(this.getClass().getSimpleName());
        el.setAttribute("x",""+x);
        el.setAttribute("y",""+y);
        el.setAttribute("on",""+on);
        return el;
    }

    public static MazeCell fromXMLElement(Element e) {
        if(e.getTagName().equals(Interruttore.class.getSimpleName())){
            int x = Integer.parseInt(e.getAttribute("x")),
                    y = Integer.parseInt(e.getAttribute("y"));
            boolean on = Boolean.parseBoolean(e.getAttribute("on"));
            return new Interruttore(x,y,on);
        }
        else return null;
    }

}
