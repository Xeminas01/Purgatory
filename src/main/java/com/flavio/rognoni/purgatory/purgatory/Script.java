package com.flavio.rognoni.purgatory.purgatory;

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
            for(int i=1;i<=1024;i++){
                System.out.println(i+" "+(Math.log(i)/Math.log(4)));
            }
        }catch (Exception e){

        }
    }

    private static boolean tautology(){ //1
        return true;
    }

    private static boolean contradiction(){ //2
        return false;
    }

    private static boolean prop(boolean a){ //3,4 sia per a che per b
        return a;
    }

    private static boolean not(boolean a){ //5,6 sia per a che per b
        return !a;
    }

    private static boolean and(boolean a,boolean b){ //7
        return a && b;
    }

    private static boolean nand(boolean a,boolean b){ //8
        return !(a && b);
    }

    private static boolean or(boolean a,boolean b){ //9
        return a || b;
    }

    private static boolean nor(boolean a,boolean b){ //10
        return !(a || b);
    }

    public static boolean imply(boolean a,boolean b){ //11
        return !a || b;
    }

    public static boolean nimply(boolean a,boolean b){ //12
        return !(!a || b);
    }

    public static boolean converse(boolean a,boolean b){ //13
        return a || !b;
    }

    public static boolean nconverse(boolean a,boolean b){ //14
        return !(a || !b);
    }

    public static boolean xor(boolean a,boolean b){ //15
        return a ^ b;
    }

    public static boolean iff(boolean a,boolean b){ //16
        return a == b;
    }

}
