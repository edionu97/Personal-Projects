package Services;

import Domain.Tema;
import Errors.RepositoryError;
import Repository.Repository;
import Utils.Filter;
import Utils.Notifier;
import Utils.Observable;
import Validators.TemeValidator;
import Validators.ValidationException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class TemeService extends Observable {

    private Repository<Tema> repo;

    private TemeValidator temeValidator;

    private Notifier notifier;

    private String sendInfoFormatUpdate(int idTema){

        Tema tema = repo.find(t->t.getNrTema() == idTema).get();

        LocalDate date = LocalDate.now();

        return "Date: " + date + "<br>" +"The homework with id " + tema.getNrTema()+ " and demand: " + tema.getCerinta().toLowerCase() +" has been updated." + "<br>" +
                "New deadline is " + tema.getDeadline() + "<br>";

    }

    private String sendInfoFormatAdd(Tema tema){
        return "Date: " + LocalDate.now() + "<br>" + "A new homework has been added.<br>The new homework's id is: " + tema.getNrTema() + " and requirement is :" + tema.getCerinta().toLowerCase() + "<br>";
    }

    /**
     * Creates a TemeService object and sets it's fields
     * @param temaAbstractRepo
     * @param temeValidator
     */
    public TemeService(Repository < Tema > temaAbstractRepo,TemeValidator temeValidator,Notifier notifier){
        repo = temaAbstractRepo;this.temeValidator = temeValidator;this.notifier = notifier;
    }

    /**
     * Trying to add a object in data base
     * If the data is invalid then the ValidationException is thrown
     * If in data base exists an element with the same id then repository exception is thrown
     * @param cerinta
     * @param deadLine
     * @param idTema
     * @throws ValidationException
     * @throws RepositoryError
     */


    public void addTema(String cerinta,int deadLine,int idTema) throws ValidationException, RepositoryError {
        Tema tema = new Tema(idTema, deadLine, cerinta);
        temeValidator.validate(tema);
        repo.add(tema);
        notifier.notify(sendInfoFormatAdd(tema));
        notifyAllObservers();
    }

    /**
     * Removes from database the object with the id = idTema
     * Throws RepositoryError if such a object don't exist
     * @param idTema
     * @throws RepositoryError
     */

    public void deleteTema(int idTema) throws  RepositoryError{
        repo.delete(new Tema(idTema,0,""));
        notifyAllObservers();
    }

    /**
     * This function updates the deadline for a specific object( object.id == idTema) only if the object.deadLine < saptamana and newDeadline > oldDeadline
     * @param idTema
     * @param newDeadline
     * @param saptamana
     */

    public  void updateDeadline(int idTema,int newDeadline,int saptamana) throws Exception{

        Tema old = new Tema(idTema,newDeadline,repo.find(p->p.getNrTema() == idTema).get().getCerinta());

        temeValidator.validate(old);

        for (Tema tema : repo.getAll()) {

            if(tema.getNrTema() != idTema)continue;

            if(!(tema.getDeadline() > saptamana && tema.getDeadline() <  newDeadline))throw  new Exception("Modificare nepermisa");

            notifier.notify(sendInfoFormatUpdate(tema.getNrTema()));
            break;
        }

        repo.delete(old);repo.add(old);

        notifyAllObservers();

        repo.save();
    }

    public  boolean find(int idTema){
        return repo.find(new Tema(idTema,0,""));
    }

    public List< Tema > getAll(){
        return repo.getAll();
    }

    public List <Tema> getPage(int pageNumber,int pageElements){
        return repo.getPage(pageNumber,pageElements);
    }

    public List < Tema > getAllWithDeadline(int deadline){
        return Filter.filter(getAll(), x->x.getDeadline() <= deadline, (x,y)->-(x.getNrTema() - y.getNrTema()));
    }

    public List <Tema> getAllThatNeed(String needWhat) {
        return Filter.filter(getAll(),
                x->{Pattern p = Pattern.compile(needWhat);return p.matcher(x.getCerinta()).find();},
                (x,y)->x.getDeadline() == y.getDeadline() ? x.getNrTema() - y.getNrTema() : x.getDeadline() - y.getDeadline()
        );
    }

    public List <Tema >getAllDeadline(int deadline){
        return Filter.filter(getAll(), x->x.getDeadline() == deadline, (x,y)->-(x.getNrTema() - y.getNrTema()));
    }

    public List <Tema> filter(Predicate <Tema> predicate){
        return Filter.filter(getAll(),predicate, Comparator.comparing(Tema::getNrTema));
    }

    public List <Tema> getAllWithEvenId(){
        return Filter.filter(getAll(),x->x.getNrTema() % 2 == 0,(x,y)->-(x.getDeadline()- y.getDeadline()));
    }
}
