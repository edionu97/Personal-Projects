package User.FXML.Controlers;


import Utils.PDFs;
import com.itextpdf.text.PageSize;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;

import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.scene.control.*;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.controlsfx.control.PopOver;


import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;



public class PDFSaveController implements Initializable {

    @FXML
    private ComboBox <String> comboBoxFontSize;

    @FXML
    private ComboBox <String> comboBoxFontPageSize;

    @FXML
    private ComboBox <String> comboBoxFontTitleSize;

    @FXML
    private Button buttonPdfCancel;

    @FXML
    private Button buttonBrowse;

    @FXML
    private Button buttonPdfSave;

    @FXML
    private Label labelPDF;

    @FXML
    private Label iconPDF;

    private StatisticsController statisticsController;

    public PDFSaveController(){}

    private PopOver mainPopOver;

    private Stage stage;

    private Tooltip tooltip = new Tooltip();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        tooltip.setStyle("-fx-font-size:15;");

        labelPDF.setVisible(false);
        labelPDF.setText("Location not set");

        comboBoxFontSize.setItems(FXCollections.observableArrayList("10","11","12","13","14","15","17","18"));
        comboBoxFontSize.getSelectionModel().select("15");
        comboBoxFontPageSize.setItems(FXCollections.observableArrayList("A0","A1","A2","A3" ,"A4","A5","A6","A7"));
        comboBoxFontPageSize.getSelectionModel().select("A4");
        comboBoxFontTitleSize.setItems(FXCollections.observableArrayList("20","21","22","23","24","25","27","28"));
        comboBoxFontTitleSize.getSelectionModel().select("25");

        buttonPdfCancel.setOnAction(this::handleCancelEvent);

        buttonBrowse.setOnAction(this::handleButtonBrowse);

