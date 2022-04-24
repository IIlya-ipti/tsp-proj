package com.example.superproject;

import javafx.geometry.Point2D;

import java.util.List;

public class PhysicModel {
    static final double g = 9.8;
    final double  mass;
    final double inertia;
    public final double restitution = 0.0;
    public final double sFriction  = 0.8;
    public final double dFriction = 0.2;
    public final Block block;
    public List<Point> contacts;

    PhysicModel(double inertia,double mass, Block block){
        this.inertia = inertia;
        this.mass = mass;
        this.block = block;
    }



    /*
     * apply the previously calculated momentum to the body
     * */
    public void applyImpulse(final Point contact, final Point2D impulse, final Point2D vec, double inertia){
        Point2D velocity = impulse.multiply(1.0 / mass);
        double wVelocity = (vec.getX() * impulse.getY() - vec.getY() * impulse.getX()) / inertia;

        Point2D point2D = block.getXY().subtract(new Point2D(
                contact.circle.getCenterX(),
                contact.circle.getCenterY()
        ));
        contact.setVelocity(velocity.add(Utility_Functions.Cross(wVelocity,point2D)));
    }

    public double getWVelocity(){
        Point2D point2D = block.getXY().subtract(block.centerBlock());
        return Utility_Functions.tangentVelocity(block.getPointList().get(0).getVelocity(),point2D)/point2D.magnitude();
    }
    public Point2D getVelocity(){
        Point2D point2D = block.centerBlock().subtract(block.getXY());
        Point2D velocityOne = block.getPointList().get(0).getVelocity();
        return velocityOne.add(Utility_Functions.Cross(getWVelocity(),point2D));

    }
}
