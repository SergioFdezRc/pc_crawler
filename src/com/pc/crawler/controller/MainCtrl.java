package com.pc.crawler.controller;

import com.pc.crawler.Crawler;
import com.pc.crawler.model.helper.Labels;
import com.pc.crawler.model.helper.ModelTable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controlador principal de la aplicación. Gestiona la lógica de interacción entre la vista principal y el modelo,
 * así como la configuración, búsqueda, exportación y manejo de directorios recientes.
 * Implementa Initializable para inicializar la interfaz y los datos al arrancar la aplicación.
 */
public class MainCtrl implements Initializable {

    /**
     * Stage principal de la aplicación.
     */
    private static Stage myStage;

    /**
     * Campo de texto para la palabra a buscar.
     */
    @FXML
    private TextField tf_palabra;

    /**
     * Campo de texto para el directorio seleccionado.
     */
    @FXML
    private TextField tf_dircectorio;

    /**
     * Botón para iniciar la búsqueda.
     */
    @FXML
    private Button btn_buscar;

    /**
     * Botón para seleccionar el directorio.
     */
    @FXML
    private Button btn_sel_dir;

    /**
     * Botón para configurar el crawler.
     */
    @FXML
    private Button btn_configurar;


    /**
     * Columna de la tabla que muestra el documento.
     */
    @FXML
    private TableColumn col_ft;

    /**
     * Columna de la tabla que muestra el documento.
     */
    @FXML
    private TableColumn col_doc;

    /**
     * Columna de la tabla que muestra el ranking.
     */
    @FXML
    private TableColumn col_ranking;

    /**
     * Botón para exportar los resultados.
     */
    @FXML
    private Button btn_export;

    /**
     * Panel desplegable para mostrar el log.
     */
    @FXML
    private TitledPane tp_mostrar_log;

    /**
     * Etiqueta para mostrar el término buscado.
     */
    @FXML
    private Label lbl_termino;

    /**
     * Instancia del crawler principal.
     */
    private Crawler myCrawler;

    /**
     * Lista de directorios recientes utilizados.
     */
    private ArrayList<String> recientesArray;
    /**
     * Tabla para mostrar los resultados.
     */
    @FXML
    private TableView table;

    /**
     * Menú de directorios recientes.
     */
    @FXML
    private Menu mi_recientes;

    /**
     * Opción de menú para salir de la aplicación.
     */
    @FXML
    private MenuItem mi_salir;

    /**
     * Opción de menú para abrir un directorio.
     */
    @FXML
    private MenuItem mi_abrir;

    /**
     * Opción de menú para mostrar la ventana "Acerca de".
     */
    @FXML
    private MenuItem mi_about;

    /**
     * Opción de menú para mostrar el diccionario.
     */
    @FXML
    private MenuItem mi_dicc;

    /**
     * Opción de menú para mostrar la ventana FAT.
     */
    @FXML
    private MenuItem mi_fat;

    /**
     * ArrayList que almacena los modelos de la tabla para mostrar los resultados.
     */
    private ArrayList<ModelTable> modelTable;


    /**
     * Inicializa la interfaz y los datos necesarios al arrancar la aplicación.
     * Configura listeners, carga directorios recientes y prepara la configuración inicial.
     * @param location URL de localización
     * @param resources Recursos utilizados
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        recientesArray = new ArrayList<>();
        myCrawler = Crawler.getInstance();

        /* Si no es la primera vez que usamos el programa, tendremos ya un diccionario, una fat y
         * una lista de directorios recientes, por lo que preconfiguraremos el programa con los parámetros
         * del último directorio que usamos en el programa la última vez que lo usamos. */
        if (existenFicherosSer()) {
            leerDirectorios();
            myCrawler.configurar();
            System.out.println("Se ha configurado el diccionario y la fat del directorio " + myCrawler.getOrigen());
        }

