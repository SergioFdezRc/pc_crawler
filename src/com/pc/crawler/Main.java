package com.pc.crawler;

import com.pc.crawler.controller.MainCtrl;
import com.pc.crawler.model.helper.Labels;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("view/indexView.fxml"));
        root.getStylesheets().add("com/julliadevs/crawler/resources/css/bootstrap3.css");
        stage.setResizable(false);
        stage.setTitle(Labels.nombre_titulo_ventana);
        stage.getIcons().add(new Image(getClass().getResourceAsStream(Labels.icon_app)));
        stage.setScene(new Scene(root, 800, 600));
        MainCtrl.setStage(stage);
        stage.show();
    }
}
