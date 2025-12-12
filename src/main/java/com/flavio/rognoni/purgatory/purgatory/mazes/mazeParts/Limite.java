package com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Limite extends MazeCell {

    public Limite(int x,int y){ super(x,y); }

    @Override
    public MazeCellType type() { return MazeCellType.LIMITE; }

    @Override
    public boolean isWalkable() { return false; }

    @Override
    public String color() { return "brown"; }

    @Override
    public MazeCell copy() { return new Limite(x,y); }

    @Override
    public MazeCell copyOf(int x, int y) {
        return new Limite(x,y);
    }

    @Override
    public Element toXMLElement(Document doc) {
        Element el = doc.createElement(this.getClass().getSimpleName());
        el.setAttribute("x",""+x);
        el.setAttribute("y",""+y);
        return el;
    }

    public static MazeCell fromXMLElement(Element e) {
        if(e.getTagName().equals(Limite.class.getSimpleName())){
            int x = Integer.parseInt(e.getAttribute("x")),
                    y = Integer.parseInt(e.getAttribute("y"));
            return new Limite(x,y);
        }
        else return null;
    }

}
