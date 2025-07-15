package com.pc.crawler.controller;

import com.pc.crawler.Crawler;
import com.pc.crawler.model.helper.Labels;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador de la ventana FAT. Gestiona la visualización y exportación de la FAT (File Allocation Table)
 * del Crawler, permitiendo al usuario ver y exportar la información a un archivo.
 * Implementa Initializable para inicializar la interfaz y los datos al abrir la ventana.
 */
public class FatCtrl implements Initializable {

    /**
     * Stage de la ventana FAT.
     */
    private static Stage myStage;

    /**
     * ListView para mostrar la FAT.
     */
    @FXML
    private ListView lv_fat;

    /**
     * Botón para exportar la FAT.
     */
    @FXML
    private Button btn_exp_fat;

    /**
     * Botón para cancelar y cerrar la ventana FAT.
     */
    @FXML
    private Button btn_cancel_fat;

    /**
     * Inicializa la ventana FAT, llenando la lista y configurando los eventos de los botones.
     * @param location URL de localización
     * @param resources Recursos utilizados
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        llenarListaFat();
        exportFat();
        btn_cancel_fat.addEventHandler(ActionEvent.ACTION, event -> {
            myStage.close();
        });
    }

    /**
     * Exporta la FAT a un archivo de texto.
     * @return True si se ha exportado correctamente, false en caso contrario
     */
    private boolean exportarFatOp() {
        File file = new File(Labels.fat_file_exp);
        FileWriter f = null;
        PrintWriter pw = null;
        try {
            if (file.exists())
                file.delete();
            f = new FileWriter(file);
            pw = new PrintWriter(f);

            for (int i = 0; i < Crawler.getInstance().getFatSize(); i++) {
                pw.println(Crawler.getInstance().getFat(i).getUrl());
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally { // nos aseguramos de que el fichero se cierre
            try {
                if (null != f) {
                    f.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Asigna el evento de exportación al botón correspondiente. Muestra un mensaje de éxito si la exportación es correcta.
     */
    private void exportFat() {
        btn_exp_fat.addEventHandler(ActionEvent.ACTION, event -> {
            if (exportarFatOp()) {
                System.out.println("Fichero fat generado correctamente.");
                MainCtrl.showAlert(Labels.information_dialog_title + " - FAT exportada",
                        "FAT EXPORTADA",
                        "Se generado un fichero llamado fat.txt que contiene toda la fat del Crawler.",
                        Alert.AlertType.INFORMATION);
            }
        });
    }

    /**
     * Llena la lista de la FAT con las rutas almacenadas en el Crawler.
     */
    private void llenarListaFat() {
        if (Crawler.getInstance().getFatSize() > 0) {
            for (int i = 0; i < Crawler.getInstance().getFatSize(); i++) {
                lv_fat.getItems().add(Crawler.getInstance().getFat(i).getUrl());
                System.out.println("Se ha añadido " + Crawler.getInstance().getFat(i).getUrl() + " a la vista de la FAT.");
            }
        }
    }


    /**
     * Establece el Stage de la ventana FAT.
     * @param myStage Nuevo stage.
     */
    public static void setStage(Stage myStage) {
        FatCtrl.myStage = myStage;
    }

}
