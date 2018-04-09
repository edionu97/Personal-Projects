package Repository;

import Errors.RepositoryError;

import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class FileRepo<T> extends  InMemoryRepo < T> {

    private File f;

    /**
     * Reads data from file and loads it in a vector
     */

    @SuppressWarnings("unchecked")
    private void readFromFile() {
        content.clear();

        try (ObjectInputStream fin = new ObjectInputStream(new BufferedInputStream(new FileInputStream(f.getName())))){
            T obj;
            while((obj = (T)fin.readObject()) != null)content.add(obj);
        }catch (Exception e){ }
    }


    /**
     * Write into file data,in object format
     */

    private void writeToFile(){

        try(ObjectOutputStream fout = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(f.getName())))){
            for(T obj: content)fout.writeObject(obj);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    /**
     * If the file does not exist it will be created
     * If the creation of fileName will not succeed IOException will be thrown
     * @param fileName
     * @throws IOException
     */

    public FileRepo(String fileName) throws IOException{
        f = new File(fileName);
        if(!f.exists())f.createNewFile();
        readFromFile();
    }

    /**
     * Adding obj in the collection.
     * If in collection exists an obj with the same id, RepositoryError will be thrown
     * @param obj
     * @throws RepositoryError
     */

    @Override
    public void add(T obj) throws RepositoryError {
        if(find(obj))throw  new RepositoryError("Mai exista");
        content.add(obj);
        writeToFile();
    }

    /**
     * Manually save data into file
     * This is util when for example you want to sort the elements or to update one element
     * It will write the content into file
     */

    @Override
    public void update(T obj1, T obj2) throws  RepositoryError{
        readFromFile();
        super.update(obj1,obj2);
        writeToFile();
    }

    @Override
    public void save() {
        writeToFile();
    }

    /**
     * Removes the object obj or throws RepositoryError if in colection  this does not exist.
     * Rewrites data again into file excepting the obj object
     * @param obj
     * @throws RepositoryError
     */

    @Override
    public void delete(T obj) throws RepositoryError {
        readFromFile();
        super.delete(obj);
        writeToFile();
    }

    /**
     *
     * @param obj
     * @return true if the object obj is in the collection or false contrary
     */

    @Override
    public boolean find(T obj) {
        readFromFile();
        return super.find(obj);
    }

    /**
     * Returns an object which respects a given criteria
     * @param predicate
     * @return Optional.empty() if in colection not exist such an element or optional.of(value)
     */

    @Override
    public Optional<T> find(Predicate <T> predicate){
        readFromFile();
        return super.find(predicate);
    }

    /**
     *
     * @return a list formed by all the elements in the colection
     */

    @Override
    public List< T> getAll(){
        readFromFile();
        return super.getAll();
    }

    @Override
    public List<T> getPage(int pageNumber,int pageElements){
        readFromFile();
        return super.getPage(pageNumber,pageElements);
    }
}
