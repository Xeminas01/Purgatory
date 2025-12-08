package com.flavio.rognoni.purgatory.purgatory.mazes.mazeParts;

public abstract class MazeCell {

    public final int x,y;

    protected MazeCell(int x,int y){
        this.x = x;
        this.y = y;
    }

    public abstract MazeCellType type();
    public abstract boolean isWalkable();
    public abstract String color();
    public abstract MazeCell copy();
    public abstract MazeCell copyOf(int x,int y);

    public int manhattanDistance(MazeCell cell){
        return Math.abs(x - cell.x) + Math.abs(y - cell.y);
    }

    public String cStr() {
        return x+","+y+"["+type()+"]";
    }

    @Override
    public String toString() {
        return "Cell("+x+","+y+")["+type()+"]";
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(obj == null) return false;
        if(!(obj instanceof MazeCell mc)) return false;
        return mc.x == x && mc.y == y;
    }

    @Override
    public int hashCode() { return 0; }

}
