
import Domain.Nota;
import Domain.Student;
import Domain.Tema;

import Repository.Repository;
import Services.NotaService;
import Services.StatisticsService;
import Services.StudentService;
import Services.TemeService;
import User.FXML.Controlers.*;
import Utils.Notifier;
import Utils.StudentFileMover;
import Utils.XMLConfigLoader;
import Validators.StudentValidator;
import Validators.TemeValidator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Paths;


public class Main extends Application{

    private Repository<Student> studentAbstractRepo;

    private Repository <Tema> temaAbstractRepo;

    private Repository <Nota> notaAbstractRepo;

    private StudentService studentService;

    private TemeService temeService;

    private NotaService notaService;

    private StatisticsService statisticsService;

    private MarksViewController marksViewController;

    private StudentsWindowController studentsWindowController;

    private HomeworkWindowViewController homeworkWindowViewController;

    private MainWindowViewController mainWindowViewController;

    private StatisticsController statisticsController;

    private PDFSaveController pdfSaveController;

    private AddMarkWindowController addMarkWindowController;

    private XMLConfigLoader configLoader = XMLConfigLoader.getInstance();

    private Notifier notifier;

    private Parent getStudentWindowAndSetControllers() throws  Exception{

        //studentAbstractRepo = new FileRepo<Student>("Students.txt");

        //studentAbstractRepo = new MSSQLStudentRepository();

        studentAbstractRepo = configLoader.getStudentsRepo();

        studentService = new StudentService(studentAbstractRepo,new StudentValidator());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/StudentsWindowView.fxml"));

        FXMLLoader l2 = new FXMLLoader(getClass().getResource("/Views/AddWindowFxml.fxml"));

        FXMLLoader l3 = new FXMLLoader(getClass().getResource("/Views/StudentEditWindowView.fxml"));

        Parent studentWindow = loader.load();

        Parent studentEditWindow = l3.load();

        Parent addWindow = l2.load();

        studentsWindowController = loader.getController();

        studentsWindowController.setStudentService(studentService);

        studentsWindowController.addWindow(addWindow);

        studentsWindowController.addEditWindow(studentEditWindow);

        AddWindowFxmlController addWindowFxmlController = l2.getController();

        addWindowFxmlController.setStudentWindowControler(studentsWindowController);

        StudentEditWindowViewController studentEditWindowViewController = l3.getController();

        studentEditWindowViewController.setStudentsWindowController(studentsWindowController);

        studentsWindowController.addEditWindowControler(studentEditWindowViewController);

        return studentWindow;
    }

    private Parent getHomeworkWindowAndSetControlers() throws  Exception{

        //temaAbstractRepo = new FileRepo<Tema>("Teme.txt");

        //temaAbstractRepo = new MSSQLTemaRepository();

        temaAbstractRepo = configLoader.getHomeworksRepo();

        notifier = new Notifier(studentAbstractRepo);

        temeService = new TemeService(temaAbstractRepo,new TemeValidator(),notifier);


        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/HomeworkWindowView.fxml"));

        Parent homeworkWindow = loader.load();

        homeworkWindowViewController = loader.getController();

        temeService.addObserver(homeworkWindowViewController);

        homeworkWindowViewController.setTemeService(temeService);

        return homeworkWindow;
    }

    private Parent getMarksWindowAndSetControlers() throws  Exception{

        //notaAbstractRepo = new FileRepo<Nota>("Catalog.txt");

        //notaAbstractRepo = new MSSQLNotaRepository();

        notaAbstractRepo = configLoader.getMarksRepo();

        notaService = new NotaService(notaAbstractRepo,studentService,temeService,notifier);


        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/MarksView.fxml"));

        Parent markWindow = loader.load();

        marksViewController = loader.getController();

        marksViewController.setNotaService(notaService);

        return markWindow;
    }

