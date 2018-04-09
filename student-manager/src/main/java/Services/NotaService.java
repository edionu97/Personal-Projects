package Services;

import Domain.Nota;
import Domain.Student;
import Domain.Tema;
import Errors.StudentNotExist;
import Errors.TemaNotExist;
import Repository.Repository;
import Utils.*;

import java.io.*;
import java.net.Inet4Address;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class NotaService extends Observable{

    private Repository<Nota> repoNota;

    private StudentService studentService;

    private TemeService temeService;

    private IdStudentWriter notaWriter = IdStudentWriter.getInstance();

    private Notifier notifier;


    private void checkErrors(int saptamanaPredare, float nota,String idStudent,int idTema) throws  Exception{

        if(saptamanaPredare < 1 || saptamanaPredare > 14)throw  new Exception("Saptamana trebuie sa fie intre 1-14");

        if(nota < 1 || nota > 10) throw new Exception("Nota trebuie sa fie intre 1-10");

        if(!studentService.find(idStudent))throw new StudentNotExist("Studentul nu exista");

        if(!temeService.find(idTema))throw  new TemaNotExist("Tema nu exista");
    }

    /**
     * This function modifies the mark accordingly to @trebuiaPredata parameter
     * @param nota
     * @param spatamanaPredare
     * @param trebuiaPredata
     * @return
     */

    private float modifyNota(float nota,int spatamanaPredare,int trebuiaPredata){
        if(spatamanaPredare - trebuiaPredata >= 2)return 1;
        if(spatamanaPredare - trebuiaPredata == 1)return Math.max(nota - 2, 1);
        return  nota;
    }

    /**
     * This function eliminates all the notes that belong to a student that no longer exists or a homework that no longer exists
     */

    public void eliminateAllIncorect(){

        repoNota.getAll().removeIf(n->!studentService.find(n.getIdStudent()) || !temeService.find(n.getIdTema()));repoNota.save();

        //todo fa ceva cu fiserele id.txt(o functie la constructor care sa rescrie fisierrele un functie de datele actuale din repository)
    }

    private String sentFormatAdd(String fileName) throws  Exception{

        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName.concat(".txt")))){

            String message = "Date: " + LocalDate.now().toString() + "<br>";

            String x;

            while((x = bufferedReader.readLine()) != null){

                String[] fields = x.split(",");


                Optional<Tema> opTema = temeService.getAll().stream().filter(tema1 -> tema1.getNrTema() == Integer.parseInt(fields[1])).findFirst();

                if(!opTema.isPresent())continue;

                Tema tema = opTema.get();

                message = message.concat("<br><strong>"+fields[0]+"</strong><br>" +"Homework id: "  + fields[1] + " demand " + tema.getCerinta() +
                        "<br>Mark: " + fields[2] + " deadline: " + fields[3] + " evaluated: week " + fields[4] +
                        ((fields[5].contains("no penality") || fields[5].contains("no penalty")) ? "" : "<br><strong>" + fields[5].toLowerCase() + "</strong>")) + "<br>";
            }

            return  message;
        }catch (Exception e){
            e.printStackTrace();
        }

        return "Error";
    }

    /**
     * Used to find the deadline of a Tema object
     * @param idT
     * @return
     */

    private Optional<Integer> getDeadline(int idT){
        for(Tema t : temeService.getAll())
            if(t.getNrTema() == idT)return Optional.of(t.getDeadline());
        return Optional.empty();
    }

    /**
     * This function returns the mark which belongs to the Student with IdS and The homework with idT
     * @param idT
     * @return Optional.of(nota) if is data base exists such a mark or optional.empty() if not
     */

    private Optional <Nota> getNota(int idT,String idS){
        for(Nota n : getAll())
            if(n.getIdTema() == idT && n.getIdStudent().equals(idS))return Optional.of(n);
        return Optional.empty();
    }

    /**
     * Creates an NotaService  object
     * @param repoNota
     * @param studentService
     * @param temeService
     */


    public NotaService(Repository<Nota> repoNota, StudentService studentService, TemeService temeService,Notifier notifier) {
        this.repoNota = repoNota;
        this.studentService = studentService;
        this.temeService = temeService;
        this.notifier = notifier;
    }

    /**
     * This method tries to add a mark to a student and a homework, it automatically decreases the mark if the homework was not
     * taught at the specific time
     * @param idStudent
     * @param idTema
     * @param saptamanaPredare
     * @param nota
     * @throws Exception
     */

    public void addNota(String idStudent,int idTema,int saptamanaPredare,float nota,String obs) throws  Exception{

        eliminateAllIncorect();
        checkErrors(saptamanaPredare,nota,idStudent,idTema);

        Optional <Integer> optional = getDeadline(idTema);

        if(!optional.isPresent())throw new Exception("Nu exista tema cu id dat");

        float newNota = modifyNota(nota,saptamanaPredare,optional.get());

        Nota nota1 = new Nota(idTema,idStudent,newNota);

        nota1.setObservations(obs+"(week:" + saptamanaPredare +")");

        repoNota.add(nota1);

        notaWriter.setWriter(idStudent.concat(".txt"),idTema,newNota,optional.get(),saptamanaPredare);

        notaWriter.setTypeOfMod("Adaugare nota");

        notaWriter.write(newNota == nota ? obs.concat("(no penality)") : "Penalizare depasire deadline " + obs);

        notifyAllObservers();

        notifier.notify(idStudent,sentFormatAdd(idStudent));

        //notifier.notify(idStudent,"--> " + Files.lines(Paths.get(idStudent.concat(".txt"))).reduce("",(x,y)->x.concat(y+'\n')));
    }

    /**
     * This function updates the student mark only if the new mark is greater than the existing one
     * Writes in a file (idStudent.txt) the modification and all the penaltys that could appear
     * @param idStudent the student id
     * @param idTema  the tema id
     * @param saptamanaPredare  the week in which the homework was given
     * @param nota  the homework's mark
     * @param obs   the observations
     * @throws Exception
     */

    public void updateNota(String idStudent,int idTema,int saptamanaPredare,float nota,String obs) throws  Exception{

        eliminateAllIncorect();

        checkErrors(saptamanaPredare,nota,idStudent,idTema);

        Optional < Nota > n = getNota(idTema,idStudent);

        if(!n.isPresent())throw  new Exception("Nu exista tema/studentul cu id dat");

        float newNota = modifyNota(nota,saptamanaPredare,getDeadline(idTema).get());

        if(n.get().getNota() >= newNota)throw  new Exception("Nota nemodificata");

        n.get().setNota(newNota);
        n.get().setObservations(obs + " (week:" + saptamanaPredare +")" );

        repoNota.save();

        repoNota.update(n.get(),n.get());

        notaWriter.setWriter(idStudent.concat(".txt"),idTema,n.get().getNota(),getDeadline(idTema).get(),saptamanaPredare);

        notaWriter.setTypeOfMod("Modificare nota");

        notaWriter.write(nota == newNota ? obs.concat("(no penalty)") : "Penalizare depasire deadline " + obs);

        notifyAllObservers();

        notifier.notify(idStudent,sentFormatAdd(idStudent));
       // notifier.notify(idStudent,"--> " + Files.lines(Paths.get(idStudent.concat(".txt"))).reduce("",(x,y)->x.concat(y + '\n')));
    }

    public List < Nota > getAll(){

        eliminateAllIncorect();
        return repoNota.getAll();
    }

    public List <NotaTranslated> getTranslated(){

        eliminateAllIncorect();

        MarksDTO marksDTO = new MarksDTO(temeService,studentService);

        List <NotaTranslated> list = new ArrayList<>();

        repoNota.getAll().forEach(N-> list.add(new NotaTranslated(marksDTO,N)));

        return  list;
    }

    public List <NotaTranslated> getTranslatedInPage(int pageNumber,int pageElement){

        eliminateAllIncorect();

        MarksDTO marksDTO = new MarksDTO(temeService,studentService);

        List <NotaTranslated> list = new ArrayList<>();

        repoNota.getPage(pageNumber,pageElement).forEach(N-> list.add(new NotaTranslated(marksDTO,N)));

        return list;
    }

    public List<Student>getStudents(){
        return studentService.getAll();
    }

    public List<Tema> getTeme(){
        return temeService.getAll();
    }

    /**
     * Returns a list which contains all students and marks which are greater or equal to mark
     * @param mark
     * @return a list of Nota
     */

    public List < Nota > getWithUpper(float mark){
        return Filter.filter(getAll(),x->x.getNota() >= mark,(x,y)->(int)x.getNota()-(int)y.getNota());
    }

    /**
     * @param a lowest bound  of the interval
     * @param b upper bound of the interval
     */

    public List <Nota> getAllThatHaveMarkBetwenn(float a,float b){
        return Filter.filter(getAll(),x->x.getNota() >= Float.min(a,b) && x.getNota() <= Float.max(a,b),(x,y)->x.getIdStudent().compareTo(y.getIdStudent()));
    }

    public List <Nota> getFirstPercentOf(int percent) {

        percent = Integer.min(percent,100);
        percent = Integer.max(percent,0);

        List<Nota> l = Filter.filter(getAll(), x -> true, (x, y) -> -(int) Math.floor(x.getNota() - y.getNota()));
        return l.subList(0,  l.size() * percent / 100);
    }

    public List <NotaTranslated> translate(List < Nota > list){
        eliminateAllIncorect();

        MarksDTO marksDTO = new MarksDTO(temeService,studentService);

        List <NotaTranslated> lista = new ArrayList<>();

        list.forEach(N-> lista.add(new NotaTranslated(marksDTO,N)));

        return  lista;
    }

    public List < NotaTranslated > filter(Predicate < NotaTranslated > predicate){
        return Filter.filter(getTranslated(),predicate, Comparator.comparing(NotaTranslated::getIdTema));
    }
}
