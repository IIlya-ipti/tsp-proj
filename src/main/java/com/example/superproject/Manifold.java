package com.example.superproject;

import javafx.geometry.Point2D;
import javafx.util.Pair;


import java.util.List;

public class Manifold {
    public Block A;
    public Block B;
    public boolean isCollide = true;

    public Manifold(){}

    public void setBlocks(Block A, Block B){
        this.A = A;
        this.B = B;
        isCollide = !A.bindBlocks.contains(B);

        sf = Math.sqrt(A.physics_model.sFriction * A.physics_model.sFriction + B.physics_model.sFriction * B.physics_model.sFriction);
        df = Math.sqrt(A.physics_model.dFriction * A.physics_model.dFriction + B.physics_model.dFriction * B.physics_model.dFriction);
        e = Math.min(A.physics_model.restitution, B.physics_model.restitution);
    }

    public void solveCollision(){ // Generate contact information
        normal = new Point2D(0,0);
        displacement = 0;
        contacts = Utility_Functions.IntersectsPoints(A, B);

        Triple<Double,Point2D,Short> length_and_normal1 = FindAxisLeastPenetration(A,B);
        Triple<Double,Point2D,Short> length_and_normal2 = FindAxisLeastPenetration(B,A);

        // choose max deep
        if (length_and_normal1.getOne() > length_and_normal2.getOne()) {
            normal = length_and_normal1.getTwo();
            displacement = length_and_normal1.getOne();

        }
        else {
            normal = length_and_normal2.getTwo();
            displacement = length_and_normal2.getOne();
            normal = normal.multiply(-1);

        }
        displacement = -displacement;
    }

    /*
     * get the deepest point for block of normal
     * */
    private Point getDepthPoint(List<Point> points,Point2D normal,Block block){
        double min_val = Double.NaN;
        double val;
        Point bestPoint = null;
        for(Point point : points){
            if(block.hasPoint(point)) {
                val = point.getPos().dotProduct(normal);
                if (Double.isNaN(min_val) || val < min_val) {
                    min_val = val;
                    bestPoint = point;
                }
            }
        }
        return bestPoint;
    }

    /*
     * get the deepest point of normal
     * */
    private Point getDepthPoint(List<Point> points,Point2D normal){
        double min_val = points.get(0).getPos().dotProduct(normal);
        double val;
        Point bestPoint = points.get(0);
        for(Point point : points){
            val = point.getPos().dotProduct(normal);
            if(val < min_val){
                min_val = val;
                bestPoint = point;
            }
        }
        return bestPoint;
    }

    /*
     * get side of contact
     * */
    private Pair<Point,Point> getSide(Point contact,Point2D normal){
        if(A.hasPoint(contact)){
            return getSideBlock(A,contact,normal);
        }
        return getSideBlock(B,contact,normal);
    }

    /*
     * get contact size of block
     * */
    private Pair<Point,Point> getSideBlock(Block block, Point contact, Point2D normal){
        int index = block.getIndexPoint(contact);
        if (index == -1){
            int index2 = block.getIndexOfAllPoints(contact);
            return block.getSide(index2/block.pointsInSize);
        }

        Point a = block.getPointList().get((4 + index + 1) % 4);
        Point b = block.getPointList().get((4 + index - 1) % 4);

        Point2D ca = a.getPos().subtract(contact.getPos()).normalize();
        Point2D cb = b.getPos().subtract(contact.getPos()).normalize();

        if (Math.abs(ca.dotProduct(normal)) < Math.abs(cb.dotProduct(normal))) {
            return new Pair<>(contact, a);
        } else {
            return new Pair<>(contact, b);
        }
    }


    /*
     * Get os of Intersection
     * */
    Triple<Double,Point2D,Short> FindAxisLeastPenetration(Block blockA, Block blockB){
        List<Point2D> normals = blockA.getNormals();
        List<Point2D> pointsA = blockA.getPoints();
        List<Point2D> pointsB = blockB.getPoints();

        short BestIndex = -1;
        double BestDistance = -Double.POSITIVE_INFINITY;
        double dist;

        for(short i =0;i < normals.size();i++){
            //  получаем нормаль
            Point2D normal = normals.get(i);

            // опорная точка по нормале в блоке B
            Point2D s = GetSupport(normal.multiply(-1),pointsB);

            // вершина на ребре блока A
            Point2D v = pointsA.get(i);

            dist = normal.dotProduct(s.subtract(v));


            // наибольшее расстояние
            if(dist > BestDistance){
                BestDistance = dist;
                BestIndex = i;
            }
        }
        return new Triple<>(BestDistance, normals.get(BestIndex),BestIndex);
    }

