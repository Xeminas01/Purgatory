package com.flavio.rognoni.purgatory.purgatory.mazes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    public List<MazeSquare> viciniPath(MazeSquare pos){
        return vicini(pos).stream()
                .filter(p -> p.isPath() || p.isStartEnd())
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

    public boolean isAllReachable(){
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
            return false;
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
        System.out.println(Math.abs(visited.size()-paths.size()));
        return visited.size() == paths.size();
    }

    public void fixMaze(){
        var walls = getAllWalls();
        System.out.println(walls);
        while(!isAllReachable()){
            var wall = walls.remove(0);
            System.out.println(wall+" "+viciniPath(wall));
            if(viciniPath(wall).size() == 2) {
                setTypeAt(wall.x,wall.y,MazeSquare.PATH);
                System.out.println(matrix[wall.x][wall.y]);
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

}
