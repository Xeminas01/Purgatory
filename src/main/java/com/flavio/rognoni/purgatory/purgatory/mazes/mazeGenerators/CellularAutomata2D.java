package com.flavio.rognoni.purgatory.purgatory.mazes.mazeGenerators;

import com.flavio.rognoni.purgatory.purgatory.mazes.Maze;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.InizioFine;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.MazeCellType;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.Muro;
import com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts.Percorso;

import java.util.*;

public class CellularAutomata2D {

    public int distType,d,h,w;
    public final Set<Integer> stati;
    private final Map<Rule,Integer> ruleMap;
    public final static int MOORE_TYPE = 0, VON_NEUMANN_TYPE = 1;
    private final AutomataCell[][] cells;
    private Map<Integer,String> initState;
    public final int defaultState;
    private int t;

    public CellularAutomata2D(int h,int w,int distType,int d,Set<Integer> stati,
                              String rules,Map<Integer,String> initState,
                              int defaultState) throws Exception{
        // aggiungere mappa di liste di celle in certi stati all'inizio
        if(h < 0 || w < 0)
            throw new Exception("Invalid grid dimensions");
        if(distType != MOORE_TYPE && distType != VON_NEUMANN_TYPE)
            throw new Exception("Invalid neighbor distance");
        if(d < 1 || d > 10)
            throw new Exception("Invalid distance radius");
        if(stati == null || stati.size() < 2 || invalidStates(stati))
            throw new Exception("Invalid or missing states");
        if(rules == null)
            throw new Exception("Missing rules");
        this.distType = distType;
        this.d = d;
        this.stati = Set.copyOf(stati);
        this.ruleMap = buildRuleMap(rules);
        this.h = h;
        this.w = w;
        this.cells = new AutomataCell[h][w];
        this.initState = initState;
        this.defaultState = ckDefaultState(stati,defaultState);
        for(int i=0;i<h;i++)
            for(int j=0;j<w;j++)
                this.cells[i][j] = new AutomataCell(i,j,this.defaultState);
        setInitState(false);
        this.t = 0;
    }

    private boolean invalidStates(Set<Integer> stati){
        for(Integer s : stati)
            if(s < 0)
                return true;
        return !stati.contains(0);
    }

    private Map<Rule,Integer> buildRuleMap(String rules){
        Map<Rule,Integer> rMap = new HashMap<>();
        String[] rulesS = rules.split(";");
        for(String r : rulesS){
            String[] rParts = r.split(",");
            if(rParts.length == 4){
                try{
                    int state = Integer.parseInt(rParts[0]),
                            nState = Integer.parseInt(rParts[1]),
                            start,end;
                    String[] range = rParts[2].split("-");
                    if(range.length <= 2){
                        if(range[0].equals("n")) start = maxVicini();
                        else start = Integer.parseInt(range[0]);
                        if(range.length == 2) {
                            if(range[1].equals("n")) end = maxVicini();
                            else end = Integer.parseInt(range[1]);
                        }
                        else end = start;
                        Rule rule = new Rule(state,nState,start,end);
                        int oState = Integer.parseInt(rParts[3]);
                        if(ruleConsistency(rMap,rule,oState)){
                            rMap.put(rule,oState);
                        }
                    }
                }catch (Exception e){

                }
            }
        }
        return Map.copyOf(rMap);
    }

    private boolean ruleConsistency(Map<Rule,Integer> rMap,Rule rule,int oState){
        if(!stati.contains(rule.state) || !stati.contains(rule.nState) ||
                !stati.contains(oState))
            return false;
        if(rule.startR < 0 || rule.endR > maxVicini())
            rule = new Rule(rule.state,rule.nState,0,maxVicini());
        for(Rule oldRule : rMap.keySet()){
            if(oldRule.notConsistentWith(rule))
                return false;
        }
        return true;
    }

    public int maxVicini(){
        if(distType == MOORE_TYPE)
            return (int) (Math.pow((2*d+1),2)-1);
        else
            return (int) (Math.pow(d,2) + Math.pow(d+1,2)) - 1;
    }

    private int ckDefaultState(Set<Integer> stati,
                                   int defualtState){
        return (stati.contains(defualtState)) ? defualtState :
                stati.stream().mapToInt(v -> v).min().orElse(0);
    }

