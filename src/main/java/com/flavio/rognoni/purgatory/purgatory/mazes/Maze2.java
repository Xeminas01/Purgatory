package com.flavio.rognoni.purgatory.purgatory.mazes;

import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.*;

import java.util.*;
import java.util.stream.Collectors;

public class Maze2 {

    public final int h,w;
    public final MazeCell[][] cells;
    public final List<Set<MazeCell>> walkSets;

    public static final int MIN_DIM = 10, MAX_DIM = 1000;

    public Maze2(int h,int w) throws Exception{
        if(h < MIN_DIM || h > MAX_DIM)
            throw new Exception("Invalid rows too less or too many [10,1000]");
        if(w < MIN_DIM || w > MAX_DIM)
            throw new Exception("Invalid columns too less or too many [10,1000]");
        this.h = h;
        this.w = w;
        this.cells = new MazeCell[h][w];
        for(int i=0;i<h;i++)
            for(int j=0;j<w;j++)
                cells[i][j] = (isLimit(i,j)) ? new Limite(i,j) : new Muro(i,j);
        this.walkSets = walkSets();
    }

    public boolean isLimit(int x,int y){
        return x == 0 || y == 0 || x == h-1 || y == w-1;
    }

    public boolean isLimit(MazeCell cell){
        return isLimit(cell.x,cell.y) && cell.type().isLimite();
    }

    public MazeCell cellAt(int x,int y){
        if(x < 0 || x >= h) return null;
        if(y < 0 || y >= w) return null;
        return cells[x][y];
    }

    public MazeCell defaultInit(){ return cells[h-2][1]; }

    public List<MazeCell> getAllOfTypes(MazeCellType ...types){
        var l = new ArrayList<MazeCell>();
        for(MazeCell[] i : cells)
            for(MazeCell p : i)
                if(MazeCellType.isOneOfTheTypes(p.type(),types))
                    l.add(p);
        return l;
    }

    public List<MazeCell> getAllWalkable(boolean walkable){
        var l = new ArrayList<MazeCell>();
        for(MazeCell[] i : cells)
            for(MazeCell p : i)
                if(p.isWalkable() == walkable)
                    l.add(p);
        return l;
    }

    public List<MazeCell> vicini(MazeCell cell){
        List<MazeCell> v = new ArrayList<>();
        if(cell.x + 1 < h) v.add(cells[cell.x+1][cell.y]);
        if(cell.x - 1 >= 0) v.add(cells[cell.x-1][cell.y]);
        if(cell.y + 1 < w) v.add(cells[cell.x][cell.y+1]);
        if(cell.y - 1 >= 0) v.add(cells[cell.x][cell.y-1]);
        return v;
    }

    public List<MazeCell> viciniFilter(MazeCell cell,MazeCellType ...types){
        return vicini(cell).stream()
                .filter(p -> MazeCellType.isOneOfTheTypes(p.type(),types))
                .toList();
    }

    public List<MazeCell> viciniWalkable(MazeCell cell,boolean walkable){
        return vicini(cell).stream()
                .filter(p -> p.isWalkable() == walkable)
                .toList();
    }

    public MazeCell furthestFromManhattan(MazeCell cell){
        int d = Integer.MIN_VALUE;
        MazeCell furthest = null;
        for(MazeCell[] i : cells) {
            for(MazeCell p : i) {
                if(p.isWalkable()){
                    int dist = cell.manhattanDistance(p);
                    if(dist >= d){
                        d = dist;
                        furthest = p;
                    }
                }
            }
        }
        return furthest;
    }

    public int unreachablePaths(){
        List<MazeCell> paths = getAllWalkable(true),
                start = getAllOfTypes(MazeCellType.INIZIO_FINE);
        if(paths.isEmpty() || start.isEmpty()) return -1;
        Set<MazeCell> visited = new HashSet<>();
        List<MazeCell> walk = new ArrayList<>();
        walk.add(start.get(0));
        while(!walk.isEmpty()){
            MazeCell cur = walk.remove(0);
            if(visited.add(cur))
                paths.addAll(viciniWalkable(cur,true));
        }
        return Math.abs(visited.size()-paths.size());
    }

    public boolean isAllReachable(){
        return unreachablePaths() == 0;
    }

    public void fixMaze(){ //funziona che sistema il labirinto in fase di creazione con l'automa cellulare
        var walls = getAllOfTypes(MazeCellType.MURO);
        int best = unreachablePaths();
        while(!walls.isEmpty()){
            var wall = walls.remove(0);
            if(viciniWalkable(wall,true).size() >= 2){
                cells[wall.x][wall.y] = new Percorso(wall.x,wall.y);
                int delta = unreachablePaths();
                if(delta < best){
                    System.out.println("best: "+best +" delta: "+delta);
                    best = delta;
                    if(best == 0) break;
                }else
                    cells[wall.x][wall.y] = new Muro(wall.x,wall.y);
            }
        }
    }

