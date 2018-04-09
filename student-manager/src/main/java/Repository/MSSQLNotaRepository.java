package Repository;

import Domain.Nota;
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

public class MSSQLNotaRepository implements Repository<Nota> {

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

            insertStatement = connection.prepareStatement("insert into Nota values (?,?,?,?)");

            deleteStatement = connection.prepareStatement("delete from Nota where idStudent = ? and idTema = ?");

            updateStatement = connection.prepareStatement("update Nota set observations = ?, nota = ? where idStudent  = ? and idTema = ?");

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public MSSQLNotaRepository(){
        establishConnection("Student Manager");
        setStatements();
    }

    private void insertNota(Nota nota) {

        try{
            insertStatement.setInt(1,Integer.parseInt(nota.getIdStudent()));
            insertStatement.setInt(2,nota.getIdTema());
            insertStatement.setString(3,nota.getObservations());
            insertStatement.setFloat(4,nota.getNota());

            insertStatement.execute();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void deleteNota(Nota obj){

        try{

            deleteStatement.setInt(1,Integer.parseInt(obj.getIdStudent()));
            deleteStatement.setInt(2,obj.getIdTema());
            deleteStatement.execute();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void updateNota(Nota obj1,Nota obj2){

        try{

            updateStatement.setString(1,obj2.getObservations());
            updateStatement.setFloat(2,obj2.getNota());
            updateStatement.setInt(3,Integer.parseInt(obj1.getIdStudent()));
            updateStatement.setInt(4,obj1.getIdTema());

            updateStatement.execute();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Nota createNota(ResultSet resultSet){

        Nota nota = null;

        try {

            nota = new Nota(resultSet.getInt(2),resultSet.getString(1),resultSet.getFloat(4));
            nota.setObservations(resultSet.getString(3));

        }catch ( Exception e){
            e.printStackTrace();
        }

        return nota;
    }

    @Override
    public void add(Nota obj) throws RepositoryError {
        if(find(obj))throw new RepositoryError("Mai exista");
        insertNota(obj);
    }

    @Override
    public void save() {

    }

    @Override
    public void delete(Nota obj) throws RepositoryError {
        if(!find(obj))throw  new RepositoryError("Nu exista");
        deleteNota(obj);
    }

    @Override
    public void update(Nota obj1, Nota obj2) throws RepositoryError {
        if(!find(obj1)) throw  new RepositoryError("Nu exista");
        updateNota(obj1,obj2);
    }

    @Override
    public boolean find(Nota obj) {
        boolean result = true;

        try (Statement find = connection.createStatement(); ResultSet resultSet  = find.executeQuery("select * from Nota where idStudent =" + obj.getIdStudent() + " and  idTema =" + obj.getIdTema() )){
            result = resultSet.next();
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public Optional<Nota> find(Predicate<Nota> predicate) {
        try(Statement statement = connection.createStatement();ResultSet resultSet = statement.executeQuery("select * from Nota")){

            while (resultSet.next()){
                Nota nota = createNota(resultSet);

                if(!predicate.test(nota)) continue;

                return Optional.of(nota);
            }

        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return  Optional.empty();
    }

    @Override
    public List<Nota> getAll() {
        List < Nota >list = new ArrayList<>();

        try(Statement getAllStatement = connection.createStatement();ResultSet resultSet = getAllStatement.executeQuery("select * from Nota")){

            while(resultSet.next()) list.add(createNota(resultSet));

        }catch (Exception e){
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public List <Nota> getPage(int pageNumber,int pageElements){
        List <Nota> list = new ArrayList<>();



        try(Statement statement = connection.createStatement();ResultSet resultSet = statement.executeQuery("select * from dbo.paginateNota(" + pageNumber +"," + pageElements + ")")){
            while(resultSet.next())list.add(createNota(resultSet));
        }catch (Exception e){
            e.printStackTrace();
        }

        return list;
    }
}
