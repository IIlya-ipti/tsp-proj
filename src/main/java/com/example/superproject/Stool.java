package com.example.superproject;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class Stool {
    final Block leftLeg;
    final Block rightLeg;
    final Block body;
    final Game game;
    private Point2D normalDown;
    private final double startPos;
    // parameters
    double alpha1 = 0.5;

    Stool(Point2D point2D,double weightBody, double heightBody, double weightLeg, double heightLeg, Game game, Color color){
        this.startPos = point2D.getX();
        this.game =game;
        this.leftLeg = game.addBlock(
                new Point2D(point2D.getX(), point2D.getY()),
                weightLeg,
                heightLeg,
                50,
                color
        );
        this.rightLeg = game.addBlock(
                new Point2D(point2D.getX() + weightBody - weightLeg, point2D.getY()),
                weightLeg,
                heightLeg,
                50,
                color
        );
        this.body = game.addBlock(
                new Point2D(150,360),
                weightBody,
                heightBody,
                50,
                color
        );
        game.linkList.add(new Link(
                leftLeg.getPointList().get(0).circle,
                body.getPointList().get(3).circle,
                0
        ));
        game.linkList.add(new Link(
                body.getPointList().get(2).circle,
                rightLeg.getPointList().get(1).circle,
                0
        ));
        game.setStool(this);
        this.game.bl = body;
        Utility_Functions.bindBlocks(leftLeg,body);
        Utility_Functions.bindBlocks(body,rightLeg);
        Utility_Functions.bindBlocks(leftLeg,rightLeg);
    }
    void setParameters(double parameters){
        this.alpha1 = parameters;
    }
    void run(double t){
        normalDown = body.getNormals().get(2);
        double phi = alpha1*Math.sin(t/1.2);

        // left leg run
        runLeg(phi,leftLeg.getPointList().get(0),
                leftLeg.getPointList().get(3)
                );

        //right leg run
        runLeg(-phi,rightLeg.getPointList().get(1),
                rightLeg.getPointList().get(2)
        );
    }
    double getDist(){
        return body.getPointList().get(0).circle.getCenterX() - startPos;
    }
    void runLeg(double phi,Point OS_P, Point CorrPos_P ){

        // os of rotate
        Point2D OS = OS_P.getPos();
        Point2D startPoint = OS.add(normalDown.multiply(leftLeg.height));
        Point2D delta = OS.subtract(new Point2D(0,0));

        // move OS to x = 0 y = 0 and rotate
        startPoint = startPoint.subtract(delta);

        // new coords
        double x_ = startPoint.getX() * Math.cos(phi) - startPoint.getY()*Math.sin(phi) + delta.getX();
        double y_ = startPoint.getX() * Math.sin(phi) + startPoint.getY()*Math.cos(phi) + delta.getY();

        // set new pos of point leg
        CorrPos_P.setPos(new Point2D(x_,y_));
    }
    public List<Block> getAllBlocks(){
        List<Block> blockList = new ArrayList<>();
        blockList.add(leftLeg);
        blockList.add(body);
        blockList.add(rightLeg);
        return blockList;
    }
}

