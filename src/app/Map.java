package app;

import graphics.MapGraphic;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Map extends JFrame implements Runnable{

    private int WIDTH = 700;
    private int HEIGHT = 700;

    private BufferedImage road;
    private BufferedImage roadh;
    private BufferedImage ground;
    private BufferedImage simple_car;

    private MapGraphic map;

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

        if(Main.gui){
            map = new MapGraphic();
            this.getContentPane().add(map);
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setSize(WIDTH, HEIGHT);
            this.setVisible(true);
        }
    }

    @Override
    public void run() {
        if(Main.gui) {
            map.setMap(newMap);
            for (Character[] line : newMap) {
                for (Character pos : line) {
                    System.out.print(pos);
                    System.out.print(' ');
                }
                System.out.print('\n');
            }
            System.out.print("\n\n\n");
        }

        copyMap(newMap, oldMap);
        copyMap(originalMap, newMap);
    }

    private void copyMap(Character[][] from, Character[][] to){
        for (int r = 0; r < from.length; r++) {
            to[r] = from[r].clone();
        }
    }

    public void loadAssets(){
        File file = new File("./resources/road.jpg");
        File file1 = new File("./resources/roadh.jpg");
        File file2 = new File("./resources/ground.jpg");
        File file3 = new File("./resources/simple_car.jpg");

        try{
            this.road = ImageIO.read(file);
            this.roadh = ImageIO.read(file1);
            this.ground = ImageIO.read(file2);
            this.simple_car = ImageIO.read(file3);
        }catch(IOException e){
            e.printStackTrace();
        }

    }
}
