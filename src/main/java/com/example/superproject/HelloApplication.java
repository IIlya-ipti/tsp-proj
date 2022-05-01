package com.example.superproject;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {


    @Override
    public void start(Stage stage) {
        Game game = new Game();
        Stool stool1 = new Stool(new Point2D(150,360),300,50,50,200,game, Color.AQUAMARINE);
        //Stool stool2 = new Stool(new Point2D(150,360),300,50,50,200,game, Color.BLACK);
        stool1.setParameters(0.5);
        //stool2.setParameters(0.2);
        game.initObjects();
        game.run(stage);
        System.out.println(game.TrainRun());
    }

    public static void main(String[] args) {
        launch();
    }
}