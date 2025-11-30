package com.flavio.rognoni.purgatory.purgatory.mazes.mazeGenerators;

import com.flavio.rognoni.purgatory.purgatory.mazes.Maze;
import com.flavio.rognoni.purgatory.purgatory.mazes.MazeSquare;

import java.util.*;

public class IRPGen { //Iterative randomized Prim's algorithm (without stack, without sets)

    private final Maze maze;
    private final Random rand;
    private final List<MazeSquare> walls;
    private final Set<MazeSquare> visited;
    private boolean isGen;

    public IRPGen(Maze maze){
        this.maze = maze;
        this.rand = new Random();
        this.isGen = false;
        this.maze.setTypeAt(1,1, MazeSquare.START_END);
        this.maze.setTypeAt(maze.h-2,maze.w-2,MazeSquare.START_END);
        this.walls = new ArrayList<>();
        this.walls.addAll(maze.viciniWall(maze.getCellAt(1,1)));
        this.visited = new HashSet<>();
        this.visited.add(maze.getCellAt(1,1));
    }

    public void step(){
        if(isGen) return;
        if(walls.isEmpty()){
            isGen = true;
            //maze.setTypeAt();
            return;
        }
        MazeSquare cur = walls.get(rand.nextInt(walls.size()));
        //System.out.println(cur);
        var vicini = maze.viciniNotLimit(cur);
        //System.out.println(vicini);
        int uv = 0;
        for(MazeSquare ms : vicini)
            if(visited.contains(ms)) uv++;
        //System.out.println(uv);
        if(uv == 1){
            maze.setTypeAt(cur.x,cur.y,MazeSquare.PATH);
            visited.add(cur);
            walls.addAll(maze.viciniWall(cur));
        }
        walls.remove(cur);
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
