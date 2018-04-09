package User.FXML.Controlers;

import Domain.Tema;
import Services.TemeService;
import User.FXML.UTILS.AlertClass;
import User.FXML.UTILS.InfoCell;
import Utils.InternetChecker;
import Utils.Observable;
import Utils.Observer;
import Utils.WeekCount;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.controlsfx.control.PopOver;


import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class HomeworkWindowViewController implements Observer,Initializable {

    @FXML
    TableView <Tema> tableViewHomeworks;

    @FXML
    TableColumn<Tema,String> tableColumnId;

    @FXML
    TableColumn<Tema,Boolean> tableColumnReq;

    @FXML
    TableColumn<Tema,String>tableColumnDeadline;

    @FXML
    private TextArea textArea;

    @FXML
    private Label lableId;

    @FXML
    private Label lableDemand;

    @FXML
    private Label lableDeadline;

    @FXML
    private TextField textFieldId;

    @FXML
    private TextField textFieldDeadline;

    @FXML
    private TextField textFieldDemand;

    @FXML
    private Button prevButton;

    @FXML
    private Button nextButton;

    @FXML
    private Button homeworkDeleteButton;

    @FXML
    private Button buttonEdit;

    @FXML
    private Button buttonAdd;

    @FXML
    private Label lableShow;

    @FXML
    private Label lablePage;

    @FXML
    private Label homeworkTitleWindow;

    @FXML
    private ComboBox<Integer> pageCombo;

    @FXML
    private TextField textFieldSearchHomework;

    private Tooltip tooltipId = new Tooltip(),tooltipDemand = new Tooltip(),tooltipDeadline = new Tooltip(),toolTipSearch = new Tooltip();

    private CheckBox checkBoxDeadline = new CheckBox(),checkBoxDesc = new CheckBox();

    private PopOver searchPopOver = new PopOver(searchOptions()),internetPopOver = new PopOver(getInfoMessage());

    private TemeService temeService;

    private ObservableList <Tema> list = FXCollections.observableArrayList(),filteredList = FXCollections.observableArrayList();

    private InternetChecker internetChecker = InternetChecker.getInstance();

    private Map<String,Consumer <String> > filters = new TreeMap<>();

    private int pageNumber = 0,nrPag = 0,elements = 8;

    private int getNumberOfPages(List <Tema> list){
        return (list.size() / elements + ((list.size() % elements ==0) ? 0 : 1));
    }

    private void setNextPrev(){

        prevButton.setVisible(false);

        nextButton.setVisible(false);

        if(pageNumber - 1 >= 0)prevButton.setVisible(true);
        if(pageNumber + 1 < nrPag)nextButton.setVisible(true);
    }

    private Parent getInfoMessage(){
        Label label = new Label("No internet connection.The students could not be informed");
        label.setStyle("-fx-text-fill: black;-fx-font-style:italic;-fx-font-weight: bold;-fx-font-size: 17;-fx-padding: 5 5 5 5;");

        return  label;
    }

    private Parent searchOptions(){

        GridPane gridPane = new GridPane();

        gridPane.add(new Label("Search in deadline"),2,1);gridPane.add(checkBoxDeadline,1,1);
        gridPane.add(new Label("Search in description"),2,2);gridPane.add(checkBoxDesc,1,2);
        gridPane.setPadding(new Insets(5,5,10,5));

        gridPane.setVgap(5);gridPane.setHgap(5);

        return  gridPane;
    }

    private void redirectedCombo(ActionEvent event){
        elements = pageCombo.getValue() == null ? Integer.parseInt(pageCombo.getPromptText()) : pageCombo.getValue();
        refreshContent(filteredList);
    }

    private void refreshContent(List< Tema > list){

        buttonAdd.setDisable(true);
        buttonEdit.setDisable(true);
        homeworkDeleteButton.setDisable(true);

        nrPag = getNumberOfPages(list);

        setNextPrev();

        if(pageNumber * elements + 1 > Math.min(list.size(),(pageNumber+ 1)*elements) && list.size() != 0) --pageNumber;

        lableShow.setText("Showing elements " + (list.size() != 0 ?(pageNumber*elements + 1) : 0)+ " to " + Math.min(list.size(),(pageNumber+ 1)*elements) + " of " + list.size());

        lablePage.setText("Page: " + (pageNumber + 1));

        this.list.setAll(
                list.subList(pageNumber * elements,Math.min((pageNumber + 1)*elements,list.size()))
        );
    }

    private void redirect(){

        pageNumber = 0;nrPag = getNumberOfPages(filteredList);

        nextButton.setOnAction(this::redirectedNext);

        prevButton.setOnAction(this::redirectedPrev);

        pageCombo.setOnAction(this::redirectedCombo);
    }

    private void redirectedNext(ActionEvent event){

        if(pageNumber + 1 > nrPag)return;

        ++pageNumber;

        refreshContent(filteredList);
    }

    private void redirectedPrev(ActionEvent event){

        if(pageNumber - 1 < 0)return;;

        --pageNumber;

        refreshContent(filteredList);
    }

    private void refreshFields(){

        textArea.setVisible(false);

        textFieldId.setText("");textFieldDeadline.setText("");textFieldDemand.setText("");

        lableDeadline.setTooltip(null);lableId.setTooltip(null);lableId.setTooltip(null);

        lableDeadline.setStyle("-fx-text-fill: whitesmoke");lableDemand.setStyle("-fx-text-fill: whitesmoke");lableId.setStyle("-fx-text-fill: whitesmoke");

        tableViewHomeworks.getSelectionModel().select(null);

        prevButton.setOnAction(this::handlePrevEvent);nextButton.setOnAction(this::handleNextEvent);pageCombo.setOnAction(this::handleComboEvent);

        textFieldSearchHomework.setText("");

        realPaginate(temeService.getAll());
    }

    private void parseErrors(String error){

        lableDeadline.setStyle("-fx-text-fill: whitesmoke");lableDemand.setStyle("-fx-text-fill: whitesmoke");lableId.setStyle("-fx-text-fill: whitesmoke");

        Map<String,Consumer <String> > errors = new TreeMap<>();

        errors.put("Cerinta vida",(S)->{
            tooltipDemand.setText("The requirement must be a non void value");
            lableDemand.setStyle("-fx-text-fill: darkred");
            lableDemand.setTooltip(tooltipDemand);
        });

        errors.put("Deadline invalid",(S)->{
            tooltipDeadline.setText("Deadline must be a integer value between 1 an 14");
            lableDeadline.setStyle("-fx-text-fill: darkred");
            lableDeadline.setTooltip(tooltipDeadline);
        });

        errors.put("Id invalid",(S)->{
            tooltipId.setText("The id must be a non void integer value");
            lableId.setTooltip(tooltipId);
            lableId.setStyle("-fx-text-fill: darkred");
        });

        errors.put("Modificare nepermisa",(S)->{
            tooltipDeadline.setText("The new deadline must be higher than the old deadline\n.The modification must be done until the final week");
            lableDeadline.setStyle("-fx-text-fill: darkred");
            lableDeadline.setTooltip(tooltipDeadline);
        });

        String[] err = error.split("\n");

        for (String s : err) {

            if(errors.get(s) == null){
                tooltipId.setText("In database exist one homework with the same id");
                lableId.setTooltip(tooltipId);
                lableId.setStyle("-fx-text-fill: darkred");
                continue;
            }

            errors.get(s).accept("");
        }
    }

    private void handleSearchEvent(){

        Predicate < Tema > filterPredicate = x->false;

        Predicate <Tema> filterDeadline = x->{

            int deadline;

            try{
                deadline = Integer.parseInt(textFieldSearchHomework.getText());
            }catch ( Exception e){
                deadline = 0;
            }

            return x.getDeadline() <= deadline;
        };

        Predicate <Tema> filterDesc = x->{
            Pattern p = Pattern.compile(textFieldSearchHomework.getText());
            return p.matcher(x.getCerinta()).find();
        };

        if(checkBoxDeadline.isSelected())filterPredicate = filterPredicate.or(filterDeadline);
        if(checkBoxDesc.isSelected())filterPredicate = filterPredicate.or(filterDesc);


        if(textFieldSearchHomework.getText().equals("")){
            return;
        }

        redirect();

        filteredList.setAll(temeService.filter(filterPredicate));

        refreshContent(filteredList);

        searchPopOver.hide();
    }

    private void handleComboEvent(ActionEvent event){
        elements = pageCombo.getValue() == null ? Integer.parseInt(pageCombo.getPromptText()) : pageCombo.getValue();
        handleClearEvent(event);
    }

    private void selectionHandle(Tema oldT,Tema newT){

        if(newT == null){
            textFieldDemand.setVisible(true);textFieldId.setVisible(true);
            lableDemand.setVisible(true);lableId.setVisible(true);
            return;
        }

        buttonEdit.setDisable(false);

        homeworkDeleteButton.setDisable(false);

        buttonAdd.setDisable(true);


        textFieldId.setVisible(false);textFieldDemand.setVisible(false);
        lableId.setVisible(false);lableDemand.setVisible(false);
    }

    private void realPaginate(List < Tema > list){

        buttonAdd.setDisable(false);
        buttonEdit.setDisable(true);
        homeworkDeleteButton.setDisable(true);

        nrPag = getNumberOfPages(list);

        setNextPrev();

        if(pageNumber*elements + 1 > Math.min(list.size(),(pageNumber+ 1)*elements) && list.size() != 0)--pageNumber;

        lablePage.setText("Page: " + (pageNumber+ 1));

        lableShow.setText("Showing elements " + (list.size() != 0 ?(pageNumber*elements + 1) : 0)+ " to " + Math.min(list.size(),(pageNumber+ 1)*elements) + " of " + list.size());

        this.list.setAll(temeService.getPage(pageNumber + 1,elements));
    }

    public HomeworkWindowViewController(){

        filters.put("deadline",(S)->{

            int value = 0;

            try{
                value = Integer.parseInt(S);
            }catch (Exception e){}

            filteredList.setAll(temeService.getAllWithDeadline(value));

            refreshContent(filteredList);
        });

        filters.put("with",(S)->{
            int value = 0;

            try{
                value = Integer.parseInt(S);
            }catch (Exception e){}

            filteredList.setAll(temeService.getAllDeadline(value));
            refreshContent(filteredList);
        });

        filters.put("word",(S)->{
            filteredList.setAll(temeService.getAllThatNeed(S));
            refreshContent(filteredList);
        });

    }

    public void handleNextEvent(ActionEvent event){

        if(pageNumber + 1 > nrPag)return;

        ++pageNumber;

        realPaginate(temeService.getAll());
    }

    public  void handlePrevEvent(ActionEvent event){
        if(pageNumber - 1 < 0)return;

        --pageNumber;

        realPaginate(temeService.getAll());

    }

    public void handleEditEvent(ActionEvent event){

        Tema t = tableViewHomeworks.getSelectionModel().getSelectedItem();

        if(t == null) return;

        try{

            int deadline;

            try{
                deadline = Integer.parseInt(textFieldDeadline.getText());
            }catch (Exception e){
                deadline = -1;
            }

            temeService.updateDeadline(t.getNrTema(),deadline,(int)WeekCount.getWeekCount());

            refreshFields();

            if(!internetChecker.isInternet())internetPopOver.show(homeworkTitleWindow);
        }catch (Exception e){
            parseErrors(e.getMessage());
        }
    }

    public void handleAddEvent(ActionEvent event){

        try{
            int deadline,id;

            try{
                deadline = Integer.parseInt(textFieldDeadline.getText());
            }catch (Exception e){
                deadline = -1;
            }

            try{
                id = Integer.parseInt(textFieldId.getText());
            }catch (Exception e){
                id = -1;
            }

            temeService.addTema(textFieldDemand.getText(),deadline,id);

            refreshFields();

            if(!internetChecker.isInternet())internetPopOver.show(homeworkTitleWindow);
        }catch (Exception e){
            parseErrors(e.getMessage());
        }
    }

    public void handleClearEvent(ActionEvent event){
        pageNumber = 0;
        refreshFields();
    }

    public void handleDeleteEvent(ActionEvent event){
        if(tableViewHomeworks.getSelectionModel().getSelectedItem() == null || new AlertClass("\nhomework",buttonAdd,450,260).getAlert().showAndWait().get() == ButtonType.CANCEL)return;

        try{
            temeService.deleteTema(tableViewHomeworks.getSelectionModel().getSelectedItem().getNrTema());
            refreshFields();
        }catch (Exception e){

        }

    }

    public void setTemeService(TemeService temeService){
        this.temeService = temeService;
        update(null);
    }

    public void hideAllPopOvers(){
        if(searchPopOver != null) searchPopOver.hide(Duration.millis(0));
        internetPopOver.hide(Duration.millis(0));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        toolTipSearch.setText("Select an option for filter and then type text");

        pageCombo.setItems(FXCollections.observableArrayList(1,2,4,6,8));

        internetPopOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);

        pageCombo.setConverter(new StringConverter<Integer>() {
            @Override
            public String toString(Integer object) {
                return object.toString();
            }

            @Override
            public Integer fromString(String string) {
                return null;
            }
        });

        pageCombo.setPromptText("8");

        pageCombo.setOnAction(this::handleComboEvent);

        textFieldSearchHomework.focusedProperty().addListener(event->{

            if(textFieldSearchHomework.isFocused()){
                searchPopOver.show(textFieldSearchHomework);
                return;
            }

            searchPopOver.hide();
        });


        ContextMenu contextMenu = new ContextMenu();

        MenuItem searchSelector = new MenuItem("Open search selector");
        MenuItem closeSelector  = new MenuItem("Close search selector");
        MenuItem cancelSearch = new MenuItem("Cancel search");

        contextMenu.setId("contextMenu");

        contextMenu.getItems().addAll(searchSelector,closeSelector,cancelSearch);

        searchSelector.setOnAction(event -> searchPopOver.show(textFieldSearchHomework));

        closeSelector.setOnAction(event -> searchPopOver.hide());

        cancelSearch.setOnAction(event->{
            handleClearEvent(event);searchPopOver.hide();
        });

        textFieldDemand.textProperty().addListener(event->{
            if(textFieldDemand.getText().length() == 501)textFieldDemand.setText(textFieldDemand.getText().substring(0,500));
        });

        textFieldSearchHomework.setContextMenu(contextMenu);

        textFieldSearchHomework.setPromptText("Type here...");

        textFieldSearchHomework.setTooltip(toolTipSearch);

        textFieldSearchHomework.textProperty().addListener(event->handleSearchEvent());

        tableColumnDeadline.setCellValueFactory(new PropertyValueFactory<>("deadline"));

        tableColumnId.setCellValueFactory(new PropertyValueFactory<>("nrTema"));

        tableColumnReq.setCellValueFactory(new PropertyValueFactory<>(""));

        tableColumnReq.setCellFactory(element->new InfoCell(textArea,tableViewHomeworks));

        tableViewHomeworks.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> textArea.setVisible(false));

        textArea.setVisible(false);

        tableViewHomeworks.setItems(list);

        tableViewHomeworks.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> selectionHandle(oldValue,newValue));

    }

    @Override
    public void update(Observable observable) {
        realPaginate(temeService.getAll());
    }
}
