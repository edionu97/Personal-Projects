package Utils;

import java.io.File;

public class DeleteFile {
    public DeleteFile(String fileName){
        File deleter = new File(fileName);
        if(deleter.exists())deleter.delete();
    }
}
