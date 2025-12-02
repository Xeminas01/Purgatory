package com.flavio.rognoni.purgatory.purgatory.mazes;

public class MazeSquare {

    public final int x,y,type;
    public final static int LIMIT = 0, WALL = 1, PATH = 2, START_END = 3,
            PORTA = 4, INTERRUTTORE = 5, TESORO = 6, TRAPPOLA = 7,
            MURI_INVISIBILI = 8, TELETRASPORTI = 9;

    public MazeSquare(int x, int y, int type){
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public MazeSquare changeType(int type){
        return new MazeSquare(x,y,type);
    }

    public boolean isLimit(){
        return type == LIMIT;
    }

    public boolean isWall(){
        return type == WALL;
    }

    public boolean isPath(){
        return type == PATH;
    }

    public boolean isStartEnd(){
        return type == START_END;
    }

    public int manhattanDistance(MazeSquare pos){
        return Math.abs(x - pos.x) + Math.abs(y - pos.y);
    }

    public String cStr() {
        return x+","+y+"["+type+"]";
    }

    @Override
    public String toString() {
        return "P("+x+","+y+")["+type+"]";
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(obj == null) return false;
        if(obj.getClass() != MazeSquare.class) return false;
        MazeSquare lp = (MazeSquare) obj;
        return lp.x == this.x && lp.y == this.y;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    public MazeSquare copyOf(int x,int y){
        return new MazeSquare(x,y,type);
    }

}
