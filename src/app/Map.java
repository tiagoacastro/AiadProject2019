package app;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Map implements Runnable{

    public static Character[][] originalMap = new Character[][]{
            {' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' ', '|', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' ', '|', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' ', '|', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' ', '|', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' ', '|', ' ', ' '},
            {'-', '-', '-', '-', '-', '+', '-', '-', '-', '-', '-', '+', '-', '-', '-', '-', '-', '+', '-', '-'},
            {' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' ', '|', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' ', '|', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' ', '|', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' ', '|', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' ', '|', ' ', ' '},
            {'-', '-', '-', '-', '-', '+', '-', '-', '-', '-', '-', '+', '-', '-', '-', '-', '-', '+', '-', '-'},
            {' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' ', '|', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' ', '|', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' ', '|', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' ', '|', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' ', '|', ' ', ' '},
            {'-', '-', '-', '-', '-', '+', '-', '-', '-', '-', '-', '+', '-', '-', '-', '-', '-', '+', '-', '-'},
            {' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' ', '|', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' ', '|', ' ', ' '}
    };

    public static Character[][] oldMap = new Character[originalMap[0].length][originalMap.length];
    public static Character[][] newMap = new Character[originalMap[0].length][originalMap.length];

    Map(){
        copyMap(originalMap, oldMap);
        copyMap(originalMap, newMap);
    }

    @Override
    public void run() {
//        Calendar cal = Calendar.getInstance();
//        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SS");
//        System.out.println( "Map: " + sdf.format(cal.getTime()) );

        for(Character[] line : newMap) {
            for (Character pos : line) {
                System.out.print(pos);
                System.out.print(' ');
            }
            System.out.print('\n');
        }
        System.out.print('\n');
        copyMap(newMap, oldMap);
        copyMap(originalMap, newMap);

//        cal = Calendar.getInstance();
//        sdf = new SimpleDateFormat("HH:mm:ss:SS");
//        System.out.println( "Map_end: " + sdf.format(cal.getTime()) );
    }

    private void copyMap(Character[][] from, Character[][] to){
        for (int r = 0; r < from.length; r++) {
            to[r] = from[r].clone();
        }
    }
}
