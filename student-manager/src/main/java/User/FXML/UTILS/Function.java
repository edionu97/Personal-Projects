package User.FXML.UTILS;

@FunctionalInterface
public interface Function {
     void execute(String idS, int idT, int week, float mark, String obs) throws  Exception;
}
