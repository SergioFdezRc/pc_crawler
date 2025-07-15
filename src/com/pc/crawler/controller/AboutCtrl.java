package com.pc.crawler.controller;

import com.pc.crawler.model.helper.Labels;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador de la ventana Acerca de (About). Gestiona la visualización de la información del autor,
 * mostrando su imagen, biografía y permitiendo cerrar la ventana.
 * Implementa Initializable para inicializar la interfaz y los datos al abrir la ventana.
 */
public class AboutCtrl implements Initializable {

    /**
     * Stage de la ventana About.
     */
    private static Stage myStage;

    /**
     * Etiqueta para el nombre del autor.
     */
    @FXML
    private Label label_autor;

    /**
     * Círculo para mostrar la imagen del autor.
     */
    @FXML
    private Circle circulo_autor;

    /**
     * Área de texto para la biografía del autor.
     */
    @FXML
    private TextArea desc_autor;

    /**
     * Botón para cerrar la ventana About.
     */
    @FXML
    private Button btn_cancel_about;

    /**
     * Inicializa la ventana About, mostrando la imagen y biografía del autor, y configurando el botón de cierre.
     * @param location URL de localización
     * @param resources Recursos utilizados
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String urlAutor = System.getenv("AUTHOR_IMG_PATH");
        crearCirculo(circulo_autor, urlAutor);
        desc_autor.setText(Labels.biografia_autor);
        btn_cancel_about.addEventHandler(ActionEvent.ACTION, event -> {
            myStage.close();
        });
    }

    /**
     * Crea un círculo con la imagen del autor y aplica un efecto de sombra.
     * @param circle Círculo a crear
     * @param url URL de la imagen
     */
    private void crearCirculo(Circle circle, String url) {
        circle.setStroke(null);
        Image im = null;
        try {
            im = new Image(new FileInputStream(url));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        circle.setFill(new ImagePattern(im));
        circle.setEffect(new DropShadow(+8d, 2d, +6d, Color.SILVER));
    }

    /**
     * Establece el Stage de la ventana About.
     * @param myStage Nuevo stage.
     */
    public static void setStage(Stage myStage) {
        AboutCtrl.myStage = myStage;
    }
}
