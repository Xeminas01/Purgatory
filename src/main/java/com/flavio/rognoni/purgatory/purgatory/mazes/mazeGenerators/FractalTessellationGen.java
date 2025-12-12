package com.flavio.rognoni.purgatory.purgatory.mazes.mazeGenerators;

import com.flavio.rognoni.purgatory.purgatory.mazes.Maze;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.*;

import java.util.*;
import java.util.stream.Collectors;

public class FractalTessellationGen {

    public final int rounds,dim;
    private final MazeCell[][] matrix;
    private final Random rand;
    private boolean generato;
    private int gStep;
    private MazeCell initCell;
    private int t;

    public FractalTessellationGen(int rounds, int x, int y) {
        this.rounds = rounds;
        this.dim = (int) (Math.pow(2,rounds) + 2);
        this.matrix = new MazeCell[dim][dim];
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                if(i == 0 || i == dim-1 || j == 0 || j == dim-1) {
                    matrix[i][j] = new Limite(i,j);
                }else{
                    matrix[i][j] = new Muro(i,j);
                }
            }
        }
        matrix[1][1] = new Percorso(1,1);
        rand = new Random();
        generato = false;
        gStep = 0;
        if(!matrix[x][y].type().isLimite())
            this.initCell = matrix[x][y];
        else
            this.initCell = matrix[1][1];
        this.t = 0;
    }

    public void step(){
        if(generato) return;
        int exp = gStep/2;
        int stepCol = (int) Math.pow(2,exp+1);
        if(gStep%2==0) duplicateMaze(stepCol);
        if(gStep%2==1) removeWallsOnCoord(stepCol);
        gStep++;
        if(gStep == (rounds-1)*2) generato = true;
        t++;
    }

    public void generate(){
        for(int i=1;i<rounds;i++){
            int stepCol = (int) Math.pow(2,i);
            duplicateMaze(stepCol);
            t++;
            removeWallsOnCoord(stepCol);
            t++;
        }
        generato = true;
    }

    private void duplicateMaze(int stepCol){
        for(int i=1;i<=stepCol;i++){
            for(int j=1;j<=stepCol;j++){
                matrix[i+stepCol][j] = matrix[i][j].copyOf(i+stepCol,j);
                matrix[i][j+stepCol] = matrix[i][j].copyOf(i,j+stepCol);
                matrix[i+stepCol][j+stepCol] =
                        matrix[i][j].copyOf(i+stepCol,j+stepCol);
            }
        }
    }

    private void removeWallsOnCoord(int stepCol){
        List<MazeCell> wallsVU = new ArrayList<>(),
                wallsVD = new ArrayList<>(),
                wallsHR = new ArrayList<>(),
                wallsHL = new ArrayList<>();
        for(int i=1;i<=stepCol*2;i++){
            if(matrix[i][stepCol].type().isMuro()) {
                if(i<stepCol) wallsVU.add(matrix[i][stepCol]);
                else if(i>stepCol) wallsVD.add(matrix[i][stepCol]);
            }
            if(matrix[stepCol][i].type().isMuro()) {
                if(i<stepCol) wallsHL.add(matrix[stepCol][i]);
                else if(i>stepCol) wallsHR.add(matrix[stepCol][i]);
            }
        }
        wallsVU = removableWalls(wallsVU);
        wallsVD = removableWalls(wallsVD);
        wallsHR = removableWalls(wallsHR);
        wallsHL = removableWalls(wallsHL);
        List<MazeCell> rmWalls = new ArrayList<>();
        rmWalls.add(wallsVU.get(rand.nextInt(wallsVU.size())));
        rmWalls.add(wallsVD.get(rand.nextInt(wallsVD.size())));
        rmWalls.add(wallsHR.get(rand.nextInt(wallsHR.size())));
        rmWalls.add(wallsHL.get(rand.nextInt(wallsHL.size())));
        Collections.shuffle(rmWalls);
        for(int i=0;i<3;i++){
            var rmW = rmWalls.get(i);
            matrix[rmW.x][rmW.y] = new Percorso(rmW.x,rmW.y);
        }
    }

    private List<MazeCell> removableWalls(List<MazeCell> walls){
        return walls.stream().filter(this::isRemovable)
                .collect(Collectors.toList());
    }

    private boolean isRemovable(MazeCell w){
        List<MazeCell> vicini = new ArrayList<>();
        vicini.add(matrix[w.x+1][w.y]);
        vicini.add(matrix[w.x-1][w.y]);
        vicini.add(matrix[w.x][w.y+1]);
        vicini.add(matrix[w.x][w.y-1]);
        vicini = vicini.stream().filter(
                cell -> cell.type().isPercorso())
                .collect(Collectors.toList());
        return vicini.size() >= 2;
    }

    public MazeCell[][] getMatrix() {
        return matrix;
    }

    public boolean isGenerato() {
        return generato;
    }

    public Maze getMaze() {
        try{
            Maze maze = new Maze(dim,dim,MazeGenType.FRACTAL_GEN);
            for(int i=0;i<dim;i++)
                System.arraycopy(matrix[i], 0, maze.cells[i], 0, dim);
            if(generato){
                if(!initCell.type().isPercorso()) {
                    var vP = maze.viciniFilter(initCell, MazeCellType.PERCORSO);
                    if(!vP.isEmpty()) initCell = vP.get(0);
                    else initCell = maze.cellAt(1, 1);
                }
                System.out.println(initCell);
                maze.cells[initCell.x][initCell.y] = new InizioFine(initCell.x,initCell.y,true);
                var further = maze.furthestFromManhattan(maze.cellAt(initCell.x,initCell.y));
                maze.cells[further.x][further.y] = new InizioFine(further.x,further.y,false);
            }
            return maze;
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public String toString() {
        String s = "d:"+dim+"\n";
        for(int i=0;i<dim;i++){
            for(int j=0;j<dim;j++){
                s += matrix[i][j] + " ";
            }
            s+="\n";
        }
        return s;
    }

    public int getT() { return t; }

}
