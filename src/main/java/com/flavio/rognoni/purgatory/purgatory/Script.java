package com.flavio.rognoni.purgatory.purgatory;

import com.flavio.rognoni.purgatory.purgatory.mazes.HyperMaze;
import com.flavio.rognoni.purgatory.purgatory.mazes.Maze;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.Porta;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Script {

    public static void main(String[] args){
        try{
//            CellularAutomata2D ca2d = new CellularAutomata2D(100,100,
//                    CellularAutomata2D.MOORE_TYPE,1, Set.of(0,1),
//                    "0,1,3,1;1,0,6-n,0;1,0,0,0",
//                    new HashMap<>(){{
//                        put(1,"50,50;49,50");
//                    }},
//                    0
//            );
//            //System.out.println(ca2d.vicini(0,0));
//            System.out.println(ca2d);
//            Maze maze = Maze.mazeFromXML("");
//            System.out.println(maze);
//            MazeCellType type = MazeCellType.LIMITE;
//            System.out.println(type.isMuro());
//            byte a = 1;
//            for(byte b = Byte.MIN_VALUE;b < Byte.MAX_VALUE;b++){
//                System.out.println("1 XOR "+b+" "+(a ^ b));
//            }
//            boolean[] ground = {false,true};
//            for(int i=0;i<ground.length;i++){
//                for(int j=0;j<ground.length;j++){
//                    boolean a = ground[i], b = ground[j];
//                    System.out.println("a="+a+"|b="+b+"|a IFF b="+iff(a,b));
//                }
//            }
//            for(int i=1;i<=1024;i++){
//                System.out.println(i+" "+(Math.log(i)/Math.log(4)));
//            }
//            InputStream is = App.class.getResourceAsStream("labirinti/hm1.xml");
//            Maze maze = Maze.mazeFromXML(is);
//            is = App.class.getResourceAsStream("labirinti/hm2.xml");
//            Maze maze2 = Maze.mazeFromXML(is);
//            is = App.class.getResourceAsStream("labirinti/hm3.xml");
//            Maze maze3 = Maze.mazeFromXML(is);
//            is = App.class.getResourceAsStream("labirinti/hm4.xml");
//            Maze maze4 = Maze.mazeFromXML(is);
//            InputStream is = App.class.getResourceAsStream("labirinti/200x200 DFS 17-12-2025 22_33_09_maze.xml");
//            Maze maze = Maze.mazeFromXML(is);
            //40x40 DFS 13-12-2025 15_32_19_maze.xml
//            InputStream is = App.class.getResourceAsStream("labirinti/test1010.xml");
//            Maze maze1010 = Maze.mazeFromXML(is);
//            is = App.class.getResourceAsStream("labirinti/test1020.xml");
//            Maze maze1020 = Maze.mazeFromXML(is);
//            is = App.class.getResourceAsStream("labirinti/test2010.xml");
//            Maze maze2010 = Maze.mazeFromXML(is);
//            is = App.class.getResourceAsStream("labirinti/test2020.xml");
//            Maze maze2020 = Maze.mazeFromXML(is);
//            is = App.class.getResourceAsStream("labirinti/testPorte.xml");
//            Maze mazePorte = Maze.mazeFromXML(is);
//            is = App.class.getResourceAsStream("labirinti/testSbocchi.xml");
//            Maze mazeSbocchi = Maze.mazeFromXML(is);
////            if(maze != null && maze2 != null && maze3 != null && maze4 != null){
////                HyperMaze hyperMaze = new HyperMaze(5,List.of(
////                        maze,maze2,maze3,maze4,maze,
////                        maze2,maze3,maze4,maze,maze2,
////                        maze3,maze4,maze,maze2,maze3,
////                        maze4,maze,maze2,maze3,maze4,
////                        maze,maze2,maze3,maze4,maze),
////                        1,0,20,99);
////                Maze m = hyperMaze.getMaze();
////                System.out.println(m);
////                Maze.mazeToXML(m,null);
////            }
//            if(maze1010 != null && maze1020 != null && maze2010 != null && maze2020 != null &&
//                    mazePorte != null && mazeSbocchi != null){
////                HyperMaze hyperMaze = new HyperMaze(5,List.of(
////                        maze,maze,maze,maze,maze,
////                        maze,maze,maze,maze,maze,
////                        maze,maze,maze,maze,maze,
////                        maze,maze,maze,maze,maze,
////                        maze,maze,maze,maze,maze),
////                        1,0,20,999);
////                HyperMaze hyperMaze = new HyperMaze(3,List.of(
////                        maze,maze,maze,
////                        maze,maze,maze,
////                        maze,maze,maze),
////                        1,0,20,599);
////                HyperMaze hyperMaze = new HyperMaze(3,List.of(
////                        maze2010,maze2010,maze2020,
////                        maze1010,maze1010,maze1020,
////                        maze1010,maze1010,maze1020),
////                        1,0,10,39);
//                HyperMaze hyperMaze = new HyperMaze(2,List.of(
//                        mazeSbocchi,mazeSbocchi,
//                        mazeSbocchi,mazeSbocchi),
//                        1,0,2,19);
//                Maze m = hyperMaze.getMaze();
//                System.out.println(m.h+" "+m.w);
//                Maze.mazeToXML(m,null);
//            }
//            List<String> fn = new ArrayList<>();
//            for(String f : new File("./src/main/resources/com/flavio/rognoni/purgatory/purgatory/labirinti").list()){
//                System.out.println(f);
//                fn.add(f);
//            }
//            System.out.println(fn);
//            for(String fileName : fn){
//                InputStream is = App.class.getResourceAsStream("labirinti/"+fileName);
//                Maze.tmpFixIdXML(is,fileName.replace(".xml",""));
//            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
