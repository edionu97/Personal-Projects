package Utils;

import Domain.Nota;

public class NotaTranslated {

    private String name;
    private String group;
    private String idTema;
    private String mark;
    private String obs;

    public String getName() {
        return name;
    }

    public String getGroup() {
        return group;
    }

    public String getIdTema() {
        return idTema;
    }

    public String getMark() {
        return mark;
    }

    public String getObs() {
        return obs;
    }

    public NotaTranslated(MarksDTO marksDTO, Nota nota){

        if(marksDTO.translate(nota) ==null)return;

        String[] str = marksDTO.translate(nota).split("\\|");


        name = str[0];group = str[1];idTema = str[2];mark = str[3];

        if(str.length == 4){
            obs = "No description";
            return;
        }

        obs = str[4];
    }
}
