package Utils;

import java.io.FileOutputStream;
import java.io.IOException;

public class ClearFile {
    public static  void clear(String s){
        try{
            FileOutputStream file = new FileOutputStream(s,false);
            file.close();
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}
