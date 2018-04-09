package User.FXML.Controlers;

import Domain.Student;
import Services.StudentService;
import User.FXML.UTILS.ButtonCell;
import Utils.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;
import java.net.URL;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;


public class StudentsWindowController implements Observer,Initializable {

    @FXML
    private ComboBox <String> comboBox;

    @FXML
    private TableColumn<Student,String> tableColumnId;

    @FXML
    private TableColumn<Student,String> tableColumnNume;

    @FXML
    private TableColumn<Student,String> tableColumnEmail;

    @FXML
    private TableColumn<Student,String> tableColumnTeacher;

    @FXML
    private TableColumn<Student,String> tableColumnGroup;

    @FXML
    private TableColumn<Student,Boolean> tableColumnAction;

    @FXML
    private TableView <Student> tableViewStudents;

    @FXML
    private Button buttonNext;

    @FXML
    private  Button buttonPrev;

    @FXML
    private Label labelPageNr;

    @FXML
    private Label labelInfo;

    @FXML
    private Button butonStudentList;

    @FXML
    private Button butonAddStudent;

    @FXML
    private BorderPane borderPane;

    @FXML
    private Label lableEntities;

    @FXML
    private Label lableShow;

    @FXML
    private TextField  textFieldSearch;

    @FXML
    private Label studentTitle;

    private CheckBox checkBoxNume = new CheckBox(),checkBoxEmail = new CheckBox(),checkBoxTeacher = new CheckBox(),checkBoxGrupa = new CheckBox();

    private PopOver searchPopOver = new PopOver(buildPopOverContent());

    private Parent buildPopOverContent(){

        GridPane gridPane = new GridPane();

        gridPane.setHgap(5);
        gridPane.setVgap(5);

        gridPane.add(checkBoxNume,1,1);
        gridPane.add(new Label("Search in name"),2,1);

        gridPane.add(checkBoxEmail,1,2);
        gridPane.add(new Label("Search in email"),2,2);

        gridPane.add(checkBoxTeacher,1,3);
        gridPane.add(new Label("Search in teacher"),2,3);

        gridPane.add(checkBoxGrupa,1,4);
        gridPane.add(new Label("Search in group"),2,4);

        gridPane.setPadding(new Insets(5,5,7,5));

        return gridPane;

    }

    private ObservableList <Student >list = FXCollections.observableArrayList(),filteredList = FXCollections.observableArrayList();

    private StudentService studentService;

    private StudentEditWindowViewController studentEditWindowViewController;

    private final ObservableList < String > comboBoxList = FXCollections.observableArrayList("2","4","6","8");

    private int pageNumber = -1,nrPg = 0;

    private Parent parent,editWindow;

    private boolean anotherWindowActive = false;

    private ContextMenu contextMenu;

    private void hideAll(boolean hide){

        buttonNext.setVisible(hide);buttonPrev.setVisible(hide);labelInfo.setVisible(hide);

        labelPageNr.setVisible(hide);comboBox.setVisible(hide);lableShow.setVisible(hide);

        lableEntities.setVisible(hide);textFieldSearch.setVisible(hide);

        if(!hide)return;

        studentTitle.setText("Student information");

        buttonNext.setOnAction(this::handleNextEvent);

        buttonPrev.setOnAction(this::handlePrevEvent);

        comboBox.setOnAction(this::handleComboAction);


        setPrev();setNext();
    }

    private void redirect(){
        pageNumber = -1;
        buttonNext.setOnAction(this::redirectNext);
        buttonPrev.setOnAction(this::redirectPrev);
        comboBox.setOnAction(this::redirectCombo);
    }

    private void redirectCombo(ActionEvent event){
        pageNumber = -1;
        setTableContent(filteredList);
    }

    private void realPaginate(List <Student> l){

        nrPg = calculatePagini(comboBox.getValue() ==null ? comboBox.getPromptText() : comboBox.getValue(),l);

        if(!anotherWindowActive) {
            setNext();setPrev();
        }
        int value = comboBox.getValue() == null ? Integer.parseInt(comboBox.getPromptText()) : Integer.parseInt(comboBox.getValue());

        if((value * (pageNumber + 1) + 1) > Math.min(l.size(),value *(pageNumber + 2)) && l.size() != 0)--pageNumber;

        list.setAll(studentService.getPage(pageNumber + 2,value));

        labelPageNr.setText(""+(l.size() != 0 ? (pageNumber + 2) : 0));

        labelInfo.setText("Showing " + (l.size() != 0 ? (value * (pageNumber + 1) + 1) : 0 ) + " to " + (Math.min(l.size(),value *(pageNumber + 2))) + " from " + l.size() + " entities" );
    }

