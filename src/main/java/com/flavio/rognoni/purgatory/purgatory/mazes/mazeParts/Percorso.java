package com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Percorso extends MazeCell{

    public Percorso(int x,int y){ super(x,y); }

    @Override
    public MazeCellType type() { return MazeCellType.PERCORSO; }

    @Override
    public boolean isWalkable() { return true; }

    @Override
    public String color() { return "rgb(128,128,128)"; }

    @Override
    public MazeCell copy() { return new Percorso(x,y); }

    @Override
    public MazeCell copyOf(int x, int y) {
        return new Percorso(x,y);
    }

    @Override
    public Element toXMLElement(Document doc) {
        Element el = doc.createElement(this.getClass().getSimpleName());
        el.setAttribute("x",""+x);
        el.setAttribute("y",""+y);
        return el;
    }

    public static MazeCell fromXMLElement(Element e) {
        if(e.getTagName().equals(Percorso.class.getSimpleName())){
            int x = Integer.parseInt(e.getAttribute("x")),
                    y = Integer.parseInt(e.getAttribute("y"));
            return new Percorso(x,y);
        }
        else return null;
    }

}
