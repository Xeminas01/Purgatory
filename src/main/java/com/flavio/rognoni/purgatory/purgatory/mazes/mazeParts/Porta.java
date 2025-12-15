package com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

public class Porta extends MazeCell{

    private boolean open;
    public final int type,nChiavi;
    public final List<Interruttore> interruttori;
    public static final int PORTA_A_CHIAVI = 0, PORTA_A_INTERRUTTORI = 1;

    public Porta(int x,int y,boolean open,int nChiavi){
        super(x,y);
        this.open = open;
        this.type = PORTA_A_CHIAVI;
        this.nChiavi = Math.min(10,Math.max(nChiavi,1));
        this.interruttori = List.of();
    }

    public Porta(int x,int y,boolean open,List<Interruttore> interruttori){
        super(x,y);
        this.open = open;
        this.type = PORTA_A_INTERRUTTORI;
        this.nChiavi = 0;
        this.interruttori = List.copyOf(interruttori);
    }

    @Override
    public MazeCellType type() { return MazeCellType.PORTA; }

    @Override
    public boolean isWalkable() { return open; }

    @Override
    public String color() {
        if(open)
            return (type == PORTA_A_CHIAVI) ?
                "rgb(128,128,128)" : "Thistle";
        return (type == PORTA_A_CHIAVI) ? "Coral" : "Orchid";
    }

    @Override
    public MazeCell copy() {
        return (type == PORTA_A_CHIAVI) ?
                new Porta(x,y,open,nChiavi) :
                new Porta(x,y,open,interruttori);
    }

    @Override
    public MazeCell copyOf(int x, int y) {
        return (type == PORTA_A_CHIAVI) ?
                new Porta(x,y,open,nChiavi) :
                new Porta(x,y,open,interruttori);
    }

    public boolean isChiavi(){ return type == PORTA_A_CHIAVI; }

    public boolean isInterruttori(){ return type == PORTA_A_INTERRUTTORI; }

    public boolean isOpen() { return open; }

    public boolean isClosed() { return !open; }

    public boolean isToOpen(int chiavi){
        if(open) return false;
        if(type == PORTA_A_CHIAVI)
            return chiavi >= nChiavi;
        else{
            boolean b = true;
            for(Interruttore i : interruttori)
                b = b && i.isOn();
            return b;
        }
    }

    public boolean isToClose(){
        if(!open) return false;
        if(type == PORTA_A_INTERRUTTORI){
            boolean b = true;
            for(Interruttore i : interruttori)
                b = b && i.isOn();
            return !b;
        }else return false;
    }

//    public void openDoor(int chiavi){
//        //if(isToOpen(chiavi))
//        open = true;
//    }

    public void openDoor(){
        //if(isToOpen(chiavi))
        open = true;
    }

    public void closeDoor(){
        //if(isToClose() && type != PORTA_A_CHIAVI)
        open = false;
    }

    @Override
    public String toString() {
        String s = (type == PORTA_A_CHIAVI) ? "chiavi:"+nChiavi : "interruttori:"+interruttori;
        return super.toString()+"{"+((open) ? "aperta" : "chiusa")+"}{"+s+"}";
    }

    @Override
    public Element toXMLElement(Document doc) {
        Element el = doc.createElement(this.getClass().getSimpleName());
        el.setAttribute("x",""+x);
        el.setAttribute("y",""+y);
        el.setAttribute("open",""+open);
        el.setAttribute("type",""+type);
        el.setAttribute("chiavi",""+nChiavi);
        el.setAttribute("interruttori",getInterruttoriStr());
        return el;
    }

    private String getInterruttoriStr(){
        String s = "";
        for(Interruttore i : interruttori)
            s += i.x+","+i.y+";";
        if(!s.isEmpty())
            s = s.substring(0,s.length()-1);
        return s;
    }

    public static MazeCell fromXMLElement(Element e) {
        if(e.getTagName().equals(Porta.class.getSimpleName())){
            int x = Integer.parseInt(e.getAttribute("x")),
                    y = Integer.parseInt(e.getAttribute("y")),
                    type = Integer.parseInt(e.getAttribute("type"));
            boolean open = Boolean.parseBoolean(e.getAttribute("open"));
            if(type == PORTA_A_CHIAVI){
                int chiavi = Integer.parseInt(e.getAttribute("chiavi"));
                return new Porta(x,y,open,chiavi);
            }else{
                List<Interruttore> interruttori = new ArrayList<>();
                for(String i : e.getAttribute("interruttori").split(";")){
                    String[] coords = i.split(",");
                    interruttori.add(new Interruttore(
                            Integer.parseInt(coords[0]),Integer.parseInt(coords[1]),false));
                }
                return new Porta(x,y,open,interruttori);
            }
        }
        else
            return null;
    }

}
