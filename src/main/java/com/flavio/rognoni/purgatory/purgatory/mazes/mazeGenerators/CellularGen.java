package com.flavio.rognoni.purgatory.purgatory.mazes.mazeGenerators;

import com.flavio.rognoni.purgatory.purgatory.mazes.Maze;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.MazeCell;

public class CellularGen extends MazeGen {

    private final CellularAutomata2D mazectric;

    public CellularGen(Maze maze, int x, int y){
        super(maze,getInitCell(maze,x,y));
        this.mazectric = CellularAutomata2D.mazectric(maze.h-2,maze.w-2);
    }

    private static MazeCell getInitCell(Maze maze, int x, int y){
        if(!maze.cells[x][y].type().isLimite())
            return maze.cells[x][y];
        else
            return maze.cells[1][1];
    }

    @Override
    public MazeCell step(){
        if(gen) return null;
        mazectric.step();
        maze = mazectric.getMazeRender();
        t++;
        if(mazectric.getT() == 40){
            maze = mazectric.getMaze(initCell.x,initCell.y);
            gen = true;
        }
        return null;
    }

    public Maze getMaze() { return maze; }

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

}
