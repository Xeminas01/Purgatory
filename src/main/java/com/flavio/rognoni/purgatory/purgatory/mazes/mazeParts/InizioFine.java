package com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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

    @Override
    public MazeCell copyOf(int x, int y) {
        return new InizioFine(x,y,isStart);
    }

    public MazeCell invertInizioFine(){ return new InizioFine(x,y,!isStart); }

    @Override
    public String toString() {
        return super.toString()+"{"+((isStart) ? "inizio" : "fine")+"}";
    }

    @Override
    public Element toXMLElement(Document doc) {
        Element el = doc.createElement(this.getClass().getSimpleName());
        el.setAttribute("x",""+x);
        el.setAttribute("y",""+y);
        el.setAttribute("start",""+isStart);
        return el;
    }

    public static MazeCell fromXMLElement(Element e) {
        if(e.getTagName().equals(InizioFine.class.getSimpleName())){
            int x = Integer.parseInt(e.getAttribute("x")),
                    y = Integer.parseInt(e.getAttribute("y"));
            boolean start = Boolean.parseBoolean(e.getAttribute("start"));
            return new InizioFine(x,y,start);
        }
        else return null;
    }

}
