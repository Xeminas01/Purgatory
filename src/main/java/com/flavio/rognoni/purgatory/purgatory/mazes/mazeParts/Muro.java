package com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Muro extends MazeCell {

    public Muro(int x,int y){ super(x,y); }

    @Override
    public MazeCellType type() { return MazeCellType.MURO; }

    @Override
    public boolean isWalkable() { return false; }

    @Override
    public String color() { return "black"; }

    @Override
    public MazeCell copy() { return new Muro(x,y); }

    @Override
    public MazeCell copyOf(int x, int y) {
        return  new Muro(x,y);
    }

    @Override
    public Element toXMLElement(Document doc) {
        Element el = doc.createElement(this.getClass().getSimpleName());
        el.setAttribute("x",""+x);
        el.setAttribute("y",""+y);
        return el;
    }

    public static MazeCell fromXMLElement(Element e) {
        if(e.getTagName().equals(Muro.class.getSimpleName())){
            int x = Integer.parseInt(e.getAttribute("x")),
                    y = Integer.parseInt(e.getAttribute("y"));
            return new Muro(x,y);
        }
        else return null;
    }

}
