package Validators;

import Domain.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class StudentValidator implements  ValidatorInteface <Student> {

    private String getAllMessages(List <String > s){
        String errList = new String();
        for(String err : s)errList = errList.concat(err);
        return errList;
    }

    @Override
    public void validate(Student student) throws ValidationException {
        ArrayList < String  > list = new ArrayList<>();

        if(student.getIdStudent().trim().equals(""))list.add("Id vid\n");

        try{
            Integer.parseInt(student.getIdStudent());
        }catch (Exception e){
            list.add("Id string\n");
        }

        if(student.getNume().trim().equals("")) list.add("Nume vid\n");
        if(student.getCadruDidactic().trim().equals(""))list.add("Cadru vid\n");
        if(student.getGrupa() <= 0)list.add("Grupa invalida\n");

        if(!Pattern.compile("^[a-zA-Z][\\w0-9\\.]*@.*\\.[a-zA-Z]+$").matcher(student.getEmail()).find())list.add("Email invalid");

        if(list.isEmpty())return;

        throw new ValidationException(getAllMessages(list));
    }
}
