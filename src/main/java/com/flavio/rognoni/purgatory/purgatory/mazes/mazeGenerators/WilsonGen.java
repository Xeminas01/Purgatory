package com.flavio.rognoni.purgatory.purgatory.mazes.mazeGenerators;

import com.flavio.rognoni.purgatory.purgatory.mazes.Maze;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.InizioFine;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.MazeCell;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.MazeCellType;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.Percorso;

import java.util.*;

public class WilsonGen { //loop-erased random walk (funziona male da rivedere)

    private final Maze maze;
    private final Random rand;
    private List<MazeCell> stack;
    private Set<MazeCell> visited;
    private final MazeCell startCell;
    private boolean isGen;
    private int t;

    public WilsonGen(Maze maze, int x, int y){
        this.maze = maze;
        this.rand = new Random();
        this.isGen = false;
        this.maze.setGridForIRK();
        if(maze.cells[x][y].type().isPercorso())
            this.startCell = new InizioFine(x,y,true);
        else
            this.startCell = new InizioFine(1,1,true);
        this.maze.cells[startCell.x][startCell.y] = this.startCell;
        var far = this.maze.furthestFromManhattan(startCell);
        this.maze.cells[far.x][far.y] = new InizioFine(far.x,far.y,false);
        this.stack = new ArrayList<>();
        stack.add(startCell);
        this.visited = new HashSet<>();
        this.visited.add(startCell);
        t = 0;
    }

    public void step(){
        if(isGen) return;
        if(maze.isAllReachable()){
            isGen = true;
            //maze.setTypeAt();
            return;
        }
        //System.out.println(stack);
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
    }

    public void generate(){
        while(!isGen){
            step();
        }
    }

    public Maze getMaze() {
        return maze;
    }

    public boolean isGen() {
        return isGen;
    }

    public int getT() {
        return t;
    }
}
