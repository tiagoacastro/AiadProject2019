package app;

public class Map implements Runnable{

    public static Character[][] originalMap = new Character[][]{
            {' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' '},
            {'-', '-', '-', '-', '-', '+', '-', '-', '-', '-', '-'},
            {' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' '},
    };

    public static Character[][] oldMap = originalMap;
    public static Character[][] newMap = originalMap;

    @Override
    public void run() {
        for(Character[] line : newMap) {
            for (Character pos : line) {
                System.out.print(pos);
                System.out.print(' ');
            }
            System.out.print('\n');
        }
        System.out.print('\n');
        oldMap = newMap;
        newMap = originalMap;
    }
}
