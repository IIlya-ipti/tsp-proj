package com.example.superproject;

import javafx.geometry.Point2D;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Block {
    public final PhysicModel physics_model;
    private final List<Point> pointList = new ArrayList<>();

    /*
    * not use!
    * */
    private final List<Double> doublePointList = new ArrayList<>();
    private final List<Point2D> point2DS = new ArrayList<>();


    private final List<Point2D> normals = new ArrayList<>();
    private final Polygon polygon = new Polygon();


    Block(Point one, Point two, Point three, Point four, double mass){
        pointList.add(one);
        pointList.add(two);
        pointList.add(three);
        pointList.add(four);
        normals.add(new Point2D(0, -1));
        normals.add(new Point2D(1, 0));
        normals.add(new Point2D(0, 1));
        normals.add(new Point2D(-1, 0));
        point2DS.add(new Point2D(one.circle.getCenterX(),one.circle.getCenterY() ));
        point2DS.add(new Point2D(two.circle.getCenterX(),two.circle.getCenterY() ));
        point2DS.add(new Point2D(three.circle.getCenterX(),three.circle.getCenterY() ));
        point2DS.add(new Point2D(four.circle.getCenterX(),four.circle.getCenterY() ));
        UpdateList();
        double height = point2DS.get(0).subtract(point2DS.get(3)).magnitude();
        double width = point2DS.get(0).subtract(point2DS.get(1)).magnitude();
        physics_model = new PhysicModel(mass * (height * height + width * width)/12,mass, this);
    };
    Point2D centerBlock(){
        return new Point2D(pointList.get(2).circle.getCenterX() - pointList.get(0).circle.getCenterX(),
                pointList.get(2).circle.getCenterY() - pointList.get(0).circle.getCenterY());
    }
    List<Point2D> getNormals(){
        getPoints();
        Point2D vec;
        for(int i = 0;i < point2DS.size() - 1;i++){
            vec = point2DS.get(i + 1).subtract(point2DS.get(i));
            vec = new Point2D(vec.getY()*-1, vec.getX());
            vec = vec.normalize();
            vec = vec.multiply(-1);
            normals.set(i,vec);
        }
        vec = point2DS.get(0).subtract(point2DS.get(point2DS.size() - 1));
        vec = new Point2D(vec.getY()*-1, vec.getX());
        vec = vec.normalize();
        vec = vec.multiply(-1);
        normals.set(point2DS.size() - 1,vec);
        return normals;
    }
    /*
    * Move one point to point2D
    * */
    void MoveTo(Point2D point2D){
        Point2D start = getXY();
        Point2D vec = point2D.subtract(start);
        for(Point point: pointList){
            point.circle.setCenterX(point.circle.getCenterX() + vec.getX());
            point.circle.setCenterY(point.circle.getCenterY() + vec.getY());
        }
    }
    void setGravity(boolean gravity){
        if(gravity){
            for (Point point:pointList){
                point.acc = new Point2D(0,PhysicModel.g);
            }
        }else{
            for (Point point:pointList){
                point.acc = new Point2D(0,0);
            }
        }
    }
    boolean hasPoint(Point point){
        for (Point point1 : pointList){
            if(point == point1){
                return true;
            }
        }
        return false;
    }
    void setVelocityPoint(Point2D velocity,int index){
        pointList.get(index).setVelocity(velocity);
    }
    Point2D getXY(){
        return new Point2D(pointList.get(0).circle.getCenterX(),pointList.get(0).circle.getCenterY());
    }
    void UpdateList(){
        doublePointList.clear();
        for(Point point: pointList){
            doublePointList.add(point.getPos().getX());
            doublePointList.add(point.getPos().getY());
        }

    }
    List<Point> getPointList(){
        return pointList;
    }
    List<Point2D> getPoints(){
        double x,y;
        for(int i = 0;i < pointList.size();i++){
            x = pointList.get(i).circle.getCenterX();
            y = pointList.get(i).circle.getCenterY();
            point2DS.set(i,new Point2D(x,y));
        }
        return point2DS;
    }
    Polygon getPolygon(){
        return polygon;
    }
    boolean contains(Point2D point){
        List<Point2D> corners = this.getPoints();

        // sides of the rectangle
        Point2D one = corners.get(1).subtract(corners.get(0));
        Point2D two = corners.get(2).subtract(corners.get(1));
        Point2D three = corners.get(3).subtract(corners.get(2));
        Point2D four = corners.get(0).subtract(corners.get(3));

        // vector from point to sides
        Point2D one_point = point.subtract(corners.get(0));
        Point2D two_point = point.subtract(corners.get(1));
        Point2D three_point = point.subtract(corners.get(2));
        Point2D four_point = point.subtract(corners.get(3));

        //calc the dot product
        boolean sca_one = one.dotProduct(one_point) >= 0;
        boolean sca_two = two.dotProduct(two_point) >= 0;
        boolean sca_three = three.dotProduct(three_point) >= 0;
        boolean sca_four = four.dotProduct(four_point) >= 0;

        if(sca_one && sca_two && sca_three && sca_four){
            return true;
        }else return !(sca_one | sca_two | sca_three | sca_four);
    }
    void update(){
        UpdateList();
        polygon.getPoints().clear();
        polygon.getPoints().addAll(doublePointList);
    }
    boolean contains(Point point){
        Point2D point2D = new Point2D(point.circle.getCenterX(),point.circle.getCenterY());
        return contains(point2D);
    }
}
