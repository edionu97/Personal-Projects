package Validators;

import Domain.Tema;

import java.util.ArrayList;
import java.util.List;

public class TemeValidator implements  ValidatorInteface <Tema> {

    private String getMessages(List< String  > s){
        String error = "";
        for(String err : s)error = error.concat(err);
        return error;
    }

    @Override
    public void validate(Tema obj) throws ValidationException {
        ArrayList < String > errors = new ArrayList<>();

        if(obj.getCerinta().compareTo("") == 0) errors.add("Cerinta vida\n");
        if(obj.getDeadline() < 1 || obj.getDeadline() > 14)errors.add("Deadline invalid\n");
        if(obj.getNrTema() < 1)errors.add("Id invalid");
        if(errors.isEmpty())return;

        throw  new ValidationException(getMessages(errors));
    }
}
