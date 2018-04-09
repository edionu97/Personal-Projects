package Repository;

import Domain.Student;
import Errors.RepositoryError;
import Utils.SQLConnectionBuilder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class MSSQLStudentRepository implements Repository <Student>{


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

            insertStatement = connection.prepareStatement("insert into Student values (?,?,?,?,?)");

            deleteStatement = connection.prepareStatement("delete from Student where idStudent = ?");

            updateStatement = connection.prepareStatement("update Student set nume = ?,email = ?,cadruDidactic = ?,grupa = ? where idStudent = ?");

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private Student createSudent(ResultSet resultSet){

        Student student = null;

        try {
            student = new Student(
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getString(4),
                    resultSet.getString(1),
                    resultSet.getInt(5)
            );
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return student;
    }

    private void insertStudent(Student student){

        try {

            insertStatement.setInt(5, student.getGrupa());
            insertStatement.setInt(1,Integer.parseInt(student.getIdStudent()));
            insertStatement.setString(2,student.getNume());
            insertStatement.setString(3, student.getEmail());
            insertStatement.setString(4,student.getCadruDidactic());
            insertStatement.execute();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void deleteStudent(Student obj){

        try{
            deleteStatement.setString(1,obj.getIdStudent());
            deleteStatement.execute();

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void updateStudent(Student oldInfo,Student newInfo){
        try {

            updateStatement.setString(1, newInfo.getNume());
            updateStatement.setString(2,newInfo.getEmail());
            updateStatement.setString(3,newInfo.getCadruDidactic());
            updateStatement.setInt(4,newInfo.getGrupa());
            updateStatement.setInt(5,Integer.parseInt(oldInfo.getIdStudent()));
            updateStatement.execute();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public MSSQLStudentRepository(){
        establishConnection("Student Manager");
        setStatements();
    }

    @Override
    public void add(Student obj) throws RepositoryError {
        if(find(obj))throw  new RepositoryError("Mai exista");
        insertStudent(obj);
    }

    @Override
    public void save() {

    }

    @Override
    public void delete(Student obj) throws RepositoryError {
        if(!find(obj))throw  new RepositoryError("Nu exista");
        deleteStudent(obj);
    }

    @Override
    public void update(Student obj1, Student obj2) throws RepositoryError {
        if(!find(obj1))throw new RepositoryError("Nu exista");
        updateStudent(obj1,obj2);
    }

    @Override
    public boolean find(Student obj) {

        boolean result = true;

        try (Statement find = connection.createStatement();  ResultSet resultSet  = find.executeQuery("select * from Student where idStudent =" + obj.getIdStudent())){
            result = resultSet.next();
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public Optional<Student> find(Predicate<Student> predicate) {


        try(Statement statement = connection.createStatement();ResultSet resultSet = statement.executeQuery("select * from Student")){

            while (resultSet.next()){
                Student student = createSudent(resultSet);

                if(!predicate.test(student)) continue;

                return Optional.of(student);
            }

        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return  Optional.empty();
    }

    @Override
    public List<Student> getAll() {

        List <Student> list = new ArrayList<>();

        try(Statement getAllStatement = connection.createStatement();ResultSet resultSet = getAllStatement.executeQuery("select * from Student")){

            while(resultSet.next()) list.add(createSudent(resultSet));

        }catch (Exception e){
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public List <Student> getPage(int pageNumber,int pageElements){
        List <Student> list = new ArrayList<>();


        try(Statement statement = connection.createStatement();ResultSet resultSet = statement.executeQuery("select * from dbo.paginateStudents(" + pageNumber +"," + pageElements + ")")){
            while(resultSet.next())list.add(createSudent(resultSet));
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return list;
    }
}
