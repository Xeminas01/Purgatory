package com.flavio.rognoni.purgatory.purgatory.mazes.mazeGenerators;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CellularAutomata2D {

    private int distType,d;
    private Set<Integer> stati;
    private Map<Integer,Integer> birthRule,deathRule,survivalRule;
    private final static int MOORE_TYPE = 0, VON_NEUMANN_TYPE = 1;
    private final AutomataCell[][] cells;

    public CellularAutomata2D(int distType,int d,Set<Integer> stati,
                              Map<Integer,Integer> birthRule,
                              Map<Integer,Integer> deathRule,
                              Map<Integer,Integer> survivalRule,
                              int h,int w){
        // aggiungere mappa di liste di celle in certi stati all'inizio
        this.distType = distType;
        this.d = d;
        this.stati = stati;
        this.birthRule = birthRule;
        this.deathRule = deathRule;
        this.survivalRule = survivalRule;
        this.cells = new AutomataCell[h][w];
        //build cells
    }

    private Map<Integer,Integer> vicini(AutomataCell cell){
        Map<Integer,Integer> map = new HashMap<>();
        if(distType == MOORE_TYPE){

        }else if(distType == VON_NEUMANN_TYPE){

        }
        return map;
    }

    private static class AutomataCell{

        private final int x,y;
        private int state;

        public AutomataCell(int x,int y,int state){
            this.x = x;
            this.y = y;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        @Override
        public String toString() {
            return "Cell["+x+","+y+"]{"+state+"}";
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == this) return true;
            if(obj == null) return false;
            if(obj.getClass() != AutomataCell.class) return false;
            AutomataCell ac = (AutomataCell) obj;
            return x == ac.x && y == ac.y;
        }

        @Override
        public int hashCode() {
            return 0;
        }

    }

}