        //MARCO DE OPERACIONES
        listeners();
        aboutUsOp();
        verDiccionarioOp();
        verFatOp();
        abrir();
        configurar();
        buscar();
        exportar();
        salir();

    }

    /**
     * Muestra la ventana de about
     */
    private void aboutUsOp() {
        mi_about.addEventHandler(ActionEvent.ACTION, event -> {
            Stage stage = new Stage();
            lanzarApp(stage, Labels.about_view, Labels.stylesheet_about_view, Labels.title_about_view, Labels.icon_app_ctrl);
            AboutCtrl.setStage(stage);
        });
    }

    /**
     * Muestra la ventana de diccionario
     */
    private void verDiccionarioOp() {
        mi_dicc.addEventHandler(ActionEvent.ACTION, event -> {
            Stage stage = new Stage();
            lanzarApp(stage, Labels.dicc_view, null, Labels.title_dicc_view, Labels.icon_app_ctrl);
            DiccCtrl.setStage(stage);
        });
    }


    /**
     * Muestra la ventana de fat
     */
    private void verFatOp() {
        mi_fat.addEventHandler(ActionEvent.ACTION, event -> {
            Stage stage = new Stage();
            lanzarApp(stage, Labels.fat_view, null, Labels.title_fat_view, Labels.icon_app_ctrl);
            FatCtrl.setStage(stage);
        });
    }

    /**
     * Lanza una vista específica en un nuevo Stage.
     * @param stage Stage a lanzar
     * @param vista Vista a lanzar
     * @param stylesheet Estilo de la vista
     * @param title Título de la vista
     * @param icon Icono de la vista
     */
    private void lanzarApp(Stage stage, String vista, String stylesheet, String title, String icon) {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource(vista));
            root.getStylesheets().add("com/julliadevs/crawler/resources/css/bootstrap3.css");
            if (stylesheet != null)
                root.getStylesheets().add("com/julliadevs/crawler/resources/css/" + stylesheet + ".css");
            Scene scene = new Scene(root);
            stage.setTitle(title);
            if (icon != null)
                stage.getIcons().add(new Image(getClass().getResourceAsStream(icon)));
