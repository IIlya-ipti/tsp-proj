package com.example.superproject;

import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;


enum SHAPES{
    TEXT(0);
    public final int id;
    SHAPES(int id) {
        this.id = id;
    }
}


public class Game {
    private double MouseX = 0;
    private double MouseY = 0;
    public final List<Link> linkList = new ArrayList<>();
    private final List<Point> pointList = new ArrayList<>();
    private final List<Block> blocklist = new ArrayList<>();
    final private List<Shape> shapes = new ArrayList<>();
    public final List<Stool> stoolList = new ArrayList<>();
    private final Group group = new Group();
    boolean DEBUG = false;
    boolean mouse = true;
    private final double MAX_TIME = 30;
    public Block bl;

    static final double SCENE_X = 800;
    static final double SCENE_Y = 700;


    public void setStool(Stool stool){
        stoolList.add(stool);
    }
    public List<Double> TrainRun(){
        Manifold manifold = new Manifold();
        final double dt = 2 * 1./50;
        double t = 0;

        while(t  < MAX_TIME) {
            Utility_Functions.sortOSX(blocklist);
            Utility_Functions.sortOSY(blocklist);

            for (int j = 0; j < 1; j++) {
                t += dt;

                for (Point point : pointList) {
                    point.run(dt) ;
                }
                for(Stool stool : stoolList){
                    stool.run(t);
                }
                for (int i = 0; i < 5; i++) {
                    makeCollision(manifold);
                }
                makeLink();

                for (Block block : blocklist) {
                    block.update();
                }
            }
        }
        List<Double> DistList = new ArrayList<>();
        for (Stool stool : stoolList) {
            DistList.add(stool.getDist());
        }
        return DistList;
    }
    public void initObjects(){

        Block platform = addBlock(
                new Point2D(-500,600),
                5000,
                100,
                1000000,
                Color.GREEN
        );
        platform.setGravity(false);
        platform.switchPowers();

        //stoolList.add(new Stool(new Point2D(150,360),300,50,50,200,this,Color.AQUAMARINE));
        //stoolList.add(new Stool(new Point2D(150,360),300,50,50,200,this,Color.GREEN));

        //stoolList.get(0).setParameters(0.08);
        //stoolList.get(1).setParameters(0.6);

        Utility_Functions.bindStools(stoolList);

        //addBlock(new Point2D(20, 20), 100, 100, 50, Color.AQUAMARINE);




        // add text
        Text text = new Text("FF");
        text.setFont(new Font(30));
        text.setX(SCENE_X * 0.5) ;
        text.setY(SCENE_Y - 50);
        shapes.add(text);
    }


    private void makeCollision(Manifold manifold){

        for (int k1 = 0; k1 < blocklist.size(); k1++){
            for (int k2 = 0; k2 < blocklist.size(); k2++){
                if (k1 == k2){
                    continue;
                }
                if (Utility_Functions.IntersectsPoints(blocklist.get(k1), blocklist.get(k2)).size() > 0){
                    manifold.setBlocks(blocklist.get(k1), blocklist.get(k2));
                    if(manifold.isCollide) {
                        manifold.solveCollision();
                        manifold.posCorr();
                    }
                }
            }
        }
    }

