package com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class MazeCell {

    public final int x,y;

    protected MazeCell(int x,int y){
        this.x = x;
        this.y = y;
    }

    public abstract MazeCellType type();
    public abstract boolean isWalkable();
    public abstract String color();
    public abstract MazeCell copy();
    public abstract MazeCell copyOf(int x,int y);
    public abstract Element toXMLElement(Document doc);

    public int manhattanDistance(MazeCell cell){
        return Math.abs(x - cell.x) + Math.abs(y - cell.y);
    }

    public String cStr() {
        return x+","+y+"["+type()+"]";
    }

    @Override
    public String toString() {
        return "Cell("+x+","+y+")["+type()+"]";
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(obj == null) return false;
        if(!(obj instanceof MazeCell mc)) return false;
        return mc.x == x && mc.y == y;
    }

    @Override
    public int hashCode() { return 0; }

    public static MazeCell fromXMLElement(Element e){
        try{
            switch(e.getTagName()){
                case "Limite" -> { return Limite.fromXMLElement(e); }
                case "Muro" -> { return Muro.fromXMLElement(e); }
                case "Percorso" -> { return Percorso.fromXMLElement(e); }
                case "InizioFine" -> { return InizioFine.fromXMLElement(e); }
                case "Porta" -> { return Porta.fromXMLElement(e); }
                case "Interruttore" -> { return Interruttore.fromXMLElement(e); }
                case "Tesoro" -> { return Tesoro.fromXMLElement(e); }
                case "Trappola" -> { return Trappola.fromXMLElement(e); }
                case "MuroInvisibile" -> { return MuroInvisibile.fromXMLElement(e); }
                case "Teletrasporto" -> { return Teletrasporto.fromXMLElement(e); }
                default -> { return null; }
            }
        }catch (Exception ex){
            return null;
        }
    }

}