//            stage.setAlwaysOnTop(true);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Pone el último directorio utilizado en el campo de directorios.
     */
    private void setRecientesMenu() {
        tf_dircectorio.setText(recientesArray.get(0));
    }

    /**
     * Configura los directorios recientes que el usuario haya usado en ejecuciones del programa anteriores.
     */
    private void leerDirectorios() {
        leerFicheroRecientes(); //Lee el fichero de recientes
        //Le damos la vuelta al array
        recientesArray = clonarInvertido(); //Clona el array de forma inversa
        cargarMenuRecientes(); //Carga en el menú los recientes
        setRecientesMenu(); //Setea en el campo de directorio el más reciente usado

    }

    /**
     * Carga en el menú de recientes tantos items como líneas tenga el fichero de recientes.
     * Se les asigna un ID y un eventHandler para manejarlos.
     */
    private void cargarMenuRecientes() {
        MenuItem menuItem;
        for (int i = 0; i < recientesArray.size(); i++) {
            menuItem = new MenuItem(recientesArray.get(i));
            mi_recientes.getItems().add(menuItem);
            menuItem.setId("reciente_" + i);
            MenuItem finalMenuItem = menuItem;
            menuItem.addEventHandler(ActionEvent.ACTION, event -> {
                tf_dircectorio.setText(finalMenuItem.getText());
                myCrawler.setOrigen(finalMenuItem.getText());
                escribirReciente(finalMenuItem.getText());
                limpiarCache();
            });
        }
    }

    /**
     * Exporta los resultados de la tabla a un archivo Excel.
     */
    private void exportarExcel() {
        File excel;
        ArrayList<String> headers = new ArrayList<>();
        headers.add(col_doc.getText());
        headers.add(col_ft.getText());
        headers.add(col_ranking.getText());

        String nameFile = tf_palabra.getText() + "-log";
        String excel_ext = ".xlsx";
        XSSFWorkbook libro = new XSSFWorkbook();
        XSSFSheet hoja1 = libro.createSheet("Crawler");

        CellStyle style = libro.createCellStyle();
        Font font = libro.createFont();
        font.setBold(true);
        style.setFont(font);

        for (int i = 0; i < modelTable.size() + 1; i++) {
            //Cremos las filas
            XSSFRow row = hoja1.createRow(i);
            for (int j = 0; j < table.getColumns().size(); j++) {
                if (i == 0) {
                    XSSFCell cabecera = row.createCell(j);
                    cabecera.setCellStyle(style);
                    cabecera.setCellValue(headers.get(j));
                } else {
                    XSSFCell celda = row.createCell(j);
                    if (j == 0)
                        celda.setCellValue(modelTable.get((i - 1) + j).getUrl());
                    if (j == 1)
                        celda.setCellValue(modelTable.get((i - 1) + j - 1).getFrecuencia());
                    if (j == 2)
                        celda.setCellValue(modelTable.get((i - 1) + j - 2).getRanking());
                }
            }
        }

        excel = new File(nameFile + excel_ext);
        try (FileOutputStream fos = new FileOutputStream(excel)) {
            if (excel.exists()) {
                excel.delete();
                System.out.println("El fichero '" + nameFile + excel_ext + "' ya existía y se ha eliminado.");
            }
            libro.write(fos);
            fos.flush();
            fos.close();
            System.out.println("Archivo '" + nameFile + excel_ext + "' creado correctamente.");
            //Alerta
            showAlert(Labels.information_dialog_title + " - " + Labels.fichero_generado, "El Fichero " + nameFile + excel_ext + " ha sido generado.", null, Alert.AlertType.INFORMATION);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Construye un diálogo personalizado.
     *
     * @param titulo Título que tendrá el diálogo
     * @param header Cabecera del diálogo
     * @param cuerpo Contenido del diálogo
     * @param type Tipo de diálogo
     * @return Devuelve la opción pulsada del cuadro de diálogo.
     */
    public static Optional<ButtonType> showAlert(String titulo, String header, String cuerpo, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titulo);
        alert.setHeaderText(header);
        alert.setContentText(cuerpo);
        return alert.showAndWait();
    }

    /**
     * Clona de forma inversa un ArrayList.
     *
     * @return Devuelve una copia del array invertido.
     */
    private ArrayList<String> clonarInvertido() {
        int max = recientesArray.size();
        ArrayList<String> array_copy = new ArrayList<>();
        for (int i = 0; i < recientesArray.size(); i++) {
            array_copy.add(recientesArray.get(max - 1));
            max--;
        }
        return array_copy;
    }

    /**
     * Llamada a los listeners de los items que tengamos.
     */
    private void listeners() {
        tf_listeners();
    }

    /**
     * Añade listeners a los campos de texto tf_dircectorio y tf_palabra para habilitar o deshabilitar
     * los botones btn_configurar y btn_buscar respectivamente, dependiendo de si los campos están vacíos o no.
     * <p>
     * - Cuando tf_dircectorio no está vacío, habilita btn_configurar; si está vacío, lo deshabilita.
     * - Cuando tf_palabra no está vacío (ignorando espacios), habilita btn_buscar; si está vacío, lo deshabilita.
     */
    private void tf_listeners() {
        tf_dircectorio.textProperty().addListener((observable, oldDirectory, newDirectory) -> {
            if (!newDirectory.isEmpty()) {
                btn_configurar.setDisable(false);
            } else {
                btn_configurar.setDisable(true);
            }
        });

        tf_palabra.textProperty().addListener((observable, oldValue, newValue) -> {
            newValue = newValue.trim();
            if (!newValue.isEmpty()) {
                btn_buscar.setDisable(false);
            } else
                btn_buscar.setDisable(true);
        });
    }


    /**
     * Busca una palabra en el diccionario y actualiza la tabla de resultados.
     */
    private void buscar() {
        btn_buscar.addEventHandler(ActionEvent.ACTION, event -> {
            Object o = myCrawler.getDiccionario().get(tf_palabra.getText());
            myCrawler.resetRanking();
            table.getItems().clear();
            if (o == null) {
                showAlert(Labels.information_dialog_title + " - " + Labels.not_found,
                        Labels.information_dialog_headerText_not_found,
                        tf_palabra.getText() + " no existe en el diccionario",
                        Alert.AlertType.INFORMATION);
                btn_export.setDisable(true);
                System.out.println(tf_palabra.getText() + " No esta en diccionario");
            } else {
                myCrawler.generaMuestraRanking(o, tf_palabra.getText());
                cargarTabla();
                btn_export.setDisable(false);
            }

        });
    }

    /**
     * Asignamos un ActionEvent exportar al botón btn_export. Esto hará que cuando pulsemos el botón "Exportar"
     * se llame al método exportarExcel() que leerá la tabla en el caso en el que tenga datos y creará un fichero
     * excel con extensión .xlsx con el contenido de la tabla.
     */
    private void exportar() {
        btn_export.addEventHandler(ActionEvent.ACTION, event -> {
            //Generar csv
            exportarExcel();
        });
    }

    /**
     * Asignamos un ActionEvent al botón btn_sel_dir y al item "Abrir" del menú. Ambos, al hacer clic sobre cualquiera de ellos,
     * hará una llamada al método selDirectorio, lo que nos abrirá un cuado de diálogo para seleccionar un directorio desde el
     * que partir.
     */
    private void abrir() {

        //Opción abrir desde el seleccionar directorio
        btn_sel_dir.addEventHandler(ActionEvent.ACTION, event -> {
            selDirectorio();
        });

        //Opción abrir del menú
        mi_abrir.addEventHandler(ActionEvent.ACTION, event -> {
            selDirectorio();
        });
    }

    /**
     * Comprueba si los ficheros de configuración existen.
     *
     * @return true si existen, false si no existen
     */
    private boolean existenFicherosSer() {
        return new File(Labels.nombre_fichero_fat).exists()
                && new File(Labels.nombre_fichero_map).exists()
                && new File(Labels.nombre_fichero_tes).exists();
    }

    /**
     * Elimina, si ya existen, los ficheros contenidos en el array pasado por parámetro.
     *
     * @param files Lista de ficheros a eliminar
     */
    private void deleteFiles(ArrayList<File> files) {
        for (File file : files) {
            if (file.exists())
                if (file.delete())
                    System.out.println("Fichero " + file.getName() + " elimnado.");
        }
    }

    /**
     * Carga la tabla con el contenido de la clase ModelTable creada exclusivamente para este fin.
     */
    private void cargarTabla() {
        table.getItems().clear(); //Limpiamos los campos de la tabla cada vez que entramos al método
        modelTable = new ArrayList<>();

        // Cargamos el arrayList de la clase ModelTable
        for (int i = 0; i < myCrawler.getRankingSize(); i++) {
            String[] urlTroceada = myCrawler.getFat(myCrawler.getRanking(i).getUrl()).getUrl().split("\\\\");
            String url = urlTroceada[urlTroceada.length - 1];
            modelTable.add(new ModelTable(url, myCrawler.getRanking(i).getFT(), myCrawler.getRanking(i).getRanking()));
        }

        ObservableList<ModelTable> modelTableOL = FXCollections.observableArrayList(this.modelTable);

        //Seteamos los valores a las celdas con el contenido de la Lista Observable de la clase ModelTable
        col_doc.setCellValueFactory(new PropertyValueFactory<ModelTable, String>(Labels.crawler_url));
        col_ft.setCellValueFactory(new PropertyValueFactory<ModelTable, String>(Labels.crawler_frecuencia));
        col_ranking.setCellValueFactory(new PropertyValueFactory<ModelTable, String>(Labels.crawler_ranking));
        table.setItems(modelTableOL); // Y seteamos los items a la tabla.

        System.out.println("Num Eltos Tabla:" + table.getItems().size());

    }

    /**
     * Comprueba si el String que le entra por parámetro existe en el fichero
     * de recientes y, si existe, elimina la entrada en la posición dada.
     *
     * @param reciente Nueva ruta a añadir en recientesMenu.
     * @return true si existe, false si no.
     */
    private boolean comprobarReciente(String reciente) {
        for (int i = 0; i < recientesArray.size(); i++) {
            if (recientesArray.get(i).equals(reciente)) {
                recientesArray.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Lee el fichero de recientes y lo almacena en un ArrayList para tratar con él.
     */
    private void leerFicheroRecientes() {
        File recientesFile = new File(Labels.nombre_fichero_recientes);
        FileReader fr = null;
        try {
            if (recientesFile.exists()) {
                fr = new FileReader(recientesFile);
                BufferedReader br = new BufferedReader(fr);
                String linea;
                while ((linea = br.readLine()) != null) {
                    recientesArray.add(linea);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fr)
                    fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Muestra un cuadro de diálogo que permite seleccionar un directorio del equipo.
     * Si el directorio seleccionado es válido, establece la ruta al campo directorio, llama al método limpiarCache(),
     * establece el origen desde el que el crawler realizará su función y escribe en el fichero de recientes la ruta seleccionada.
     */
    private void selDirectorio() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Selecciona un directorio");
        System.out.println(System.getProperty("user.home") + "\\desktop");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home") + "\\desktop\\MiPCCrawler\\test"));
        File directory = directoryChooser.showDialog(myStage);
        if (directory != null) {
            tf_dircectorio.setText(directory.getAbsolutePath());
            limpiarCache();
            myCrawler.setOrigen(directory.getAbsolutePath());
            escribirReciente(directory.getAbsolutePath());
        }
    }

    /**
     * Crea un ArrayList con los ficheros que usamos y, después, llama al método deleteFiles que comprobará si existe cada fichero y, si existe, lo elimina.
     */
    private void limpiarCache() {
        ArrayList<File> myFiles = new ArrayList<>();
        myFiles.add(new File(Labels.nombre_fichero_map));
        myFiles.add(new File(Labels.nombre_fichero_tes));
        myFiles.add(new File(Labels.nombre_fichero_fat));
        deleteFiles(myFiles);
    }

    /**
     * Vuelca una nueva ruta usada para configurar el programa en el fichero de rutas recientes.
     * Para ello, comprueba si existe la ruta ya y, si existe, la establece como la más reciente.
     * Si no existe, simplemente la añade al fichero.
     *
     * @param absolutePath Ruta del nuevo directorio.
     */
    private void escribirReciente(String absolutePath) {
        if (comprobarReciente(absolutePath))
            System.out.println("La ruta ya se ha usado con anterioridad.");
        recientesArray.add(0, absolutePath);
        recientesArray = clonarInvertido();
        //Escribimos normal en el fichero
        FileWriter f = null;
        PrintWriter pw = null;
        try {
            f = new FileWriter(Labels.nombre_fichero_recientes);
            pw = new PrintWriter(f);
            pw.write("");//Eliminamos el contenido del fichero

            for (String s : recientesArray) {
                pw.println(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
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
     * Crea un ActionEvent al item Salir del menú que llamará al método opSalir() cuando se haga clic sobre él.
     */
    private void salir() {
        mi_salir.addEventHandler(ActionEvent.ACTION, event -> {
            opSalir();
        });
    }

    /**
     * Muestra un cuadro de diálogo de tipo Confirmation. Si se pulsa "Ok", la aplicación se cerrará. En caso contrario,
     * se volverá al flujo de ejecución normal del programa.
     */
    private void opSalir() {
        Optional<ButtonType> resultado = showAlert(Labels.confirmation_dialog_title,
                Labels.confirmation_dialog_headerText_closeApp,
                Labels.confirmation_dialog_contentText_closeApp,
                Alert.AlertType.CONFIRMATION);

        //Si pulsamos el botón OK, cerramos.
        if (resultado.get() == ButtonType.OK)
            myStage.close();
        else
            System.out.println("Se ha cancelado la operación \"Salir\".");
    }

    /**
     * Crea un ActionEvent al botón btn_configurar que llama al método configurar() de la clase Crawler cada vez que se haga clic sobre él.
     */
    private void configurar() {
        btn_configurar.addEventHandler(ActionEvent.ACTION, event -> {
            myCrawler.configurar();
        });

    }

    /**
     * Establece un nuevo Stage a la clase.
     *
     * @param myStage Nuevo stage.
     */
    public static void setStage(Stage myStage) {
        MainCtrl.myStage = myStage;
    }
}
