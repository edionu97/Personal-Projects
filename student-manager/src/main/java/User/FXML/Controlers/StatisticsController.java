package User.FXML.Controlers;

import Domain.Student;
import Services.StatisticsService;
import Utils.Observable;
import Utils.Observer;
import Utils.PDFPrinter;
import Utils.PDFs;
import com.itextpdf.text.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Duration;
import javafx.util.Pair;
import org.controlsfx.control.PopOver;


import java.net.URL;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class StatisticsController implements Initializable, Observer {

    @FXML
    private PieChart piechartMarks;

    @FXML
    private PieChart pieChartPromoted;

    @FXML
    private PieChart consPieChart;

    @FXML
    private Button buttonLabMarks;

    @FXML
    private Button buttonLabPassed;

    @FXML
    private Button buttonLabStudents;


    @FXML
    private BarChart<String,Integer> homeworksBarChart;

    @FXML
    private BarChart<String,Integer>barChartConsc;

    @FXML
    private TableView< Pair< Student,Float > > tableViewMarks;

    @FXML
    private TableView <Student> tableViewPromoted;

    @FXML
    private TableView <Student> tableViewConsciencious;

    @FXML
    private TableColumn<Pair<Student,Float>,String> tableColumnName;

    @FXML
    private TableColumn <Pair<Student,Float>,String> tableColumnGroup;

    @FXML
    private TableColumn <Pair<Student,Float>,String> tableColumnMark;

    @FXML
    private TableColumn <Pair<Student,Float>,String> tableColumnEmail;

    @FXML
    private TableColumn<Student,String> tableColumnPromotedName;

    @FXML
    private TableColumn<Student,String> tableColumnPromotedEmail;

    @FXML
    private TableColumn<Student,String> tableColumnPromotedGroup;

    @FXML
    private TableColumn<Student,String> tableColumnPromotedTeacher;

    @FXML
    private TableColumn<Student,String> tableColumnConscName;

    @FXML
    private TableColumn<Student,String> tableColumnConscGroup;

    @FXML
    private TableColumn<Student,String> tableColumnConscTeacher;

    @FXML
    private TableColumn<Student,String> tableColumnConscEmail;

    private StatisticsService statisticsService;

    private PDFPrinter pdfPrinter = new PDFPrinter();

    private ObservableList <Pair<Student,Float>> tableModel = FXCollections.observableArrayList();

    private ObservableList<PieChart.Data> marksList = FXCollections.observableArrayList();

    private ObservableList<XYChart.Series<String,Integer>> homeList = FXCollections.observableArrayList();

    private ObservableList<PieChart.Data> promotedList = FXCollections.observableArrayList();

    private ObservableList <Student> promotedStudentList = FXCollections.observableArrayList();

    private ObservableList <Student> conscienciousStudents = FXCollections.observableArrayList();

    private ObservableList <PieChart.Data> consListPieChart = FXCollections.observableArrayList();

    private ObservableList< XYChart.Series<String,Integer> > chartConsc = FXCollections.observableArrayList();

    private PopOver popOver = new PopOver();
    private PDFSaveController saveControler;

    public void setPopOverContent(Parent parent){
        popOver.setContentNode(parent);
    }

    void createMarksPdf(String location,Rectangle pageSize,int fontSize,int titleSize) throws  Exception{
        pdfPrinter.setFontSize(fontSize);

        pdfPrinter.setSpacing(3 * fontSize);

        pdfPrinter.setPdfFileTitle("Lab notes for students");

        pdfPrinter.setTheHarderHomework(statisticsService.getTheHarderOne());

        pdfPrinter.setRepresentSeparator(";");

        pdfPrinter.setPdfFileName(location);

        pdfPrinter.setTitleSize(titleSize);

        pdfPrinter.printFile(pageSize,
                tableViewMarks,
                tableModel.stream().sorted(Comparator.comparingInt(x -> -Math.round(x.getValue()))).collect(Collectors.toList()),
                P->{
                    Student student = P.getKey();
                    return student.getNume() + ";" + student.getGrupa() +";" +P.getValue()+";" + student.getEmail();
                }
        );

    }

    void createPassedPdf(String location,Rectangle pageSize,int fontSize,int titleSize) throws  Exception{

        pdfPrinter.setFontSize(fontSize);

        pdfPrinter.setSpacing(3 * fontSize);

        pdfPrinter.setPdfFileTitle("Students who passed the lab");

        pdfPrinter.setTheHarderHomework(null);

        pdfPrinter.setRepresentSeparator(";");

        pdfPrinter.setPdfFileName(location);

        pdfPrinter.setTitleSize(titleSize);

        pdfPrinter.printFile(pageSize,
                tableViewPromoted,
                promotedStudentList.stream().sorted(Comparator.comparing(Student::getNume)).collect(Collectors.toList()),
                S-> S.getNume() + ";" + S.getGrupa() +";" +S.getCadruDidactic()+";" + S.getEmail()
        );
    }

    void createConsPdf(String location,Rectangle pageSize,int fontSize,int titleSize) throws  Exception{

        pdfPrinter.setFontSize(fontSize);

        pdfPrinter.setSpacing(3 * fontSize);

        pdfPrinter.setPdfFileTitle("Most diligent students");

        pdfPrinter.setTheHarderHomework(null);

        pdfPrinter.setRepresentSeparator(";");

        pdfPrinter.setPdfFileName(location);

        pdfPrinter.setTitleSize(titleSize);

        pdfPrinter.printFile(pageSize,
                tableViewConsciencious,
                conscienciousStudents.stream().sorted(Comparator.comparing(Student::getNume)).collect(Collectors.toList()),
                S-> S.getNume() + ";" + S.getGrupa() +";" +S.getCadruDidactic()+";" + S.getEmail()
        );
    }

    private void handleMarksPDF(ActionEvent event){
        saveControler.resetAll();
        if(!popOver.isShowing())popOver.show(buttonLabMarks);
        saveControler.setMainPopOver(popOver);
        saveControler.setWindow(PDFs.Marks);
    }

    private void handlePassedPDF(ActionEvent event){
        saveControler.resetAll();
        if(!popOver.isShowing())popOver.show(buttonLabPassed);
        saveControler.setMainPopOver(popOver);
        saveControler.setWindow(PDFs.Promoted);
    }

    private void handleStudentsPDF(ActionEvent event){
        saveControler.resetAll();
        if(!popOver.isShowing())popOver.show(buttonLabStudents);
        saveControler.setMainPopOver(popOver);
        saveControler.setWindow(PDFs.Cons);
    }

    public void hideAllPopOvers(){
        popOver.hide(Duration.millis(0));
    }

    public void setPdfControler(PDFSaveController saveControler){
        this.saveControler = saveControler;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        popOver.setArrowLocation(PopOver.ArrowLocation.LEFT_TOP);
        popOver.setTitle("");

        buttonLabMarks.setOnAction(this::handleMarksPDF);

        buttonLabPassed.setOnAction(this::handlePassedPDF);

        buttonLabStudents.setOnAction(this::handleStudentsPDF);

        tableColumnName.setCellValueFactory(value->new SimpleStringProperty(value.getValue().getKey().getNume()));

        tableColumnGroup.setCellValueFactory(value->new SimpleStringProperty(value.getValue().getKey().getGrupa()+""));

        tableColumnMark.setCellValueFactory(value->new SimpleStringProperty(value.getValue().getValue()+""));

        tableColumnEmail.setCellValueFactory(value->new SimpleStringProperty(value.getValue().getKey().getEmail()));

        tableColumnPromotedEmail.setCellValueFactory(student->new SimpleStringProperty(student.getValue().getEmail()));

        tableColumnPromotedGroup.setCellValueFactory(student->new SimpleStringProperty(student.getValue().getGrupa() + ""));

        tableColumnPromotedTeacher.setCellValueFactory(student->new SimpleStringProperty(student.getValue().getCadruDidactic()));

        tableColumnPromotedName.setCellValueFactory(student->new SimpleStringProperty(student.getValue().getNume()));

        tableColumnConscName.setCellValueFactory(student->new SimpleStringProperty(student.getValue().getNume()));

        tableColumnConscGroup.setCellValueFactory(student->new SimpleStringProperty(student.getValue().getGrupa()+""));

        tableColumnConscTeacher.setCellValueFactory(student->new SimpleStringProperty(student.getValue().getCadruDidactic()));

        tableColumnConscEmail.setCellValueFactory(student->new SimpleStringProperty(student.getValue().getEmail()));

        tableViewMarks.setItems(tableModel);
        tableViewPromoted.setItems(promotedStudentList);
        tableViewConsciencious.setItems(conscienciousStudents);

        piechartMarks.setData(marksList);
        homeworksBarChart.setData(homeList);
        pieChartPromoted.setData(promotedList);
        consPieChart.setData(consListPieChart);
        barChartConsc.setData(chartConsc);

        pieChartPromoted.setTitle("Top promoted students");
        piechartMarks.setTitle("Top lab marks");
        homeworksBarChart.setTitle("Top 10 hardest homeworks");
        barChartConsc.setTitle("Top 10 lazy students");

        tableColumnMark.setComparator(Comparator.comparing(Double::parseDouble));

        tableColumnGroup.setComparator(Comparator.comparing(Integer::parseInt));

        tableColumnPromotedGroup.setComparator(Comparator.comparing(Integer::parseInt));
    }


    private void insertDataInMarksTable(){
        tableModel.setAll(statisticsService.getMedieForStudents().stream().sorted(Comparator.comparing(x -> x.getKey().getNume())).collect(Collectors.toList()));
    }

    private void insertDataInMarksPieChart(){
        marksList.setAll(statisticsService.getAllMarksCount());
    }

    private void insertDataInBarChart() {
        homeList.setAll(statisticsService.getTop10Hardest());
    }

    private void insertDataInPromotedBarChart(){
        promotedList.setAll(statisticsService.getPromotedMarks());
    }

    private void insertDataIntoPromotedTableView(){
        promotedStudentList.setAll(statisticsService.getAllThatPromoted());
    }

    private void insetDataIntoConsTableView(){

        conscienciousStudents.setAll(statisticsService.getAllConscientious());
    }

    private void insertDataIntoConsPieChart(){
        consListPieChart.setAll(statisticsService.getDelays());
    }


    private void insertDataIntoLazyBarChart(){
        chartConsc.setAll(statisticsService.getTop10Lazy());
    }

    @Override
    public void update(Observable observable) {

        insertDataInMarksTable();insertDataInMarksPieChart();insertDataInBarChart();insertDataIntoConsPieChart();
        insertDataInPromotedBarChart();insertDataIntoPromotedTableView();insetDataIntoConsTableView();
        insertDataIntoLazyBarChart();
    }

    public void setStatisticsService(StatisticsService statisticsService){
        this.statisticsService = statisticsService;
        update(null);
    }
}
