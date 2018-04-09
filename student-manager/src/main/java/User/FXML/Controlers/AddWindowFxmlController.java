package User.FXML.Controlers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

import java.net.URL;
import java.util.ResourceBundle;

public class AddWindowFxmlController implements Initializable {

    @FXML
    private Label labelStudentId;

    @FXML
    private Label labelName;

    @FXML
    private Label labelEmail;

    @FXML
    private Label lableTeacher;

    @FXML
    private Label labelGroup;

    @FXML
    private TextField textFieldId;

    @FXML
    private TextField textFieldName;

    @FXML
    private TextField textFieldEmail;

    @FXML
    private TextField textFieldTeacher;

    @FXML
    private TextField textFieldGroup;

    private StudentsWindowController controler;

    private Tooltip tooltipId = new Tooltip(),tooltipEmail=new Tooltip(),tooltipName=new Tooltip(),tooltipTeacher=new Tooltip(),tooltipGroup = new Tooltip();

    private void parseErrors(String errVector){
        String[] errors = errVector.split("\n");
        for (String error : errors) setErrors(error.trim());
    }

    private void clearFields(){
        textFieldId.setText("");textFieldName.setText("");textFieldEmail.setText("");textFieldGroup.setText("");textFieldTeacher.setText("");
    }

    public AddWindowFxmlController(){}

    public void refreshLables(){
        labelEmail.setStyle("-fx-text-fill: white");
        labelStudentId.setStyle("-fx-text-fill: white");
        labelName.setStyle("-fx-text-fill: white");
        lableTeacher.setStyle("-fx-text-fill: white");
        labelGroup.setStyle("-fx-text-fill: white");
        labelGroup.setTooltip(null);lableTeacher.setTooltip(null);
        labelName.setTooltip(null);labelStudentId.setTooltip(null);
        labelEmail.setTooltip(null);
    }

    public void setErrors(String error){

        switch ( error ){

            case "Id vid": {
                tooltipId.setText("Student id must be a non void value");
                labelStudentId.setTooltip(tooltipId);
                labelStudentId.setStyle("-fx-text-fill: darkred");
                break;
            }

            case "Cadru vid":{
                lableTeacher.setTooltip(tooltipTeacher);
                lableTeacher.setStyle("-fx-text-fill: darkred");
                break;
            }

            case "Nume vid": {
                labelName.setTooltip(tooltipName);
                labelName.setStyle("-fx-text-fill: darkred");
                break;
            }

            case "Grupa invalida":{
                labelGroup.setStyle("-fx-text-fill: darkred");
                labelGroup.setTooltip(tooltipGroup);
                break;
            }

            case "Email invalid":{
                labelEmail.setTooltip(tooltipEmail);
                labelEmail.setStyle("-fx-text-fill: darkred");
                break;
            }

            case "Id string":{
                tooltipId.setText("Id should be a integer value.\nPlease insert a integer value");
                labelStudentId.setTooltip(tooltipId);
                labelStudentId.setStyle("-fx-text-fill: darkred");
                break;
            }

            default:{
                tooltipId.setText("In database exists a student with the same id.\nPlease enter a new id");
                labelStudentId.setTooltip(tooltipId);
                labelStudentId.setStyle("-fx-text-fill: darkred");
            }

        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tooltipEmail.setText("Enter a valid email address");
        tooltipGroup.setText("Group must be a positive number and a non void value");
        tooltipTeacher.setText("The teacher name should not be null");
        tooltipName.setText("The student name should not be null");
    }

    public void setStudentWindowControler(StudentsWindowController studentWindowControler){
        controler = studentWindowControler;
    }

    public void handleCancelEvent(ActionEvent event){
        refreshLables();clearFields();
    }

    public void handleSaveEvent(ActionEvent event){
        refreshLables();
        try {
            controler.handleAddEvent(textFieldId.getText(),textFieldName.getText(),textFieldEmail.getText(),textFieldTeacher.getText(),textFieldGroup.getText());
            refreshLables();clearFields();
        }catch (Exception e){
            parseErrors(e.getMessage());
        }
    }
}
