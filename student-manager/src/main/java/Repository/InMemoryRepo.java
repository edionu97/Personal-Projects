package Repository;

import Errors.RepositoryError;

import java.util.Optional;
import java.util.function.Predicate;

public class InMemoryRepo <T> extends AbstractRepo <T> {

    @Override
    public void add(T obj)throws  RepositoryError {
        if(find(obj))throw  new RepositoryError("Mai exista");
        content.add(obj);
    }

    @Override
    public void update(T obj1, T obj2) throws  RepositoryError{
        if(!find(obj1))throw new RepositoryError("Nu exista");
        content.remove(obj1);content.add(obj2);
    }

    @Override
    public void delete(T obj) throws  RepositoryError {
        if(!find(obj))throw new RepositoryError("Nu exista");
        content.remove(obj);
    }

    @Override
    public boolean find(T obj) {
        for(T el  : content)
            if(el.equals(obj))return true;
        return false;
    }

    @Override
    public Optional<T> find(Predicate<T> predicate) {
        for(T inregistration : content)
            if(predicate.test(inregistration))return Optional.of(inregistration);
        return Optional.empty();
    }
}
