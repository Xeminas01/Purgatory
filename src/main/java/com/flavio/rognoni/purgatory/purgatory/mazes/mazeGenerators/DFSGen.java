package com.flavio.rognoni.purgatory.purgatory.mazes.mazeGenerators;

import com.flavio.rognoni.purgatory.purgatory.mazes.Maze;
import com.flavio.rognoni.purgatory.purgatory.mazes.MazeSquare;

import java.util.*;
import java.util.stream.Collectors;

public class DFSGen {

    private final Maze maze;
    private final Set<MazeSquare> visti;
    private final List<MazeSquare> stack;
    private final Random rand;
    private final MazeSquare initCell;
    private boolean isGen;

    public DFSGen(Maze maze, int x, int y) {
        this.maze = maze;
        this.visti = new HashSet<>();
        this.stack = new ArrayList<>();
        this.rand = new Random();
        var iCell = maze.getCellAt(x,y);
        if(iCell != null && !iCell.isLimit())
            this.initCell = iCell;
        else
            this.initCell = maze.getDeafultInit();
        this.isGen = false;
    }

    public DFSGen(Maze maze) {
        this(maze,maze.h-2,1);
    }

    public void generate(){
        maze.setTypeAt(maze.h-2, 1,MazeSquare.PATH);
        stack.add(initCell);
        visti.add(initCell);
        while(!stack.isEmpty()){
            MazeSquare curr = stack.get(stack.size()-1);
            //System.out.println("curr: "+curr+" "+viciniNoLimit(curr));
            var viciniPossibili = viciniPossibili(curr,viciniNoLimit(curr));
            //System.out.println("vicini possibili: "+viciniPossibili);
            if(viciniPossibili.isEmpty()){
                stack.remove(stack.size()-1);
            }else{
                var next = viciniPossibili.get(rand.nextInt(viciniPossibili.size()));
                maze.setTypeAt(next.x,next.y,MazeSquare.PATH);
                visti.add(next);
                stack.add(next);
            }
        }
        MazeSquare mostDist = maze.mostDistanceFrom(initCell);
        maze.setTypeAt(mostDist.x,mostDist.y,MazeSquare.START_END);
        maze.setTypeAt(initCell.x,initCell.y,MazeSquare.START_END);
    }

    public void start(){
        maze.setTypeAt(initCell.x,initCell.y,MazeSquare.PATH);
        MazeSquare start = maze.getCellAt(initCell.x,initCell.y);
        stack.add(start);
        visti.add(start);
    }

    public MazeSquare step(){
        if(isGen) return null;
        if(stack.isEmpty()) {
            isGen = true;
            System.out.println("cerco fine");
            System.out.println(maze);
            MazeSquare mostDist = maze.mostDistanceFrom(initCell);
            maze.setTypeAt(mostDist.x,mostDist.y,MazeSquare.START_END);
            maze.setTypeAt(initCell.x,initCell.y,MazeSquare.START_END);
            System.out.println(maze);
            return null;
        }
        //System.out.println(stack);
        MazeSquare curr = stack.get(stack.size()-1);
        //System.out.println("curr: "+curr+" "+viciniNoLimit(curr));
        var viciniPossibili = viciniPossibili(curr,viciniNoLimit(curr));
        //System.out.println("vicini possibili: "+viciniPossibili);
        if(viciniPossibili.isEmpty()){
            stack.remove(stack.size()-1);
        }else{
            var next = viciniPossibili.get(rand.nextInt(viciniPossibili.size()));
            maze.setTypeAt(next.x,next.y,MazeSquare.PATH);
            visti.add(next);
            stack.add(next);
        }
        return curr;
    }

    private List<MazeSquare> viciniNoLimit(MazeSquare pos){
        return maze.vicini(pos).stream()
                .filter(p -> !p.isLimit() && !visti.contains(p))
                .collect(Collectors.toList());
    }

    private List<MazeSquare> viciniPossibili(MazeSquare source,List<MazeSquare> vicini){
        List<MazeSquare> vp = new ArrayList<>();
        for(MazeSquare p : vicini){
            var prossimi = maze.vicini(p);
            prossimi.remove(source);
            //System.out.println(p+" "+prossimi);
            int s = prossimi.size();
            prossimi = prossimi.stream().filter(
                            pr -> !pr.isPath())
                    .collect(Collectors.toList());
            if(prossimi.size() == s){
                vp.add(p);
            }
        }
        return vp;
    }

    public Maze getMaze() {
        return maze;
    }

    public boolean isGen() {
        return isGen;
    }

}
