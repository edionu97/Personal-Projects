package User.FXML.UTILS;

import Domain.Student;
import User.FXML.Controlers.StudentsWindowController;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;


public class ButtonCell extends TableCell <Student,Boolean> {

    private Button edit = new Button("Edit"),delete = new Button("Delete");

    private HBox vBox = new HBox();

    private Tooltip tooltipEdit = new Tooltip(),tooltipDelete = new Tooltip();

    private StudentsWindowController studentsWindowController;

    private AlertClass alertClass = new AlertClass("\n  student",vBox,400,205);

    private void setActions() {

        tooltipEdit.setText("Press to edit the student");
        tooltipDelete.setText("Press to delete the student");

        tooltipEdit.setStyle("-fx-text-fill:white");

        tooltipDelete.setStyle("-fx-text-fill:white");

        edit.setTooltip(tooltipEdit);
        delete.setTooltip(tooltipDelete);

        edit.setOnAction(event -> {
            getTableView().getSelectionModel().select(getIndex());
            studentsWindowController.handleEditEvent(event,edit);

        });

        delete.setOnAction(event -> {
            getTableView().getSelectionModel().select(getIndex());

            if(alertClass.getAlert().showAndWait().get() == ButtonType.CANCEL){
                getTableView().getSelectionModel().select(null);
                return;
            }

            studentsWindowController.handleDeleteEvent(event, delete);
        });

    }

    public ButtonCell(StudentsWindowController studentsWindowController){
        super();
        edit.setId("editButton");delete.setId("deleteButton");

        vBox.setPadding(new Insets(1,1,1,20));
        vBox.getChildren().addAll(edit,delete);
        vBox.setSpacing(10);
        this.studentsWindowController = studentsWindowController;
        setActions();
    }

    @Override
    protected void updateItem(Boolean item,boolean empty){
        super.updateItem(item,empty);

        if(empty){
            setGraphic(null);
            return;
        }

        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        setGraphic(vBox);
    }
}
