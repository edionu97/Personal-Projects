package Utils;

import Domain.Nota;
import Domain.Student;
import Domain.Tema;
import Services.StudentService;
import Services.TemeService;

import java.util.Optional;

public class MarksDTO {

    private TemeService temeService;
    private StudentService studentService;

    public MarksDTO(TemeService temeService, StudentService studentService) {
        this.temeService = temeService;
        this.studentService = studentService;
    }

    public String translate(Nota mark){

        Optional<Student> student = studentService.getAll().stream().filter(S->S.getIdStudent().equals(mark.getIdStudent())).findAny();

        Optional<Tema> tema = temeService.getAll().stream().filter(T->T.getNrTema() == mark.getIdTema()).findAny();

        if(!student.isPresent() || !tema.isPresent())return null;

        return student.get().getNume() + "|" + student.get().getGrupa() + "|" +tema.get().getNrTema() +  "|" + mark.getNota() + "|" +mark.getObservations();
    }
}
