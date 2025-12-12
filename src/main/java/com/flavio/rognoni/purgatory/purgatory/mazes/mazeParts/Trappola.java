package com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Trappola extends MazeCell {

    public final int danni;
    //aggiungere tipo di trappola?
    // (fare diversi tipi di trappola non solo quella danni, anche status, elementi e malus e (bonus?))
    private boolean activated;

    public Trappola(int x,int y,int danni,boolean activated){
        super(x,y);
        this.danni = danni;
        this.activated = activated;
    }

    public boolean isActivated() { return activated; }

    public boolean isNotActivated(){ return !activated; }

    public void activate(){
        if(!activated) activated = true;
    }

    //effect

    @Override
    public MazeCellType type() { return MazeCellType.TRAPPOLA; }

    @Override
    public boolean isWalkable() { return true; }

    @Override
    public String color() { return "Crimson"; }

    @Override
    public MazeCell copy() { return new Trappola(x,y,danni,activated); }

    @Override
    public MazeCell copyOf(int x, int y) {
        return new Trappola(x,y,danni,activated);
    }

    @Override
    public String toString() {
        return super.toString()+"{"+((activated) ? "attivata" : "non attivata")+"}{"+danni+"}";
    }

    @Override
    public Element toXMLElement(Document doc) {
        Element el = doc.createElement(this.getClass().getSimpleName());
        el.setAttribute("x",""+x);
        el.setAttribute("y",""+y);
        el.setAttribute("danni",""+danni);
        el.setAttribute("activated",""+activated);
        return el;
    }

    public static MazeCell fromXMLElement(Element e) {
        if(e.getTagName().equals(Trappola.class.getSimpleName())){
            int x = Integer.parseInt(e.getAttribute("x")),
                    y = Integer.parseInt(e.getAttribute("y")),
                    danni = Integer.parseInt(e.getAttribute("danni"));
            boolean activated = Boolean.parseBoolean(e.getAttribute("activated"));
            return new Trappola(x,y,danni,activated);
        }
        else return null;
    }

}
