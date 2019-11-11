package app;

public class Map implements Runnable{

    public static Character[][] originalMap = new Character[][]{
            {' ', ' ', ' ', ' ', ' ', '_', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', '_', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', '_', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', '_', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', '_', ' ', ' ', ' ', ' ', ' '},
            {'_', '_', '_', '_', '_', '_', '_', '_', '_', '_', '_'},
            {' ', ' ', ' ', ' ', ' ', '_', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', '_', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', '_', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', '_', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', '_', ' ', ' ', ' ', ' ', ' '},
    };

    public static Character[][] map = originalMap;

    @Override
    public void run() {
        for(Character[] line : map) {
            for (Character pos : line) {
                System.out.print(pos);
                System.out.print(' ');
            }
            System.out.print('\n');
        }
        System.out.print('\n');
        map = originalMap;
    }
}
