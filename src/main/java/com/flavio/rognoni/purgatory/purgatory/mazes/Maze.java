package com.flavio.rognoni.purgatory.purgatory.mazes;


import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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

    public MazeSquare atDistanceOf(double d){
        if(d < 0.0 || d > 1.0) d = 1.0;
        List<MazeSquare> startEnd = getAllStartEnd();
        List<SquareDist> dI = distancesFrom(startEnd.get(0)),
                dF = distancesFrom(startEnd.get(1));
//        System.out.println(dI+"\n"+dF);
//        System.out.println(dI.size()+"\n"+dF.size());
        Collections.reverse(dF);
        List<SquareMiddleDist> dists = new ArrayList<>();
        for(SquareDist sd1 : dI)
            for(SquareDist sd2 : dF)
                if(sd1.square.equals(sd2.square))
                    dists.add(new SquareMiddleDist(sd1.square,sd1.d,sd2.d));
        Collections.sort(dists);
        System.out.println(dists);
        if(d == 0.0) return dists.get(0).square;
        int idx = (int) ((double) dists.size() * d);
        return dists.get(idx-1).square;
    }

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

    private MazeSquare localBridgeAt(double d){
        //todo: implementare deve ritornare il path che convertito a wall crea la separazione a d% nella lista
        if(d < 0.0 || d > 1.0) d = 1.0;
        return null;
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

    public static Maze mazeFromXML(String path){ //todo: implementare l'import dei labirinti
        return null;
    }

}
