package com.flavio.rognoni.purgatory.purgatory;

import com.flavio.rognoni.purgatory.purgatory.mazes.HyperMaze;
import com.flavio.rognoni.purgatory.purgatory.mazes.Maze;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.Porta;

import java.io.InputStream;
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
            InputStream is = App.class.getResourceAsStream("labirinti/hm1.xml");
            Maze maze = Maze.mazeFromXML(is);
            is = App.class.getResourceAsStream("labirinti/hm2.xml");
            Maze maze2 = Maze.mazeFromXML(is);
            is = App.class.getResourceAsStream("labirinti/hm3.xml");
            Maze maze3 = Maze.mazeFromXML(is);
            is = App.class.getResourceAsStream("labirinti/hm4.xml");
            Maze maze4 = Maze.mazeFromXML(is);
            if(maze != null && maze2 != null && maze3 != null && maze4 != null){
                HyperMaze hyperMaze = new HyperMaze(5,List.of(
                        maze,maze2,maze3,maze4,maze,
                        maze2,maze3,maze4,maze,maze2,
                        maze3,maze4,maze,maze2,maze3,
                        maze4,maze,maze2,maze3,maze4,
                        maze,maze2,maze3,maze4,maze),
                        1,0,20,99);
                Maze m = hyperMaze.getMaze();
                System.out.println(m);
                Maze.mazeToXML(m,null);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
