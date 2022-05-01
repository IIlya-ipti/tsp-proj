package com.example.superproject;

import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;

public class Point {
    Point2D acc = new Point2D(0, PhysicModel.g);
    Point2D pos;

    private Point2D old_pos;
    private Point2D new_pos;
    private Point2D velocity = new Point2D(0,0);
    boolean velocityMode = false;
    final double tr = 0;
    Circle circle;
    double dt;

    Point(Circle circle){
        this.circle = circle;
    }

    void run(final double dt){
        this.dt = dt;

        pos = new Point2D(circle.getCenterX(), circle.getCenterY());
        if (old_pos == null) {
            old_pos = pos;
        }
        new_pos = pos.multiply(2 - tr).subtract(old_pos.multiply(1 - tr)).add(acc.multiply(dt * dt));
        velocity = new_pos.subtract(old_pos).multiply(1 / (2 * dt));

        old_pos = pos;
        pos = new_pos;
        circle.setCenterX(new_pos.getX());
        circle.setCenterY(new_pos.getY());
    }

    Point2D getVelocity(){
        return velocity;
    }

    void setVelocity(Point2D velocity){
        this.velocity = velocity;
    }

    void setPos(Point2D vec){
        circle.setCenterX(vec.getX());
        circle.setCenterY(vec.getY());
        old_pos = new Point2D(old_pos.getX() + (circle.getCenterX() - old_pos.getX())*PhysicModel.FrictionCoefficient, old_pos.getY());
    }

    Point2D getPos(){
        return new Point2D(circle.getCenterX(),circle.getCenterY());
    }
}