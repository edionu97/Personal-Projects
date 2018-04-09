package Utils;

import Domain.Nota;
import Domain.Student;
import Domain.Tema;
import Repository.Repository;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class XMLConfigLoader {

    private static  XMLConfigLoader instance = new XMLConfigLoader();

    private Document document;

    private StudentFileMover fileMover = new StudentFileMover();

    private RepoLoader repoLoader = new RepoLoader();

    private String type;

    /**
     *
     * @param tagName the name of the field from the xml config
     * @return String[] where string[0] is the package-location of the class,string[1] class name,string[2] file name and string[3] is type
     */

    private String[] getInfo(String tagName){

        String[] fields = new String[4];

        Node node = document.getElementsByTagName(tagName).item(0);

        fields[0] = node.getAttributes().getNamedItem("package").getTextContent(); //location
        fields[1] = node.getAttributes().getNamedItem("repoName").getTextContent();//class name
        fields[2] = node.getAttributes().getNamedItem("fileName").getTextContent();//fileName

        node = document.getElementsByTagName("Repositories").item(0);

        type = node.getAttributes().getNamedItem("type").getTextContent();

        return  fields;
    }

    private XMLConfigLoader(){
        repoLoader.setRepoLocation("Repository.");

        try{

            String path = getClass().getResource("/Config/config.xml").getPath();

            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(path);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private File getConfigFile(String configFileName){

        File file = null;

        try {
            file = new File( getClass().getResource(configFileName).toURI().getPath());

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return file;
    }

    private void writeType(String type){

        String whatToWrite ="";

        switch (type){
            case "SQL":whatToWrite = "DDD";break;
            case "File":whatToWrite ="FFF";break;
            case "Memory":whatToWrite = "MMM";break;
        }



        try(BufferedWriter writer = new BufferedWriter(new FileWriter(getConfigFile("/Config/currentConfig.txt")))){
            writer.write(whatToWrite);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private String getType(){


        try(BufferedReader reader = new BufferedReader(new FileReader(getConfigFile("/Config/currentConfig.txt")))){
            return  reader.readLine();
        }catch (Exception e){
            e.printStackTrace();
        }

        return  null;
    }

    public static XMLConfigLoader getInstance() {
        return instance;
    }

    public  Repository <Student> getStudentsRepo()throws  Exception{

        String[] fields = getInfo("Student_Repository");

        String className = fields[1];
        String location = fields[0];
        String fileName= fields[2];

        writeType(type);

        repoLoader.setFileName(fileName.equals("null") ? null : fileName);repoLoader.setRepoLocation(location);

        Repository <Student> repository = repoLoader.getRepoStudent(className);

        fileMover.setRepository(repository);
        fileMover.setCurrentLocation(getClass().getResource("/Config").toURI().getPath().concat("/"+getType()));
        fileMover.setDestination(Paths.get("").toAbsolutePath().toString());
        fileMover.moveAll();


        return  repository;
    }

    public Repository <Tema> getHomeworksRepo() throws  Exception{

        String[] fields = getInfo("Homework_Repository");

        String className = fields[1];
        String location = fields[0];
        String fileName= fields[2];

        repoLoader.setFileName(fileName.equals("null") ? null : fileName);repoLoader.setRepoLocation(location);

        return repoLoader.getRepoHomework(className);
    }

    public Repository<Nota> getMarksRepo() throws  Exception{

        String[] fields = getInfo("Marks_Repository");

        String className = fields[1];
        String location = fields[0];
        String fileName= fields[2];

        repoLoader.setFileName(fileName.equals("null") ? null : fileName);repoLoader.setRepoLocation(location);

        return repoLoader.getRepoMarks(className);
    }

    public void checkCurrentConfig()throws Exception{

        String className1 = getInfo("Student_Repository")[1];
        String className2 = getInfo("Homework_Repository")[1];
        String className3 = getInfo("Marks_Repository")[1];

        if(className1.contains(type) && className2.contains(type) && className3.contains(type))return;

        throw new Exception("Invalid repository types.Repos must be of the same type");
    }
}
