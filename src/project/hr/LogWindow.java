/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project.hr;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

/**
 * FXML Controller class
 *
 * @author g0403053
 */
public class LogWindow implements Initializable {
    @FXML
    private ListView<String> logList;
    
    Controller controller;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        controller = Controller.getInstance();
        controller.registerView(this);
        controller.getLog();
    }    
    
    public void updateList(ArrayList<String[]> list) {
        for(String[] item: list){
            StringBuilder builder = new StringBuilder();
            for(String s : item) {
                builder.append(s);
            }
            logList.getItems().add(builder.toString());
    }   }
    
}