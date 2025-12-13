package com.flavio.rognoni.purgatory.purgatory.mazes.mazeGenerators;

import com.flavio.rognoni.purgatory.purgatory.mazes.Maze;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.InizioFine;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.MazeCell;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.MazeCellType;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.Percorso;

import java.util.*;

public class WilsonGen extends MazeGen { //loop-erased random walk (funziona male da rivedere)

    private final List<MazeCell> stack;
    private final Set<MazeCell> visited;

    public WilsonGen(Maze maze, int x, int y){
        super(maze,getInitCell(maze,x,y));
        this.maze.setGridForIRK();
        if(maze.cells[x][y].type().isPercorso())
            this.initCell = new InizioFine(x,y,true);
        else
            this.initCell = new InizioFine(1,1,true);
        this.maze.cells[initCell.x][initCell.y] = this.initCell;
        var far = this.maze.furthestFromManhattan(initCell);
        this.maze.cells[far.x][far.y] = new InizioFine(far.x,far.y,false);
        this.stack = new ArrayList<>();
        stack.add(initCell);
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
        if(maze.isAllReachable()){
            gen = true;
            return null;
        }
        if(stack.isEmpty()){
            var paths = maze.getAllOfTypes(MazeCellType.PERCORSO,MazeCellType.INIZIO_FINE);
            paths.removeAll(visited);
            var path = paths.get(rand.nextInt(paths.size()));
            stack.add(path);
        }else{
            var cell = stack.get(stack.size()-1);
            var vicini = maze.viciniNotFilter(cell,MazeCellType.LIMITE);
            var next = vicini.get(rand.nextInt(vicini.size()));
            if(next.type().isMuro())
                maze.cells[next.x][next.y] = new Percorso(next.x,next.y);
            next = maze.cellAt(next.x,next.y);
            if(!stack.contains(next)){
                stack.add(next);
                if(visited.contains(next)){
                    stack.clear();
                }else{
                    visited.add(next);
                }
            }else{
                stack.remove(stack.size()-1);
            }
        }
        t++;
        return null;
    }

    public Maze getMaze() { return maze; }

}
