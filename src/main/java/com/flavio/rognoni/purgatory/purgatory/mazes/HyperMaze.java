package com.flavio.rognoni.purgatory.purgatory.mazes;

import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.MazeCell;

import java.util.List;

public class HyperMaze { //todo: work in progress

    private final int d,h,w;
    public Maze[][] mazeMatrix;
    public final MazeCell[][] cells;
    public static final int MIN_DIM = 200, MAX_DIM = 1000, MIN_D = 2, MAX_D = 5;

    public HyperMaze(int d,List<Maze> mazes) throws Exception{
        if(d < MIN_D || d > MAX_D)
            throw new Exception("hyper matrix wrong dim");
        if(mazes == null || mazes.size() != d*d)
            throw new Exception("missing mazes [needed mazes:"+(d*d)+"]");
        var mm = buildMazeMatrix(d,mazes);
        if(mm == null)
            throw new Exception("wrong mazes size");
        this.d = d;
        this.h = getHH(mm);
        this.w = getHW(mm);
        if(h < MIN_DIM || h > MAX_DIM)
            throw new Exception("Invalid rows too less or too many [200,1000]");
        if(w < MIN_DIM || w > MAX_DIM)
            throw new Exception("Invalid columns too less or too many [200,1000]");
        this.mazeMatrix = mm;
        this.cells = new MazeCell[h][w];
        for(int i=0;i<h;i++)
            for(int j=0;j<w;j++)
                cells[i][j] = null;
        buildHyperMaze();
    }

    private Maze[][] buildMazeMatrix(int d,List<Maze> mazes) throws Exception{
        Maze[][] mm = new Maze[d][d];
        for(int i=0;i<d;i++) {
            for(int j=0;j<d;j++) {
                var maze = mazes.get(i*d+j);
                if(!maze.isSolvable())
                    return null;
                mm[i][j] = mazes.get(i*d+j);
            }
        }
        for(int i=0;i<d;i++){
            int hRow = mm[i][0].h;
            for(int j=0;j<d;j++)
                if(mm[i][j].h != hRow)
                    return null;
        }
        for(int i=0;i<d;i++){
            int wCol = mm[0][i].w;
            for(int j=0;j<d;j++)
                if(mm[j][i].w != wCol)
                    return null;
        }
        return mm;
    }

    private int getHH(Maze[][] mm){
        int hh = 0;
        for(int i=0;i<d;i++)
            hh += mm[i][0].h;
        return hh;
    }

    private int getHW(Maze[][] mm){
        int hw = 0;
        for(int i=0;i<d;i++)
            hw += mm[0][i].w;
        return hw;
    }

    private void buildHyperMaze(){
        int sx = 0, sy = 0;
        for(int i=0;i<d;i++) {
            int hh = mazeMatrix[i][0].h;
            for(int j=0;j<d;j++) {
                var maze = mazeMatrix[i][j];
                for(int x=0;x<maze.h;x++){
                    for(int y=0;y<maze.w;y++){
                        cells[sx+x][sy+y] = mazeMatrix[i][j].cellAt(x,y);
                    }
                }
                sy += maze.w;
            }
            sx += hh;
        }
        //collegare togliendo i limiti, inizio e fine si collegano, inizio solo per [0][0] e fine solo per [d][d]
    }

}
