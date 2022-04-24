package com.example.superproject;

import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;

public class Point {
    Point2D acc = new Point2D(0,9.8);
    Point2D pos;
    private Point2D old_pos;
    private Point2D new_pos;
    private Point2D velocity = new Point2D(0,0);
    boolean velocityMode = false;
    Circle circle;
    double dt;
    void setForce(){};
    Point(Circle circle){
        this.circle = circle;
    }
    void run(final double dt){
        this.dt = dt;

        pos = new Point2D(circle.getCenterX(), circle.getCenterY());

        if(!velocityMode) {
            if (old_pos == null) {
                old_pos = pos;
            }
            new_pos = pos.multiply(2).subtract(old_pos).add(acc.multiply(dt * dt));
            velocity = new_pos.subtract(old_pos).multiply(1 / (2 * dt));
            constr();
        }else{
            new_pos = pos.add(velocity.multiply(dt)).add(acc.multiply(0.5 * dt * dt));
            Point2D new_acc = acc;
            velocity = velocity.add(acc.add(new_acc).multiply(0.5 *dt));
        }
        old_pos = pos;
        pos = new_pos;
        circle.setCenterX(new_pos.getX());
        circle.setCenterY(new_pos.getY());
    }
    Point2D getVelocity(){
        return velocity;
    }
    void setVelocity(Point2D velocity){
        velocityMode =true;
        this.velocity = velocity;
    }
    void setPos(Point2D vec){
        circle.setCenterX(circle.getCenterX() + vec.getX());
        circle.setCenterY(circle.getCenterX() + vec.getY());
    }
    Point2D getPos(){
        return new Point2D(circle.getCenterX(),circle.getCenterY());
    }
    void constr(){
        if(new_pos.getY() > 700){
            new_pos = new Point2D(new_pos.getX(), 700);
        }
        if(new_pos.getX() > 700){
            new_pos = new Point2D(700, new_pos.getY());
        }
        if(new_pos.getX() < 0){
            new_pos = new Point2D(0, new_pos.getY());
        }
    }

}
