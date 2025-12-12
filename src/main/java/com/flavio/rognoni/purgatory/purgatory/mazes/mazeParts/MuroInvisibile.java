package com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class MuroInvisibile extends MazeCell{

    public MuroInvisibile(int x,int y){ super(x,y); }

    @Override
    public MazeCellType type() { return MazeCellType.MURO_INVISIBILE; }

    @Override
    public boolean isWalkable() { return true; }

    @Override
    public String color() { return "white"; }

    @Override
    public MazeCell copy() { return new MuroInvisibile(x,y); }

    @Override
    public MazeCell copyOf(int x, int y) {
        return new MuroInvisibile(x,y);
    }

    @Override
    public Element toXMLElement(Document doc) {
        Element el = doc.createElement(this.getClass().getSimpleName());
        el.setAttribute("x",""+x);
        el.setAttribute("y",""+y);
        return el;
    }

    public static MazeCell fromXMLElement(Element e) {
        if(e.getTagName().equals(MuroInvisibile.class.getSimpleName())){
            int x = Integer.parseInt(e.getAttribute("x")),
                    y = Integer.parseInt(e.getAttribute("y"));
            return new MuroInvisibile(x,y);
        }
        else return null;
    }

}