    public Map<Integer,Integer> vicini(int x,int y){
        return vicini(cells[x][y]);
    }

    private Map<Integer,Integer> vicini(AutomataCell cell){
        Map<Integer,Integer> map = new HashMap<>();
        Set<AutomataCell> set = new HashSet<>();
        if(distType == MOORE_TYPE){
            for(int i=0;i<=d;i++){
                for(int j=1;j<=d;j++){
                    addCellsToSetMooreWay(set,cell,i,j);
                    addCellsToSetMooreWay(set,cell,j,i);
                }
            }
        }else if(distType == VON_NEUMANN_TYPE){
            for(int i=0;i<=d;i++){
                for(int j=1;j<=d;j++){
                    addCellsToSetVonNeumannWay(set,cell,i,j);
                    addCellsToSetVonNeumannWay(set,cell,j,i);
                }
            }
        }
        //System.out.println(set.size()+ " " +set);
        for(Integer stato : stati)
            map.put(stato,set.stream().filter(c -> c.state == stato).toList().size());
        return map;
    }

    private void addCellsToSetMooreWay(Set<AutomataCell> set,
                                       AutomataCell cell, int p, int s){
        if(getCellAt(cell.x+p,cell.y+s) != null)
            set.add(cells[cell.x+p][cell.y+s]);
        if(getCellAt(cell.x+p,cell.y-s) != null)
            set.add(cells[cell.x+p][cell.y-s]);
        if(getCellAt(cell.x-p,cell.y+s) != null)
            set.add(cells[cell.x-p][cell.y+s]);
        if(getCellAt(cell.x-p,cell.y-s) != null)
            set.add(cells[cell.x-p][cell.y-s]);
    }

    private void addCellsToSetVonNeumannWay(Set<AutomataCell> set,
                                       AutomataCell cell, int p, int s){
        if(getCellAt(cell.x+p,cell.y+s) != null && p+s <= d)
            set.add(cells[cell.x+p][cell.y+s]);
        if(getCellAt(cell.x+p,cell.y-s) != null && p+s <= d)
            set.add(cells[cell.x+p][cell.y-s]);
        if(getCellAt(cell.x-p,cell.y+s) != null && p+s <= d)
            set.add(cells[cell.x-p][cell.y+s]);
        if(getCellAt(cell.x-p,cell.y-s) != null && p+s <= d)
            set.add(cells[cell.x-p][cell.y-s]);
    }

    private void setInitState(boolean clean){
        if(clean){
            for(int i=0;i<h;i++)
                for(int j=0;j<w;j++)
                    this.cells[i][j].setState(defaultState);
        }
        for(Integer state : initState.keySet()){
            String[] cells = initState.get(state).split(";");
            for(String c : cells){
                String[] cCoords = c.split(",");
                if(cCoords.length == 2){
                    try{
                        int x = Integer.parseInt(cCoords[0]),
                                y = Integer.parseInt(cCoords[1]);
                        if(x >= 0 && x < h && y >= 0 && y <= w)
                            this.cells[x][y].setState(state);
                    }catch(Exception e){

                    }
                }
            }
        }
    }

    public void step(){
        Map<AutomataCell,Integer> nextStep = new HashMap<>();
        for(int i=0;i<h;i++){
            for(int j=0;j<w;j++){
                AutomataCell cell = cells[i][j];
                var viciniMap = vicini(cell);
                for(Rule r : ruleMap.keySet()){
                    if(r.state == cell.state){
                        int n = viciniMap.get(r.nState);
                        if(n >= r.startR && n <= r.endR){
                            nextStep.put(cell,ruleMap.get(r));
                            break;
                        }
                    }
                }
            }
        }
        for(AutomataCell cell : nextStep.keySet())
            cell.setState(nextStep.get(cell));
        t++;
    }

    public void reset(){
        t = 0;
        setInitState(true);
    }

    public int[][] getStateMatrix(){
        int[][] matrix = new int[h][w];
        for(int i=0;i<h;i++)
            for(int j=0;j<w;j++)
                matrix[i][j] = cells[i][j].state;
        return matrix;
    }

    public int getT() {
        return t;
    }