    private void setTableContent(List <Student> l){

        nrPg = calculatePagini(comboBox.getValue() ==null ? comboBox.getPromptText() : comboBox.getValue(),l);

        int value = comboBox.getValue() == null ? Integer.parseInt(comboBox.getPromptText()) : Integer.parseInt(comboBox.getValue());

        list.setAll(l.subList(value * (pageNumber + 1),Math.min(l.size(),value *(pageNumber + 2))));

        if(!anotherWindowActive) {
            setNext();setPrev();
        }

        labelPageNr.setText(""+(l.size() != 0 ? (pageNumber + 2) : 0));

        labelInfo.setText("Showing " + (l.size() != 0 ? (value * (pageNumber + 1) + 1) : 0 ) + " to " + (Math.min(l.size(),value *(pageNumber + 2))) + " from " + l.size() + " entities" );
    }

    private String buttonListStyle(){
        return "-fx-background-color: whitesmoke;" +
                "    -fx-border-color: black black transparent;" +
                "    -fx-font-style: italic";
    }

    private String buttonAddStyle(){
        return  "-fx-background-color: linear-gradient(gainsboro,whitesmoke);" +
                "    -fx-border-color: black;" +
                "    -fx-border-width: 1;" +
                "    -fx-font-style: italic;";
    }

    private int calculatePagini(String nr,List<Student> list) {


        int value = Math.min(Integer.parseInt(nr), list.size());

        int num = list.size();

        return value != 0 ? num / value + ((num - (num / value) * value == 0) ? 0 : 1) : 0;

    }

    private void setTableColumnAction(){

        tableColumnAction.setCellValueFactory(new PropertyValueFactory<>(""));

        tableColumnAction.setCellFactory(param -> new ButtonCell(this));
    }

    private void setNext(){
        buttonPrev.setVisible(pageNumber - 1 >= -1);
    }

    private void setPrev(){
        buttonNext.setVisible(pageNumber + 2 < nrPg);
    }

    private void redirectNext(ActionEvent event){

        if((pageNumber + 1) + 1 >= nrPg){
            return;
        }

        ++pageNumber;

        setTableContent(filteredList);
    }

    private void redirectPrev(ActionEvent event){

        if(pageNumber -1 < -1){
            return;
        }

        --pageNumber;

        setTableContent(filteredList);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        textFieldSearch.selectionProperty().addListener(((observable, oldValue, newValue) -> handleSearchEvent(null)));

        textFieldSearch.focusedProperty().addListener((observable -> {

            textFieldSearch.setStyle("-fx-background-image: url(../Images/search.png) ");
            textFieldSearch.setStyle("-fx-background-size: contain");

            if(textFieldSearch.isFocused()){
                textFieldSearch.setStyle("-fx-background-image: inherit");
                searchPopOver.show(textFieldSearch);
                return;
            }

            update(null);
        }));

        contextMenu = new ContextMenu();

        searchPopOver.setDetachable(false);


        MenuItem closePopOver = new MenuItem("Close search options"),showPopOver = new MenuItem("Show search options"),cancelSearch = new MenuItem("Cancel search");

        closePopOver.setOnAction(event -> searchPopOver.hide());

        showPopOver.setOnAction(event -> searchPopOver.show(textFieldSearch));

        cancelSearch.setOnAction(event -> {
            searchPopOver.hide();
            textFieldSearch.clear();
            handleListStudent(event);
        });

        contextMenu.getItems().addAll(closePopOver,showPopOver,cancelSearch);

        contextMenu.setId("contextMenu");

        textFieldSearch.setContextMenu(contextMenu);

        searchPopOver.setArrowLocation(PopOver.ArrowLocation.LEFT_BOTTOM);

        comboBox.setPromptText("8");

        comboBox.setItems(comboBoxList);

        tableColumnId.setCellValueFactory(new PropertyValueFactory<>("idStudent"));

        tableColumnId.setMinWidth(20);

        tableColumnId.setComparator(Comparator.comparingInt(Integer::parseInt));

        tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        tableColumnEmail.setMinWidth(130);

        tableColumnNume.setCellValueFactory(new PropertyValueFactory<>("nume"));

        tableColumnNume.setMinWidth(119);

        tableColumnTeacher.setCellValueFactory(new PropertyValueFactory<>("cadruDidactic"));

        tableColumnTeacher.setMinWidth(200);

        tableColumnGroup.setCellValueFactory(new PropertyValueFactory<>("grupa"));

        tableViewStudents.setItems(list);

        tableColumnAction.setMinWidth(180);

        tableColumnAction.setSortable(false);
        tableColumnAction.setEditable(false);

        setTableColumnAction();
    }

    public void hideAllPopOvers(){
        if(searchPopOver != null) searchPopOver.hide(Duration.millis(0));
    }

