package com.example.superproject;


import javafx.geometry.Point2D;
import javafx.util.Pair;


import java.util.List;

public class Manifold {
    public Block A;
    public Block B;
    public boolean isCollide = true;

    public Manifold(){}
    public Manifold(Block A, Block B){
        this.A = A;
        this.B = B;
        //isCollide = !A.bindBlocks.contains(B);


        sf = Math.sqrt(A.physics_model.sFriction * A.physics_model.sFriction + B.physics_model.sFriction * B.physics_model.sFriction);
        df = Math.sqrt(A.physics_model.dFriction * A.physics_model.dFriction + B.physics_model.dFriction * B.physics_model.dFriction);
        e = Math.min(A.physics_model.restitution, B.physics_model.restitution);
    }

    public void setBlocks(Block A, Block B){
        this.A = A;
        this.B = B;
        //isCollide = !A.bindBlocks.contains(B);


        sf = Math.sqrt(A.physics_model.sFriction * A.physics_model.sFriction + B.physics_model.sFriction * B.physics_model.sFriction);
        df = Math.sqrt(A.physics_model.dFriction * A.physics_model.dFriction + B.physics_model.dFriction * B.physics_model.dFriction);
        e = Math.min(A.physics_model.restitution, B.physics_model.restitution);
    }

    public void solveCollision(){ // Generate contact information
        normal = new Point2D(0,0);
        displacement = 0;
        contacts = Utility_Functions.IntersectsPoints(A, B);

        // TODO: watch here!
        //A.physics_model.contacts = contacts;



        Triple<Double,Point2D,Short> length_and_normal1 = FindAxisLeastPenetration(A,B);
        Triple<Double,Point2D,Short> length_and_normal2 = FindAxisLeastPenetration(B,A);

        // choose max deep
        if (length_and_normal1.getOne() > length_and_normal2.getOne()) {
            normal = length_and_normal1.getTwo();
            displacement = length_and_normal1.getOne();

            // ADD UPDATE
            contactSide = A.getSize(length_and_normal1.getThree());

        }
        else {
            normal = length_and_normal2.getTwo();
            displacement = length_and_normal2.getOne();
            normal = normal.multiply(-1);

            // ADD UPDATE
            contactSide = B.getSize(length_and_normal2.getThree());

        }
        displacement = -displacement;
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

    // TODO: Исправить эту функцию
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
        B.MoveTo(B.getXY().add(normal.multiply(displacement)));
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
    public Pair<Point2D,Point2D> contactSide;
    public double displacement;     // Depth of collision
}