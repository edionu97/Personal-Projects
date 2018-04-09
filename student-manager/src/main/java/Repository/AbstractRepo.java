package Repository;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public abstract class AbstractRepo <T> implements  Repository <T>{
    protected List< T > content = new LinkedList<>();

    @Override
    public void save(){}

    @Override
    public List<T> getAll() {
        return content;
    }

    @Override
    public List<T> getPage(int pageNumber,int pageElements){

        if((pageNumber - 1) * pageElements > content.size())return new ArrayList<>();

        return content.subList((pageNumber - 1) * pageElements,Math.min(pageNumber * pageElements,content.size()));
    }
}
