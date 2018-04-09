package User.FXML.Controlers;

import Domain.Student;
import Domain.Tema;

import Services.NotaService;
import Services.StudentService;
import Services.TemeService;
import User.FXML.UTILS.Function;
import Utils.InternetChecker;
import Utils.Observer;
import Utils.WeekCount;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AddMarkWindowController implements Initializable, Observer {

    @FXML
    private ListView <Student> listViewStudents;

    @FXML
    private ListView <Tema> listViewTema;

    @FXML
    private TextField textFieldMark;

    @FXML
    private TextField textFieldDescription;

    @FXML
    private Label lableTitle;

    @FXML
    private Label lableMark;

    @FXML
    private Label lableDesc;

    private Function function;

    private Tooltip tooltipTitle = new Tooltip(),tooltipMark = new Tooltip(),tooltipDesc = new Tooltip();

    private ObservableList<Student> studentsModel = FXCollections.observableArrayList();

    private ObservableList<Tema> temaModel = FXCollections.observableArrayList();

    private StudentService studentService;

    private TemeService temeService;

    private NotaService notaService;

    private MarksViewController marksViewController;

    private PopOver internetPopOver = new PopOver(getInfoMessage());

    private InternetChecker internetChecker = InternetChecker.getInstance();

    private Parent getInfoMessage(){
        Label label = new Label("No internet connection.The student could not be informed");
        label.setStyle("-fx-text-fill: black;-fx-font-style:italic;-fx-font-weight: bold;-fx-font-size: 17;-fx-padding: 5 5 5 5;");
        return  label;
    }

    private void clearFields(){
        textFieldDescription.setText("");
        textFieldMark.setText("");
        lableTitle.setTooltip(null);lableDesc.setTooltip(null);lableMark.setTooltip(null);

        lableMark.setStyle("-fx-text-fill: whitesmoke");
        lableDesc.setStyle("-fx-text-fill: whitesmoke");
        lableTitle.setStyle("-fx-text-fill: whitesmoke");

        listViewStudents.getSelectionModel().select(null);
        listViewTema.getSelectionModel().select(null);

        textFieldMark.setPromptText("");
    }

    private void refreshContentStudents(List<Student> list){

        studentsModel.setAll(
                list.stream().sorted(
                        (x,y)->x.getNume().compareTo(y.getNume()) == 0 ? x.getGrupa() - y.getGrupa() : x.getNume().compareTo(y.getNume())
                ).collect(Collectors.toList())
        );

    }

    private  void refreshContentTema(List <Tema> list){
        temaModel.setAll(

                list.stream().sorted(
                        (x,y)->x.getNrTema() - y.getNrTema()
                ).collect(Collectors.toList())
        );
    }

    private void refreshContent(){
        refreshContentStudents(studentService.getAll());
        refreshContentTema(temeService.getAll());
    }

    private void parseErrors(String err){

        if(err.compareTo("Nota trebuie sa fie intre 1-10") == 0){
            tooltipMark.setText("Mark should be a non void float between 1 and 10");
            lableMark.setTooltip(tooltipMark);
            lableMark.setStyle("-fx-text-fill: darkred");
            return;
        }

        if(err.compareTo("Nota nemodificata") == 0){
            tooltipTitle.setText("The calculated mark(the mark you've just entered) is smaller or equal to the old mark(consider that penalties could appear).\nThe mark will not be updated :((");
            lableTitle.setTooltip(tooltipTitle);
            lableTitle.setStyle("-fx-text-fill: yellow");
            return;
        }

        tooltipTitle.setText("The student already has a mark at this homework. Please choose another homework");
        lableTitle.setStyle("-fx-text-fill: darkred");
        lableTitle.setTooltip(tooltipTitle);
    }

    void setTitleLable(String windowTitle){
        lableTitle.setText(windowTitle);
    }

    void selectStudentAndHomework(Student student,Tema tema){

        listViewStudents.getSelectionModel().select(student);
        listViewTema.getSelectionModel().select(tema);

        listViewTema.scrollTo(tema);
        listViewStudents.scrollTo(student);

    }

    void disableAll(){
        listViewStudents.setDisable(true);
        listViewTema.setDisable(true);
    }

    void enableAll(){
        listViewStudents.setDisable(false);
        listViewTema.setDisable(false);
    }

    void setFunction(Function function){
        this.function = function;
    }

    void setMarkText(String text){
        textFieldMark.setText(text);
    }

    public AddMarkWindowController(){}

    public void setStudentService(StudentService studentService) {
        this.studentService = studentService;
        studentService.addObserver(this);
        refreshContentStudents(studentService.getAll());
    }

    public void setTemeService(TemeService temeService) {
        this.temeService = temeService;
        temeService.addObserver(this);
        refreshContentTema(temeService.getAll());
    }

    public void setNotaService(NotaService notaService){
        this.notaService = notaService;
    }

    public void setMarksViewController(MarksViewController marksViewController){
        this.marksViewController = marksViewController;
    }

    public void handleAddEvent(ActionEvent event){

        Student student = listViewStudents.getSelectionModel().getSelectedItem();
        Tema  tema = listViewTema.getSelectionModel().getSelectedItem();

        lableTitle.setTooltip(null);lableDesc.setTooltip(null);lableMark.setTooltip(null);
        lableMark.setStyle("-fx-text-fill: whitesmoke");
        lableDesc.setStyle("-fx-text-fill: whitesmoke");
        lableTitle.setStyle("-fx-text-fill: whitesmoke");
        tooltipTitle.setText("");

        if(student == null || tema == null) {

            lableTitle.setStyle("-fx-text-fill: darkred");
            lableTitle.setTooltip(tooltipTitle);

            if (student == null) {
                tooltipTitle.setText("You need to select a student");
            }

            if (tema == null) {
                tooltipTitle.setText(tooltipTitle.getText().equals("") ? "You need to  select a homework" : tooltipTitle.getText().concat(" and a homework"));
            }

            return;
        }

        try{

            float nota;

            try{
                nota = Float.parseFloat(textFieldMark.getText());
            }catch ( Exception e){
                nota = -1;
            }

            function.execute(student.getIdStudent(),tema.getNrTema(),(int) WeekCount.getWeekCount(),nota,textFieldDescription.getText());

            clearFields();

            if(!internetChecker.isInternet())internetPopOver.show(lableTitle);

        }catch (Exception e){
            parseErrors(e.getMessage());
        }
    }

    public void handleExitEvent(){
        clearFields();
        marksViewController.reset();
    }

    public void hideAllPopOvers(){
        internetPopOver.hide(Duration.millis(0));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        internetPopOver.setArrowLocation(PopOver.ArrowLocation.BOTTOM_CENTER);

        listViewStudents.setItems(studentsModel);
        listViewTema.setItems(temaModel);

        tooltipTitle.setStyle("-fx-font-size: 17");

        tooltipMark.setStyle("-fx-font-size: 17");

        tooltipDesc.setStyle("-fx-font-size: 17");

        listViewTema.setCellFactory(param -> new ListCell<Tema>(){
            @Override
            protected  void updateItem(Tema tema,boolean empty){

                super.updateItem(tema,empty);

                if(empty){
                    setText(null);return;
                }

                setText(tema.getNrTema() +" "+ tema.getCerinta());
            }
        });

        textFieldDescription.textProperty().addListener(event->{
            if(textFieldDescription.getText().length() == 501){
                textFieldDescription.setText(textFieldDescription.getText().substring(0,500));
            }
        });

        listViewStudents.setCellFactory(param -> new ListCell<Student>(){
            @Override
            protected  void updateItem(Student student,boolean empty){

                super.updateItem(student,empty);

                if(empty){
                    setText(null);return;
                }

                setText(student.getNume() + " " + student.getGrupa());
            }
        });

    }

    @Override
    public void update(Utils.Observable observable) {
        refreshContent();
    }
}
