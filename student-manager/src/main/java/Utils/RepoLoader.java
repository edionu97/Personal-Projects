package Utils;

import Domain.Nota;
import Domain.Student;
import Domain.Tema;
import Repository.Repository;
import javafx.util.Pair;

import java.lang.reflect.Constructor;


public class RepoLoader {
    private String repoLocation;
    private String fileName;

    private Student testStudent = new Student("","","","-1",0);
    private Tema testTema = new Tema(-1,0,"");

    public void setRepoLocation(String location){
        repoLocation = location;
    }

    public void setFileName(String fileName){
        this.fileName = fileName;
    }

    private Pair <Class,Constructor> loadClassAndGetConstructor(String repoType) throws Exception{

        String location = repoLocation + repoType;

        Class loadedClass = Class.forName(location);

        Constructor constructor = null;

        for (Constructor constructor1 : loadedClass.getDeclaredConstructors()) {
            if(constructor1.getParameterCount() == 1){
                constructor = constructor1;
            }
        }

        return new Pair<>(loadedClass,constructor);
    }

    @SuppressWarnings("unchecked")
    private Repository <Student> getStudentRepos(Pair <Class,Constructor> classInfo)throws  Exception{

        Constructor constructor = classInfo.getValue();

        Class loaded = classInfo.getKey();

        Repository <Student > repository = (Repository <Student>)((constructor == null) ? loaded.newInstance() : constructor.newInstance(fileName));

        try {
            repository.add(testStudent);
            repository.delete(testStudent);

        }catch (Exception e){
            throw  new Exception("Invalid repository");
        }

        return repository;
    }

    @SuppressWarnings("unchecked")
    private Repository <Tema> getHomeworkRepos(Pair <Class,Constructor> classInfo)throws  Exception{

        Constructor constructor = classInfo.getValue();

        Class loaded = classInfo.getKey();

        Repository <Tema > repository = (Repository <Tema>)((constructor == null) ? loaded.newInstance() : constructor.newInstance(fileName));

        try {
            repository.add(testTema);
            repository.delete(testTema);

        }catch (Exception e){
            throw  new Exception("Invalid repository");
        }
        return repository;
    }

    @SuppressWarnings("unchecked")
    private Repository <Nota> getMarksRepos(Pair <Class,Constructor> classInfo)throws  Exception{

        Constructor constructor = classInfo.getValue();

        Class loaded = classInfo.getKey();

        return  (Repository <Nota>)((constructor == null) ? loaded.newInstance() : constructor.newInstance(fileName));
    }

    public  Repository <Student> getRepoStudent(String repoType) throws  Exception{
       return getStudentRepos(loadClassAndGetConstructor(repoType));
    }

    public Repository <Tema> getRepoHomework(String repoType) throws  Exception{
        return getHomeworkRepos(loadClassAndGetConstructor(repoType));
    }

    public Repository <Nota> getRepoMarks(String repoType) throws  Exception {
       return  getMarksRepos(loadClassAndGetConstructor(repoType));
    }
}
