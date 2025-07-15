package com.pc.crawler.controller;

import com.pc.crawler.Crawler;
import com.pc.crawler.model.Ocurrencia;
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
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controlador de la ventana Diccionario. Gestiona la visualización y exportación del diccionario
 * del Crawler, permitiendo al usuario ver y exportar las palabras almacenadas.
 * Implementa Initializable para inicializar la interfaz y los datos al abrir la ventana.
 */
public class DiccCtrl implements Initializable {

    /**
     * Stage de la ventana Diccionario.
     */
    private static Stage myStage;

    /**
     * ListView para mostrar el diccionario.
     */
    @FXML
    private ListView lv_dicc;

    /**
     * Botón para exportar el diccionario.
     */
    @FXML
    private Button btn_exp_dicc;

    /**
     * Botón para cancelar y cerrar la ventana Diccionario.
     */
    @FXML
    private Button btn_cancel_dicc;

    /**
     * Inicializa la ventana Diccionario, llenando la lista y configurando los eventos de los botones.
     * @param location URL de localización
     * @param resources Recursos utilizados
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        llenarListaDicc();
        exportDicc();
        btn_cancel_dicc.addEventHandler(ActionEvent.ACTION, event -> {
            myStage.close();
        });
    }

    /**
     * Asigna el evento de exportación al botón correspondiente. Muestra un mensaje de éxito si la exportación es correcta.
     */
    private void exportDicc() {
        btn_exp_dicc.addEventHandler(ActionEvent.ACTION, event ->  {
            if (exportDiccOp()){
                System.out.println("Fichero diccionario.txt generado correctamente.");
                MainCtrl.showAlert(Labels.information_dialog_title+" - Diccionario exportado",
                        "DICCIONARIO EXPORTADO",
                        "Se generado un fichero llamado diccionario.txt que contiene el diccionario del Crawler.",
                        Alert.AlertType.INFORMATION);
            }
        });
    }

    /**
     * Exporta el diccionario a un archivo de texto.
     * @return True si se ha exportado correctamente, false en caso contrario
     */
    private boolean exportDiccOp() {
        File file = new File(Labels.dicc_file_exp);
        FileWriter f = null;
        PrintWriter pw = null;
        try {
            if (file.exists())
                file.delete();
            f = new FileWriter(file);
            pw = new PrintWriter(f);

            //pw.write("");//Eliminamos el contenido del fichero

            for (Map.Entry<String, Ocurrencia> entry : Crawler.getInstance().getDiccionario().entrySet()) {
                pw.println(entry.getKey());
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
     * Llena la lista del diccionario con las palabras almacenadas en el Crawler.
     */
    private void llenarListaDicc() {
        if (Crawler.getInstance().getDiccionario() != null)
            for (Map.Entry<String, Ocurrencia> entry : Crawler.getInstance().getDiccionario().entrySet()) {
                String k = entry.getKey();
                lv_dicc.getItems().add(k);
                System.out.println("Se ha añadido " + k + " a la vista del diccionario.");
            }
    }

    /**
     * Establece el Stage de la ventana Diccionario.
     * @param myStage Nuevo stage.
     */
    public static void setStage(Stage myStage) {
        DiccCtrl.myStage = myStage;
    }
}
