package com.flavio.rognoni.purgatory.purgatory.mazes.mazeGenerators;

import com.flavio.rognoni.purgatory.purgatory.mazes.Maze;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.InizioFine;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.MazeCell;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.MazeCellType;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.Percorso;

import java.util.*;

public class IRKGen { //Iterative randomized Kruskal's algorithm (with sets)

    private final Maze maze;
    private final Random rand;
    private final List<Set<MazeCell>> sets;
    private final List<MazeCell> walls;
    private final MazeCell initCell;
    private boolean gen;
    private int t;

    public IRKGen(Maze maze, int x, int y){
        this.maze = maze;
        this.sets = new ArrayList<>();
        this.maze.setGridForIRK();
        if(maze.cells[x][y].type().isLimite())
            this.initCell = maze.cells[x][y];
        else
            this.initCell = maze.cells[1][1];
        this.maze.cells[initCell.x][initCell.y] = new InizioFine(initCell.x,initCell.y,true);
        var far = this.maze.furthestFromManhattan(initCell);
        this.maze.cells[far.x][far.y] = new InizioFine(far.x,far.y,false);
        this.walls = maze.getAllOfTypes(MazeCellType.MURO);
        for(MazeCell cell : maze.getAllOfTypes(MazeCellType.PERCORSO,MazeCellType.INIZIO_FINE))
            sets.add(new HashSet<>(Collections.singletonList(cell)));
        this.rand = new Random();
        this.gen = false;
        t = 0;
    }

    public void step(){
        if(gen) return;
        if(maze.isAllReachable()){
            gen = true;
            return;
        }
        MazeCell wall = walls.remove(rand.nextInt(walls.size()));
        var vicini = maze.viciniNotFilter(wall,MazeCellType.LIMITE);
        System.out.println(walls.size());
        if(vicini.size() >= 2){
            for(int i=0;i<vicini.size()-1;i++){
                Set<MazeCell> a = null, b = null;
                for(Set<MazeCell> set : sets){
                    if(set.contains(vicini.get(i))) a = set;
                    else if(set.contains(vicini.get(i+1))) b = set;
                }
                if(a != null && b != null){
                    Set<MazeCell> tmpSet = new HashSet<>(a);
                    tmpSet.retainAll(b);
                    if(tmpSet.isEmpty()){
                        maze.cells[wall.x][wall.y] = new Percorso(wall.x,wall.y);
                        a.addAll(b);
                        sets.remove(b);
                        break;
                    }
                }
            }
        }
        t++;
    }

    public void generate(){
        while(!gen){
            step();
        }
    }

    public Maze getMaze() { return maze; }

    public boolean isGen() { return gen; }

    public int getT() { return t; }

}
