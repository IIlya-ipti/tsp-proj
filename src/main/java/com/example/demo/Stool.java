package com.example.demo;


import javafx.geometry.Orientation;
import javafx.geometry.Point2D;

public class Stool {
    private final Block body;
    public final Block leftLeg;
    private final Block RightLeg;
    private double k1 = -1;
    private double k2 = -1;

    Stool(final Block body, final  Block leftLeg,final Block RightLeg){
        this.body = body;
        this.leftLeg = leftLeg;
        this.RightLeg = RightLeg;

        // connect with body in points
        Point2D one = body.getPoints().get(3);
        Point2D two = body.getPoints().get(2);
        one = one.add(25,15);
        two = two.add(-25,15);
        body.connect(leftLeg,one);
        body.connect(RightLeg,two);


        leftLeg.name = "left";
        RightLeg.name = "right";
        body.name = "body";
        Utility_Functions.bindBlocks(this.leftLeg,this.RightLeg);

    }

    public void run(double t){
        Point2D orientationNormal = body.getNormals().get(2);
        double alpha1 = 1.5;
        double alpha2 = 1.5;

        Point2D vec1 = leftLeg.getPoints().get(3).subtract(leftLeg.getPoints().get(0));
        if(Math.abs(orientationNormal.angle(vec1)) > 20) {
            k1 *= -1;
            leftLeg.physics_model.setWVelocity(2*k1 * alpha1);

        }else {
            leftLeg.physics_model.setWVelocity(k1 * alpha1);
        }




        Point2D vec2 = RightLeg.getPoints().get(2).subtract(RightLeg.getPoints().get(1));
        if(Math.abs(orientationNormal.angle(vec2)) > 20) {
            k2 *= -1;
            RightLeg.physics_model.setWVelocity(2*k1 * alpha2);
        }else {
            RightLeg.physics_model.setWVelocity(k1 * alpha2);
        }


    }
}
