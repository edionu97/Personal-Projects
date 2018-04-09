package Utils;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class RealEmailSender {

    private final String schoolAddress = "mailtester634@gmail.com";

    private String mailTo,content,studentName,htmlContent;

    private Session session;

    private  void setMail(){
        Properties properties = System.getProperties();

        properties.put("mail.smtp.port","587");//setting the mail servers's port(587)
        properties.put("mail.smtp.host","smtp.gmail.com");//setting the server
        properties.put("mail.smtp.auth",true);//allowing permission to authentificate

        properties.put("mail.smtp.starttls.enable",true);
        properties.put("mail.smtp.ssl.trust","smtp.gmail.com");

        session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(schoolAddress,"2345678912");
            }
        });
    }

    private RealEmailSender(){
        setMail();htmlContent = readContent();
    }

    private static RealEmailSender instance = new  RealEmailSender();

    static RealEmailSender getInstance(){
        return instance;
    }

    /**
     *
     * @param destination(the destionation email)
     */

    void setDestination(String destination){
        mailTo = destination;
    }

    /**
     *
     * @param content(the content of message)
     */

    void setContent(String content){
        this.content = content;
    }

    private File getHtmlFormat(){

        File file = null;

        try {
            file = new File( getClass().getResource("/Emails/html.txt").toURI().getPath());

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return file;
    }

    /**
     * The send function(this function sends one email from  the school adress (mailtester634@gmail.com) to the student email)
     */

    private String readContent() {

        String readedContent = "";



        try(BufferedReader reader = new BufferedReader(new FileReader(getHtmlFormat()))){

            String line;

            while((line = reader.readLine()) != null)readedContent = readedContent.concat(line);

        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return readedContent;
    }

    private String getHTML(String htmlText,String ...arguments){

        Pattern pattern = Pattern.compile("\\?");

        Matcher matcher;

        int index = 0;

        while((matcher = pattern.matcher(htmlText)).find())htmlText = matcher.replaceFirst(arguments[index++]);

        return htmlText;
    }

    private Multipart getContent() throws  Exception{

        Multipart multipart = new MimeMultipart("related");

        BodyPart bodyPart = new MimeBodyPart();

        bodyPart.setContent(getHTML(htmlContent,studentName,content),"text/html");

        multipart.addBodyPart(bodyPart);
        return multipart;
    }

    void setStudentName(String studentName){
        this.studentName = studentName;
    }

    void send(){

        try{

            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(schoolAddress));

            message.addRecipient(Message.RecipientType.TO,new InternetAddress(mailTo));

            message.setContent(getContent());

            message.setSubject("UBB Faculty of Computer Science");

            Transport.send(message);

        }catch (Exception e){

            System.out.println(e.getMessage());
            //System.out.println("No internet connection");
        }
    }
}