    /*
     * extreme point on normal
     * */
    private Point2D GetSupport(Point2D direction,List<Point2D> points)
    {
        if(points == null || points.size() == 0){
            return null;
        }
        double BestDot = direction.dotProduct(points.get(0));
        Point2D BestPoint = points.get(0);
        double dot;
        for(Point2D point2D : points){
            dot = direction.dotProduct(point2D);
            if(dot > BestDot){
                BestDot = dot;
                BestPoint = point2D;
            }
        }
        return BestPoint;
    }



    @Deprecated
    public void applyImpulse(){  // solve impulse
        // We need coordinates of center mass
        Point2D centerA = A.centerBlock();
        Point2D centerB = B.centerBlock();

        for (Point contact_point : contacts){
            Point2D contact = new Point2D(contact_point.circle.getCenterX(),
                    contact_point.circle.getCenterY());


            Point2D RA = contact.subtract(centerA);
            Point2D RB = contact.subtract(centerB);
            final double inertiaA = A.physics_model.inertia;
            final double inertiaB = B.physics_model.inertia;

            // Разрешающая скорость
            Point2D RV = getResultSpeed(RA, RB);
            double RVContact = RV.dotProduct(normal);
            // Если положительно, то точки удаляются, либо движение сонаправлено
            if (RVContact > 0) {
                return;
            }

            double RACrossN = RA.getX() * normal.getY() - RA.getY() * normal.getX();
            double RBCrossN = RB.getX() * normal.getY() - RB.getY() * normal.getX();

            double iMassSum = 1.0 / A.physics_model.mass + 1.0 / B.physics_model.mass
                    + RACrossN * RACrossN / inertiaA
                    + RBCrossN * RBCrossN / inertiaB;

            double j = -(1.0 + e) * RVContact / iMassSum;
            j /= contacts.size();

            Point2D impulse = normal.multiply(j);
            A.physics_model.applyImpulse(contact_point,impulse.multiply(-1.0), RA, inertiaA);
            B.physics_model.applyImpulse(contact_point,impulse, RB, inertiaB);

            // friction impulse
            RV = getResultSpeed(RA, RB);
            Point2D tang = RV.subtract(normal.multiply(RV.dotProduct(normal)));
            tang = tang.normalize();

            // j for tangent force
            double jt = -(RV.dotProduct(tang)) / iMassSum;
            jt /= contacts.size();

            if (Math.abs(jt) < 0.1){
                return;
            }

            Point2D tangentImpulse = Math.abs(jt) < j * sf ? tang.multiply(jt) : tang.multiply(-j * df);


            A.physics_model.applyImpulse(contact_point,tangentImpulse.multiply(-1.0), RA, inertiaA);
            B.physics_model.applyImpulse(contact_point,tangentImpulse, RB, inertiaB);
        }
    }

    public void posCorr()  {

        Point2D collisionVector = normal.multiply(displacement);

        if (!B.isPowers){
            return;
        }

        boolean[] flags = {false, false, false, false};

        for (Point contact : contacts){

            if (!B.hasPoint(contact)) {
                continue;
            }

            contactSide = getSide(contact, normal);
            Point2D E1 = contactSide.getKey().getPos();
            Point2D E2 = contactSide.getValue().getPos();

            double t;
            if (Math.abs(E1.getX() - E2.getX()) >
                    Math.abs(E1.getY() - E2.getY())){
                t = (contact.getPos().getX() - collisionVector.getX() - E1.getX()) / (E2.getX() - E1.getX());
            }
            else{
                t = (contact.getPos().getY() - collisionVector.getY() - E1.getY()) / (E2.getY() - E1.getY());
            }

            double lambda = 1.0 / (t*t + (1-t)*(1-t)) ;


            contactSide.getKey().setPos(contactSide.getKey().getPos().add(collisionVector.multiply((1 - t) * 0.5 * lambda)));
            contactSide.getValue().setPos(contactSide.getValue().getPos().add(collisionVector.multiply(t * 0.5 * lambda)));


        }
    }

    private Point2D getResultSpeed(final Point2D RA, final Point2D RB){
        Point2D tempB = B.physics_model.getVelocity().add(Utility_Functions.Cross(B.physics_model.getWVelocity(), RB));
        Point2D tempA = A.physics_model.getVelocity().add(Utility_Functions.Cross(A.physics_model.getWVelocity(), RA));

        return tempB.subtract(tempA);
    }

    private double e;
    private double sf;
    private double df;
    public Point2D normal;           // From A to B
    public List<Point> contacts;
    public Pair<Point, Point> contactSide;
    public double displacement;     // Depth of collision
}