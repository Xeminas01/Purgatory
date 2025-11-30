package com.flavio.rognoni.purgatory.purgatory.mazes;

public class SquareMiddleDist implements Comparable<SquareMiddleDist>{

    public final MazeSquare square;
    public final int d1,d2;

    public SquareMiddleDist(MazeSquare square,int d1,int d2){
        this.square = square;
        this.d1 = d1;
        this.d2 = d2;
    }

    @Override
    public String toString() {
        return square+"<"+d1+","+d2+">";
    }

    @Override
    public int compareTo(SquareMiddleDist o) {
        if(d1 == o.d1)
            return Integer.compare(d2,o.d2);
        return Integer.compare(d1,o.d1);
    }

}
