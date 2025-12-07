package com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts;

import java.util.Arrays;
import java.util.List;

public class CellVector {

    public final MazeCell cell;
    public final Integer[] d;

    public CellVector(MazeCell cell,Integer ...d){
        this.cell = cell;
        this.d = d;
    }

    public CellVector(MazeCell cell, List<Integer> d){
        this(cell,d.toArray(new Integer[0]));
    }

    public int diffPointByPoint(){ //linear O(n)
        int diff = 0;
        if(d == null || d.length == 0) return diff;
        if(d.length == 1) return d[0];
        for(int i=0;i<d.length-1;i++)
            diff += Math.abs(d[i] - d[i+1]);
        return diff;
    }

    public int diff(){ // quadratic O(n^2)
        int diff = 0;
        if(d == null || d.length == 0) return diff;
        if(d.length == 1) return d[0];
        for(int i=0;i<d.length-1;i++)
            for(int j=i+1;j<d.length;j++)
                diff += Math.abs(d[i] - d[j]);
        return diff;
    }

    @Override
    public String toString() { return cell+Arrays.toString(d); }

}
