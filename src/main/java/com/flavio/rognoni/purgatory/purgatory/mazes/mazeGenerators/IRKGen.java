package com.flavio.rognoni.purgatory.purgatory.mazes.mazeGenerators;

import com.flavio.rognoni.purgatory.purgatory.mazes.Maze;
import com.flavio.rognoni.purgatory.purgatory.mazes.MazeSquare;

import java.util.*;

public class IRKGen { //Iterative randomized Kruskal's algorithm (with sets)

    private final Maze maze;
    private final Random rand;
    private final List<Set<MazeSquare>> sets;
    private final List<MazeSquare> walls;
    private boolean isGen;

    public IRKGen(Maze maze){
        this.maze = maze;
        this.sets = new ArrayList<>();
        this.maze.setGridForIRK();
        this.maze.setTypeAt(1,1,MazeSquare.START_END);
        this.maze.setTypeAt(maze.h-2,maze.w-2,MazeSquare.START_END);
        this.walls = maze.getAllWalls();
        for(MazeSquare ms : maze.getAllPaths())
            sets.add(new HashSet<>(Collections.singletonList(ms)));
        this.rand = new Random();
        this.isGen = false;
    }

    public void step(){
        if(isGen) return;
        if(maze.isAllReachable()){
            isGen = true;
            //maze.setTypeAt();
            return;
        }
        MazeSquare wall = walls.remove(rand.nextInt(walls.size()));
        var vicini = maze.viciniNotLimit(wall);
        if(vicini.size() >= 2){
            for(int i=0;i<vicini.size()-1;i++){
                Set<MazeSquare> a = null, b = null;
                for(Set<MazeSquare> set : sets){
                    if(set.contains(vicini.get(i))) a = set;
                    else if(set.contains(vicini.get(i+1))) b = set;
                }
                if(a != null && b != null){
                    Set<MazeSquare> tmpSet = new HashSet<>(a);
                    tmpSet.retainAll(b);
                    if(tmpSet.isEmpty()){
                        maze.setTypeAt(wall.x,wall.y,MazeSquare.PATH);
                        a.addAll(b);
                        sets.remove(b);
                        break;
                    }
                }
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
