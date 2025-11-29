package com.flavio.rognoni.purgatory.purgatory;

import com.flavio.rognoni.purgatory.purgatory.mazes.mazeGenerators.CellularAutomata2D;

import java.util.HashMap;
import java.util.Set;

public class Script {

    public static void main(String[] args){
        try{
            CellularAutomata2D ca2d = new CellularAutomata2D(100,100,
                    CellularAutomata2D.MOORE_TYPE,1, Set.of(0,1),
                    "0,1,3,1;1,0,6-n,0;1,0,0,0",
                    new HashMap<>(){{
                        put(1,"50,50;49,50");
                    }},
                    0
            );
            //System.out.println(ca2d.vicini(0,0));
            System.out.println(ca2d);
        }catch (Exception e){

        }
    }

}
