package com.flavio.rognoni.purgatory.purgatory.mazes;


import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class Maze {

    public final int h,w;
    public final MazeSquare[][] matrix;

    public Maze(int h,int w){
        this.h = h;
        this.w = w;
        this.matrix = new MazeSquare[h][w];
        for(int i=0;i<h;i++){
            for(int j=0;j<w;j++){
                matrix[i][j]=new MazeSquare(i,j,
                        (isLimit(i,j)) ? MazeSquare.LIMIT : MazeSquare.WALL);
            }
        }
    }

    public MazeSquare getCellAt(int x, int y){
        if(x < 0 || x >= h) return null;
        if(y < 0 || y >= w) return null;
        return matrix[x][y];
    }

    public MazeSquare getDeafultInit(){
        return matrix[h-2][1];
    }

    public void setTypeAt(int x,int y,int type){
        matrix[x][y] = matrix[x][y].changeType(type);
    }

    public boolean isLimit(MazeSquare pos){
        return isLimit(pos.x,pos.y);
    }

    public boolean isLimit(int x,int y){
        return x == 0 || y == 0 || x == h-1 || y == w-1;
    }

    public List<MazeSquare> vicini(MazeSquare pos){
        List<MazeSquare> l = new ArrayList<>();
        if(pos.x + 1 < h) l.add(matrix[pos.x+1][pos.y]);
        if(pos.x - 1 >= 0) l.add(matrix[pos.x-1][pos.y]);
        if(pos.y + 1 < w) l.add(matrix[pos.x][pos.y+1]);
        if(pos.y - 1 >= 0) l.add(matrix[pos.x][pos.y-1]);
        return l;
    }

    public List<MazeSquare> viciniNotLimit(MazeSquare pos){
        return vicini(pos).stream()
                .filter(p -> !p.isLimit())
                .collect(Collectors.toList());
    }

    public List<MazeSquare> viciniPath(MazeSquare pos){
        return vicini(pos).stream()
                .filter(p -> p.isPath() || p.isStartEnd())
                .collect(Collectors.toList());
    }

    public List<MazeSquare> viciniWall(MazeSquare pos){
        return vicini(pos).stream()
                .filter(MazeSquare::isWall)
                .collect(Collectors.toList());
    }

    public MazeSquare mostDistanceFrom(MazeSquare pos){
        int d = Integer.MIN_VALUE;
        MazeSquare mostDist = null;
        for(MazeSquare[] i : matrix){
            for(MazeSquare p : i){
                if(p.isPath() && pos.manhattanDistance(p) >= d){
                    d = pos.manhattanDistance(p);
                    mostDist = p;
                }
            }
        }
        return mostDist;
    }

    public int unreacheablePaths(){
        List<MazeSquare> paths = new ArrayList<>(),
                start = new ArrayList<>();
        for(MazeSquare[] i : matrix) {
            for(MazeSquare p : i) {
                if(p.isPath())
                    paths.add(p);
                else if(p.isStartEnd())
                    start.add(p);
            }
        }
        if(paths.isEmpty() || start.isEmpty())
            return -1;
        paths.addAll(start);
        Set<MazeSquare> visited = new HashSet<>();
        List<MazeSquare> path = new ArrayList<>();
        path.add(start.get(0));
        while(!path.isEmpty()){
            MazeSquare curr = path.remove(0);
            if(!visited.contains(curr)){
                visited.add(curr);
                path.addAll(viciniPath(curr));
            }
        }
        return Math.abs(visited.size()-paths.size());
    }

    public boolean isAllReachable(){
        return unreacheablePaths() == 0;
    }

    public void fixMaze(){ // funziona ma va ottimizzato (riconsiderare anche vecchi muri in ciclo while esterno?)
        var walls = getAllWalls();
        System.out.println(walls);
        int best = unreacheablePaths();
        while(!walls.isEmpty()){
            var wall = walls.remove(0);
            System.out.println(wall);
            if(viciniPath(wall).size() >= 2) {
                setTypeAt(wall.x,wall.y,MazeSquare.PATH);
                //System.out.println(matrix[wall.x][wall.y]);
                int delta = unreacheablePaths();
                if(delta < best){
                    System.out.println("best: "+best +" delta: "+delta);
                    best = delta;
                    if(best == 0) break;
                }else{
                    setTypeAt(wall.x,wall.y,MazeSquare.WALL);
                }
            }
        }
    }

    public List<MazeSquare> getAllWalls(){
        List<MazeSquare> walls = new ArrayList<>();
        for(MazeSquare[] i : matrix)
            for(MazeSquare j : i)
                if(j.isWall()) walls.add(j);
        return walls;
    }

    public List<MazeSquare> getAllPaths(){
        List<MazeSquare> walls = new ArrayList<>();
        for(MazeSquare[] i : matrix)
            for(MazeSquare j : i)
                if(j.isPath() || j.isStartEnd())
                    walls.add(j);
        return walls;
    }

    public List<MazeSquare> getAllStartEnd(){
        List<MazeSquare> walls = new ArrayList<>();
        for(MazeSquare[] i : matrix)
            for(MazeSquare j : i)
                if(j.isStartEnd())
                    walls.add(j);
        return walls;
    }

    public List<MazeSquare> getAllDoors(){
        List<MazeSquare> walls = new ArrayList<>();
        for(MazeSquare[] i : matrix)
            for(MazeSquare j : i)
                if(j.isDoor())
                    walls.add(j);
        return walls;
    }

    public List<MazeSquare> getAllObjectOf(int type){
        List<MazeSquare> walls = new ArrayList<>();
        for(MazeSquare[] i : matrix)
            for(MazeSquare j : i)
                if(j.type == type)
                    walls.add(j);
        return walls;
    }

    public List<MazeSquare> getAllObjects(){
        List<MazeSquare> walls = new ArrayList<>();
        for(MazeSquare[] i : matrix)
            for(MazeSquare j : i)
                if(j.isSwitch() || j.isTreasure() || j.isTrap())
                    walls.add(j);
        return walls;
    }

    public List<MazeSquare> getAllInvWalls(){
        List<MazeSquare> walls = new ArrayList<>();
        for(MazeSquare[] i : matrix)
            for(MazeSquare j : i)
                if(j.isInvWall())
                    walls.add(j);
        return walls;
    }

    public List<MazeSquare> getAllTeles(){
        List<MazeSquare> walls = new ArrayList<>();
        for(MazeSquare[] i : matrix)
            for(MazeSquare j : i)
                if(j.isTeleport())
                    walls.add(j);
        return walls;
    }

    public void setGridForIRK(){
        for(int i=0;i<h;i++){
            for(int j=0;j<w;j++){
                if(!matrix[i][j].isLimit()){
                    if(i%2 == 1 && j%2 == 1){
                        setTypeAt(i,j,MazeSquare.PATH);
                    }
                }
            }
        }
    }

    //middleDistancePointToPoint

    public MazeSquare middleDistanceStartEnd(double d, int qty){
        if(!isAllReachable()) return null;
        if(d < 0.0 || d > 1.0) d = 1.0;
        if(qty < 0 || qty > 10) qty = 5;
        List<MazeSquare> startEnd = getAllStartEnd();
        List<SquareDist> dI = distancesFrom(startEnd.get(0)),
                dF = distancesFrom(startEnd.get(1));
        dI.removeIf(sd -> sd.square.isStartEnd());
        dF.removeIf(sd -> sd.square.isStartEnd());
//        System.out.println(dI+"\n"+dF);
//        System.out.println(dI.size()+"\n"+dF.size());
        Collections.reverse(dF);
        List<SquareMiddleDist> dists = new ArrayList<>();
        for(SquareDist sd1 : dI)
            for(SquareDist sd2 : dF)
                if(sd1.square.equals(sd2.square))
                    dists.add(new SquareMiddleDist(sd1.square,sd1.d,sd2.d));
        if(dists.isEmpty()) return null;
        dists.sort(Comparator.comparingInt(SquareMiddleDist::diff));
        dists = dists.subList(0,Math.min(qty,dists.size()));
        Collections.reverse(dists);
        System.out.println(dists);
        if(d == 0.0) return dists.get(0).square;
        int idx = (int) ((double) dists.size() * d);
        return dists.get(idx-1).square;
    }

    //isSetAllConnected

    public List<SquareDist> distancesFrom(MazeSquare square){
        return distancesFrom(square.x,square.y);
    }

    public List<SquareDist> distancesFrom(int x,int y){
        List<SquareDist> d = new ArrayList<>(),
                queue = new ArrayList<>();
        Set<MazeSquare> visited = new HashSet<>();
        queue.add(new SquareDist(getCellAt(x,y),0));
        while(!queue.isEmpty()){
            var cur = queue.remove(0);
            if(!visited.contains(cur.square)){
                visited.add(cur.square);
                d.add(cur);
                for(MazeSquare ms : viciniPath(cur.square)){
                    if(!visited.contains(ms)){
                        queue.add(new SquareDist(ms,cur.d+1));
                    }
                }
            }
        }
        Collections.sort(d);
        return d;
    }

    public List<Set<MazeSquare>> pathSets(){
        return pathSets(getAllPaths());
    }

    public List<Set<MazeSquare>> pathSets(List<MazeSquare> paths){
        int d = paths.size();
        Set<MazeSquare> visited = new HashSet<>();
        List<Set<MazeSquare>> finalSets = new ArrayList<>();
        while(visited.size() != d && !paths.isEmpty()){
            Set<MazeSquare> set = new HashSet<>();
            List<MazeSquare> queue = new ArrayList<>();
            queue.add(paths.remove(0));
            while(!queue.isEmpty()){
                MazeSquare cur = queue.remove(0);
                if(!visited.contains(cur)){
                    visited.add(cur);
                    set.add(cur);
                    for(MazeSquare vicino : viciniPath(cur)){
                        if(!visited.contains(vicino))
                            queue.add(vicino);
                    }
                }
            }
            if(!set.isEmpty())
                finalSets.add(set);
        }
        return finalSets;
    }

    public Set<MazeSquare> oppWall2Set(Set<MazeSquare> pathSet){
        if(pathSet.size() < 30) return null;
        var doorSet = pathSet.stream().filter(this::isOppWall2)
                .collect(Collectors.toSet());
        doorSet.removeIf(MazeSquare::isStartEnd);
        System.out.println(doorSet);
        return doorSet;
    }

    public MazeSquare bestDoor(double d, Set<MazeSquare> pathSet){
        if(pathSet == null) return null;
        if(d < 0.0 || d > 1.0) d = 1.0;
        var dSet = oppWall2Set(pathSet);
        if(dSet == null) return null;
        List<SquareDist> sepa = new ArrayList<>();
        int c = 0;
        for(MazeSquare ms : dSet){
            setTypeAt(ms.x,ms.y,MazeSquare.PORTA);
            List<MazeSquare> lis = new ArrayList<>(pathSet);
            lis.remove(ms);
            var sets = pathSets(lis);
            System.out.println(ms+" "+sets.size()+" "+c+"/"+dSet.size());
            if(sets.size() == 2){
                //System.out.println(sets);
                sepa.add(new SquareDist(ms,Math.abs(sets.get(0).size()-sets.get(1).size())));
            }
            setTypeAt(ms.x,ms.y,MazeSquare.PATH);
            c++;
        }
        if(!sepa.isEmpty()) {
            Collections.sort(sepa);
            Collections.reverse(sepa);
        }else return null;
        System.out.println(sepa);
        if(d == 0.0) return sepa.get(0).square;
        int idx = (int) ((double) sepa.size() * d);
        return sepa.get(idx-1).square;
    }

    public Set<MazeSquare> wall3Set(Set<MazeSquare> pathSet){
        if(pathSet.size() < 30) return null;
        var ittSet = pathSet.stream().filter(this::isWall3)
                .collect(Collectors.toSet());
        ittSet.removeIf(MazeSquare::isStartEnd);
        return ittSet;
    }

    public List<MazeSquare> bestITT(Set<MazeSquare> pathSet,int n,int type){ // sta per interruttori, tesori, trappole
        if(type < MazeSquare.INTERRUTTORE || type > MazeSquare.TRAPPOLA)
            return null;
        var ittSet = wall3Set(pathSet);
        if(ittSet == null || ittSet.isEmpty())
            return null;
        System.out.println("wall3Set: "+ittSet.size()+" "+ittSet);
        n = Math.min(n,ittSet.size());
        Random rand = new Random();
        var ittL = new ArrayList<>(ittSet);
        var objCells = new ArrayList<MazeSquare>();
        for(int i=0;i<n;i++)
            objCells.add(ittL.remove(rand.nextInt(ittL.size())));
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
                        if(!ittL.isEmpty()){
                            var randRm = objCells.remove(rand.nextInt(objCells.size()));
                            var randAdd = ittL.remove(rand.nextInt(ittL.size()));
                            objCells.add(randAdd);
                            ittL.add(randRm);
                        }else{
                            break;
                        }
                    }
                }else{
                    diff = nDiff;
                    bestCells.clear();
                    bestCells.addAll(objCells);
                    if(!ittL.isEmpty()){
                        var randRm = objCells.remove(rand.nextInt(objCells.size()));
                        var randAdd = ittL.remove(rand.nextInt(ittL.size()));
                        objCells.add(randAdd);
                        ittL.add(randRm);
                    }else{
                        break;
                    }
                }
            }
        }
        return bestCells;
    }

    public List<MazeSquare> randomInvWalls(Set<MazeSquare> pathSet,int n){
        var opp2Set = oppWall2Set(pathSet);
        if(opp2Set == null) return null;
        n = Math.min(n,opp2Set.size());
        List<MazeSquare> l = new ArrayList<>(opp2Set),
                ris = new ArrayList<>();
        Random rand = new Random();
        for(int i=0;i<n;i++)
            ris.add(l.remove(rand.nextInt(l.size())));
        return ris;
    }

    public MazeSquare[] randomTeleport(Set<MazeSquare> pathSetA,
                                           Set<MazeSquare> pathSetB){
        if(isIntersection(pathSetA,pathSetB)) return null;
        MazeSquare[] teleports = new MazeSquare[2];
        Random rand = new Random();
        var wall3A = wall3Set(pathSetA);
        var wall3B = wall3Set(pathSetB);
        if(wall3A == null || wall3A.isEmpty() ||
                wall3B == null || wall3B.isEmpty()) return null;
        var wall3AL = new ArrayList<>(wall3A);
        var wall3BL = new ArrayList<>(wall3B);
        teleports[0] = wall3AL.get(rand.nextInt(wall3AL.size()));
        teleports[1] = wall3BL.get(rand.nextInt(wall3BL.size()));
        return teleports;
    }

    private boolean isIntersection(Set<MazeSquare> pathSetA,
                                   Set<MazeSquare> pathSetB){
        Set<MazeSquare> set = new HashSet<>(pathSetA);
        set.retainAll(pathSetB);
        return !set.isEmpty();
    }

    public boolean isOppWall2(int x,int y){
        return isOppWall2(getCellAt(x,y));
    }

    public boolean isOppWall2(MazeSquare ms){
        var viciniP = viciniPath(ms);
        var viciniW = viciniWall(ms);
        if(viciniP.size() == 2 && viciniW.size() == 2){
            return viciniP.get(0).x == viciniP.get(1).x ||
                    viciniP.get(0).y == viciniP.get(1).y;
        }else return false;
    }

    public boolean isWall3(int x,int y){
        return isWall3(getCellAt(x,y));
    }

    public boolean isWall3(MazeSquare ms){
        var viciniP = viciniPath(ms);
        var viciniW = viciniWall(ms);
        return viciniP.size() == 1 && viciniW.size() == 3;
    }

    @Override
    public String toString() {
        String s = "h:"+h+",w:"+w+"\n";
        for(int i=0;i<h;i++){
            for(int j=0;j<w;j++){
                s += matrix[i][j] + " ";
            }
            s+="\n";
        }
        return s;
    }

    public Maze copy() {
        Maze m = new Maze(h,w);
        for(int i=0;i<h;i++){
            for(int j=0;j<w;j++){
                var cell = matrix[i][j];
                m.setTypeAt(i,j,cell.type);
            }
        }
        return m;
    }

    public static void mazeToXML(Maze maze){
        try{
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();
            Element element = doc.createElement("Maze");
            doc.appendChild(element);
            Attr hAttr = doc.createAttribute("h"),
                    wAttr = doc.createAttribute("w");
            hAttr.setValue(maze.h+"");
            wAttr.setValue(maze.w+"");
            element.setAttributeNode(hAttr);
            element.setAttributeNode(wAttr);
            //element.appendChild(doc.createTextNode("Maze x"));
            for(MazeSquare[] i : maze.matrix){
                for(MazeSquare j : i){
                    Element sqEl = doc.createElement("MazeSquare");
                    sqEl.setAttribute("x",j.x+"");
                    sqEl.setAttribute("y",j.y+"");
                    sqEl.setAttribute("type",j.type+"");
                    element.appendChild(sqEl);
                }
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"no");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(
                    "src/main/resources/com/flavio/rognoni/purgatory/purgatory/labirinti/"+
                            new Date().getTime()+"_maze.xml"));
            transformer.transform(source,result);
            StreamResult consoleResult = new StreamResult(System.out);
            transformer.transform(source,consoleResult);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static Maze mazeFromXML(String path){
        try {
            File file = new File("src/main/resources/com/flavio/rognoni/purgatory/" +
                    "purgatory/labirinti/1764713272395_maze.xml");
            // 1764540968716_maze.xml 20x20
            // 1764715136966_maze.xml 200x200
            // 1764541189706_maze.xml 100x100
            // 1764713272395_maze.xml 50x50
            // 1764936824966_maze.xml 20 x 30
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();
            Element root = (Element) doc.getElementsByTagName("Maze").item(0);
            int h = Integer.parseInt(root.getAttribute("h")),
                    w = Integer.parseInt(root.getAttribute("w"));
            Maze maze = new Maze(h,w);
            NodeList childs = root.getChildNodes();
            for(int i=0;i<childs.getLength();i++){
                if (childs.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element ms = (Element) childs.item(i);
                    int x = Integer.parseInt(ms.getAttribute("x")),
                            y = Integer.parseInt(ms.getAttribute("y")),
                            type = Integer.parseInt(ms.getAttribute("type"));
                    maze.setTypeAt(x,y,type);
                }
            }
            return maze;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
