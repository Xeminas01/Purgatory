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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Maze {

    public final int h,w;
    public final MazeGenType genType;
    public final MazeCell[][] cells;
    public final List<Set<MazeCell>> walkSets;
    private final Random rand = new Random();

    public static final int MIN_DIM = 10, MAX_DIM = 200;

    // todo: controllare anche che inizio e fine siano nel limite,
    //  posizionabili solo nel limite e partibili dal limite, non angoli limite

    public Maze(int h, int w, MazeGenType type) throws Exception{
        if(h < MIN_DIM || h > MAX_DIM)
            throw new Exception("Invalid rows too less or too many [10,300]");
        if(w < MIN_DIM || w > MAX_DIM)
            throw new Exception("Invalid columns too less or too many [10,300]");
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
            //System.out.println(visited.size()+"/"+paths.size());
            MazeCell cur = walk.remove(0);
            if(visited.add(cur))
                walk.addAll(viciniWalkable(cur,true));
        }
        return Math.abs(visited.size()-paths.size());
    }

    public boolean isAllReachable(){ return unreachablePaths() == 0; }

    public int unreachablePathsWithTele(){
        List<MazeCell> paths = getAllWalkable(true),
                start = getAllOfTypes(MazeCellType.INIZIO_FINE);
        if(paths.isEmpty() || start.isEmpty()) return -1;
        Set<MazeCell> visited = new HashSet<>();
        List<MazeCell> walk = new ArrayList<>();
        walk.add(start.get(0));
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

    public boolean hasInizioAndFine(){
        var inF = getAllOfTypes(MazeCellType.INIZIO_FINE);
        if(inF.size() != 2) return false;
        InizioFine i = (InizioFine) inF.get(0),
                f = (InizioFine) inF.get(1),
                inizio = (i.isStart) ? i : f,
                fine = (!i.isStart) ? i : f;
        return inizio.isStart != fine.isStart;
    }

    public boolean hasInizioOrFine(boolean inizio){
        var inF = getAllOfTypes(MazeCellType.INIZIO_FINE);
        for(MazeCell cell : inF)
            if(((InizioFine) cell).isStart == inizio)
                return true;
        return false;
    }

    public List<Interruttore> topoCorrectInterruttori(Porta porta,List<MazeCell> cellList){
        List<Interruttore> list = new ArrayList<>();
        for(MazeCell cell : cellList){
            if(cell.type().isInterruttore())
                list.add((Interruttore) cell);
            else return null;
        }
        var topoMap = topologicalOrderOfWalkSets();
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

    public List<Interruttore> topoCorrectInterruttori(Porta porta,Map<Integer, List<Integer>> topoMap){
        List<Interruttore> list = new ArrayList<>(porta.interruttori);
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
        var topoMap = topologicalOrderOfWalkSets();
        for(Porta p : porte){
            if(p.isInterruttori()){
                if(topoCorrectInterruttori(p,topoMap) == null)
                    return false;
            }
            p.openDoor();
        }
        if(getAllOfTypes(MazeCellType.TELETRASPORTO).isEmpty() &&
                !isAllReachable())
            return false;
        else if(!isAllReachableWithTele()) {
            System.out.println("not tele");
            return false;
        }
        for(Porta p : porte) p.closeDoor();
        return true;
    }

    private boolean validITT(){
        for(MazeCell cell : getAllOfTypes(MazeCellType.INTERRUTTORE,
                MazeCellType.TESORO,MazeCellType.TRAPPOLA)){
            if(cell.type().isInterruttore()) if(((Interruttore) cell).isOn()) return false;
            if(cell.type().isTesoro()) if(((Tesoro) cell).isTaken()) return false;
            if(cell.type().isTrappola()) if(((Trappola) cell).isActivated()) return false;
        }
        return true;
    }

    public boolean isSolvable() throws Exception{
        if(!hasInizioAndFine())
            throw new Exception("Manca l'Inizio o la Fine del Labirinto");
        if(!validTeleports())
            throw new Exception("Teletrasporti invalidi end points mancanti o errati");
        if(!validPorte())
            throw new Exception("Porte con interruttori invalide o non possibile calpestare tutto con le porte aperte");
        if(!validITT())
            throw new Exception("Interruttori non disattivati, tesori presi o trappole attivate");
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

    public static void mazeToXML(Maze maze,String fn){
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();
            Element element = doc.createElement("Maze");
            doc.appendChild(element);
            Attr hAttr = doc.createAttribute("h"),
                    wAttr = doc.createAttribute("w"),
                    gtAttr = doc.createAttribute("genType");
            hAttr.setValue(maze.h+"");
            wAttr.setValue(maze.w+"");
            gtAttr.setValue(maze.genType.ordinal()+"");
            element.setAttributeNode(hAttr);
            element.setAttributeNode(wAttr);
            element.setAttributeNode(gtAttr);
            for(MazeCell[] i : maze.cells)
                for(MazeCell j : i)
                    element.appendChild(j.toXMLElement(doc));
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"no");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH_mm_ss");
            fn = (fn != null) ? fn : maze.h+"x"+maze.w+" "+maze.genType.getNome()+" "+
                    sdf.format(new Date())+"_maze.xml";
            StreamResult result = new StreamResult(new File(
                    "src/main/resources/com/flavio/rognoni/purgatory/purgatory/labirinti/"+fn));
            transformer.transform(source,result);
            StreamResult consoleResult = new StreamResult(System.out);
            transformer.transform(source,consoleResult);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static Maze mazeFromXML(String path){
        try{
            File file = new File(path);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();
            Element root = (Element) doc.getElementsByTagName("Maze").item(0);
            int h = Integer.parseInt(root.getAttribute("h")),
                    w = Integer.parseInt(root.getAttribute("w")),
                    gType = Integer.parseInt(root.getAttribute("genType"));
            Maze maze = new Maze(h,w,MazeGenType.values()[gType]);
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
//                        if(!t.noEndPoint()){
//                            var cell = maze.cells[t.ex][t.ey];
//                            if(cell.type().isTeletrasporto()){
//                                Teletrasporto ep = (Teletrasporto) cell;
//                                maze.cells[t.x][t.y] = new Teletrasporto(t.x,t.y,t.ex,t.ey);
//                            }
//                        }
                    }
                }
            }
            return maze;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
