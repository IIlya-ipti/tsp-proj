package com.example.superproject;

import javafx.scene.shape.Circle;

import static java.lang.Double.NaN;

public class Link {
    private final double restDistance;
    private final Circle one;
    private final Circle two;
    Link(Circle one, Circle two, double restDistance){
        this.one = one;
        this.two = two;
        this.restDistance = restDistance;
    }
    void solve(){

        double diffX = one.getCenterX() - two.getCenterX();
        double diffY = one.getCenterY() - two.getCenterY();

        double d = Math.sqrt(diffX* diffX + diffY * diffY);
        double difference = (restDistance - d)/d;

        if(Double.isNaN(difference)){
            difference = 0;
        }
        double translateX = diffX* 0.5 * difference;
        double translateY = diffY* 0.5 * difference;

        one.setCenterX(one.getCenterX() + translateX);
        one.setCenterY(one.getCenterY() + translateY);

        two.setCenterX(two.getCenterX() - translateX);
        two.setCenterY(two.getCenterY() - translateY);
    }
}
