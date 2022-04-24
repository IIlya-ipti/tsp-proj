package com.example.superproject;

import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.shape.Circle;
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
    private final List<Link> linkList = new ArrayList<>();
    private final List<Point> pointList = new ArrayList<>();
    private final List<Block> blocklist = new ArrayList<>();
    private final List<Manifold> manifoldList = new ArrayList<>();
    final private List<Shape> shapes = new ArrayList<>();
    private final Group group = new Group();
    boolean DEBUG = false;
    boolean mouse = true;

    final double SCENE_X = 800;
    final double SCENE_Y = 700;


    public void initObjects(){
        Block leftneg = addBlock(
                new Point2D(150,100),
                new Point2D(200,100),
                new Point2D(200,300),
                new Point2D(150,300),
                50
        );

        Block body = addBlock(
                new Point2D(150,0),
                new Point2D(450,0),
                new Point2D(450,100),
                new Point2D(150,100),
                50
        );
        Block rightleg = addBlock(
                new Point2D(400,100),
                new Point2D(450,100),
                new Point2D(450,300),
                new Point2D(400,300),
                50
       );



        linkList.add(new Link(
                blocklist.get(0).getPointList().get(0).circle,
                blocklist.get(1).getPointList().get(3).circle,
                0
        ));
        linkList.add(new Link(
                blocklist.get(1).getPointList().get(2).circle,
                blocklist.get(2).getPointList().get(1).circle,
                0
        ));
        //linkList.add(new Link(blocklist.get(0).getPointList().get(0).circle,
        //blocklist.get(1).getPointList().get(3).circle,0));


        //linkList.add(new Link(blocklist.get(1).getPointList().get(2).circle,
        //        blocklist.get(2).getPointList().get(0).circle,0));






        // add text
        Text text = new Text("FF");
        text.setFont(new Font(30));
        text.setX(SCENE_X * 0.5) ;
        text.setY(SCENE_Y - 50);
        shapes.add(text);
    }

    public void run(Stage stage){
        Scene scene = new Scene(group,SCENE_X,SCENE_Y);
        Manifold manifold = new Manifold();
        new AnimationTimer(){
            final double t = 2 * 1./30;
            @Override
            public void handle(long currentNanoTime) {
                if(!mouse) {
                    blocklist.get(1).getPointList().get(0).circle.setCenterX(MouseX);
                    blocklist.get(1).getPointList().get(0).circle.setCenterY(MouseY);
                }
                if(!DEBUG) {

                    for (int j = 0; j < 2; j++) {
                        for (int i = 0; i < 10; i++) {
                            for (Link link : linkList) {
                                link.solve();
                            }
                        }
                        for (Point point : pointList) {
                            point.run(t / 2);
                        }
                        for (Block block : blocklist) {
                            block.update();
                        }
                    }

                }
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
        });
        group.getChildren().add(text);


        // Mose event
        scene.setOnMouseMoved(mouseEvent -> {

                MouseX = mouseEvent.getX();
                MouseY = mouseEvent.getY();

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
    public Block addBlock(Point2D one, Point2D two, Point2D three, Point2D four, double mass){
        Point onePoint = new Point(new Circle(one.getX(),one.getY(),0));
        Point twoPoint = new Point(new Circle(two.getX(),two.getY(),0));
        Point threePoint = new Point(new Circle(three.getX(),three.getY(),0));
        Point fourPoint = new Point(new Circle(four.getX(),four.getY(),0));


        double weight = onePoint.getPos().subtract(twoPoint.getPos()).magnitude();
        double height = twoPoint.getPos().subtract(threePoint.getPos()).magnitude();
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

        Block newBlock = new Block(onePoint,twoPoint,threePoint,fourPoint,mass);

        blocklist.add(newBlock);
        linkList.add(up);
        linkList.add(right);
        linkList.add(down);
        linkList.add(left);
        linkList.add(diagLeft);

        group.getChildren().add(newBlock.getPolygon());

        return newBlock;
    }

}
