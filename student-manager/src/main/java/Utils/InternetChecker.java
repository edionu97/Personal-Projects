package Utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class InternetChecker {

    private static InternetChecker internetChecker = new InternetChecker();

    private  InternetChecker(){}

    public static  InternetChecker getInstance(){
        return  internetChecker;
    }

    public  boolean isInternet() {
        try {

            Process process = Runtime.getRuntime().exec("ping www.google.com");

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            return !bufferedReader.readLine().equals("Ping request could not find host www.google.com. Please check the name and try again.");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return  false;
    }

}
