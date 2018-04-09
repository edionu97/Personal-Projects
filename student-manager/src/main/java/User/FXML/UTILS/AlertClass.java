package User.FXML.UTILS;

import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class AlertClass {

     private String text;
     private  Parent parent;
     private int offsetX,offsetY;

     public AlertClass(String alert, Parent parent,int offsetX,int offsetY){
         text = alert;this.parent = parent;
         this.offsetX = offsetX;this.offsetY = offsetY;
     }

     public Alert getAlert(){

         Window window  = parent.getScene().getWindow();


         Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

         alert.initStyle(StageStyle.UNDECORATED);

         alert.initModality(Modality.APPLICATION_MODAL);

         alert.initOwner(window);

         alert.getDialogPane().getStylesheets().add(getClass().getResource("/Sheets/style.css").toExternalForm());

         alert.setHeaderText("Delete the" + text);

         alert.setX((window.getX() + offsetX));
         alert.setY((window.getY() + offsetY));

         alert.getDialogPane().setMaxWidth(150);

         return alert;
    };
}
