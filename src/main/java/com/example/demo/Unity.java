package com.example.demo;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;
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

public class Unity {
    final private List<Block> blocks = new ArrayList<>();
    final private List<Shape> shapes = new ArrayList<>();
    final private List<Manifold> contacts = new ArrayList<>();
    final double SCENE_X = 1000;
    final double SCENE_Y = 600;
    double TIMER = 1.0/30.0 ;

    /*
     * function for add objects
     * */
    public void unObjects(){
        // add block
        Block block = addBlock (0,0,150,150,500,new Point2D(20,0), Color.AQUAMARINE);
        //Block block2 = addBlock (400,100,150,150,234,new Point2D(0,0), Color.AQUAMARINE);
        // add platform
        Block platform = addBlock(-400,SCENE_Y-300,5000,200,202000000,new Point2D(0,0), Color.BLACK);
        platform.physics_model.stopPower();
        // add text
        Text text = new Text("FF");
        text.setFont(new Font(30));
        text.setX(SCENE_X * 0.5) ;
        text.setY(SCENE_Y - 50);
        shapes.add(text);


    }
    public void RUN(Stage stage)  {

        //create scene
        Group group = new Group();
        Scene scene = new Scene(group,SCENE_X,SCENE_Y);

       Block block = blocks.get(0);

        // while of animation
        new AnimationTimer(){
            @Override
            public void handle(long l) {
                if(TIMER != 0) {
                    for (int i = 0; i < blocks.size() - 1; i++) {
                        for (int j = i + 1; j < blocks.size(); j++) {

                            Manifold manifold = new Manifold(blocks.get(i), blocks.get(j));
                            if (blocks.get(i).getRectangle().getBoundsInParent().intersects(blocks.get(j).getRectangle().getBoundsInParent())) {
                                    manifold.applyImpulse();
                                    manifold.posCorrection();

                            }

                            List<Point2D> point2 = blocks.get(i).physics_model.contacts;


                            if (point2 != null) {
                                for (Point2D point2D : point2) {
                                    Circle circle = new Circle(point2D.getX(), point2D.getY(), 2, Color.RED);
                                    group.getChildren().addAll(circle);
                                }
                            }
                            blocks.get(i).run(TIMER);
                        }
                    }


                }
            }
        }.start();

        // for testing /// // // for testing // /// //
        Text text = (Text) shapes.get(SHAPES.TEXT.id);
        scene.setOnMouseMoved(event -> {
            String msg =
                    "x: " +event.getX()      + ", y: "       + event.getY()        ;
            text.setText(msg);
        });

        // EVENT KEY
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                switch (keyEvent.getCode()) {
                    case UP -> block.physics_model.addY(-40);
                    case DOWN -> block.physics_model.addY(40);
                    case LEFT -> block.physics_model.addX(-40);
                    case RIGHT -> block.physics_model.addX(40);
                    case R -> {
                        block.getRectangle().setScaleX(block.getRectangle().getScaleX() + 10);
                    }
                    case Q -> {
                        block.getRectangle().setScaleX(block.getRectangle().getScaleX() - 10);
                    }
                    case U -> {
                        block.getRectangle().setRotate(block.getRectangle().getRotate() + 10);

                    }
                    case O -> {
                        Transform transform = block.getRectangle().getLocalToParentTransform();
                        System.out.println(transform);
                        System.out.println(block.getRectangle().getX());
                        System.out.println(block.getRectangle().getX() + block.getRectangle().getWidth());
                        System.out.println(transform.transform(block.getRectangle().getX(), block.getRectangle().getY()));
                        System.out.print(transform.transform(block.getRectangle().getX() + block.getRectangle().getWidth(), block.getRectangle().getY()));
                    }
                    case A -> {
                        Transform transform = block.getRectangle().getLocalToSceneTransform();
                        System.out.println(transform);
                    }
                    case SPACE -> {
                        block.physics_model.invX(0);
                        block.physics_model.invY(0);
                    }
                    case I -> {
                        Transform transform = block.getRectangle().getLocalToSceneTransform();
                        Point2D point2D = new Point2D(100, 100);
                        System.out.println(block.getRectangle().getTranslateX());

                    }
                    case C -> {
                        TIMER = 0;

                    }
                    case V -> {
                        TIMER = 1.0/30.0;
                    }
                }
            }
        });

        group.getChildren().add(text);
        for (Block block1 : blocks) {
            group.getChildren().add(block1.getRectangle());
        }
        block.getRectangle().toFront();
        stage.setScene(scene);
        stage.show();
    }
    private Block addBlock(double x, double y, double width, double height, double mass, Point2D speed, Paint paint){
        Block block = new Block(new Rectangle(x,y,width,height), mass, speed);
        block.getRectangle().setFill(paint);
        blocks.add(block);
        return block;
    }

}