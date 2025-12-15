package com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Teletrasporto extends MazeCell {

    public final int ex,ey;

    public Teletrasporto(int x,int y,int ex,int ey){
        super(x,y);
        this.ex = ex;
        this.ey = ey;
    }

    public Teletrasporto(int x,int y){ this(x,y,-1,-1); }

    public boolean noEndPoint(){
        return ex == -1 && ey == -1;
    }

    @Override
    public MazeCellType type() { return MazeCellType.TELETRASPORTO; }

    @Override
    public boolean isWalkable() { return true; }

    @Override
    public String color() { return "DeepSkyBlue"; }

    @Override
    public MazeCell copy() { return new Teletrasporto(x,y,ex,ey); }

    @Override
    public MazeCell copyOf(int x, int y) {
        return new Teletrasporto(x,y,-1,-1);
    }

    @Override
    public String toString() {
        String ep = (ex != -1 && ey != -1) ?
                "{endPoint:"+ex+","+ey+"}" : "{endPoint:miss}";
        return super.toString()+ep;
    }

    @Override
    public Element toXMLElement(Document doc) {
        Element el = doc.createElement(this.getClass().getSimpleName());
        el.setAttribute("x",""+x);
        el.setAttribute("y",""+y);
        el.setAttribute("ex",""+ex);
        el.setAttribute("ey",""+ey);
        return el;
    }

    public static MazeCell fromXMLElement(Element e) {
        if(e.getTagName().equals(Teletrasporto.class.getSimpleName())){
            int x = Integer.parseInt(e.getAttribute("x")),
                    y = Integer.parseInt(e.getAttribute("y"));
            String[] eCoords = e.getAttribute("endPoint").split(",");
            int ex = Integer.parseInt(eCoords[0]),
                    ey = Integer.parseInt(eCoords[1]);
            return new Teletrasporto(x,y,ex,ey);
        }
        else return null;
    }

}
