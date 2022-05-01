package com.example.superproject;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Utility_Functions {
    public static Point2D Cross(double c, final Point2D v){
        return new Point2D(-c * v.getY(), c * v.getX());
    }

    static void bindStools(Stool A, Stool B){
        List<Block> Abl = A.getAllBlocks();
        List<Block> Bbl = B.getAllBlocks();
        for (Block block1 : Abl){
            for(Block block2 : Bbl){
                bindBlocks(block1,block2);
            }
        }

    }
    static void bindStools(List<Stool> stoolList){
        for(int i = 0;i < stoolList.size();i++){
            for(int j = i + 1;j < stoolList.size();j++){
                bindStools(stoolList.get(i),stoolList.get(j));
            }
        }
    }
    static void bindBlocks(Block A, Block B){
        A.bindBlocks.add(B);
        B.bindBlocks.add(A);
    }
    static List<Point> IntersectsPoints(Block block1, Block block2){
        List<Point> block1Points = block1.getAllPointList();
        List<Point> block2Points = block2.getAllPointList();
        List<Point> point2DS = new ArrayList<>();
        for (Point block1_point : block1Points) {
            if (block2.contains(block1_point)) {
                point2DS.add(block1_point);
            }
        }
        for(Point block2_point : block2Points){
            if(block1.contains(block2_point)){
                point2DS.add(block2_point);
            }
        }
        return point2DS;
    }
    static double tangentVelocity(Point2D velocity, Point2D radius){
        Point2D PerpendicularRadius = new Point2D(radius.getY()*-1,radius.getX());
        PerpendicularRadius = PerpendicularRadius.normalize();
        return velocity.dotProduct(PerpendicularRadius);
    }

    static final Comparator<Block> OSX = new Comparator<Block>() {
        @Override
        public int compare(Block one, Block two) {
            return Double.compare(one.getPointList().get(0).getPos().getX(), two.getPointList().get(0).getPos().getX());
        }
    };
    static final Comparator<Block> OSY = new Comparator<Block>() {
        @Override
        public int compare(Block one, Block two) {
            return Double.compare(one.getPointList().get(0).getPos().getY(), two.getPointList().get(0).getPos().getY());
        }
    };

    static void sortOSX(List<Block> blocks){
        blocks.sort(OSX);
    }

    static void sortOSY(List<Block> blocks){
        blocks.sort(OSY);
    }

}