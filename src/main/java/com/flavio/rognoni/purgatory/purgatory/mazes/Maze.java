package com.flavio.rognoni.purgatory.purgatory.mazes;

import com.flavio.rognoni.purgatory.purgatory.mazes.mazeGenerators.MazeGenType;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.*;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Maze {

    private String id;
    public final int h,w;
    public final MazeGenType genType;
    public final MazeCell[][] cells;
    public final List<Set<MazeCell>> walkSets;
    private final Random rand = new Random();

    public static final int MIN_DIM = 10, MAX_DIM = 200, MAX_H_DIM = 1000;

    public Maze(String id,int h, int w, MazeGenType type) throws Exception{
        if(h < MIN_DIM || h > MAX_H_DIM)
            throw new Exception("Invalid rows too less or too many [10,1000]");
        if(w < MIN_DIM || w > MAX_H_DIM)
            throw new Exception("Invalid columns too less or too many [10,1000]");
        this.id = id;
        this.h = h;
        this.w = w;
        this.genType = type;
        this.cells = new MazeCell[h][w];
        for(int i=0;i<h;i++)
            for(int j=0;j<w;j++)
                cells[i][j] = (isLimit(i,j)) ? new Limite(i,j) : new Muro(i,j);
        this.walkSets = new ArrayList<>();
        walkSets();
    }

    public Maze(int h, int w, MazeGenType type) throws Exception{
        this(null,h,w,type);
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

    public MazeCell defaultInit(){ return cells[1][0]; }

    public MazeCell defaultFinish(){ return cells[h-1][w-2]; }

    public void fixInizioFine(boolean inizio){
        MazeCell fi = (inizio) ? getInizio() : getFine();
        if(fi != null){
            var viciniP = viciniFilter(fi,MazeCellType.PERCORSO);
            var viciniM = viciniFilter(fi,MazeCellType.MURO);

            while(viciniP.isEmpty()){
                if(!viciniM.isEmpty()){
                    var muro = viciniM.get(0);
                    cells[muro.x][muro.y] = new Percorso(muro.x,muro.y);
                    fi = cells[muro.x][muro.y];
                    viciniP = viciniFilter(fi,MazeCellType.PERCORSO);
                    viciniM = viciniFilter(fi,MazeCellType.MURO);
                }
            }
        }
    }

    public List<MazeCell> getAllOfTypes(MazeCellType ...types){
        var l = new ArrayList<MazeCell>();
        for(MazeCell[] i : cells)
            for(MazeCell p : i)
                if(MazeCellType.isOneOfTheTypes(p.type(),types))
                    l.add(p);
        return l;
    }

    public List<MazeCell> getAllNotOfTypes(MazeCellType ...types){
        var l = new ArrayList<MazeCell>();
        for(MazeCell[] i : cells)
            for(MazeCell p : i)
                if(MazeCellType.isNotOneOfTheTypes(p.type(),types))
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

    public int unreachablePaths(){
        List<MazeCell> paths = getAllWalkable(true);
        MazeCell start = getInizio();
        if(paths.isEmpty() || start == null) return -1;
        if(paths.size() > 10000){
            int c = 0;
            for(MazeCell p : paths) {
                if(!viciniWalkable(p,true).isEmpty()) {
                    c++;
                    System.out.println("up 40k "+c+"/"+paths.size());
                }
            }
            return Math.abs(c-paths.size());
        }
        Set<MazeCell> visited = new HashSet<>();
        List<MazeCell> walk = new ArrayList<>();
        walk.add(start);
        while(!walk.isEmpty()){
            System.out.println("up "+visited.size()+"/"+paths.size());
            MazeCell cur = walk.remove(0);
            if(visited.add(cur))
                walk.addAll(viciniWalkable(cur,true));
        }
        return Math.abs(visited.size()-paths.size());
    }

    public boolean isAllReachable(){ return unreachablePaths() == 0; }

    public int unreachablePathsWithTele(){
        List<MazeCell> paths = getAllWalkable(true);
        MazeCell start = getInizio();
        if(paths.isEmpty() || start == null) return -1;
        if(paths.size() > 150000){
            int c = 0;
            for(MazeCell p : paths) {
                if(!viciniWalkable(p,true).isEmpty() ||
                        p.type().isTeletrasporto()) {
                    c++;
                    System.out.println("up "+c+"/"+paths.size());
                }
            }
            return Math.abs(c-paths.size());
        }
        Set<MazeCell> visited = new HashSet<>();
        List<MazeCell> walk = new ArrayList<>();
        walk.add(start);
        while(!walk.isEmpty()){
            MazeCell cur = walk.remove(0);
            if(visited.add(cur)) {
                walk.addAll(viciniWalkable(cur,true));
                if(cur.type().isTeletrasporto()) {
                    var t = (Teletrasporto) cur;
                    if(!t.noEndPoint())
                        walk.add(cellAt(t.ex,t.ey));
                }
            }
        }
        return Math.abs(visited.size()-paths.size());
    }

    public boolean isAllReachableWithTele(){ return unreachablePathsWithTele() == 0; }

    public void fixMaze(){ //funziona che sistema il labirinto in fase di creazione con l'automa cellulare
        int best = unreachablePaths();
        while(best != 0){
            var walls = getAllOfTypes(MazeCellType.MURO);
            System.out.println("altro giro");
            while(!walls.isEmpty()){
                var wall = walls.remove(0);
                if(viciniWalkable(wall,true).size() >= 2){
                    cells[wall.x][wall.y] = new Percorso(wall.x,wall.y);
                    int delta = unreachablePaths();
                    System.out.println(delta + " " + walls.size());
                    if(delta < best){
                        System.out.println("best: "+best +" delta: "+delta);
                        best = delta;
                        if(best == 0) break;
                    }else
                        cells[wall.x][wall.y] = new Muro(wall.x,wall.y);
                }
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

    public List<Set<MazeCell>> walkSets(){
        return walkSets(getAllWalkable(true));
    }

    public List<Set<MazeCell>> walkSets(List<MazeCell> walk){
        setInizioAsFirst(walk);
        int d = walk.size();
        Set<MazeCell> visited = new HashSet<>();
        List<Set<MazeCell>> finalSets = new ArrayList<>();
        while(visited.size() != d && !walk.isEmpty()){
            Set<MazeCell> set = new HashSet<>();
            List<MazeCell> queue = new ArrayList<>();
            queue.add(walk.remove(0));
            while(!queue.isEmpty()){
                System.out.println(visited.size()+"/"+d);
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

    private void setInizioAsFirst(List<MazeCell> walk){
        MazeCell inizio = null;
        for(MazeCell cell : walk){
            if(cell.type().isInizioFine()){
                var initF = (InizioFine) cell;
                if(initF.isStart) {
                    inizio = cell;
                    break;
                }
            }
        }
        if(inizio != null){
            walk.remove(inizio);
            walk.add(0,inizio);
        }
    }

    public Set<MazeCell> oppNoWalk2Set(Set<MazeCell> walkSet){
        if(walkSet.size() < 30) return null;
        var oppNoWalkSet = walkSet.stream().filter(this::isOppNoWalk2)
                .collect(Collectors.toSet());
        oppNoWalkSet.removeIf(e -> !e.type().isPercorso());
        //System.out.println(oppNoWalkSet);
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
        //System.out.println(noWalk3Set);
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
        //System.out.println(sepa);
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
        //System.out.println("wall3Set: "+noWalk3Set.size()+" "+noWalk3Set);
        n = Math.min(n,noWalk3Set.size());
        var nw3L = new ArrayList<>(noWalk3Set);
        var objCells = new ArrayList<MazeCell>();
        for(int i=0;i<n;i++)
            objCells.add(nw3L.remove(rand.nextInt(nw3L.size())));
        var bestCells = new ArrayList<>(objCells);
        //System.out.println(objCells+" "+bestCells);
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
                //System.out.println(nDiff + " " + objCells);
                System.out.println(nDiff);
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

    public List<MazeCell> randomNoWalk3(Set<MazeCell> walkSet, int n){
        var noWalk3Set = noWalk3Set(walkSet);
        if(noWalk3Set == null) return null;
        n = Math.min(n,noWalk3Set.size());
        List<MazeCell> l = new ArrayList<>(noWalk3Set),
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
        teleports[0] = new Teletrasporto(ta.x,ta.y,tb.x,tb.y); //questa rivedere
        teleports[1] = new Teletrasporto(tb.x,tb.y,ta.x,ta.y);
        return teleports;
    }

    private boolean isIntersection(Set<MazeCell> pathSetA,
                                   Set<MazeCell> pathSetB){
        Set<MazeCell> set = new HashSet<>(pathSetA);
        set.retainAll(pathSetB);
        return !set.isEmpty();
    }

    public boolean areCoordinateLimite(int x, int y){
        return x == 0 || x == h-1 || y == 0 || y == w-1;
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

    public boolean hasInizioOrFine(boolean inizio){
        return (inizio) ?
                getInizio() != null :
                getFine() != null;
    }

    public boolean hasInizioAndFine(){
        InizioFine i=getInizio(),f=getFine();
        return i != null && f != null &&
                isValidInizio(i.x, i.y) &&
                isValidFine(i.x, i.y, f.x, f.y);
    }

    public boolean isValidInizio(int x,int y){
        return isValidInizio(h,w,x,y);
    }

    public boolean isValidFine(int x,int y,int ex,int ey){
        if(isValidInizio(ex,ey))
            return x != ex || y != ey;
        return false;
    }

    public static boolean isValidInizio(int h,int w,int x,int y){
        if(x == 0 && y != 0 && y != w-1) return true;
        if(x == h-1 && y != 0 && y != w-1) return true;
        if(y == 0 && x != 0 && x != h-1) return true;
        return y == w-1 && x != 0 && x != h-1;
    }

    public static boolean isValidFine(int h,int w,int x,int y,int ex,int ey){
        if(isValidInizio(h,w,ex,ey))
            return x != ex || y != ey;
        return false;
    }

    public List<Interruttore> topoCorrectInterruttori(Porta porta,List<MazeCell> cellList){
        List<Interruttore> list = new ArrayList<>();
        for(MazeCell cell : cellList){
            if(cell.type().isInterruttore())
                list.add((Interruttore) cell);
            else return null;
        }
        return topoCorrectInterruttori(porta,list,topologicalOrderOfWalkSets());
    }

    public List<Interruttore> topoCorrectInterruttori(Porta porta,Map<Integer, List<Integer>> topoMap){
        return topoCorrectInterruttori(porta,new ArrayList<>(porta.interruttori),topoMap);
    }

    private List<Interruttore> topoCorrectInterruttori(Porta porta,List<Interruttore> list,
                                                       Map<Integer, List<Integer>> topoMap){
        var vicini = viciniFilter(porta,MazeCellType.PERCORSO);
        if(vicini.size() == 2){
            int setA = -1, setB = -1;
            Map<Interruttore,Integer> interrSetMap = new HashMap<>();
            for(int i=0;i<walkSets.size();i++){
                Set<MazeCell> set = walkSets.get(i);
                if(set.contains(vicini.get(0))) setA = i;
                else if(set.contains(vicini.get(1))) setB = i;
                for(Interruttore interruttore : list)
                    if(set.contains(interruttore))
                        interrSetMap.put(interruttore,i);
            }
            if(interrSetMap.size() != list.size())
                return null;
            int next = followInTopo(setA,setB,topoMap);
            for(Interruttore interr : list){
                int set = interrSetMap.get(interr);
                if(!isPrevInTopo(set,next,topoMap))
                    return null;
            }
        }else return null;
        return list;
    }

    //todo: ck porte con chiavi se vuoi farlo

    private int followInTopo(int a,int b,Map<Integer, List<Integer>> topoMap){
        if(isFollowInTopo(a,b,topoMap)) return b;
        else if(isFollowInTopo(b,a,topoMap)) return a;
        else return -1;
    }

    private boolean isFollowInTopo(int a,int b,Map<Integer, List<Integer>> topoMap){
        List<Integer> lA = topoMap.get(a), queue = new ArrayList<>();
        Set<Integer> set = new HashSet<>();
        if(lA != null && !lA.isEmpty()){ // a ha dei successori
            queue.add(a);
            while(!queue.isEmpty()){
                int cur = queue.remove(0);
                set.add(cur);
                for(Integer succ : topoMap.get(cur)){
                    if(succ == b)
                        return true;
                    if(!set.contains(succ))
                        queue.add(succ);
                }
            }
        }
        return false; // b non viene dopo a
    }

    private boolean isPrevInTopo(int a,int b,Map<Integer, List<Integer>> topoMap){
        return followInTopo(a,b,topoMap) == b;
    }

    private int rootOfTopo(Map<Integer, List<Integer>> topoMap){
        List<Integer> ks = new ArrayList<>(topoMap.keySet());
        Set<Integer> v = new HashSet<>();
        for(List<Integer> l : topoMap.values())
             v.addAll(l);
        ks.removeAll(v);
        if(!ks.isEmpty())
            return ks.get(0);
        else return -1;
    }

    public boolean validTeleports(){
        var tele = getAllOfTypes(MazeCellType.TELETRASPORTO);
        if(tele.size() % 2 != 0) return false;
        List<Teletrasporto> teleports = new ArrayList<>();
        for(MazeCell cell : tele) teleports.add((Teletrasporto) cell);
        for(Teletrasporto t : teleports){
            if(t.noEndPoint()) return false;
            else{
                Teletrasporto ep = (Teletrasporto) cellAt(t.ex,t.ey);
                System.out.println(ep);
                if(!(ep.ex == t.x && ep.ey == t.y))
                    return false;
            }
        }
        return true;
    }

    private boolean validPorte(){
        var port = getAllOfTypes(MazeCellType.PORTA);
        List<Porta> porte = new ArrayList<>();
        for(MazeCell cell : port) porte.add((Porta) cell);
        if(!porte.isEmpty()){
            var topoMap = topologicalOrderOfWalkSets();
            for(Porta p : porte){
                if(p.isInterruttori()){
                    if(topoCorrectInterruttori(p,topoMap) == null)
                        return false;
                }
                // todo: fare anche il check con i tesori con le chiavi?
                //  1. lista con tutti i tesori con chiavi
                //  2. mappa degli insiemi successivi alle porte con chivi per ogni porta
                //  3. mappa con porte a chiavi e liste di tesori negli insiemi precedenti
                //  4. apro le porte da quella più vicino all'inizio eliminando dalle liste i tesori già usati
                //  5. se la mappa è vuota (riesco ad aprire tutto) allora ritorno vero altrimeti falso
                p.openDoor();
            }
        }
        if(getAllOfTypes(MazeCellType.TELETRASPORTO).isEmpty() &&
                !isAllReachable())
            return false;
        else if(!isAllReachableWithTele()) {
            System.out.println("not tele");
            return false;
        }
        if(!porte.isEmpty())
            for(Porta p : porte) p.closeDoor();
        return true;
    }

    private boolean validITTO(){
        for(MazeCell cell : getAllOfTypes(MazeCellType.INTERRUTTORE,
                MazeCellType.TESORO,MazeCellType.TRAPPOLA)){
            if(cell.type().isInterruttore()) if(((Interruttore) cell).isOn()) return false;
            if(cell.type().isTesoro()) if(((Tesoro) cell).isTaken()) return false;
            if(cell.type().isTrappola()) if(((Trappola) cell).isActivated()) return false;
            if(cell.type().isOstacolo()) if(((Ostacolo) cell).isDisable()) return false;
        }
        return true;
    }

    public boolean isSolvable() throws Exception{
        if(!hasInizioAndFine())
            throw new Exception("Manca l'Inizio o la Fine del Labirinto o sono in posizioni errate");
        System.out.println("ok fi");
        if(!validTeleports())
            throw new Exception("Teletrasporti invalidi end points mancanti o errati");
        System.out.println("ok tele");
        if(!validPorte())
            throw new Exception("Porte con interruttori invalide o non possibile calpestare tutto con le porte aperte");
        System.out.println("ok porte");
        if(!validITTO())
            throw new Exception("Interruttori non disattivati, tesori presi o trappole attivate");
        System.out.println("ok itt");
        return true;
    }

    public Map<Integer,List<Integer>> topologicalOrderOfWalkSets(){
        Map<Integer,List<Integer>> topoMap = new HashMap<>();
        walkSets();
        for(int i=0;i<walkSets.size();i++)
            topoMap.put(i,new ArrayList<>());
        for(MazeCell cell : getAllOfTypes(MazeCellType.PORTA)){
            var vicini = viciniFilter(cell,MazeCellType.PERCORSO);
            if(vicini.size() == 2){
                int setA = -1, setB = -1;
                for(int i=0;i<walkSets.size();i++){
                    Set<MazeCell> set = walkSets.get(i);
                    if(set.contains(vicini.get(0))) setA = i;
                    else if(set.contains(vicini.get(1))) setB = i;
                }
                if(setA != -1 && setB != -1 && setA != setB){
                    var l = topoMap.get(setA);
                    l.add(setB);
                    l = topoMap.get(setB);
                    l.add(setA);
                }
            }
        }
        Map<Integer,List<Integer>> risTopoMap = new HashMap<>();
        List<Integer> queue = new ArrayList<>();
        queue.add(0);
        Set<Integer> tSet = new HashSet<>();
        while(!queue.isEmpty()){
            var cur = queue.remove(0);
            var l = topoMap.get(cur);
            tSet.add(cur);
            var nL = new ArrayList<>(l);
            nL.removeAll(tSet);
            risTopoMap.put(cur,nL);
            for(Integer next : l)
                if(!tSet.contains(next))
                    queue.add(next);
        }
        for(Integer k : topoMap.keySet())
            if(risTopoMap.get(k) == null && topoMap.get(k).isEmpty())
                risTopoMap.put(k,topoMap.get(k));
        return risTopoMap;
    }

    public boolean isValidTopoMap(Map<Integer,List<Integer>> topoMap){
        Set<Integer> set = new HashSet<>();
        for(Integer k : topoMap.keySet()){
            set.add(k);
            set.addAll(topoMap.get(k));
            for(Integer v : topoMap.get(k))
                if(!topoMap.containsKey(v))
                    return false;
        }
        return walkSets.size() == set.size();
    }

    public Set<MazeCell> wallSet(Set<MazeCell> walkSet,int nw){
        if(nw < 0 || nw > 4) nw = 2;
        int finalNw = nw;
        var oppNoWalkSet = walkSet.stream().filter(
                cell -> wallOf(cell, finalNw))
                .collect(Collectors.toSet());
        oppNoWalkSet.removeIf(e -> !e.type().isPercorso());
        //System.out.println(oppNoWalkSet);
        return oppNoWalkSet;
    }

    public boolean wallOf(MazeCell cell,int nw){
        var viciniNoWalk = viciniFilter(cell,MazeCellType.MURO,MazeCellType.LIMITE);
        return viciniNoWalk.size() == nw;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if(this.id == null)
            this.id = id;
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

    public Maze copy() throws Exception{
        Maze m = new Maze(h,w,genType);
        for(int i=0;i<h;i++)
            for(int j=0;j<w;j++)
                m.cells[i][j] = cells[i][j].copy();
        return m;
    }

    public static void mazeToXML(Maze maze,String fn){ // non usare nel jar
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH_mm_ss");
            fn = (fn != null) ? fn : maze.h+"x"+maze.w+" "+maze.genType.getNome()+" "+
                    sdf.format(new Date())+"_maze.xml";
            maze.setId(fn.replace(".xml",""));
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();
            Element element = doc.createElement("Maze");
            doc.appendChild(element);
            Attr hAttr = doc.createAttribute("h"),
                    wAttr = doc.createAttribute("w"),
                    gtAttr = doc.createAttribute("genType"),
                    idAttr = doc.createAttribute("id");
            hAttr.setValue(maze.h+"");
            wAttr.setValue(maze.w+"");
            gtAttr.setValue(maze.genType.ordinal()+"");
            idAttr.setValue(maze.id);
            element.setAttributeNode(hAttr);
            element.setAttributeNode(wAttr);
            element.setAttributeNode(gtAttr);
            element.setAttributeNode(idAttr);
            for(MazeCell[] i : maze.cells) {
                for(MazeCell j : i) {
                    System.out.println(j);
                    element.appendChild(j.toXMLElement(doc));
                }
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"no");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(
                    "src/main/resources/com/flavio/rognoni/purgatory/purgatory/labirinti/"+fn));
            transformer.transform(source,result);
            StreamResult consoleResult = new StreamResult(System.out);
            transformer.transform(source,consoleResult);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Maze mazeFromXML(InputStream is){
        try{
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);
            doc.getDocumentElement().normalize();
            Element root = (Element) doc.getElementsByTagName("Maze").item(0);
            int h = Integer.parseInt(root.getAttribute("h")),
                    w = Integer.parseInt(root.getAttribute("w")),
                    gType = Integer.parseInt(root.getAttribute("genType"));
            String id = root.getAttribute("id");
            Maze maze = new Maze(id,h,w,MazeGenType.values()[gType]);
            NodeList childs = root.getChildNodes();
            for(int i=0;i<childs.getLength();i++){
                if (childs.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) childs.item(i);
                    var cell = MazeCell.fromXMLElement(el);
                    if(cell == null) return null;
                    maze.cells[cell.x][cell.y] = cell;
                }
            }
            for(MazeCell[] i : maze.cells){
                for(MazeCell j : i){
                    if(j.type().isPorta()){
                        Porta p = (Porta) j;
                        if(p.type == Porta.PORTA_A_INTERRUTTORI){
                            List<Interruttore> interruttori = new ArrayList<>();
                            for(Interruttore interr : p.interruttori){
                                var intCell = maze.cells[interr.x][interr.y];
                                if(intCell.type().isInterruttore())
                                    interruttori.add((Interruttore) intCell);
                            }
                            maze.cells[p.x][p.y] = new Porta(p.x,p.y,p.isOpen(),interruttori);
                        }
                    }else if(j.type().isTeletrasporto()){
                        Teletrasporto t = (Teletrasporto) j;
                        maze.cells[t.x][t.y] = new Teletrasporto(t.x,t.y,t.ex,t.ey);
                    }
                }
            }
            return maze;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static Integer[] hwFromXML(InputStream is){
        try{
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);
            doc.getDocumentElement().normalize();
            Element root = (Element) doc.getElementsByTagName("Maze").item(0);
            int h = Integer.parseInt(root.getAttribute("h")),
                    w = Integer.parseInt(root.getAttribute("w"));
            return new Integer[]{h,w};
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
