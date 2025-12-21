package com.flavio.rognoni.purgatory.purgatory.dices;

import java.util.*;

public class Dado {

    public int min,max,range;
    private final Random rand;

    public Dado(int min,int max){
        if(max < min) max = min+1;
        this.min = min;
        this.max = max;
        this.range = Math.abs(min-max) + 1;
        this.rand = new Random();
    }

    public int roll(){
        return min + rand.nextInt(range);
    }

    @Override
    public String toString() {
        if(min == 1) return "d"+max;
        return "d["+min+","+max+"]";
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(!(obj instanceof Dado d)) return false;
        return min == d.max && max == d.max;
    }

    public static Map<Integer,Double> sumProbMap(int ...d) throws Exception{
        for(int i : d) if(i < 0)
            throw new Exception("only positive ranges");
        Map<Integer,Double> map = new HashMap<>();
        Map<Integer,Integer> sMap = new HashMap<>();
        double totalCases = 1;
        for(int i : d)
            totalCases *= i;
        List<List<Integer>> sums = new ArrayList<>();
        sums.add(new ArrayList<>());
        for(int i=0;i<d.length;i++){
            List<List<Integer>> tmp = new ArrayList<>();
            for(List<Integer> l : sums){
                for(int j=1;j<=d[i];j++){
                    List<Integer> cpy = new ArrayList<>(l);
                    cpy.add(j);
                    tmp.add(cpy);
                }
            }
            sums.clear();
            sums.addAll(tmp);
        }
        System.out.println(sums);
        for(List<Integer> sum : sums){
            int s = 0;
            for(Integer i : sum) s+=i;
            sMap.merge(s,1,Integer::sum);
        }
        for(Integer k : sMap.keySet())
            map.put(k,(double) sMap.get(k)/totalCases);
        return map;
    }

    public static Map<Integer,Double> sumProbMap2(int n,int d) throws Exception{
        Map<Integer,Double> sMap = new HashMap<>();
        for(int s=n;s<=n*d;s++){
            sMap.put(s,diceFormula(s,n,d));
        }
        return sMap;
    }

    public static double diceFormula(int s,int n,int d) throws Exception{
        int M = (int) (double) ((s - n) / d);
        int num = 0;
        for(int i=0;i<=M;i++){
            num += (int) (Math.pow(-1,i) * binomialCoefficient(n,i) *
                    binomialCoefficient(s-d*i-1,n-1));
        }
        return (double) num/Math.pow(d,n);
    }

    public static long binomialCoefficient(int n,int k) throws Exception{
        if(n >= k && k >= 0){
            if(k == 0 || k == n) return 1;
            if(k == 1 || k == n-1) return n;
            long num = 1;
            for(int i=0;i<k;i++)
                num *= (n-i);
            return num/fact(k);
        }else
            throw new Exception("k o n invalidi per il coefficiente binomiale");
    }

    public static int fact(int n){
        int s = 1;
        for(int i=1;i<=n;i++)
            s *= i;
        return s;
    }

}
