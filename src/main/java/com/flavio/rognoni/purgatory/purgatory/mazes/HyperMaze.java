package com.flavio.rognoni.purgatory.purgatory.mazes;

import com.flavio.rognoni.purgatory.purgatory.mazes.mazeGenerators.MazeGenType;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.*;

import java.util.*;

public class HyperMaze {

    public final int d,h,w,sx,sy,ex,ey;
    public Maze[][] mazeMatrix;
    public final MazeCell[][] cells;
    public static final int MIN_DIM = 20, MAX_DIM = 1000, MIN_D = 2, MAX_D = 5;

    public HyperMaze(int d,List<Maze> mazes,int sx,int sy,int ex,int ey) throws Exception{
        //todo: sviluppare una versione con dimensioni di matrice anche diverse
        // 2,3;3,2;2,4;4,2;2,5;5,2;3,4;4,3;3,5;5,3;4,5;5,4
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
            throw new Exception("Invalid rows too less or too many [20,1000]");
        if(w < MIN_DIM || w > MAX_DIM)
            throw new Exception("Invalid columns too less or too many [20,1000]");
        //System.out.println(h+" "+w+" "+sx+" "+sy+" "+ex+" "+ey);
        if(!Maze.isValidInizio(h,w,sx,sy))
            throw new Exception("Invalid Inizio");
        if(!Maze.isValidFine(h,w,sx,sy,ex,ey))
            throw new Exception("Invalid Fine");
        this.sx = sx;
        this.sy = sy;
        this.ex = ex;
        this.ey = ey;
        this.mazeMatrix = mm;
        this.cells = new MazeCell[h][w];
        for(int i=0;i<h;i++)
            for(int j=0;j<w;j++)
                cells[i][j] = null;
        buildHyperMaze();
    }

