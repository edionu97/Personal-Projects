package Utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public class IdStudentWriter {

    private String fileName,typeOfMod;
    private int idTema;
    private float nota;
    private int deadline;
    private int nrSapPredata;

    private static  IdStudentWriter instance = new IdStudentWriter();

    private IdStudentWriter(){};

    public void setWriter(String fileName, int idTema, float nota, int deadline, int nrSapPredata) {
        this.fileName = fileName;
        this.idTema = idTema;
        this.nota = nota;
        this.deadline = deadline;
        this.nrSapPredata = nrSapPredata;
    }

    public void setTypeOfMod(String mod){
        typeOfMod = mod;
    }

    public static IdStudentWriter getInstance(){
        return instance;
    }

    public void write(String observation){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName,true))) {
            writer.write(typeOfMod + "," + idTema +"," + nota +"," + deadline + "," + nrSapPredata +"," + observation+"\n");
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}
