package User.FXML.Controlers;


import Domain.Student;
import Domain.Tema;
import Services.NotaService;
import Utils.NotaTranslated;
import Utils.Observable;
import Utils.Observer;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;

import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import javafx.util.Duration;
import org.controlsfx.control.PopOver;

import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class MarksViewController implements Initializable, Observer {

    @FXML
    private TableView<NotaTranslated> tableView;

    @FXML
    private TableColumn<NotaTranslated,String> nameColumn;

    @FXML
    private TableColumn<NotaTranslated,String> groupColumn;

    @FXML
    private TableColumn<NotaTranslated,String> markIdColumn;

    @FXML
    private TableColumn<NotaTranslated,String> markColumn;

    @FXML
    private TableColumn<NotaTranslated,String> descColumn;

    @FXML
    private BorderPane borderPane;

    @FXML
    private Button buttonAdd;

    @FXML
    private Button buttonEdit;

    @FXML
    private AnchorPane middleAnchorPane;

    @FXML
    private Label labelElements;

    @FXML
    private Button buttonNextPage;

    @FXML
    private Button buttonPrevPage;

    @FXML
    private Button buttonHome;

    @FXML
    private TextField searchBar;

    @FXML
    private Tooltip tooltipSearch;

    @FXML
    private Label homeworkLable;

    @FXML
    private Label labelMarkWindow;

    @FXML
    private ComboBox < Integer > comboBoxPage;

    @FXML
    private TextArea marksTextArea;

    private Parent parent;

    private NotaService notaService;

    private ObservableList <NotaTranslated> model = FXCollections.observableArrayList(),filtered = FXCollections.observableArrayList();

    private Map<String,Consumer <String>> filters = new TreeMap<>();

    private AddMarkWindowController addMarkWindowController;

    private int pageNr = 0,nrPg = 0;

    private int elements = 9;

    private CheckBox checkBoxName = new CheckBox(),checkBoxGroup = new CheckBox(),checkBoxMark = new CheckBox(),checkBoxDesc = new CheckBox();

    private PopOver searchPopOver = new PopOver(buildSearchContent());

    private Parent buildSearchContent(){

        GridPane gridPane = new GridPane();

        gridPane.setVgap(5);gridPane.setHgap(5);

        gridPane.add(new Label("Search in name"),2,1);gridPane.add(checkBoxName,1,1);

        gridPane.add(new Label("Search in group"),2,2);gridPane.add(checkBoxGroup,1,2);

        gridPane.add(new Label("Search in mark"),2,3);gridPane.add(checkBoxMark,1,3);

        gridPane.add(new Label("Search in desc"),2,4);gridPane.add(checkBoxDesc,1,4);

        gridPane.setPadding(new Insets(5,5,7,5));

        return  gridPane;
    }

    public void hideAllPopOvers(){
        searchPopOver.hide(Duration.millis(0));
    }

    private int getNrPag(List <NotaTranslated> list){
        return (list.size() / elements + ((list.size() % elements ==0) ? 0 : 1));
    }

    public void setAddMarkWindowController(AddMarkWindowController addMarkWindowController) {
        this.addMarkWindowController = addMarkWindowController;
    }

    public void setNotaService(NotaService notaService1){
        notaService = notaService1;
        notaService1.addObserver(this);
        update(null);
    }

    private void setNextPrev(){
        buttonNextPage.setVisible(pageNr + 1 < nrPg);
        buttonPrevPage.setVisible(pageNr - 1 >= 0);
    }

    private void refreshContent(List<NotaTranslated> list){

        marksTextArea.setVisible(false);

        nrPg = getNrPag(list);

        int min = Math.min((pageNr + 1) *elements,list.size());

        model.setAll(
                list.subList(pageNr*elements,min)
        );

        homeworkLable.setText("Page: " + (pageNr + 1));

        labelElements.setText("Showing elements " + (list.size() == 0 ? 0 : pageNr *elements + 1)  + " to " + min + " from " + list.size() );

        buttonEdit.setDisable(true);
        setNextPrev();
    }

    private void redirect(){

        buttonNextPage.setOnAction(this::handleNextEventRedirected);

        buttonPrevPage.setOnAction(this::handlePrevEventRedirected);

        comboBoxPage.setOnAction(this::redirectedComboBox);
    }

    private void handleNextEventRedirected(ActionEvent event){
        if(pageNr + 1 >= nrPg)return;

        ++pageNr;

        refreshContent(filtered);
    }

    private void handlePrevEventRedirected(ActionEvent event){

        if(pageNr - 1 < 0)return;

        --pageNr;

        refreshContent(filtered);
    }

    public MarksViewController(){

        filters.put("upper",(S)->{

            float value;

            try{
                 value =Float.parseFloat(S);

            }catch (Exception e){
                value = 0;
            }

            filtered.setAll(notaService.translate(notaService.getWithUpper(value)));
            redirect();
            refreshContent(filtered);
        });


        filters.put("between",(S)->{

            String[] numbers = S.split(" ");

            if(numbers.length != 2)return;

            float left,right;

            try{
                left = Float.parseFloat(numbers[0]);
                right = Float.parseFloat(numbers[1]);
            }catch (Exception e){
                return;
            }

            filtered.setAll(notaService.translate(notaService.getAllThatHaveMarkBetwenn(left,right)));
            refreshContent(filtered);

            redirect();
        });


        filters.put("percent",(S)->{

            int value;

            try{
                value = Integer.parseInt(S);
            }catch (Exception e) {
                value = 0;
            }

            filtered.setAll(notaService.translate(notaService.getFirstPercentOf(value)));

            refreshContent(filtered);redirect();

        });
    }

    public void addInCenter(Parent parent){

        this.parent = parent;

        buttonEdit.setDisable(true);
    }

    private void selectItems(){

        NotaTranslated notaTranslated = tableView.getSelectionModel().getSelectedItem();

        if(notaTranslated == null){
            addMarkWindowController.selectStudentAndHomework(null,null);
            return;
        }

        Tema tema = new Tema(Integer.parseInt(notaTranslated.getIdTema()),0,"");

        Student student = notaService.getStudents().stream().filter(S->S.getNume().equals(notaTranslated.getName())).findFirst().get();

        addMarkWindowController.selectStudentAndHomework(student,tema);
        addMarkWindowController.setMarkText(notaTranslated.getMark());
    }

    private void configureForAdd(){
        addMarkWindowController.setTitleLable("Add a mark to a student");
        addMarkWindowController.enableAll();
        addMarkWindowController.setFunction(notaService::addNota);
        labelMarkWindow.setVisible(false);
        marksTextArea.setVisible(false);
    }

    private void configureForUpdate(){
        selectItems();
        addMarkWindowController.setTitleLable("Edit the student's mark");
        addMarkWindowController.disableAll();
        addMarkWindowController.setFunction(notaService::updateNota);
        labelMarkWindow.setVisible(false);
        marksTextArea.setVisible(false);
    }

    public void handleAddAction(ActionEvent event){
        borderPane.setCenter(parent);
        configureForAdd();
        buttonAdd.setVisible(false);
        buttonEdit.setVisible(false);
        buttonHome.setVisible(false);
        comboBoxPage.setVisible(false);
    }

    public void handleEditAction(ActionEvent event){

        if(tableView.getSelectionModel().getSelectedItem() == null)return;

        borderPane.setCenter(parent);
        configureForUpdate();
        buttonAdd.setVisible(false);
        buttonEdit.setVisible(false);
        buttonHome.setVisible(false);
        comboBoxPage.setVisible(false);
    }

    void reset(){
        borderPane.setCenter(middleAnchorPane);
        buttonAdd.setVisible(true);
        buttonEdit.setVisible(true);
        pageNr = 0;realPaginate(notaService.getTranslated());
        buttonHome.setVisible(true);
        comboBoxPage.setVisible(true);
        searchBar.setText("");
        buttonNextPage.setOnAction(this::handleNextEvent);
        buttonPrevPage.setOnAction(this::handlePrevEvent);
        comboBoxPage.setOnAction(this::handleComboEvent);
        buttonEdit.setDisable(true);
        labelMarkWindow.setVisible(true);
        marksTextArea.setVisible(false);
    }

    private void handleComboEvent(ActionEvent event){
        pageNr = 0;
        elements = comboBoxPage.getValue() == null ? Integer.parseInt(comboBoxPage.getPromptText()) : comboBoxPage.getValue();
        realPaginate(notaService.getTranslated());
    }

    private void redirectedComboBox(ActionEvent event){
        pageNr = 0;
        elements = comboBoxPage.getValue() == null ? Integer.parseInt(comboBoxPage.getPromptText()) : comboBoxPage.getValue();
        refreshContent(filtered);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        searchBar.setPromptText("Type here...");

        searchPopOver.setDetachable(false);
        searchPopOver.setArrowLocation(PopOver.ArrowLocation.LEFT_CENTER);

        groupColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        markIdColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        markColumn.setComparator(Comparator.comparingInt(x -> Math.round(Float.parseFloat(x))));
        descColumn.setSortable(false);

        comboBoxPage.setItems(FXCollections.observableArrayList(1,2,3,4,5,6,7,8,9));

        comboBoxPage.setOnAction(this::handleComboEvent);

        comboBoxPage.setPromptText("9");

        searchBar.focusedProperty().addListener(event->{
            searchPopOver.hide();
            if(searchBar.isFocused())searchPopOver.show(searchBar);
        });


        ContextMenu contextMenu = new ContextMenu();

        MenuItem showOptions = new MenuItem("Show filter options");

        showOptions.setOnAction(event -> searchPopOver.show(searchBar));

        MenuItem closeOptions = new MenuItem("Close filter options");

        closeOptions.setOnAction(event -> searchPopOver.hide());

        MenuItem cancelFilter = new MenuItem("Cancel filter");

        cancelFilter.setOnAction(event -> {
            handleHomeEvent(event);
            searchPopOver.hide();
        });

        contextMenu.getItems().addAll(showOptions,closeOptions,cancelFilter);

        searchBar.setContextMenu(contextMenu);

        contextMenu.setStyle("-fx-font-style:italic;-fx-font-weight:bold;");

        searchBar.textProperty().addListener(((observable, oldValue, newValue) -> handleSearchEvent(null)));

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        groupColumn.setCellValueFactory(new PropertyValueFactory<>("group"));

        markColumn.setCellValueFactory(new PropertyValueFactory<>("mark"));

        markIdColumn.setCellValueFactory(new PropertyValueFactory<>("idTema"));

        descColumn.setCellValueFactory(student->new SimpleStringProperty(student.getValue().getObs().substring(0,Math.min(20,student.getValue().getObs().length()))));

        tableView.setItems(model);

        tooltipSearch.setText("Select an option for filter and then type text");

        tooltipSearch.setStyle("-fx-font-size:15");

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            buttonEdit.setDisable(false);
            NotaTranslated translated = tableView.getSelectionModel().getSelectedItem();

            if(translated == null){
                marksTextArea.setVisible(false);
                return;
            }

            marksTextArea.setVisible(true);
            marksTextArea.setText("Description: " + translated.getObs());

        });
    }


    private void realPaginate(List <NotaTranslated > list){

        marksTextArea.setVisible(false);

        nrPg = getNrPag(list);


        int min = Math.min((pageNr + 1) *elements,list.size());

        model.setAll(
                notaService.getTranslatedInPage(pageNr + 1 ,elements)
        );

        homeworkLable.setText("Page: " + (pageNr + 1));
        labelElements.setText("Showing elements " + (list.size() == 0 ? 0 : pageNr *elements + 1)  + " to " + min + " from " + list.size() );

        buttonEdit.setDisable(true);
        setNextPrev();


    }

    @Override
    public void update(Observable observable) {
        realPaginate(notaService.getTranslated());
    }

    public void handleNextEvent(ActionEvent event){

        if(pageNr + 1 >= nrPg)return;

        ++pageNr;

        realPaginate(notaService.getTranslated());
    }

    public void handlePrevEvent(ActionEvent event){

        if(pageNr - 1 < 0)return;

        --pageNr;

        realPaginate(notaService.getTranslated());
    }

    public void handleHomeEvent(ActionEvent event){
        reset();
    }

    private void handleSearchEvent(ActionEvent event){

        searchPopOver.hide();

        Predicate <NotaTranslated> filterPredicate = x->false;

        Predicate <NotaTranslated> filterByName = x->{
            Pattern p = Pattern.compile(searchBar.getText());
            return p.matcher(x.getName()).find();
        };

        Predicate <NotaTranslated> filterByMark = x->{
            float value;

            try{
                value = Float.parseFloat(searchBar.getText());
            }catch (Exception e){
                value = -1;
            }

            return Float.parseFloat(x.getMark()) <= value;
        };

        Predicate <NotaTranslated> filterByGroup = x->x.getGroup().equals(searchBar.getText());

        Predicate <NotaTranslated> filterByDesc = x->Pattern.compile(searchBar.getText()).matcher(x.getObs()).find();

        if(checkBoxName.isSelected())filterPredicate = filterPredicate.or(filterByName);

        if(checkBoxMark.isSelected())filterPredicate = filterPredicate.or(filterByMark);

        if(checkBoxGroup.isSelected())filterPredicate = filterPredicate.or(filterByGroup);

        if(checkBoxDesc.isSelected())filterPredicate = filterPredicate.or(filterByDesc);

        filtered.setAll(notaService.filter(filterPredicate));

        if(searchBar.getText().equals(""))return;

        redirect();pageNr = 0;

        refreshContent(filtered);
    }

}
