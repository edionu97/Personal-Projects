package User.FXML.UTILS;

import Domain.Tema;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

public class InfoCell  extends TableCell<Tema,Boolean> {

    private Button infoButton = new Button();
    private AnchorPane anchorPane = new AnchorPane();
    private Tooltip tooltip = new Tooltip("Select the cell and then\nPress to find more about this homework");
    private TextArea textArea;
    private TableView<Tema> tableView;

    private void setButton(){
        infoButton.setId("infoButton");

        infoButton.setTooltip(tooltip);

        infoButton.setOnAction((event -> {
            getTableView().getSelectionModel().select(getIndex());
            textArea.setVisible(true);
            textArea.setText("Requirement: " + tableView.getSelectionModel().getSelectedItem().getCerinta());
        }));
    }

    private void setButtonActions(){
        setButton();
    }

    public InfoCell(TextArea textArea,TableView <Tema> tableView){
        super();
        this.textArea = textArea;
        this.tableView = tableView;
        setButtonActions();

        anchorPane.getChildren().add(infoButton);
    }

    @Override
    protected void updateItem(Boolean item,boolean empty){

        if(empty){
            setGraphic(null);return;
        }

        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        setGraphic(infoButton);
    }
}
