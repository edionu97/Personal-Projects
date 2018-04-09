package Services;

import Domain.Nota;
import Domain.Student;
import Domain.Tema;
import Repository.Repository;
import javafx.collections.FXCollections;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.util.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StatisticsService {

    private Repository <Student> repoStudent;
    private Repository <Tema> repoTeme;
    private Repository <Nota> repoNota;

    /**
     *
     * @param fileName-> (the file in which is stored the info for each student)
     * @return  true if the student is conscientious or false contrary
     */

    private boolean isConscientious(String fileName){
        Path p = Paths.get(fileName);

            try{
                Pattern pattern = Pattern.compile("Adaugare nota.*no penality.*");
                Pattern pattern2 = Pattern.compile("Modificare nota.*");
                for(String line : Files.lines(p).collect(Collectors.toList())){
                    if(pattern.matcher(line).find() || pattern2.matcher(line).find())continue;
                    return false;
                }

        }catch (IOException ex){ //the file does not exists => the student has no marks
                return  false;
        }

        return true;
    }

    /**
     *
     * @param idTema id-ul temei la care vrem sa vedem daca studentul a fost penalizta
     * @param fileName  fisierul din care citesc datele despre o tema
     * @return true daca studetul a fost penalizat la o tema si false in caz contrar
     */

    private boolean isPenalty(int idTema,String fileName){
        Path p = Paths.get(fileName);

        Stream <String> line;

        try {
            line = Files.lines(p);
        }catch (IOException e){
            return false;
        }

        return (int)line.filter(
                s->s.matches("Adaugare nota.*Penalizare.*") && s.split(",")[1].equals("" + idTema)
        ).count() > 0;
    }

    private float getMedieForStudent(String idStudent,List <Pair <Student,Float > > avgs){

        for (Pair<Student, Float> avg : avgs) {

            if (!avg.getKey().getIdStudent().equals(idStudent)) continue;

            return avg.getValue();

        }

        return -1;
    }

    private int parseObservations(String observations){

        Pattern pattern = Pattern.compile("week:([0-9]+)");

        Matcher matcher = pattern.matcher(observations);

        if(matcher.find())return Integer.parseInt(matcher.group(1));

        return -1;
    }

    public StatisticsService(Repository < Student > s1,Repository < Tema> s2,Repository < Nota > s3){
        repoStudent = s1;repoTeme = s2;repoNota = s3;
    }


    /**
     *
     * @return acea tema la care au fost penalizati cei mai multi studenti
     */

    public Tema getTheHarderOne(){

        Map < Integer, Integer > map = new HashMap<>();

        repoNota.getAll().forEach(nota->{
            if(!isPenalty(nota.getIdTema(),nota.getIdStudent().concat(".txt"))) return;

            if(!map.containsKey(nota.getIdTema())){
                map.put(nota.getIdTema(),1);return;
            }

            map.put(nota.getIdTema(),map.get(nota.getIdTema()) + 1);

        });

        Optional < Map.Entry < Integer,Integer > > value = map.entrySet().stream().max(Comparator.comparing(Map.Entry::getValue));

        if(!value.isPresent())return null;

        return repoTeme.find(tema -> tema.getNrTema() == value.get().getKey()).get();
    }

    /**
     *
     * @return a list formed by Students and for each student it will be the avg mark
     */



    public List< Pair< Student, Float> > getMedieForStudents(){
        List < Pair < Student,Float  > >list = new ArrayList<>();

        Map <String,Float> studentMap = new TreeMap<>();
        Map <String,Integer> freq = new TreeMap<>();

        repoNota.getAll().forEach(nota->{

            if(!studentMap.containsKey(nota.getIdStudent())){
                studentMap.put(nota.getIdStudent(),nota.getNota());
                freq.put(nota.getIdStudent(),1);
                return;
            }

            studentMap.put(nota.getIdStudent(),studentMap.get(nota.getIdStudent()) + nota.getNota());
            freq.put(nota.getIdStudent(),freq.get(nota.getIdStudent()) + 1);
        });



        studentMap.forEach((key, value) -> list.add(
                new Pair<>(

                        repoStudent.find(
                                stud -> key.equals(stud.getIdStudent()
                                )
                        ).get(),

                        Float.parseFloat(new DecimalFormat(".##").format(value / freq.get(key)))
                )
        ));

        return list;
    }

    /**
     *
     * @return  a list formed only by that students that have promoted to the lab
     */

    public List < Student > getAllThatPromoted(){

        List < Student > list  = new ArrayList<>();

        getMedieForStudents().stream().filter(
                pereche->Math.round(pereche.getValue()) >= 4
        ).collect(Collectors.toList()).forEach(element->list.add(element.getKey()));

        return list;
    }

    /**
     *
     * @return a list formed by all students that gave the homeworks in time
     */

    public List < Student > getAllConscientious(){
        List < Student > students = new ArrayList<>();

        repoStudent.getAll().forEach(s->{
            if(!isConscientious(s.getIdStudent().concat(".txt")))return;
            students.add(s);
        });

        return students;
    }

    /*
        Geting a list with most harder homeworks (1-10) homeworks)
    */

    public List<XYChart.Series<String,Integer>> getTop10Hardest(){

        Map < Integer, Integer > map = new HashMap<>();

        repoNota.getAll().forEach(nota->{

            if(!isPenalty(nota.getIdTema(),nota.getIdStudent().concat(".txt"))) return;

            if(!map.containsKey(nota.getIdTema()))map.put(nota.getIdTema(),1);
            else
                map.put(nota.getIdTema(),map.get(nota.getIdTema()) + 1);
        });

        XYChart.Series<String,Integer> result  = new XYChart.Series<>();

        map.forEach((key, value) ->result.getData().add(new XYChart.Data<>("Id: " + key +   " (" + value + ')',value)) );

        List < XYChart.Series<String,Integer> > list = new ArrayList<>();

        list.add(new XYChart.Series<>(

                FXCollections.observableArrayList(
                        result.getData().stream().sorted((x,y)->-(x.getYValue() - y.getYValue())).collect(Collectors.toList()).subList(0,Math.min(10,result.getData().size()))
                )

        ));

        return list;
    }

    public List < PieChart.Data > getAllMarksCount(){

        List <PieChart.Data> allMarks = new ArrayList<>();

        Map <Float,Integer >map = new TreeMap<>();

        getMedieForStudents().forEach(avg->{

            if(!map.containsKey(avg.getValue())){
                map.put(avg.getValue(),1);
                return;
            }

            map.put(avg.getValue(),map.get(avg.getValue()) + 1);
        });

        map.forEach((key,value)->allMarks.add(new PieChart.Data("Mark "+key,value)));

        return allMarks;
    }

    public List <PieChart.Data> getPromotedMarks(){

        List <PieChart.Data> list = FXCollections.observableArrayList();

        Map <Float,Integer> count = new HashMap<>();

        List < Pair <Student,Float> > avg = getMedieForStudents();

        getAllThatPromoted().forEach(student->{

            float avgS = getMedieForStudent(student.getIdStudent(),avg);

            if( !count.containsKey(avgS) ){
                count.put(avgS,1);
                return;
            }

            count.put(avgS,count.get(avgS) + 1);

        });

        count.forEach((key,value)->list.add(new PieChart.Data("Mark "+ key,value)));
        return list;
    }

    public List <PieChart.Data> getDelays(){

        List <PieChart.Data> list = FXCollections.observableArrayList();

        Map < Integer, Integer > delayMap = new HashMap<>();

        repoNota.getAll().forEach(mark->{

            int difference = parseObservations(mark.getObservations()) - repoTeme.find(x->x.getNrTema() == mark.getIdTema()).get().getDeadline();

            if( difference <= 0 )return;

            if(!delayMap.containsKey(difference)) {
                delayMap.put(difference, 1);
                return;
            }

            delayMap.put(difference,delayMap.get(difference) + 1);
        });


        delayMap.forEach((key,value)->list.add(new PieChart.Data("Delay " + key,value)));

        return list;
    }

    public List < XYChart.Series <String,Integer > > getTop10Lazy(){


        Map < String, Integer > delayMap = new HashMap<>();

        repoNota.getAll().forEach(mark->{

            int difference = parseObservations(mark.getObservations()) - repoTeme.find(x->x.getNrTema() == mark.getIdTema()).get().getDeadline();

            if( difference <= 0 )return;

            if(!delayMap.containsKey(mark.getIdStudent())) {
                delayMap.put(mark.getIdStudent(), difference);
                return;
            }

            delayMap.put(mark.getIdStudent(),delayMap.get(mark.getIdStudent()) + difference);
        });

        XYChart.Series <String,Integer> data1 = new XYChart.Series<>();



        delayMap.forEach((key,value)->
            data1.getData().add(new XYChart.Data<>("Stud id:" + key,value))
        );

        List < XYChart.Series <String,Integer> > list = new ArrayList<>();

        list.add(new XYChart.Series<>(FXCollections.observableArrayList(
                data1.getData().stream().sorted((x,y)->-x.getYValue().compareTo(y.getYValue())).collect(Collectors.toList()).subList(0,Math.min(10,data1.getData().size()))
        )));

        return list;
    }
}
