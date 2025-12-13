package com.flavio.rognoni.purgatory.purgatory.mazes.mazeGenerators;

import com.flavio.rognoni.purgatory.purgatory.mazes.Maze;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.MazeCell;

import java.util.Random;

public abstract class MazeGen {

    protected Maze maze;
    protected final Random rand;
    protected MazeCell initCell;
    protected boolean gen;
    protected int t;

    protected MazeGen(Maze maze,MazeCell initCell){
        this.maze = maze;
        this.initCell = initCell;
        this.rand = new Random();
        this.gen = false;
        this.t = 0;
    }

    public abstract MazeCell step();
    public abstract Maze getMaze();

    public void generate(){
        while(!gen)
            step();
    }

    public MazeCell getInitCell() { return initCell; }

    public boolean isGen() { return gen; }

    public int getT() { return t; }

}
