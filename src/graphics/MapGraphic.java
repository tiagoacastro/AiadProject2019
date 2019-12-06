package graphics;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MapGraphic extends JPanel {

    private int WIDTH;
    private int HEIGHT;
    private BufferedImage road;
    private BufferedImage roadh;
    private BufferedImage ground;
    private BufferedImage simple_car;
    private BufferedImage simple_car1;
    private BufferedImage simple_car2;
    private BufferedImage tf;
    private BufferedImage clear;

    public static Character[][] roadMap = new Character[][]{
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
            {' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' ', '|', ' ', ' ', ' ', ' ', ' ', '|', ' ', ' '},
    };


    public MapGraphic(){
        WIDTH = 700;
        HEIGHT = 700;

        loadAssets();
    }

    public void loadAssets(){
        File file = new File("./resources/road.jpg");
        File file1 = new File("./resources/roadh.jpg");
        File file2 = new File("./resources/ground.jpg");
        File file3 = new File("./resources/simple_car.jpg");
        File file4 = new File("./resources/simple_car1.jpg");
        File file5 = new File("./resources/simple_car2.jpg");
        File file6 = new File("./resources/tf.jpg");
        File file7 = new File("./resources/clear.jpg");

        try{
            road = ImageIO.read(file);
            roadh = ImageIO.read(file1);
            ground = ImageIO.read(file2);
            simple_car = ImageIO.read(file3);
            simple_car1 = ImageIO.read(file4);
            simple_car2 = ImageIO.read(file5);
            tf = ImageIO.read(file6);
            clear = ImageIO.read(file7);

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    protected void paintComponent(Graphics g){
        super.paintComponents(g);
        for(int i = 0; i < roadMap.length; i ++){
            for(int j = 0; j < roadMap[i].length; j++){
                switch(roadMap[j][i]){
                    case '|':
                        g.drawImage(road, i*road.getWidth(), j*road.getHeight(), null);
                        break;

                    case '-':
                        g.drawImage(roadh, i*roadh.getWidth(), j*roadh.getHeight(), null);
                        break;

                    case ' ':
                        g.drawImage(ground, i*ground.getWidth(), j*ground.getHeight(), null);
                        break;

                    case 'X':
                        if(j == 5 || j==11 || j== 17){
                            g.drawImage(simple_car, i*simple_car.getWidth(), j*simple_car.getHeight(), null);
                        }
                        else {
                            g.drawImage(simple_car1, i*simple_car1.getWidth(), j*simple_car1.getHeight(), null);
                        }
                        break;

                    case 'O':
                        g.drawImage(tf, i*tf.getWidth(), j*tf.getHeight(), null);
                        break;

                    case '+':
                        g.drawImage(clear, i*clear.getWidth(), j*clear.getHeight(), null);
                        break;
                }

            }
        }
    }

    public void setMap(Character[][] m){
        this.roadMap = m;
        repaint();
    }

}
