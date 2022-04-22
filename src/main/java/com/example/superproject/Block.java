package com.example.superproject;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

class Block {
    List<Point> pointList = new ArrayList<>();
    Block(Point one, Point two, Point three, Point four){
        pointList.add(one);
        pointList.add(two);
        pointList.add(three);
        pointList.add(four);
    };
    void getPoints(){}
    boolean contains(Point point){return false;}
}
