package com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts;

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

    public int doorType(){ return type; }

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

    public void openDoor(int chiavi){
        if(isToOpen(chiavi)) open = true;
    }

    public void closeDoor(){
        if(isToClose() && type != PORTA_A_CHIAVI)
            open = false;
    }

    @Override
    public String toString() {
        String s = (type == PORTA_A_CHIAVI) ? "chiavi:"+nChiavi : "interruttori:"+interruttori;
        return super.toString()+"{"+((open) ? "aperta" : " chiusa")+"}{"+s+"}";
    }

}
