package NameEntities;

import java.util.Comparator;
import java.util.HashMap;

public class ValueComparator implements Comparator {
    private HashMap<String, Integer> map = new HashMap<>();

    ValueComparator(HashMap<String, Integer> map){this.map.putAll(map);}

    @Override
    public int compare(Object o1, Object o2) {
        String s1 = (String) o1;
        String s2 = (String) o2;

        if (map.get(s1) >= map.get(s2)) {
            return -1;
        } else {
            return 1;
        }


    }
}
