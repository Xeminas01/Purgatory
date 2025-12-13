package com.flavio.rognoni.purgatory.purgatory.mazes.mazeGenerators;

import com.flavio.rognoni.purgatory.purgatory.mazes.Maze;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.InizioFine;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.MazeCell;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.Percorso;

import java.util.*;
import java.util.stream.Collectors;

public class DFSGen extends MazeGen {

    private final Set<MazeCell> visti;
    private final List<MazeCell> stack;

    public DFSGen(Maze maze, int x, int y) {
        super(maze,getInitCell(maze,x,y));
        this.visti = new HashSet<>();
        this.stack = new ArrayList<>();
    }

    private static MazeCell getInitCell(Maze maze, int x,int y){
        var cell = maze.cellAt(x,y);
        if(cell != null && !cell.type().isLimite())
            return cell;
        else
            return maze.defaultInit();
    }

    public DFSGen(Maze maze) {
        this(maze,maze.h-2,1);
    }

    public void start(){
        maze.cells[initCell.x][initCell.y] = new Percorso(initCell.x,initCell.y);
        MazeCell start = maze.cellAt(initCell.x,initCell.y);
        stack.add(start);
        visti.add(start);
    }

    @Override
    public MazeCell step(){
        if(gen) return null;
        if(stack.isEmpty()) {
            gen = true;
            MazeCell mostDist = maze.furthestFromManhattan(initCell);
            maze.cells[mostDist.x][mostDist.y] = new InizioFine(mostDist.x,mostDist.y,false);
            maze.cells[initCell.x][initCell.y] = new InizioFine(initCell.x,initCell.y,true);
            System.out.println(maze);
            return null;
        }
        //System.out.println(stack);
        MazeCell curr = stack.get(stack.size()-1);
        //System.out.println("curr: "+curr+" "+viciniNoLimit(curr));
        var viciniPossibili = viciniPossibili(curr,viciniNoLimit(curr));
        //System.out.println("vicini possibili: "+viciniPossibili);
        if(viciniPossibili.isEmpty()){
            stack.remove(stack.size()-1);
        }else{
            var next = viciniPossibili.get(rand.nextInt(viciniPossibili.size()));
            maze.cells[next.x][next.y] = new Percorso(next.x,next.y);
            visti.add(next);
            stack.add(next);
        }
        t++;
        return curr;
    }

    private List<MazeCell> viciniNoLimit(MazeCell pos){
        var v = maze.vicini(pos);
        v.removeIf(vic -> vic.type().isLimite() || visti.contains(vic));
        return v;
    }

    private List<MazeCell> viciniPossibili(MazeCell source,List<MazeCell> vicini){
        List<MazeCell> vp = new ArrayList<>();
        for(MazeCell p : vicini){
            var prossimi = maze.vicini(p);
            prossimi.remove(source);
            //System.out.println(p+" "+prossimi);
            int s = prossimi.size();
            prossimi = prossimi.stream().filter(
                            pr -> !pr.type().isPercorso())
                    .collect(Collectors.toList());
            if(prossimi.size() == s){
                vp.add(p);
            }
        }
        return vp;
    }

    public Maze getMaze() { return maze; }

}
