package Utils;

import Domain.Student;
import Repository.Repository;

import java.io.File;

public class StudentFileMover {

    private Repository <Student> repository;
    private String setStar="";
    private File destDirectory;

    public void setCurrentLocation(String s){
        setStar = s+"/";
    }

    public void setRepository(Repository <Student> repository){
        this.repository = repository;
    }

    public  void setDestination(String destination){
        destDirectory = new File(destination);
    }

    public void moveAll(){
        repository.getAll().forEach(student -> {
            File oldFile = new File(setStar.concat(student.getIdStudent().concat(".txt")));

            if(!oldFile.exists())return;

            if(!oldFile.renameTo(new File(destDirectory,oldFile.getName()))) System.out.println("File:" + oldFile.getName());
        });
    }

}
