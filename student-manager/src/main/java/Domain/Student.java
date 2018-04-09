package Domain;

import java.io.Serializable;

public class Student implements Serializable {

    private String nume,email,cadruDidactic,idStudent;
    private int grupa;

    /**
     * Creates a student with the following fields:
     * @param nume
     * @param email
     * @param cadruDidactic
     * @param idStudent
     * @param grupa
     */

    public Student(String nume, String email, String cadruDidactic, String idStudent, int grupa) {
        this.nume = nume;
        this.email = email;
        this.cadruDidactic = cadruDidactic;
        this.idStudent = idStudent;
        this.grupa = grupa;
    }

    public String getNume() {
        return nume;
    }

    public String getEmail() {
        return email;
    }

    public String getCadruDidactic() {
        return cadruDidactic;
    }

    public String getIdStudent() {
        return idStudent;
    }

    public int getGrupa() {
        return grupa;
    }

    /**
     *
     * @param o
     * @return true if *this is equal with object o,which in this case mean that o and *this have the same id
     */

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Student student = (Student) o;

        return idStudent.equals(student.idStudent);
    }

    /**
     *
     * @return a easy to understand representation of the Student object
     */

    @Override
    public String toString() {
        return "Id: " +idStudent + " Nume: " + nume + " Email: " + email + " Cadru: " + cadruDidactic + " Gr: " + grupa;
    }
}
