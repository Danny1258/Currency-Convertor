/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 *
 * @author Dan
 */
public class Extractor {
    
    private double rate;
    private Boolean exec=false;
    
    public void Extract(String date,String initial) throws MalformedURLException, IOException{
        // this if statement sets the USD as the "anchor" currency of the whole system and the reason for that
        // is very simple, the data is extracted from the FED, whose system is designed oround that assumption too
        if(initial.equals("USD")){
            rate=1;
            exec=true; // acts as a break;
        }else{
            // gets the proper URL address for the given currency
            String website = Extractor.getCode(initial);
            // and makes a copy of it
            URL url = new URL(website);
            InputStream is =  url.openStream();
            try( BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String line;
                // reads the HTML text behind the URL line by line
                while ((line = br.readLine()) != null&& exec!=true) {
                    // checks if the line contains the date we are looking for
                    if(line.contains(date)){
                        // it reads the next line to find the conversion rate
                        String line2= br.readLine();
                        // and it calls this function to update the rate
                        returnValue(line2);
                        //System.out.println(rate);
                        exec=true; // acts as a break
                    }
                }
            }
        }
    }
    
    // based on the selected currency it returns the appropriate URL
    public static String getCode(String input){
        String tmp = null;
        switch(input){
            case "EUR" : tmp = "https://www.federalreserve.gov/RELEASES/H10/Hist/dat00_eu.htm"; 
            break;
            case "GBP" : tmp = "https://www.federalreserve.gov/RELEASES/H10/Hist/dat00_uk.htm"; 
            break; 
        }
        return tmp;
    }
    
    // a recursive function that takes out from the line until it finds the conversion rate value
    public  void returnValue(String line){
        double tmp;
        Scanner in = new Scanner(line);
        try{
            // if the double has been found, the recursivity stops and the rate is updated
            tmp = in.nextDouble();
            rate=tmp;
        }
        catch (Exception e){
            // otherwise it keeps calling it recursively with a smaller line until it finds it
            String s = in.next();
            line = line.replace(s, "");
            //System.out.println(line);
            returnValue(line);
        }
    }   
    
    // returns the rate
    public double getRate(){
        return rate;
    }
    
}


/* Dan's comments:
    I implemented only 3 currencies because:
- having more makes the code harder to read
- it provides no further insight into how the program works
- they can be easily hardcoded since there is a limited amount of official currencies - currently 164

    The Extract algorithm is really slow, especially if your input date is quite recent because it will
compare the first char of the input with every other char in the HTML text, which is a lot of comparisons
I can think of a few ways to optimize that but can't decide which would be better:

    1. Extract all the data from website, store it in a txt, read the txt every time
Adv: allows you to pretty accurately predict where a certain date (and rate) will be so you can skip over
in-depth reading of most lines
Dis:  not the most efficient algorithm since you still have to read through it line by line or char by char
during runtime until the desired data is reached

    2. Extract all the data from website, store it in a txt, extract it in an array during runtime start
Adv: good for prolonged runtime usage as you can jump multiple sections at a time without going through them
at all
Dis: might be less efficient than 1. for a small number of conversions since writing them in an array isn't
cost free, it uses quite a bit of memory to store the array

    General Adv: both can work without an internet connection
            Dis: both rely on a txt file, needs to be updated periodically (or keep track of new date / rate inputs)
                 to ensure all the data available online has been extracted

*/