    public void setGridForIRK(){
        for(int i=0;i<h;i++)
            for(int j=0;j<w;j++)
                if(!cells[i][j].type().isLimite())
                    if(i%2 == 1 && j%2 == 1)
                        cells[i][j] = new Percorso(i,j);

    }

    public List<CellVector> middleDistancesInizioToFine(){
        var se = getAllOfTypes(MazeCellType.INIZIO_FINE);
        return middleDistancesPointToPoint(se.get(0),se.get(1));
    }

    public List<CellVector> middleDistancesPointToPoint(MazeCell p1,MazeCell p2){
        List<CellVector> d1 = distancesFrom(p1), d2 = distancesFrom(p2),
                dists = new ArrayList<>();
        for(CellVector sd1 : d1)
            for(CellVector sd2 : d2)
                if(sd1.cell.equals(sd2.cell))
                    dists.add(new CellVector(sd1.cell,sd1.d[0],sd2.d[0]));
        dists.sort(Comparator.comparingInt(CellVector::diffPointByPoint));
        return dists;
    }

    public List<CellVector> distancesFrom(MazeCell cell){
        return distancesFrom(cell.x,cell.y);
    }

    public List<CellVector> distancesFrom(int x,int y){
        List<CellVector> d = new ArrayList<>(),
                queue = new ArrayList<>();
        Set<MazeCell> visited = new HashSet<>();
        queue.add(new CellVector(cellAt(x,y),0));
        while(!queue.isEmpty()){
            var cur = queue.remove(0);
            if(visited.add(cur.cell)){
                d.add(cur);
                for(MazeCell cell : viciniWalkable(cur.cell,true))
                    if(!visited.contains(cell))
                        queue.add(new CellVector(cell,cur.d[0]+1));
            }
        }
        d.sort(Comparator.comparingInt(CellVector::diffPointByPoint));
        return d;
    }

    //isSetAllConnected?

    public List<Set<MazeCell>> walkSets(){
        return walkSets(getAllWalkable(true));
    }

    public List<Set<MazeCell>> walkSets(List<MazeCell> walk){
        int d = walk.size();
        Set<MazeCell> visited = new HashSet<>();
        List<Set<MazeCell>> finalSets = new ArrayList<>();
        while(visited.size() != d && !walk.isEmpty()){
            Set<MazeCell> set = new HashSet<>();
            List<MazeCell> queue = new ArrayList<>();
            queue.add(walk.remove(0));
            while(!queue.isEmpty()){
                MazeCell cur = queue.remove(0);
                if(visited.add(cur)){
                    set.add(cur);
                    for(MazeCell cell : viciniWalkable(cur,true))
                        if(!visited.contains(cell)) queue.add(cell);
                }
            }
            if(!set.isEmpty())
                finalSets.add(set);
        }
        walkSets.clear();
        walkSets.addAll(finalSets);
        return walkSets;
    }

    public Set<MazeCell> oppNoWalk2Set(Set<MazeCell> walkSet){
        if(walkSet.size() < 30) return null;
        var oppNoWalkSet = walkSet.stream().filter(this::isOppNoWalk2)
                .collect(Collectors.toSet());
        oppNoWalkSet.removeIf(e -> !e.type().isPercorso());
        System.out.println(oppNoWalkSet);
        return oppNoWalkSet;
    }

    public boolean isOppNoWalk2(int x,int y){
        return isOppNoWalk2(cellAt(x,y));
    }

    public boolean isOppNoWalk2(MazeCell cell){
        var viciniWalk = viciniFilter(cell,MazeCellType.PERCORSO,MazeCellType.INIZIO_FINE);
        var viciniNoWalk = viciniFilter(cell,MazeCellType.MURO,MazeCellType.LIMITE);
        if(viciniWalk.size() == 2 && viciniNoWalk.size() == 2){
            return viciniWalk.get(0).x == viciniWalk.get(1).x ||
                    viciniWalk.get(0).y == viciniWalk.get(1).y;
        }else return false;
    }

    public boolean isNoWalk3(int x,int y){
        return isNoWalk3(cellAt(x,y));
    }

    public boolean isNoWalk3(MazeCell cell){
        var viciniWalk = viciniFilter(cell,MazeCellType.PERCORSO,MazeCellType.INIZIO_FINE);
        var viciniNoWalk = viciniFilter(cell,MazeCellType.MURO,MazeCellType.LIMITE);
        return viciniWalk.size() == 1 && viciniNoWalk.size() == 3;
    }

}
