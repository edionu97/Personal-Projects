package Utils;

import Domain.Student;
import Repository.Repository;

import java.util.Vector;

public class Notifier {

    private  Repository <Student> repository;

    private RealEmailSender sender = RealEmailSender.getInstance();

    public  Notifier(Repository < Student > R){
        repository = R;
    }

    /**
     *
     * @param message(the message to be send to all the students)
     */


    public void notify(String message){


        new Thread(()-> {
            Vector <Student>students = new Vector<>(repository.getAll());
            students.forEach(S -> {
                try {
                    sender.setDestination(S.getEmail());
                    sender.setContent(message);
                    sender.setStudentName(S.getNume());
                    sender.send();
                } catch (Exception e) {
                }
            });
        }
        ).start();


    }

    /**
     *
     * @param id the student id
     * @param message   the message to be send to this student
     */

    public  void notify(String id,String message){

        new Thread(()-> {
            try {
                sender.setDestination(repository.find(S -> S.getIdStudent().equals(id)).get().getEmail());
                sender.setContent(message);
                sender.setStudentName(repository.find(s->s.getIdStudent().equals(id)).get().getNume());
                sender.send();
            } catch (Exception e) {

            }
        }).start();

    }
}
