package com.flavio.rognoni.purgatory.purgatory.mazes.mazeGenerators;

import com.flavio.rognoni.purgatory.purgatory.mazes.Maze;
import com.flavio.rognoni.purgatory.purgatory.mazes.MazeSquare;

import java.util.*;

public class WilsonGen { //loop-erased random walk (funziona male da rivedere)

    private final Maze maze;
    private final Random rand;
    private List<MazeSquare> stack;
    private Set<MazeSquare> visited;
    private final MazeSquare startCell;
    private boolean isGen;

    public WilsonGen(Maze maze){
        this.maze = maze;
        this.rand = new Random();
        this.isGen = false;
        this.maze.setGridForIRK();
        this.maze.setTypeAt(1,1, MazeSquare.START_END);
        this.maze.setTypeAt(maze.h-2,maze.w-2,MazeSquare.START_END);
        this.startCell = maze.getCellAt(1,1);
        this.stack = new ArrayList<>();
        stack.add(startCell);
        this.visited = new HashSet<>();
        this.visited.add(startCell);
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
            var paths = maze.getAllPaths();
            paths.removeAll(visited);
            var path = paths.get(rand.nextInt(paths.size()));
            stack.add(path);
        }else{
            var cell = stack.get(stack.size()-1);
            var vicini = maze.viciniNotLimit(cell);
            var next = vicini.get(rand.nextInt(vicini.size()));
            if(next.isWall())
                maze.setTypeAt(next.x,next.y,MazeSquare.PATH);
            next = maze.getCellAt(next.x,next.y);
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

}
