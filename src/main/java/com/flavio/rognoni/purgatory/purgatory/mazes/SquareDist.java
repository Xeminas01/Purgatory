package com.flavio.rognoni.purgatory.purgatory.mazes;

public class SquareDist implements Comparable<SquareDist>{
    public final MazeSquare square;
    public final int d;

    public SquareDist(MazeSquare square,int d){
        this.square = square;
        this.d = d;
    }

    @Override
    public String toString() {
        return square+"<"+d+">";
    }

    @Override
    public int compareTo(SquareDist o) {
        return Integer.compare(d,o.d);
    }

}