        buttonBrowse.hoverProperty().addListener(event->{

            if(buttonBrowse.isHover()){
                labelPDF.setVisible(true);return;
            }

            labelPDF.setVisible(false);
        });

    }

    private void handleCancelEvent(ActionEvent event){
        statisticsController.hideAllPopOvers();
    }

    public void setFileChooserStage(Stage stage){
        this.stage = stage;
    }

    void setMainPopOver(PopOver popOver){
        mainPopOver = popOver;
    }

    void resetAll(){
        buttonPdfSave.setTooltip(null);
        tooltip.setText("");
        iconPDF.setText("Save PDF");
        iconPDF.setStyle("-fx-text-fill: whitesmoke");
        buttonPdfSave.setStyle("-fx-border-color: radial-gradient(radius 180%,lightgray,gray)");
        buttonPdfSave.setDisable(false);
        labelPDF.setText("Location not set");
    }

    private void animateTitle(ActionEvent event){

        if(!tooltip.getText().equals(""))return;
        iconPDF.setStyle("-fx-text-fill: linear-gradient(whitesmoke,green);-fx-font-style: italic;");
        iconPDF.setText("PDF saved");

        Task <Void> another = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                Thread.sleep(2000);

                return null;
            }
        };

        another.setOnSucceeded(event2 -> {
            iconPDF.setText("Save PDF");
            iconPDF.setStyle("-fx-font-style: normal;-fx-text-fill: whitesmoke;");
        });

        new Thread(another).start();
    }

    private void handleButtonBrowse(ActionEvent event){
        mainPopOver.setAutoHide(false);
        File file = new FileChooser().showSaveDialog(stage);
        if(file != null)labelPDF.setText(file.getPath());
        mainPopOver.setAutoHide(true);
    }

    private void marksPdf(ActionEvent event){

        Task <Void> taskMarks = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                try {

                    buttonPdfSave.setDisable(true);

                    buttonPdfSave.setTooltip(null);

                    tooltip.setText("");

                    buttonPdfSave.setStyle("-fx-border-color: radial-gradient(radius 180%,lightgray,gray)");

                    statisticsController.createMarksPdf(
                            labelPDF.getText().equals("Location not set") ? null : labelPDF.getText(),
                            PageSize.getRectangle(comboBoxFontPageSize.getValue()),
                            Integer.parseInt(comboBoxFontSize.getValue()),
                            Integer.parseInt(comboBoxFontTitleSize.getValue())
                    );

                } catch (Exception e) {
                    String message = e.getMessage();

                    if (message == null) tooltip.setText("You must select a location\n");

                    if (message != null)
                        tooltip.setText(tooltip.getText() + "Another process is using file:" + labelPDF.getText() + " please close it and try again");

                    buttonPdfSave.setTooltip(tooltip);

                    buttonPdfSave.setStyle("-fx-border-color: linear-gradient(whitesmoke,darkred,whitesmoke);");

                } finally {
                    buttonPdfSave.setDisable(false);
                }

                return  null;
            }
        };


        taskMarks.setOnSucceeded(event1 -> animateTitle(null));

        new Thread(taskMarks).start();
    }

    private void consPdf(ActionEvent event){

        Task <Void> taskCons = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                try {

                    buttonPdfSave.setDisable(true);

                    buttonPdfSave.setTooltip(null);

                    tooltip.setText("");

                    buttonPdfSave.setStyle("-fx-border-color: radial-gradient(radius 180%,lightgray,gray)");

                    statisticsController.createConsPdf(
                            labelPDF.getText().equals("Location not set") ? null : labelPDF.getText(),
                            PageSize.getRectangle(comboBoxFontPageSize.getValue()),
                            Integer.parseInt(comboBoxFontSize.getValue()),
                            Integer.parseInt(comboBoxFontTitleSize.getValue())
                    );

                } catch (Exception e) {

                    buttonPdfSave.setStyle("-fx-border-color: linear-gradient(whitesmoke,darkred,whitesmoke);");
                    buttonPdfSave.setTooltip(tooltip);

                    String message = e.getMessage();

                    if (message == null) tooltip.setText("You must select a location\n");

                    if (message != null)
                        tooltip.setText(tooltip.getText() + "Another process is using file:" + labelPDF.getText() + " please close it and try again");

                } finally {
                    buttonPdfSave.setDisable(false);
                }

                return  null;
            }
        };

        taskCons.setOnSucceeded(event1 -> animateTitle(null));

        new Thread(taskCons).start();
    }

    private void promotedPdf(ActionEvent event){


        Task <Void> taskPassed = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                try {

                    buttonPdfSave.setDisable(true);

                    buttonPdfSave.setTooltip(null);

                    tooltip.setText("");

                    buttonPdfSave.setStyle("-fx-border-color: radial-gradient(radius 180%,lightgray,gray)");

                    statisticsController.createPassedPdf(
                            labelPDF.getText().equals("Location not set") ? null : labelPDF.getText(),
                            PageSize.getRectangle(comboBoxFontPageSize.getValue()),
                            Integer.parseInt(comboBoxFontSize.getValue()),
                            Integer.parseInt(comboBoxFontTitleSize.getValue())
                    );

                } catch (Exception e) {
                    buttonPdfSave.setTooltip(tooltip);

                    String message = e.getMessage();

                    if (message == null) tooltip.setText("You must select a location\n");

                    if (message != null)
                        tooltip.setText(tooltip.getText() + "Another process is using file:" + labelPDF.getText() + " please close it and try again");

                    buttonPdfSave.setStyle("-fx-border-color: linear-gradient(whitesmoke,darkred,whitesmoke);");

                } finally {
                    buttonPdfSave.setDisable(false);
                }

                return  null;
            }
        };

        taskPassed.setOnSucceeded(event1 -> animateTitle(null));

        new Thread(taskPassed).start();
    }

    void setWindow(PDFs type){

        switch (type){

            case Marks: {
                buttonPdfSave.setOnAction(this::marksPdf);
                break;
            }

            case Cons:{
                buttonPdfSave.setOnAction(this::consPdf);
                break;
            }

            default:{
                buttonPdfSave.setOnAction(this::promotedPdf);
                break;
            }
        }
    }

    public void setStatistics(StatisticsController statisticsController){
        this.statisticsController = statisticsController;
    }
}
