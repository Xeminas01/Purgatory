package com.flavio.rognoni.purgatory.purgatory.mazes.mazeGenerators;

import com.flavio.rognoni.purgatory.purgatory.mazes.Maze;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.InizioFine;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.MazeCell;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.MazeCellType;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.Percorso;

import java.util.*;

public class IRPGen extends MazeGen { //Iterative randomized Prim's algorithm (without stack, without sets)

    private final List<MazeCell> walls;
    private final Set<MazeCell> visited;

    public IRPGen(Maze maze, int x, int y){
        super(maze,getInitCell(maze,x,y));
        this.maze.cells[initCell.x][initCell.y] = new InizioFine(initCell.x,initCell.y,true);
        this.maze.cells[maze.h-2][maze.w-2] = new InizioFine(maze.h-2,maze.w-2,false); //da rivedere
        this.walls = new ArrayList<>();
        this.walls.addAll(maze.viciniFilter(initCell, MazeCellType.MURO));
        this.visited = new HashSet<>();
        this.visited.add(initCell);
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
        if(walls.isEmpty()){
            gen = true;
            return null;
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
        return null;
    }

    public Maze getMaze() { return maze; }

}
