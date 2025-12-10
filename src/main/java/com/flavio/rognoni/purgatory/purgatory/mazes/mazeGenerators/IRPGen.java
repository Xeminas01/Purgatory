package com.flavio.rognoni.purgatory.purgatory.mazes.mazeGenerators;

import com.flavio.rognoni.purgatory.purgatory.mazes.Maze;
import com.flavio.rognoni.purgatory.purgatory.mazes.Maze2;
import com.flavio.rognoni.purgatory.purgatory.mazes.MazeSquare;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.InizioFine;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.MazeCell;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.MazeCellType;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.Percorso;

import java.util.*;

public class IRPGen { //Iterative randomized Prim's algorithm (without stack, without sets)

    private final Maze2 maze;
    private final Random rand;
    private final List<MazeCell> walls;
    private final Set<MazeCell> visited;
    private final MazeCell initCell;
    private boolean gen;
    private int t;

    public IRPGen(Maze2 maze,int x,int y){
        this.maze = maze;
        this.rand = new Random();
        this.gen = false;
        t = 0;
        if(maze.cells[x][y].type().isLimite())
            this.initCell = maze.cells[x][y];
        else
            this.initCell = maze.cells[1][1];
        this.maze.cells[initCell.x][initCell.y] = new InizioFine(initCell.x,initCell.y,true);
        this.maze.cells[maze.h-2][maze.w-2] = new InizioFine(maze.h-2,maze.w-2,false); //da rivedere
        this.walls = new ArrayList<>();
        this.walls.addAll(maze.viciniFilter(initCell, MazeCellType.MURO));
        this.visited = new HashSet<>();
        this.visited.add(initCell);
    }

    public void step(){
        if(gen) return;
        if(walls.isEmpty()){
            gen = true;
            //maze.setTypeAt();
            return;
        }
        MazeCell cur = walls.get(rand.nextInt(walls.size()));
        //System.out.println(cur);
        var vicini = maze.viciniNotFilter(cur,MazeCellType.LIMITE);
        //System.out.println(vicini);
        int uv = 0;
        for(MazeCell ms : vicini)
            if(visited.contains(ms)) uv++;
        //System.out.println(uv);
        if(uv == 1){
            maze.cells[cur.x][cur.y] = new Percorso(cur.x,cur.y);
            visited.add(cur);
            walls.addAll(maze.viciniFilter(cur,MazeCellType.MURO));
        }
        walls.remove(cur);
        t++;
    }

    public void generate(){
        while(!gen){
            step();
        }
    }

    public Maze2 getMaze() {
        return maze;
    }

    public boolean isGen() { return gen; }

    public int getT() { return t; }

}
