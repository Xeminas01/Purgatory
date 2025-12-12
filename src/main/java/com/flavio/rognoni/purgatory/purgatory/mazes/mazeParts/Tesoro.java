package com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
    public MazeCell copyOf(int x, int y) {
        return new Tesoro(x,y,oggetto,taken);
    }

    @Override
    public String toString() {
        return super.toString()+"{"+((taken) ? "preso" : "non preso")+"}{"+oggetto+"}";
    }

    @Override
    public Element toXMLElement(Document doc) {
        Element el = doc.createElement(this.getClass().getSimpleName());
        el.setAttribute("x",""+x);
        el.setAttribute("y",""+y);
        el.setAttribute("oggetto",oggetto);
        el.setAttribute("taken",""+taken);
        return el;
    }

    public static MazeCell fromXMLElement(Element e) {
        if(e.getTagName().equals(Tesoro.class.getSimpleName())){
            int x = Integer.parseInt(e.getAttribute("x")),
                    y = Integer.parseInt(e.getAttribute("y"));
            String oggetto = e.getAttribute("oggetto");
            boolean taken = Boolean.parseBoolean(e.getAttribute("taken"));
            return new Tesoro(x,y,oggetto,taken);
        }
        else return null;
    }

}
