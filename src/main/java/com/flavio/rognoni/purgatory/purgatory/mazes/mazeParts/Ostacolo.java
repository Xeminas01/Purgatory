package com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts;

import com.flavio.rognoni.purgatory.purgatory.elements.Elemento;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Ostacolo extends MazeCell {

    public final Elemento elemento;
    public final int danni;
    private boolean enable;

    public Ostacolo(int x,int y,Elemento elemento,
                    int danni){
        super(x,y);
        this.elemento = elemento;
        this.danni = danni;
        this.enable = true;
    }

    private Ostacolo(int x,int y,Elemento elemento,
                    int danni,boolean enable){
        super(x,y);
        this.elemento = elemento;
        this.danni = danni;
        this.enable = enable;
    }

    public void enable(){ enable = true; }

    public void disable(){ enable = false; }

    public boolean isEnable() {
        return enable;
    }

    public boolean isDisable(){
        return !enable;
    }

    @Override
    public MazeCellType type() {
        return MazeCellType.OSTACOLO;
    }

    @Override
    public boolean isWalkable() {
        return true;
    }

    @Override
    public String color() {
        if(enable) return Elemento.color(elemento);
        else return "rgb(128,128,128)";
    }

    @Override
    public String toString() {
        return super.toString()+"{"+enable+"}{"+elemento+"}{"+danni+"}";
    }

    @Override
    public MazeCell copy() {
        return new Ostacolo(x,y,elemento,danni);
    }

    @Override
    public MazeCell copyOf(int x, int y) {
        return new Ostacolo(x,y,elemento,danni);
    }

    @Override
    public Element toXMLElement(Document doc) {
        Element el = doc.createElement(this.getClass().getSimpleName());
        el.setAttribute("x",""+x);
        el.setAttribute("y",""+y);
        el.setAttribute("danni",""+danni);
        el.setAttribute("enable",""+enable);
        el.setAttribute("elemento",""+elemento.ordinal());
        return el;
    }

    public static MazeCell fromXMLElement(Element e) {
        if(e.getTagName().equals(Ostacolo.class.getSimpleName())){
            int x = Integer.parseInt(e.getAttribute("x")),
                    y = Integer.parseInt(e.getAttribute("y")),
                    danni = Integer.parseInt(e.getAttribute("danni"));
            Elemento elemento = Elemento.values()[Integer.parseInt(e.getAttribute("elemento"))];
            boolean enable = Boolean.parseBoolean(e.getAttribute("enable"));
            return new Ostacolo(x,y,elemento,danni,enable);
        }
        else return null;
    }

}
