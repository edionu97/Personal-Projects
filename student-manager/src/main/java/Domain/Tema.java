package Domain;

import java.io.Serializable;

public class Tema implements Serializable{

    private int nrTema,deadline;
    private String cerinta;

    public Tema(int nrTema, int deadline, String cerinta) {
        this.nrTema = nrTema;
        this.deadline = deadline;
        this.cerinta = cerinta;
    }

    public int getNrTema() {
        return nrTema;
    }

    public int getDeadline() {
        return deadline;
    }

    public String getCerinta() {
        return cerinta;
    }

    public void setDeadline(int deadline) {
        this.deadline = deadline;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Tema tema = (Tema) o;

        return nrTema == tema.nrTema;
    }

    @Override
    public String toString(){
        return "Id " + nrTema + " DeadLine: " + deadline +" Cerinta " + cerinta;
    }

}