    private void makeLink(){
        for (int i = 0; i < 5; i++) {
            for (Link link : linkList) {
                link.solve();
            }
        }
    }
    public void run(Stage stage){
        Scene scene = new Scene(group,SCENE_X,SCENE_Y);
        Manifold manifold = new Manifold();


        new AnimationTimer(){
            final double dt = 2 * 1./50;
            private double t = 0;
            @Override
            public void handle(long currentNanoTime) {
                if(!mouse) {
                    bl.getPointList().get(0).circle.setCenterX(MouseX);
                    bl.getPointList().get(0).circle.setCenterY(MouseY);
                }

                if(!DEBUG) {

                    Utility_Functions.sortOSX(blocklist);
                    Utility_Functions.sortOSY(blocklist);

                    for (int j = 0; j < 1; j++) {
                        t += dt;

                        for (Point point : pointList) {
                            point.run(dt);
                        }
                        for(Stool stool : stoolList){
                            stool.run(t);
                        }
                        for (int i = 0; i < 5; i++) {
                            makeCollision(manifold);

                        }
                        makeLink();

                        for (Block block : blocklist) {
                            block.update();
                        }
                    }

                }
                System.out.println(t);
            }
        }.start();

        for(Point point : pointList){
            group.getChildren().add(point.circle);
        }


        // for testing /// // // for testing // /// //
        Text text = (Text) shapes.get(SHAPES.TEXT.id);
        scene.setOnMouseMoved(event -> {
            String msg =
                    "x: " +event.getX()      + ", y: "       + event.getY()        ;
            text.setText(msg);
            MouseX = event.getX();
            MouseY = event.getY();
        });
        group.getChildren().add(text);

        scene.setOnMouseClicked(event->{
            if (event.getButton() == MouseButton.PRIMARY){
                addBlock(new Point2D(event.getX(), event.getY()), 50, 50, 50, Color.AQUAMARINE);
            }
        });

        // EVENT KEY
        scene.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()) {
                case C -> {
                    DEBUG = !DEBUG;
                }
                case V ->{
                    System.out.println(blocklist.get(0).getNormals());
                }
                case S -> {
                    blocklist.get(0).MoveTo(new Point2D(10,10));
                }
                case O -> {
                    blocklist.get(0).setVelocityPoint(new Point2D(-10,0),2);
                }
                case R->{
                    mouse = !mouse;
                }
            }
        });


        stage.setScene(scene);
        stage.show();
    }

    public Block addBlock(Point2D one, double weight, double height, double mass, Paint paint){
        final double n = 3;
        final double rad = 2;


        Point2D two = new Point2D(one.getX() + weight, one.getY());
        Point2D three = new Point2D(one.getX() + weight,one.getY() + height);
        Point2D four = new Point2D(one.getX(), one.getY() + height);




        Point onePoint = new Point(new Circle(one.getX(),one.getY(),rad));
        Point twoPoint = new Point(new Circle(two.getX(),two.getY(),rad));
        Point threePoint = new Point(new Circle(three.getX(),three.getY(),rad));
        Point fourPoint = new Point(new Circle(four.getX(),four.getY(),rad));

        Block newBlock = new Block(onePoint,twoPoint,threePoint,fourPoint,mass, (int) n);
        newBlock.getAllPointList().add(onePoint);
        newBlock.getAllPointList().add(twoPoint);
        newBlock.getAllPointList().add(threePoint);
        newBlock.getAllPointList().add(fourPoint);

        // up
        Point point,point1;
        Point2D pointk;
        point = onePoint;

        for(int i =1; i <= n;i++) {
            if(i == n){
                point1 = twoPoint;
            }else {
                pointk = new Point2D(one.getX() + i * weight / n, two.getY());
                point1 = new Point(new Circle(pointk.getX(), pointk.getY(), rad));
                pointList.add(point1);
                newBlock.getAllPointList().add(point1);
            }
            linkList.add(new Link(point1.circle, point.circle, weight / n));
            point = point1;
        }

        // right
        point = twoPoint;

        for(int i =1; i <= n;i++) {
            if(i == n){
                point1 = threePoint;
            }else {
                pointk = new Point2D(two.getX(), two.getY() + i * height / n);
                point1 = new Point(new Circle(pointk.getX(), pointk.getY(), rad));
                pointList.add(point1);
                newBlock.getAllPointList().add(point1);
            }
            linkList.add(new Link(point1.circle, point.circle, height / n));
            point = point1;
        }

        // down
        point = threePoint;

        for(int i =1; i <= n;i++) {
            if(i == n){
                point1 = fourPoint;
            }else {
                pointk = new Point2D(three.getX() - i * weight/n, three.getY());
                point1 = new Point(new Circle(pointk.getX(), pointk.getY(), rad));
                pointList.add(point1);
                newBlock.getAllPointList().add(point1);
            }
            linkList.add(new Link(point1.circle, point.circle, weight / n));
            point = point1;
        }

        // left
        point = fourPoint;

        for(int i =1; i <= n;i++) {
            if(i == n){
                point1 = onePoint;
            }else {
                pointk = new Point2D(four.getX() , four.getY() - i * height/n);
                point1 = new Point(new Circle(pointk.getX(), pointk.getY(), rad));
                pointList.add(point1);
                newBlock.getAllPointList().add(point1);
            }
            linkList.add(new Link(point1.circle, point.circle, height / n));
            point = point1;
        }






        double diag_value = onePoint.getPos().subtract(threePoint.getPos()).magnitude();

        pointList.add(onePoint);
        pointList.add(twoPoint);
        pointList.add(threePoint);
        pointList.add(fourPoint);

        Link up = new Link(onePoint.circle,twoPoint.circle,weight);
        Link right = new Link(twoPoint.circle, threePoint.circle,height);
        Link down = new Link(threePoint.circle,fourPoint.circle,weight);
        Link left = new Link(fourPoint.circle, onePoint.circle,height);
        Link diagLeft = new Link(twoPoint.circle,fourPoint.circle,diag_value);


        blocklist.add(newBlock);
        linkList.add(up);
        linkList.add(right);
        linkList.add(down);
        linkList.add(left);
        linkList.add(diagLeft);

        newBlock.getPolygon().setFill(paint);
        group.getChildren().add(newBlock.getPolygon());

        return newBlock;
    }

}