/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exchange;

import java.io.IOException;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**fRF
 *
 * @author Dan
 */
public class Exchange extends Application{
    
    private Button button,button2;
    private double rate, rateA, rateB;
    private double currencyRateA,currencyRateB;
    
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        
        primaryStage.setResizable(true); // allows the window to be resized
        
        // get the screen dimensions and sets the stage dimensions accordingly
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX(primaryScreenBounds.getMinX());
        primaryStage.setY(primaryScreenBounds.getMinY());
        primaryStage.setWidth(primaryScreenBounds.getWidth());
        primaryStage.setHeight(primaryScreenBounds.getHeight());
        
        // the window title
        primaryStage.setTitle("Dan's Amazing App");
        
        //sets up a grid and border spacing
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10,10,10,10));
        grid.setVgap(8);
        grid.setHgap(10);
        
        // adds text in a set place in the scene
        Label inputLabel = new Label("Amount:");
        GridPane.setConstraints(inputLabel,0,0);
        
        // creates a text input field in the scene
        TextField amountField = new TextField();
        
        // allows only inputs of format 11(...)11 -> int or 11(...)11,11(...)11 -> double
        Pattern pattern = Pattern.compile("\\d*|\\d+\\.\\d*");
        TextFormatter formatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change -> {
        return pattern.matcher(change.getControlNewText()).matches() ? change : null;
        });
        amountField.setTextFormatter(formatter);
        GridPane.setConstraints(amountField,1,0); 
         
        Label inputLabel2 = new Label("Initial currency:");
        GridPane.setConstraints(inputLabel2,0,1);
        
        // creates a choicebox allowing the user to select the currency
        ChoiceBox<String> currencyChoiceFrom = new ChoiceBox<>();
        currencyChoiceFrom.getItems().add("USD");
        currencyChoiceFrom.getItems().add("EUR");
        currencyChoiceFrom.getItems().add("GBP");       
        currencyChoiceFrom.setValue("USD");      
        GridPane.setConstraints(currencyChoiceFrom,1,1);
        
        Label inputLabel3 = new Label("Resulting currency:");
        GridPane.setConstraints(inputLabel3,0,2);
        
        ChoiceBox<String> currencyChoiceTo = new ChoiceBox<>();
        currencyChoiceTo.getItems().add("USD");
        currencyChoiceTo.getItems().add("EUR");
        currencyChoiceTo.getItems().add("GBP");
        currencyChoiceTo.setValue("USD");
        GridPane.setConstraints(currencyChoiceTo,1,2);
               
                
        Label inputLabel4 = new Label("Resulting amount:");
        GridPane.setConstraints(inputLabel4,0,3);
        
        TextField amountField2 = new TextField();
        Pattern pattern2 = Pattern.compile("\\d*|\\d+\\.\\d*");
        TextFormatter formatter2 = new TextFormatter((UnaryOperator<TextFormatter.Change>) change -> {
        return pattern2.matcher(change.getControlNewText()).matches() ? change : null;
        });
        amountField.setTextFormatter(formatter2);
        GridPane.setConstraints(amountField2,1,3);
        
        Label inputLabelDate = new Label("Date");
        GridPane.setConstraints(inputLabelDate,0,4);
        
        TextField dateField = new TextField();
        dateField.setPromptText("eg: 9-Mar-18");
        GridPane.setConstraints(dateField,1,4); 
        
        // this button reverses the currency selections and sets both amounts to 0
        button2 = new Button();
        button2.setText("Reverse");
        GridPane.setConstraints(button2, 0,5);
        button2.setOnAction(e -> { 
            String temporary=currencyChoiceFrom.getValue();
            currencyChoiceFrom.setValue(currencyChoiceTo.getValue());
            currencyChoiceTo.setValue(temporary);
            amountField.setText("0");
            amountField2.setText("0");       
        });
        
        //converts the initial amount of a given currency to a new amount of another currency
        button = new Button();
        button.setText("Convert!");
        GridPane.setConstraints(button, 1,5);
        button.setOnAction(e -> {
            try {
                // conversion rate from the initial currency to the "anchor" currency
                rateA=updateRate(dateField.getText(),currencyChoiceFrom.getValue());
                // conversion rate from the "anchor" currency to the resulting currency
                rateB=updateRate(dateField.getText(),currencyChoiceTo.getValue());
                // composes both conversion rates, multiplies by initial amount and gets it ready to output it
                String total = String.valueOf(output(conversionRate(rateA,rateB),Double.parseDouble(amountField.getText())));
                // not very classy, but you'd need to convert over 100.000.000.000.000.000$ or 100 quadrillion $
                // at the exchange rate of 9th of March 2018 to reach that number...or just input the wrong date
                if(total.equals("9.223372036854776E16")){
                    // calls for a new window of a specified dimension with a specific message
                    AlertBox.display("Notification", "Invalid Date",(int) primaryScreenBounds.getWidth()/2 , (int) primaryScreenBounds.getHeight()/2);
                }else{
                    // displays the resulting amount
                    amountField2.setText(total);
                }
                
            } catch (IOException ex) {
                Logger.getLogger(Exchange.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        });
        
        
        
        //adds everything to the grid
        grid.getChildren().addAll(inputLabel, amountField, inputLabel2, currencyChoiceFrom,inputLabel3,
                currencyChoiceTo,inputLabel4, amountField2,inputLabelDate,dateField, button2, button);
        
        //creates a new scene with the same size as the display
        Scene scene = new Scene(grid,primaryScreenBounds.getWidth(),primaryScreenBounds.getHeight());
        primaryStage.setScene(scene);
        primaryStage.show();
   
    }
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);   
    }
    
    // rounds the number
    public double roundNumber(double oldNumber){
        return (float)Math.round(oldNumber);
    }
    
    // composes two conversion rates
    public double conversionRate(double rateA, double rateB){
        return rateA / rateB;
    }
    
    // returns a number with exactly two digits after the ',', still, not in a very classy fashion
    public double output(double rate, double amount){
        return roundNumber(rate*amount*100)/100;
        
    }
    
    // gets the conversion rate for the specified currency for the specified date
    public double updateRate(String date, String currency) throws IOException{
        Extractor test = new Extractor();
        test.Extract(date,currency);
        return test.getRate();    
    }
}