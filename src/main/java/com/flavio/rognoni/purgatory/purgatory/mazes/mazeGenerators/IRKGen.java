package com.flavio.rognoni.purgatory.purgatory.mazes.mazeGenerators;

import com.flavio.rognoni.purgatory.purgatory.mazes.Maze;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.InizioFine;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.MazeCell;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.MazeCellType;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.Percorso;

import java.util.*;

public class IRKGen extends MazeGen { //Iterative randomized Kruskal's algorithm (with sets)

    private final List<Set<MazeCell>> sets;
    private final List<MazeCell> walls;

    public IRKGen(Maze maze, int sx, int sy, int ex, int ey){
        super(maze,getInitCell(maze,sx,sy),
                getFinalCell(maze,ex,ey));
        this.sets = new ArrayList<>();
        this.maze.setGridForIRK();
        this.maze.cells[initCell.x][initCell.y] = new InizioFine(initCell.x,initCell.y,true);
        this.maze.cells[finalCell.x][finalCell.y] = new InizioFine(finalCell.x,finalCell.y,false);
        this.maze.fixInizioFine(true);
        this.maze.fixInizioFine(false);
        this.walls = maze.getAllOfTypes(MazeCellType.MURO);
        for(MazeCell cell : maze.getAllOfTypes(MazeCellType.PERCORSO,MazeCellType.INIZIO_FINE))
            sets.add(new HashSet<>(Collections.singletonList(cell)));
    }

    private static MazeCell getInitCell(Maze maze, int x, int y){
        if(maze.cells[x][y].type().isLimite())
            return maze.cells[x][y];
        else
            return maze.cells[1][0];
    }

    private static MazeCell getFinalCell(Maze maze, int x, int y){
        if(maze.cells[x][y].type().isLimite())
            return maze.cells[x][y];
        else
            return maze.cells[maze.h-1][maze.w-2];
    }

    public MazeCell step(){
        if(gen) return null;
        if(maze.isAllReachable()){
            gen = true;
            return null;
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
        return null;
    }

    public Maze getMaze() { return maze; }

}