    public void setInitState(Map<Integer, String> initState) {
        this.initState = initState;
    }

    public Maze getMazeRender(){
        try{
            Maze maze = new Maze(h+2,w+2,MazeGenType.CELLULAR_GEN);
            for(int i=0;i<h;i++) {
                for(int j=0;j<w;j++){
                    if(cells[i][j].state == 0) maze.cells[i+1][j+1] = new Percorso(i+1,j+1);
                    else maze.cells[i+1][j+1] = new Muro(i+1,j+1);
                }
            }
            return maze;
        }catch (Exception e){
            return null;
        }
    }

    public Maze getMaze(int sx, int sy, int ex, int ey){
        try{
            Maze maze = getMazeRender();
            maze.cells[sx][sy] = new InizioFine(sx,sy,true);
            maze.cells[ex][ey] = new InizioFine(ex,ey,false);
            maze.fixInizioFine(true);
            maze.fixInizioFine(false);
            System.out.println(maze.getInizio() + " "+ maze.getFine());
            if(!maze.isAllReachable())
                maze.fixMaze();
            return maze;
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public String toString() {
        String s = "CellularAutomata2D on grid "+h+"x"+w+"\ndistType "+
                ((distType == MOORE_TYPE) ? "Moore" : "Von Neumann")+" dist "+d+
                "\nstati "+stati+"\nregole\n";
        for(Rule r : ruleMap.keySet())
            s += r +" -> "+ruleMap.get(r)+"\n";
        return s;
    }

    private AutomataCell getCellAt(int x, int y){
        if(x < 0 || x >= h || y < 0 || y >= w)
            return null;
        return cells[x][y];
    }

    public static String randomState(int h,int w,double density){
        Random rand = new Random();
        if(density < 0.0 || density > 1.0)
            density = 0.5;
        Set<String> cells = new HashSet<>();
        int limit = (int) (density*h*w);
        while(cells.size() < limit){
            int x = rand.nextInt(h),
                    y = rand.nextInt(w);
            String cell = x+","+y;
            cells.add(cell);
        }
        String s = "";
        for(String cell : cells) s+=cell+";";
        s = s.substring(0,s.length()-1);
        return s;
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

    private static class Rule {

        public final int state,nState,startR,endR;

        public Rule(int state,int nState,int startR,int endR){
            if(startR < 0 || endR < 0 || startR > endR){
                startR = 0;
                endR = 0;
            }
            this.state = state;
            this.nState = nState;
            this.startR = startR;
            this.endR = endR;
        }

        public boolean notConsistentWith(Rule rule){
            if(state == rule.state){
                Set<Integer> a = new HashSet<>(),
                        b = new HashSet<>();
                for(int i=startR;i<=endR;i++)
                    a.add(i);
                for(int i=rule.startR;i<=rule.endR;i++)
                    b.add(i);
                a.retainAll(b);
                return !a.isEmpty();
            }
            return false;
        }

        @Override
        public String toString() {
            return "Rule<"+state+","+nState+","+startR+"-"+endR+">";
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == this) return true;
            if(obj == null) return false;
            if(obj.getClass() != Rule.class) return false;
            Rule r = (Rule) obj;
            return state == r.state && nState == r.nState &&
                    startR == r.startR && endR == r.endR;
        }

        @Override
        public int hashCode() {
            return 0;
        }

    }

    public static CellularAutomata2D mazectric(int h,int w){
        try{
            return new CellularAutomata2D(h,w,
                    CellularAutomata2D.MOORE_TYPE,1, Set.of(0,1),
                    "0,1,3,1;1,1,5-n,0;1,1,0,0",
                    //Maze: "0,1,3,1;1,1,6-n,0;1,1,0,0" Mazectric: "0,1,3,1;1,1,5-n,0;1,1,0,0" Game of life: "1,1,0-1,0;1,1,4-n,0;0,1,3,1"
                    new HashMap<>(){{
                        put(1,CellularAutomata2D.randomState(h,w,0.5));
                        //aliante "50,50;50,49;50,51;49,51;48,50" barca "50,50;49,49;49,51;48,50;48,49"
                    }},
                    0
            );
        }catch(Exception e){
            return null;
        }
    }

}