    private Maze[][] buildMazeMatrix(int d,List<Maze> mazes) throws Exception{
        Maze[][] mm = new Maze[d][d];
        Set<String> set = new HashSet<>();
        for(int i=0;i<d;i++) {
            for(int j=0;j<d;j++) {
                var maze = mazes.get(i*d+j);
                //System.out.println(maze);
                if(!set.contains(maze.getId()) &&
                        !maze.isSolvable())
                    return null;
                mm[i][j] = mazes.get(i*d+j);
                set.add(maze.getId());
                System.out.println(set);
            }
        }
        for(int i=0;i<d;i++){
            int hRow = mm[i][0].h;
            for(int j=0;j<d;j++)
                if(mm[i][j].h != hRow)
                    throw new Exception(i+","+j+" wrong row size "+mm[i][j].h+" != "+hRow);
        }
        for(int i=0;i<d;i++){
            int wCol = mm[0][i].w;
            for(int j=0;j<d;j++)
                if(mm[j][i].w != wCol)
                    throw new Exception(j+","+i+" wrong column size "+mm[j][i].w+" != "+wCol);
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

    private void buildHyperMaze() throws Exception{
        int ix = 0, iy = 0;
        int[] hRow = new int[d+1],
                wColumn = new int[d+1];
        for(int i=0;i<d;i++) {
            System.out.println(i);
            int hh = mazeMatrix[i][0].h;
            for(int j=0;j<d;j++) {
                System.out.println(j);
                var maze = mazeMatrix[i][j];
                for(int x=0;x<maze.h;x++){
                    for(int y=0;y<maze.w;y++){
                        var cell = mazeMatrix[i][j].cellAt(x,y);
                        if(cell.type().isInizioFine())
                            cells[ix+x][iy+y] = new Limite(ix+x,iy+y);
                        else {
                            if(cell.type().isTeletrasporto()){
                                Teletrasporto t = (Teletrasporto) cell;
                                cells[ix+x][iy+y] = new Teletrasporto(ix+x,iy+y,
                                        ix+t.ex,iy+t.ey);
                            }else
                                cells[ix+x][iy+y] = cell.copyOf(ix+x,iy+y);
                        }
                    }
                }
                iy += maze.w;
            }
            ix += hh;
            iy = 0;
        }
        hRow[0] = 0;
        wColumn[0] = 0;
        for(int i=0;i<d;i++) {
            hRow[i+1] = hRow[i] + mazeMatrix[i][0].h;
            wColumn[i+1] = wColumn[i] + mazeMatrix[0][i].w;
        }
        System.out.println("hrow "+Arrays.toString(hRow));
        System.out.println("wcolumn "+Arrays.toString(wColumn));
        for(int i=0;i<h;i++){
            for(int j=0;j<w;j++){
                var cell = cells[i][j];
                if(cell.type().isLimite() && i != 0 && i != h-1 && j != 0 && j != w-1){
                    cells[i][j] = new Muro(cell.x,cell.y);
                }
            }
        }
        Random rand = new Random();

        for(int i=0;i<d;i++)
            for(int j=0;j<d-1;j++)
                openRow(i,j,hRow,wColumn,rand);
        for(int i=0;i<d;i++)
            for(int j=0;j<d-1;j++)
                openColumn(i,j,hRow,wColumn,rand);
        cells[sx][sy] = new InizioFine(sx,sy,true);
        cells[ex][ey] = new InizioFine(ex,ey,false);
        fixInizioFine(true);
        fixInizioFine(false);
        System.out.println("ok");
    }

    private void openRow(int r, int c, int[] hRow, int[] wColumn, Random rand) throws Exception{
        System.out.println("openRow: "+r+","+c+"\n");
        List<MazeCell[]> cellsCouples = new ArrayList<>();
        int y1 = wColumn[c+1]-1,y2 = wColumn[c+1];
        for(int i=hRow[r]+1;i<=hRow[r+1]-2;i++){
            MazeCell[] arr = new MazeCell[2];
            arr[0] = cells[i][y1];
            arr[1] = cells[i][y2];
            cellsCouples.add(arr);
        }
        for(MazeCell[] mc : cellsCouples) System.out.println(Arrays.toString(mc));
        cellsCouples = cellsCouples.stream().filter(couple ->
                !viciniWalkable(couple[0],true).isEmpty() &&
                !viciniWalkable(couple[1],true).isEmpty()).toList();
        if(cellsCouples.isEmpty())
            throw new Exception("seperazioni totalmente chiuse "+r+" row");
        MazeCell[] winners = cellsCouples.get(rand.nextInt(cellsCouples.size()));
        cells[winners[0].x][winners[0].y] = new Percorso(winners[0].x,winners[0].y);
        cells[winners[1].x][winners[1].y] = new Percorso(winners[1].x,winners[1].y);
    }

    public void openColumn(int c, int r, int[] hRow,int[] wColumn, Random rand) throws Exception{
        System.out.println("openCol: "+r+","+c+"\n");
        List<MazeCell[]> cellsCouples = new ArrayList<>();
        int x1 = hRow[r+1]-1,x2 = hRow[r+1];
        System.out.println(x1+" "+x2);
        for(int i=wColumn[c]+1;i<=wColumn[c+1]-2;i++){
            MazeCell[] arr = new MazeCell[2];
            arr[0] = cells[x1][i];
            arr[1] = cells[x2][i];
            cellsCouples.add(arr);
        }
        System.out.println("openCol: "+c+"\n");
        for(MazeCell[] mc : cellsCouples) System.out.println(Arrays.toString(mc));
        cellsCouples = cellsCouples.stream().filter(couple ->
                !viciniWalkable(couple[0],true).isEmpty() &&
                        !viciniWalkable(couple[1],true).isEmpty()).toList();
        if(cellsCouples.isEmpty())
            throw new Exception("seperazioni totalmente chiuse "+c+" column");
        MazeCell[] winners = cellsCouples.get(rand.nextInt(cellsCouples.size()));
        cells[winners[0].x][winners[0].y] = new Percorso(winners[0].x,winners[0].y);
        cells[winners[1].x][winners[1].y] = new Percorso(winners[1].x,winners[1].y);
    }

    private void fixInizioFine(boolean inizio) throws Exception{
        MazeCell fi = (inizio) ? getInizio() : getFine();
        //System.out.println(fi);
        if(fi != null){
            var viciniW = viciniWalkable(fi,true);
            var viciniM = viciniFilter(fi,MazeCellType.MURO);
            //System.out.println(viciniM+" "+viciniW);
            if(viciniM.isEmpty() && viciniW.isEmpty())
                throw new Exception("Inizio o Fine non disostruibile");
            while(viciniW.isEmpty()) {
                if(!viciniM.isEmpty()) {
                    var muro = viciniM.get(0);
                    cells[muro.x][muro.y] = new Percorso(muro.x, muro.y);
                    fi = cells[muro.x][muro.y];
                    viciniW = viciniWalkable(fi, true);
                    viciniM = viciniFilter(fi, MazeCellType.MURO);
                }else
                    throw new Exception("Inizio o Fine non disostruibile");
            }
        }
    }

    public List<MazeCell> vicini(MazeCell cell){
        List<MazeCell> v = new ArrayList<>();
        if(cell.x + 1 < h) v.add(cells[cell.x+1][cell.y]);
        if(cell.x - 1 >= 0) v.add(cells[cell.x-1][cell.y]);
        if(cell.y + 1 < w) v.add(cells[cell.x][cell.y+1]);
        if(cell.y - 1 >= 0) v.add(cells[cell.x][cell.y-1]);
        return v;
    }

    public List<MazeCell> viciniFilter(MazeCell cell, MazeCellType...types){
        return new ArrayList<>(vicini(cell).stream()
                .filter(p -> MazeCellType.isOneOfTheTypes(p.type(),types))
                .toList());
    }

    public List<MazeCell> viciniNotFilter(MazeCell cell,MazeCellType ...types){
        return new ArrayList<>(vicini(cell).stream()
                .filter(p -> MazeCellType.isNotOneOfTheTypes(p.type(),types))
                .toList());
    }

    public List<MazeCell> viciniWalkable(MazeCell cell,boolean walkable){
        return new ArrayList<>(vicini(cell).stream()
                .filter(p -> p.isWalkable() == walkable)
                .toList());
    }

    public InizioFine getInizio(){
        var inF = getAllOfTypes(MazeCellType.INIZIO_FINE);
        for(MazeCell cell : inF)
            if(((InizioFine) cell).isStart)
                return (InizioFine) cell;
        return null;
    }

    public InizioFine getFine(){
        var inF = getAllOfTypes(MazeCellType.INIZIO_FINE);
        for(MazeCell cell : inF)
            if(!((InizioFine) cell).isStart)
                return (InizioFine) cell;
        return null;
    }

    public List<MazeCell> getAllOfTypes(MazeCellType ...types){
        var l = new ArrayList<MazeCell>();
        for(MazeCell[] i : cells)
            for(MazeCell p : i)
                if(MazeCellType.isOneOfTheTypes(p.type(),types))
                    l.add(p);
        return l;
    }

    public Maze getMaze() throws Exception{
        Maze maze = new Maze(h,w,MazeGenType.HYPER_MAZE);
        for(int i=0;i<h;i++)
            System.arraycopy(cells[i], 0, maze.cells[i], 0, cells[i].length);
        if(!maze.isSolvable())
            throw new Exception("not solvable");
        return maze;
    }

}
