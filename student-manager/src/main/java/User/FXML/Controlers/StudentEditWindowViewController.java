package User.FXML.Controlers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;


import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

public class StudentEditWindowViewController implements Initializable {

    @FXML
    private Label labelTitleWindow;

    @FXML
    private TextField textFieldName;

    @FXML
    private TextField textFieldTeacher;

    @FXML
    private TextField textFieldEmail;

    @FXML
    private TextField textFieldGroup;

    @FXML
    private Button buttonClear;

    @FXML
    private Button buttonSave;

    @FXML
    private Button buttonBack;

    private StudentsWindowController studentsWindowController;

    private Tooltip labelTooltip = new Tooltip();

    public void clearFields(){

        textFieldName.setText("");textFieldTeacher.setText("");textFieldGroup.setText("");textFieldEmail.setText("");

        labelTitleWindow.setTooltip(null);labelTitleWindow.setStyle("-fx-text-fill: black");
    }

    private String parseErrors(String s){

        String errorString = "";

        String [] errors = s.split("\n");

        Map<String,String> errorMap = new TreeMap<>();

        errorMap.put("Id vid","You must enter a valid id(a positive number)");
        errorMap.put("Cadru vid","The teacher name should not be void");
        errorMap.put("Nume vid","Student name must be a non void string");
        errorMap.put("Grupa invalida","The student group must a non void positive number");
        errorMap.put("Email invalid","You must enter a valid id email address");
        errorMap.put("Id string","The id should be a integer value");

        for (String error : errors) errorString = errorString.concat(errorMap.get(error) +"\n");

        return errorString;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Tooltip tooltip = new Tooltip("Press this button to clear all fields"),tooltip1 = new Tooltip("Press this button to save the changes"),tooltip2 = new Tooltip("Press to go back");

        tooltip.setStyle("-fx-font-size: 15;" + "-fx-font-style: italic");
        tooltip1.setStyle("-fx-font-size: 15;" + "-fx-font-style: italic");
        tooltip2.setStyle("-fx-font-size: 15;" + "-fx-font-style: italic");

        labelTooltip.setStyle("-fx-font-size: 20");

        buttonClear.setTooltip(tooltip);

        buttonSave.setTooltip(tooltip1);

        buttonBack.setTooltip(tooltip2);
    }

    public StudentEditWindowViewController(){ }

    public void setStudentsWindowController(StudentsWindowController studentsWindowController){
        this.studentsWindowController = studentsWindowController;
    }

    public void handleClearButton(ActionEvent event){
        clearFields();
    }

    public void handleSaveButton(ActionEvent event){

        try {
            studentsWindowController.handleEditStudent(textFieldName.getText(), textFieldEmail.getText(), textFieldTeacher.getText(), textFieldGroup.getText() .equals("") ? -1 + "" : textFieldGroup.getText());
            clearFields();
            studentsWindowController.backToMainWindow();

        }catch (Exception e){
            labelTitleWindow.setStyle("-fx-text-fill: linear-gradient(darkred,firebrick)");
            labelTooltip.setText(parseErrors(e.getMessage()));
            labelTooltip.setAutoHide(false);
            labelTitleWindow.setTooltip(labelTooltip);
        }
    }

    public void handleBackButton(ActionEvent event){
        studentsWindowController.backToMainWindow();clearFields();
    }

    public void setFieldsText(String nameTextField,String emailTextField,String teacherTextField,String groupTextfield){
        textFieldEmail.setText(emailTextField);textFieldGroup.setText(groupTextfield);textFieldTeacher.setText(teacherTextField);textFieldName.setText(nameTextField);
    }

}
