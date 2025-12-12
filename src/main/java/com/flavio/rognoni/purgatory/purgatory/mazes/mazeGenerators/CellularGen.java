package com.flavio.rognoni.purgatory.purgatory.mazes.mazeGenerators;

import com.flavio.rognoni.purgatory.purgatory.mazes.Maze;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.MazeCell;

public class CellularGen {

    private Maze maze;
    private final CellularAutomata2D mazectric;
    private boolean gen;
    private int t;
    private final MazeCell initCell;

    public CellularGen(Maze maze, int x, int y){
        this.maze = maze;
        this.mazectric = CellularAutomata2D.mazectric(maze.h-2,maze.w-2);
        this.gen = false;
        this.t = 0;
        if(!maze.cells[x][y].type().isLimite())
            this.initCell = maze.cells[x][y];
        else
            this.initCell = maze.cells[1][1];
    }

    public void step(){
        if(gen) return;
        mazectric.step();
        maze = mazectric.getMazeRender();
        t++;
        if(mazectric.getT() == 40){
            maze = mazectric.getMaze(initCell.x,initCell.y);
            gen = true;
        }
    }

    public Maze getMaze() { return maze; }

    public boolean isGen() { return gen; }

    @Override
    public String toString() {
        String s = "d:"+maze.h+"x"+maze.w+"\n";
        for(int i=0;i<maze.h;i++){
            for(int j=0;j<maze.w;j++){
                s += maze.cells[i][j] + " ";
            }
            s+="\n";
        }
        return s;
    }

    public int getT() { return t; }

}
