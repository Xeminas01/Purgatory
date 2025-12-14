package com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Teletrasporto extends MazeCell {

    public final Teletrasporto endPoint;

    public Teletrasporto(int x,int y,Teletrasporto endPoint){
        super(x,y);
        this.endPoint = endPoint;
    }

    public Teletrasporto(int x,int y){ this(x,y,null); }

    @Override
    public MazeCellType type() { return MazeCellType.TELETRASPORTO; }

    @Override
    public boolean isWalkable() { return true; }

    @Override
    public String color() { return "DeepSkyBlue"; }

    @Override
    public MazeCell copy() { return new Teletrasporto(x,y,endPoint); }

    @Override
    public MazeCell copyOf(int x, int y) {
        return new Teletrasporto(x,y,null);
    }

    @Override
    public String toString() {
        String ep = (endPoint != null) ?
                "{endPoint:"+endPoint.x+","+endPoint.y+"}" : "{endPoint:null}";
        return super.toString()+ep;
    }

    @Override
    public Element toXMLElement(Document doc) {
        Element el = doc.createElement(this.getClass().getSimpleName());
        el.setAttribute("x",""+x);
        el.setAttribute("y",""+y);
        el.setAttribute("endPoint",x+","+y);
        return el;
    }

    public static MazeCell fromXMLElement(Element e) {
        if(e.getTagName().equals(Teletrasporto.class.getSimpleName())){
            int x = Integer.parseInt(e.getAttribute("x")),
                    y = Integer.parseInt(e.getAttribute("y"));
            String[] eCoords = e.getAttribute("endPoint").split(",");
            int ex = Integer.parseInt(eCoords[0]),
                    ey = Integer.parseInt(eCoords[1]);
            Teletrasporto t = new Teletrasporto(ex,ey);
            return new Teletrasporto(x,y,t);
        }
        else return null;
    }

}