    private void handleSearchEvent(ActionEvent event){

            searchPopOver.hide();

            Predicate <Student> predicateName = x-> Pattern.compile(textFieldSearch.getText()).matcher(x.getNume()).find();

            Predicate <Student> predicateEmail = x->Pattern.compile(textFieldSearch.getText()).matcher(x.getEmail()).find();

            Predicate <Student> predicateGroup = x->Pattern.compile(textFieldSearch.getText()).matcher(x.getGrupa()+"").find();

            Predicate <Student> predicateTeacher = x->Pattern.compile(textFieldSearch.getText()).matcher(x.getCadruDidactic()).find();

            Predicate <Student> mainPredicate = x->false;

            if(checkBoxNume.isSelected()) mainPredicate = mainPredicate.or(predicateName);

            if(checkBoxEmail.isSelected())mainPredicate = mainPredicate.or(predicateEmail);

            if(checkBoxGrupa.isSelected())mainPredicate = mainPredicate.or(predicateGroup);

            if (checkBoxTeacher.isSelected()) mainPredicate = mainPredicate.or(predicateTeacher);

            filteredList.setAll(studentService.filterByPrdicate(mainPredicate));

            redirect();setTableContent(filteredList);
    }

    public void handleEditEvent(ActionEvent event,Button btn) {

        Student student = tableViewStudents.getSelectionModel().getSelectedItem();

        if (student == null) return;

        anotherWindowActive = true;

        borderPane.setCenter(editWindow);

        studentTitle.setText("Updating...");

        hideAll(false);

        studentEditWindowViewController.setFieldsText(student.getNume(), student.getEmail(), student.getCadruDidactic(), student.getGrupa() + "");
    }

    void handleEditStudent(String name,String email,String tacher,String group) throws Exception{

        Student student = tableViewStudents.getSelectionModel().getSelectedItem();

        if(student == null)return;

        String id = student.getIdStudent();

        studentService.updateStudent(id,email,tacher,name,group);
    }

    public void handleDeleteEvent(ActionEvent event,Button btn){

        Student student = tableViewStudents.getSelectionModel().getSelectedItem();

        if(student == null){
            return;
        }

        try{
            studentService.deleteStudent(student.getIdStudent());
            update(null);
        }catch (Exception e){}

    }

    public void handleListStudent(ActionEvent event){

        butonStudentList.setStyle(buttonListStyle());butonAddStudent.setStyle(buttonAddStyle());

        studentEditWindowViewController.clearFields();

        borderPane.setCenter(tableViewStudents);hideAll(true);

        anotherWindowActive = false;pageNumber = -1;

        update(null);
    }

    void backToMainWindow(){

        borderPane.setCenter(tableViewStudents);hideAll(true);

        anotherWindowActive = false;
    }

    public void handleAddStudent(ActionEvent event){

        butonAddStudent.setStyle(buttonListStyle());butonStudentList.setStyle(buttonAddStyle());

        studentTitle.setText("Adding...");

        borderPane.setCenter(parent);hideAll(false);

        studentEditWindowViewController.clearFields();

        anotherWindowActive = true;
    }

    void handleAddEvent(String studentId,String studentName,String studentEmail,String studentTeacher,String studentGroup) throws  Exception{

        try{
            Integer.parseInt(studentId);
        }catch (Exception e){
            studentId = "";
        }

        try{
            Integer.parseInt(studentGroup);
        }catch (Exception e){
            studentGroup = "";
        }

        studentService.addStudent(studentName,studentEmail,studentTeacher,studentId, !studentGroup.equals("") ? Integer.parseInt(studentGroup) : -1);
    }

    public void handleNextEvent(ActionEvent event){

        if((pageNumber + 1) + 1 >= nrPg){
            return;
        }

        ++pageNumber;

        realPaginate(studentService.getAll());
    }

    public void handlePrevEvent(ActionEvent event){

        if(pageNumber -1 < -1){
            return;
        }

        --pageNumber;

        realPaginate(studentService.getAll());
    }

    public void handleComboAction(ActionEvent event){
        pageNumber = -1;
        realPaginate(studentService.getAll());
    }

    public StudentsWindowController(){}

    public void setStudentService(StudentService studentService){

        this.studentService = studentService;

        studentService.addObserver(this);

        update(null);
    }

    public  void addWindow(Parent parent){
        this.parent = parent;
    }

    public void addEditWindow(Parent parent){
        editWindow = parent;
    }

    public void addEditWindowControler(StudentEditWindowViewController studentEditWindowViewController){
        this.studentEditWindowViewController = studentEditWindowViewController;
    }

    @Override
    public void update(Utils.Observable observable) {

        realPaginate(studentService.getAll());

        //setTableContent(studentService.getAll());
    }
}
