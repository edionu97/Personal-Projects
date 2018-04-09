package Repository;

import Errors.RepositoryError;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface Repository <T> {
    void add(T obj) throws RepositoryError;
    void save();
    void delete(T obj) throws  RepositoryError;
    void update(T obj1, T obj2) throws  RepositoryError;
    boolean find(T obj);
    Optional<T> find(Predicate<T> predicate);
    List< T > getAll();

    default List<T> getPage(int pageNumber, int pageCount){
        return new ArrayList<>();
    }
}
