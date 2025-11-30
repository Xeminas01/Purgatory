package com.flavio.rognoni.purgatory.purgatory.mazes.mazeGenerators;

import com.almasb.fxgl.pathfinding.maze.MazeCell;
import com.flavio.rognoni.purgatory.purgatory.mazes.Maze;
import com.flavio.rognoni.purgatory.purgatory.mazes.MazeSquare;

import java.util.*;

public class WilsonGen { //loop-erased random walk

    private final Maze maze;
    private final Random rand;
    private List<MazeSquare> stack;
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
    }

    public void step(){ //un casino da rivedere
        if(isGen) return;
        if(maze.isAllReachable()){
            isGen = true;
            //maze.setTypeAt();
            return;
        }
        System.out.println(stack);
        if(stack.isEmpty()){
            var paths = maze.getAllPaths();
            var path = paths.get(rand.nextInt(paths.size()));
            stack.add(path);
        }else{
            var cell = stack.get(stack.size()-1);
            var vicini = maze.viciniWall(cell);
            System.out.println("vicini:"+vicini);
            Map<MazeSquare,MazeSquare> map = new HashMap<>();
            for(MazeSquare mc : vicini){
                if(maze.viciniPath(mc).size() == 1)
                    map.put(mc,maze.viciniPath(mc).get(0));
            }
            vicini = new ArrayList<>(map.keySet());
            var next = vicini.get(rand.nextInt(vicini.size()));
            maze.setTypeAt(next.x,next.y,MazeSquare.PATH);
            System.out.println(next);
            if(!stack.contains(next)){
                stack.add(next);
            }else{
                stack.clear();
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
