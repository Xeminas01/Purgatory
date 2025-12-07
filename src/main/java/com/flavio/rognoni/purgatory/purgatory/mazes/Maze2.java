package com.flavio.rognoni.purgatory.purgatory.mazes;

import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.*;

import java.util.*;
import java.util.stream.Collectors;

public class Maze2 {

    public final int h,w;
    public final MazeCell[][] cells;
    public final List<Set<MazeCell>> walkSets;
    private final Random rand = new Random();

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
        this.walkSets = new ArrayList<>();
        walkSets();
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
        return new ArrayList<>(vicini(cell).stream()
                .filter(p -> MazeCellType.isOneOfTheTypes(p.type(),types))
                .toList());
    }

    public List<MazeCell> viciniWalkable(MazeCell cell,boolean walkable){
        return new ArrayList<>(vicini(cell).stream()
                .filter(p -> p.isWalkable() == walkable)
                .toList());
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
                walk.addAll(viciniWalkable(cur,true));
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

    public Set<MazeCell> noWalk3Set(Set<MazeCell> walkSet){
        if(walkSet.size() < 30) return null;
        var noWalk3Set = walkSet.stream().filter(this::isNoWalk3)
                .collect(Collectors.toSet());
        noWalk3Set.removeIf(e -> !e.type().isPercorso());
        System.out.println(noWalk3Set);
        return noWalk3Set;
    }

    public boolean isNoWalk3(int x,int y){
        return isNoWalk3(cellAt(x,y));
    }

    public boolean isNoWalk3(MazeCell cell){
        var viciniWalk = viciniFilter(cell,MazeCellType.PERCORSO,MazeCellType.INIZIO_FINE);
        var viciniNoWalk = viciniFilter(cell,MazeCellType.MURO,MazeCellType.LIMITE);
        return viciniWalk.size() == 1 && viciniNoWalk.size() == 3;
    }

    public MazeCell bestSeparatorOpp2(double d,Set<MazeCell> walkSet){
        if(walkSet == null) return null;
        if(d < 0.0 || d > 1.0) d = 1.0;
        var dSet = oppNoWalk2Set(walkSet);
        if(dSet == null) return null;
        List<CellVector> sepa = new ArrayList<>();
        int c = 0;
        for(MazeCell cell : dSet){
            MazeCell tmp = cells[cell.x][cell.y];
            cells[cell.x][cell.y] = new Muro(cell.x,cell.y);
            List<MazeCell> lis = new ArrayList<>(walkSet);
            lis.remove(cell);
            var sets = walkSets(lis);
            System.out.println(cell+" "+sets.size()+" "+c+"/"+dSet.size());
            if(sets.size() == 2)
                sepa.add(new CellVector(cell,
                        Math.abs(sets.get(0).size()-sets.get(1).size())));
            cells[cell.x][cell.y] = tmp;
            c++;
        }
        if(!sepa.isEmpty()){
            sepa.sort(Comparator.comparingInt(CellVector::diffPointByPoint));
            Collections.reverse(sepa);
        }
        System.out.println(sepa);
        if(d == 0.0) return sepa.get(0).cell;
        int idx = (int) ((double) sepa.size() * d);
        return sepa.get(idx-1).cell;
    }

    public List<MazeCell> bestsNoWalks3(Set<MazeCell> walkSet,int n){
        if(walkSet == null || walkSet.isEmpty()) return null;
        if(n < 1 || n > 10) n = 1;
        var noWalk3Set = noWalk3Set(walkSet);
        if(noWalk3Set == null || noWalk3Set.isEmpty())
            return null;
        System.out.println("wall3Set: "+noWalk3Set.size()+" "+noWalk3Set);
        n = Math.min(n,noWalk3Set.size());
        var nw3L = new ArrayList<>(noWalk3Set);
        var objCells = new ArrayList<MazeCell>();
        for(int i=0;i<n;i++)
            objCells.add(nw3L.remove(rand.nextInt(nw3L.size())));
        var bestCells = new ArrayList<>(objCells);
        System.out.println(objCells+" "+bestCells);
        if(objCells.size() <= 1){
            return objCells;
        }else{
            int diff = Integer.MIN_VALUE, patience = 100;
            while(true){
                var l = new ArrayList<Integer>();
                for(int i=0;i<objCells.size()-1;i++)
                    for(int j=1;j<objCells.size();j++)
                        l.add(objCells.get(i).manhattanDistance(objCells.get(j)));
                int nDiff = 0;
                for(int i=0;i<l.size()-1;i++)
                    nDiff += Math.abs(l.get(i)-l.get(i+1));
                System.out.println(nDiff + " " + objCells);
                if(nDiff <= diff){
                    patience--;
                    if(patience <= 0)
                        break;
                    else{
                        if(!nw3L.isEmpty()){
                            var randRm = objCells.remove(rand.nextInt(objCells.size()));
                            var randAdd = nw3L.remove(rand.nextInt(nw3L.size()));
                            objCells.add(randAdd);
                            nw3L.add(randRm);
                        }else{
                            break;
                        }
                    }
                }else{
                    diff = nDiff;
                    bestCells.clear();
                    bestCells.addAll(objCells);
                    if(!nw3L.isEmpty()){
                        var randRm = objCells.remove(rand.nextInt(objCells.size()));
                        var randAdd = nw3L.remove(rand.nextInt(nw3L.size()));
                        objCells.add(randAdd);
                        nw3L.add(randRm);
                    }else{
                        break;
                    }
                }
            }
        }
        return bestCells;
    }

    public List<MazeCell> randomOppNoWalk2(Set<MazeCell> walkSet, int n){
        var opp2Set = oppNoWalk2Set(walkSet);
        if(opp2Set == null) return null;
        n = Math.min(n,opp2Set.size());
        List<MazeCell> l = new ArrayList<>(opp2Set),
                ris = new ArrayList<>();
        for(int i=0;i<n;i++)
            ris.add(l.remove(rand.nextInt(l.size())));
        return ris;
    }

    public Teletrasporto[] randomTeleports(Set<MazeCell> pathSetA,
                                           Set<MazeCell> pathSetB){
        if(isIntersection(pathSetA,pathSetB)) return null;
        Teletrasporto[] teleports = new Teletrasporto[2];
        var wall3A = noWalk3Set(pathSetA);
        var wall3B = noWalk3Set(pathSetB);
        if(wall3A == null || wall3A.isEmpty() ||
                wall3B == null || wall3B.isEmpty())
            return null;
        var wall3AL = new ArrayList<>(wall3A);
        var wall3BL = new ArrayList<>(wall3B);
        var ta = wall3AL.get(rand.nextInt(wall3AL.size()));
        var tb = wall3BL.get(rand.nextInt(wall3BL.size()));
        teleports[0] = new Teletrasporto(ta.x,ta.y,teleports[1]); //questa rivedere
        teleports[1] = new Teletrasporto(tb.x,tb.y,teleports[0]);
        return teleports;
    }

    private boolean isIntersection(Set<MazeCell> pathSetA,
                                   Set<MazeCell> pathSetB){
        Set<MazeCell> set = new HashSet<>(pathSetA);
        set.retainAll(pathSetB);
        return !set.isEmpty();
    }

    @Override
    public String toString() {
        String s = "h:"+h+",w:"+w+"\n";
        for(int i=0;i<h;i++){
            for(int j=0;j<w;j++){
                s += cells[i][j] + " ";
            }
            s+="\n";
        }
        return s;
    }

    public Maze2 copy() throws Exception{
        Maze2 m = new Maze2(h,w);
        for(int i=0;i<h;i++){
            for(int j=0;j<w;j++){
                var cell = cells[i][j];
                m.cells[i][j] = cell.copy();
            }
        }
        return m;
    }

//    public static void mazeToXML(Maze2 maze){
//
//    }
//
//    public static Maze2 mazeFromXML(String path){
//
//    }

}
