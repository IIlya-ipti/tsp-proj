package com.example.superproject;

import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;

public class Point {
    Point2D velocity = new Point2D(10,0);
    Point2D acc = new Point2D(0,9.8);
    Point2D pos;
    Point2D old_pos;
    double dt;
    private Point2D new_pos;
    Circle circle;
    double mass = 50;
    void setForce(){};
    Point(Circle circle){
        this.circle = circle;
    }
    void run(final double dt){
        this.dt = dt;
        pos = new Point2D(circle.getCenterX(),circle.getCenterY());
        if(old_pos == null){
            old_pos = pos;
        }
        new_pos = pos.multiply(2).subtract(old_pos).add(acc.multiply(dt*dt));
        old_pos = pos;
        constr();

        circle.setCenterX(new_pos.getX());
        circle.setCenterY(new_pos.getY());
    }
    Point2D getPos(){
        return new Point2D(circle.getCenterX(),circle.getCenterY());
    }
    void constr(){
        if(new_pos.getY() > 600){
            new_pos = new Point2D(new_pos.getX(), 600);
        }
        if(new_pos.getX() > 600){
            new_pos = new Point2D(600, new_pos.getY());
        }
        if(new_pos.getX() < 0){
            new_pos = new Point2D(0, new_pos.getY());
        }
    }

}
