package Repository;

import Domain.Tema;
import Errors.RepositoryError;
import Utils.SQLConnectionBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class MSSQLTemaRepository implements Repository<Tema> {

    private Connection connection;

    private PreparedStatement insertStatement,deleteStatement,updateStatement;

    private void establishConnection(String databaseName){
        try {
            connection = SQLConnectionBuilder.connectTo(databaseName);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setStatements(){
        try {

            insertStatement = connection.prepareStatement("insert into Tema values (?,?,?)");

            deleteStatement = connection.prepareStatement("delete from Tema where idTema = ?");

            updateStatement = connection.prepareStatement("update Tema set deadline = ?,cerinta = ? where idTema = ?");

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public MSSQLTemaRepository(){
        establishConnection("Student Manager");
        setStatements();
    }

    private void insertTema(Tema obj){
        try {
            insertStatement.setInt(1, obj.getNrTema());
            insertStatement.setInt(2,obj.getDeadline());
            insertStatement.setString(3,obj.getCerinta());
            insertStatement.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void deleteTema(Tema obj){

        try{
            deleteStatement.setInt(1,obj.getNrTema());
            deleteStatement.execute();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void updateTema(Tema obj1,Tema obj2){

        try{

            updateStatement.setInt(1, obj2.getDeadline());
            updateStatement.setString(2,obj2.getCerinta());
            updateStatement.setInt(3,obj1.getNrTema());
            updateStatement.execute();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Tema createTema(ResultSet resultSet){
        Tema tema = null;

        try{
            tema = new Tema(resultSet.getInt(1),resultSet.getInt(2),resultSet.getString(3));
        }catch (Exception e){
            e.printStackTrace();
        }

        return  tema;
    }

    @Override
    public void add(Tema obj) throws RepositoryError {
        if(find(obj))throw new RepositoryError("Mai exista");
        insertTema(obj);
    }

    @Override
    public void save() {}

    @Override
    public void delete(Tema obj) throws RepositoryError {
        if(!find(obj))throw  new RepositoryError("Nu exista");
        deleteTema(obj);
    }

    @Override
    public void update(Tema obj1, Tema obj2) throws RepositoryError {
        if(!find(obj1)) throw  new RepositoryError("Nu exista");
        updateTema(obj1,obj2);
    }

    @Override
    public boolean find(Tema obj) {

        boolean result = true;

        try (Statement find = connection.createStatement(); ResultSet resultSet  = find.executeQuery("select * from Tema where idTema =" + obj.getNrTema())){
            result = resultSet.next();
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public Optional<Tema> find(Predicate<Tema> predicate) {

        try(Statement statement = connection.createStatement();ResultSet resultSet = statement.executeQuery("select * from Tema")){

            while (resultSet.next()){
                Tema tema = createTema(resultSet);

                if(!predicate.test(tema)) continue;

                return Optional.of(tema);
            }

        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return  Optional.empty();
    }

    @Override
    public List<Tema> getAll() {
        List <Tema> list = new ArrayList<>();

        try(Statement statement = connection.createStatement();ResultSet resultSet = statement.executeQuery("select * from Tema")){

            while(resultSet.next())list.add(createTema(resultSet));

        }catch (Exception e){
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public List <Tema> getPage(int pageNumber, int pageElements){
        List <Tema> list = new ArrayList<>();


        try(Statement statement = connection.createStatement();ResultSet resultSet = statement.executeQuery("select * from dbo.paginateTema(" + pageNumber +"," + pageElements + ")")){
            while(resultSet.next())list.add(createTema(resultSet));
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return list;
    }
}