    private Parent getAddWindow()throws  Exception{

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Views/AddMarkWindow.fxml"));

        Parent addWindow = fxmlLoader.load();

        addMarkWindowController = fxmlLoader.getController();

        addMarkWindowController.setStudentService(studentService);
        addMarkWindowController.setTemeService(temeService);
        addMarkWindowController.setNotaService(notaService);
        addMarkWindowController.setMarksViewController(marksViewController);

        marksViewController.setAddMarkWindowController(addMarkWindowController);

        return addWindow;
    }

    private Parent getMainWindow() throws  Exception{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/MainWindowView.fxml"));

        Parent mainWindow = loader.load();

        mainWindowViewController = loader.getController();

        return  mainWindow;

    }

    private Parent getStatistics() throws Exception{

        statisticsService = new StatisticsService(studentAbstractRepo,temaAbstractRepo,notaAbstractRepo);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/StatisticsView.fxml"));

        Parent window = loader.load();

        statisticsController = loader.getController();

        statisticsController.setStatisticsService(statisticsService);

        return window;
    }

    private Parent getPdfSave() throws  Exception{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/PDFSaveView.fxml"));

        Parent pdf = loader.load();

        pdfSaveController = loader.getController();

        return pdf;
    }

    private void writeBack() throws URISyntaxException {
        StudentFileMover studentFileMover = new StudentFileMover();


        studentFileMover.setCurrentLocation(Paths.get("").toAbsolutePath().toString());

        studentFileMover.setRepository(studentAbstractRepo);

        File file = null;

        try {
            file = new File(getClass().getResource("/Config/currentConfig.txt").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        String location = null;
        try(BufferedReader reader = new BufferedReader(new FileReader(file))){
            location = reader.readLine();
        }catch (Exception e){
            e.printStackTrace();
        }


        studentFileMover.setDestination(getClass().getResource("/Config").toURI().getPath() +"/"+location );
        studentFileMover.moveAll();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        try {

            configLoader.checkCurrentConfig();

            Parent studentWindow = getStudentWindowAndSetControllers();

            Parent homeworkWindow = getHomeworkWindowAndSetControlers();

            Parent markWindow = getMarksWindowAndSetControlers();

            Parent addMark = getAddWindow();

            Parent statWind = getStatistics();

            Parent mainWindow = getMainWindow();

            studentService.addObserver(marksViewController);
            studentService.addObserver(statisticsController);
            temeService.addObserver(marksViewController);
            temeService.addObserver(statisticsController);
            notaService.addObserver(statisticsController);

            marksViewController.addInCenter(addMark);

            mainWindowViewController.setHomeworkWindow(homeworkWindow);
            mainWindowViewController.setMakrsWindow(markWindow);
            mainWindowViewController.setStudentWindow(studentWindow);
            mainWindowViewController.setStatisticsWindow(statWind);

            statisticsController.setPopOverContent(getPdfSave());
            statisticsController.setPdfControler(pdfSaveController);

            pdfSaveController.setFileChooserStage(primaryStage);

            pdfSaveController.setStatistics(statisticsController);


            Scene scene = new Scene(mainWindow);
            scene.getStylesheets().add(getClass().getResource("/Sheets/style.css").toExternalForm());

            primaryStage.setScene(scene);


            primaryStage.show();

            primaryStage.setResizable(false);

            primaryStage.getIcons().add(new Image(getClass().getResource("/Images/manager.png").toExternalForm()));

            primaryStage.setOnCloseRequest(event -> {

                studentsWindowController.hideAllPopOvers();
                homeworkWindowViewController.hideAllPopOvers();
                marksViewController.hideAllPopOvers();
                statisticsController.hideAllPopOvers();
                addMarkWindowController.hideAllPopOvers();

                try {
                    writeBack();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            });

        }catch (Exception e){
            primaryStage.setScene(new Scene(new Label(e.getMessage())));
        }

    }

    public static void main(String...args){
        launch(args);
    }
}
