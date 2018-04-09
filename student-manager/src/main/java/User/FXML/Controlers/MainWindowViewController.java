package User.FXML.Controlers;

import com.sun.javafx.css.converters.EffectConverter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class MainWindowViewController implements Initializable {

    @FXML
    private BorderPane windowBorderPane;

    @FXML
    private Button buttonStudents;

    @FXML
    private Button buttonMarks;

    @FXML
    private Button buttonStatistics;

    @FXML
    private Button buttonHomework;

    public MainWindowViewController(){}

    private Parent studentWindow;

    private Parent homeworkWindow;

    private Parent makrsWindow;

    private Parent statisticsWindow;

    private Parent latter = null;

    public void setStudentWindow(Parent studentWindow) {
        this.studentWindow = studentWindow;
        windowBorderPane.setCenter(studentWindow);
        markCurrent(buttonStudents);
    }

    public void setHomeworkWindow(Parent homeworkWindow) {
        this.homeworkWindow = homeworkWindow;
    }

    public void setMakrsWindow(Parent makrsWindow) {
        this.makrsWindow = makrsWindow;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    private void markCurrent(Parent parent){
        if(latter != null) latter.setStyle("-fx-font-style: normal;");
        (latter = parent).setStyle("-fx-background-color: radial-gradient(radius 150%,steelblue,dodgerblue);-fx-font-style: italic;");
    }

    public void handleStudents(ActionEvent event){
        windowBorderPane.setCenter(studentWindow);
        markCurrent(buttonStudents);
    }

    public void handeHomework(ActionEvent event){
        windowBorderPane.setCenter(homeworkWindow);
        markCurrent(buttonHomework);
    }

    public void handleMark(ActionEvent event){
        windowBorderPane.setCenter(makrsWindow);
        markCurrent(buttonMarks);
    }

    public void handleStat(ActionEvent event){
        windowBorderPane.setCenter(statisticsWindow);
        markCurrent(buttonStatistics);
    }

    public void setStatisticsWindow(Parent parent){
        statisticsWindow = parent;
    }
}
