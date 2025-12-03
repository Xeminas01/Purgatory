package com.flavio.rognoni.purgatory.purgatory.mazes;

public class SquareMiddleDist{

    public final MazeSquare square;
    public final int d1,d2;

    public SquareMiddleDist(MazeSquare square,int d1,int d2){
        this.square = square;
        this.d1 = d1;
        this.d2 = d2;
    }

    public int diff(){
        return Math.abs(d1-d2);
    }

    @Override
    public String toString() {
        return square+"<"+d1+","+d2+">";
    }

}
