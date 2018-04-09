package Services;

import Domain.Student;
import Errors.RepositoryError;
import Repository.Repository;
import Utils.DeleteFile;
import Utils.Filter;
import Utils.Observable;
import Validators.StudentValidator;
import Validators.ValidationException;

import javax.swing.*;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;


public class StudentService extends Observable{

    private Repository< Student > repo;

    private StudentValidator studentValidator;

    private Optional<Student> getStudent(String id){
        for(Student s : repo.getAll())
            if(s.getIdStudent().compareTo(id) == 0)return Optional.of(s);
        return Optional.empty();
    }

    /**
     * Creates a StudentService object and sets it's fields
     * @param studentAbstractRepo
     * @param validator
     */

    public StudentService(Repository<Student> studentAbstractRepo, StudentValidator validator){
       repo=studentAbstractRepo;studentValidator = validator;
    }

    /**
     * Trying to add a student into database
     * If in database exists a student with the same id,then RepositoryError is thrown
     * If the data from parameters is invalid, then ValidationException is thrown
     *
     * @param nume
     * @param email
     * @param cadruDid
     * @param idStudent
     * @param grupa
     * @throws ValidationException
     * @throws RepositoryError
     */
    public void addStudent(String nume,String email,String cadruDid,String idStudent,int grupa) throws ValidationException,RepositoryError{
            Student s = new Student(nume,email,cadruDid,idStudent,grupa);
            studentValidator.validate(s);
            repo.add(s);
            notifyAllObservers();
    }

    /**
     * This function updates the information about a student
     * The id could not be changed,but the other fields could
     * @param id
     * @param email
     * @param cadruDidactic
     * @param nume
     * @param grupa
     * @throws ValidationException, if the new student has invalid  data
     * @throws RepositoryError , if in database does not exist a student with the id=idStudent
     */

    public void updateStudent(String id,String email,String cadruDidactic,String nume,String grupa) throws  ValidationException,RepositoryError{

        Optional<Student> getOptional = getStudent(id);

        if(!getOptional.isPresent())throw new RepositoryError("Student does not exist");

        Student exist =getOptional.get();


        Student s = new Student(
                nume.toLowerCase().equals("same") ? exist.getNume() : nume ,
                email.toLowerCase().equals("same") ? exist.getEmail() : email,
                cadruDidactic.toLowerCase().equals("same") ? exist.getCadruDidactic() : cadruDidactic,
                id,grupa.toLowerCase().equals("same") ? exist.getGrupa() : Integer.parseInt(grupa)
        );


        studentValidator.validate(s);
        repo.update(new Student("","","",id,0),s);

        notifyAllObservers();
    }

    /**
     * Removes the students from database or throws RepositoryException id the student does not exist
     * @param idStudent
     * @throws RepositoryError
     */

    public void deleteStudent(String idStudent) throws  RepositoryError{
            repo.delete(new Student("","","",idStudent,0));
            new DeleteFile(idStudent.concat(".txt"));
            notifyAllObservers();
    }

    /**
     *
     * @param idStudent
     * @return  true if in database exist a student with idStudent = iStudent or false if not
     */

    public boolean find(String idStudent){
        return  repo.find(new Student("","","",idStudent,0));
    }

    /**
     *
     * @return a list formed with all students in the data base
     */

    public List< Student > getAll(){
        return repo.getAll();
    }

    public List<Student> getPage(int pageNumber,int pageElements){
        return repo.getPage(pageNumber,pageElements);
    }

    public List < Student > filterByPrdicate(Predicate < Student > predicate){
        return  Filter.filter(getAll(),predicate, Comparator.comparing(Student::getIdStudent));
    }

    public List <Student> getAllThatStartWith(String letter){
        return Filter.filter(getAll(),
                (x)->x.getNume().startsWith(letter),
                (x,y)->-x.getNume().compareTo(y.getNume())
        );
    }

    public List <Student> getAllThatContains(String subs){
        return
                Filter.filter(getAll(), x->{
                    Pattern p = Pattern.compile(subs);
                    return p.matcher(x.getNume()).find();
                    },
                    (x,y)->x.getNume().compareTo(y.getNume())
                );
    }

    public  List < Student > getAllFromGroup(int grupa){
        return Filter.filter(getAll(),x->x.getGrupa() == grupa,(x,y)->x.getGrupa() - y.getGrupa());
    }

    public List <Student> gettAllFromTeacher(String teacher){

        return Filter.filter(getAll(), x->{
            Pattern p = Pattern.compile(teacher);
            return p.matcher(x.getCadruDidactic()).find();
        },(x,y)->x.getGrupa() - y.getGrupa());

    }

    public  List <Student> getAllFromGrupeStartingWith(int grupa,String string){
        return Filter.filter(
                getAll(),
                (x)->x.getNume().startsWith(string) && x.getGrupa() == grupa,
                (x,y)->x.getGrupa()-y.getGrupa()
        );
    }
}
