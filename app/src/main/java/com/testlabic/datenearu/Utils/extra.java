package com.testlabic.datenearu.Utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class extra {
    private int distanceBetweenPermutations(String s1, String s2)
    {
        ArrayList<Integer> positionIn1 = new ArrayList<Integer>(6);
        ArrayList<Integer> positionIn2 = new ArrayList<Integer>(6);
        
        for (int i = 0; i < 6; ++i)
        {
            positionIn1.set(s1.charAt(i) - 'a', i);
            positionIn2.set(s2.charAt(i) - 'a', i);
        }
        
        int distance = 0;
        
        for (int i = 0; i < 6; ++i)
        {
            distance += Math.abs(positionIn1.get(i) - positionIn2.get(i));
        }
        
        return distance;
    }
    
    // C++ next_permutation() analog in Java
    boolean next_permutation(char[] p) {
        for (int a = p.length - 2; a >= 0; --a)
            if (p[a] < p[a + 1])
                for (int b = p.length - 1;; --b)
                    if (p[b] > p[a]) {
                        char t = p[a];
                        p[a] = p[b];
                        p[b] = t;
                        for (++a, b = p.length - 1; a < b; ++a, --b) {
                            t = p[a];
                            p[a] = p[b];
                            p[b] = t;
                        }
                        return true;
                    }
        return false;
    }
    
    //private void sortList()
    {
        String s = "ABCDEF";
        char comp[] = {'A', 'B', 'C', 'D', 'E', 'F'};
        //generate all the possible permutations and store in permu
        ArrayList<String> permu = new ArrayList<String>();
        permu.add(s);
        
        while (next_permutation(comp))
        {
            permu.add(s);
        }
        
        int n = permu.size();
        
        System.out.print("Number of permutations: ");
        System.out.print(n);
        System.out.print("\n");
        
        //calculate the frequencies of all possible distances
        Map<Integer, Integer> freq = new TreeMap<Integer, Integer>();
        
        for (int i = 0; i < n; ++i)
        {
            for (int j = i + 1; j < n; ++j)
            {
                int temp = freq.get(distanceBetweenPermutations(permu.get(i), permu.get(j)));
                ++temp;
                freq.put(distanceBetweenPermutations(permu.get(i), permu.get(j)), temp);
            }
        }
        
        System.out.print("Number of possible distances: ");
        System.out.print(freq.size());
        System.out.print("\n");
        
        //to increase the number of possible distances, try adding weights
        
        //print the number of pairs having the different distances
        
        
    }
    
}
