package Domain;

import java.io.Serializable;

public class Nota implements Serializable{

    private int idTema;
    private String idStudent,observations;
    private float nota;

    public Nota(int idTema, String idStudent, float nota) {
        this.idTema = idTema;
        this.idStudent = idStudent;
        this.nota = nota;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public int getIdTema() {
        return idTema;
    }

    public String getIdStudent() {
        return idStudent;
    }

    public float getNota() {
        return nota;
    }

    public void setNota(float nota) {
        this.nota = nota;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Nota nota = (Nota) o;

        return nota.idStudent.equals( idStudent ) && nota.idTema == idTema;
    }

    @Override
    public String toString(){
        return "Id student: " + idStudent + " Id tema " + idTema + " Nota " + nota;
    }
}
