package com.example.superproject;

import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private final List<Link> linkList = new ArrayList<>();
    private final List<Point> pointList = new ArrayList<>();
    private final List<Block> blocklist = new ArrayList<>();

    public void initObjects(){
        addBlock(
                new Point2D(0,0),
                new Point2D(100,0),
                new Point2D(100,100),
                new Point2D(0,100)
        );
    }
    public void run(Stage stage){
        Group group = new Group();
        Scene scene = new Scene(group,800,700);


        new AnimationTimer(){
            final double t = 2 * 1./30;
            @Override
            public void handle(long currentNanoTime) {
                for(int j =0 ;j < 2;j++) {
                    for (int i = 0; i < 10; i++) {
                        for(Link link : linkList){
                            link.solve();
                        }
                    }
                    for(Point point: pointList){
                        point.run(t/2);
                    }
                }
            }
        }.start();

        for(Point point : pointList){
            group.getChildren().add(point.circle);
        }
        stage.setScene(scene);
        stage.show();
    }
    public void addBlock(Point2D one, Point2D two, Point2D three, Point2D four){
        Point onePoint = new Point(new Circle(one.getX(),one.getY(),4));
        Point twoPoint = new Point(new Circle(two.getX(),two.getY(),4));
        Point threePoint = new Point(new Circle(three.getX(),three.getY(),4));
        Point fourPoint = new Point(new Circle(four.getX(),four.getY(),4));

        onePoint.old_pos = new Point2D(-1,0);

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
        Link diagRight = new Link(onePoint.circle,threePoint.circle,diag_value);


        blocklist.add(new Block(onePoint,twoPoint,threePoint,fourPoint));
        linkList.add(up);
        linkList.add(right);
        linkList.add(down);
        linkList.add(left);
        linkList.add(diagLeft);
        linkList.add(diagRight);
    }

}
