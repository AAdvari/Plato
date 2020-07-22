package Plato.server;
import java.io.Serializable;
import java.util.Date;

public class TextMessage extends Message implements Serializable {
    private User sender ;
    private String content ;

    public TextMessage(Date date, User sender , String content) {
        super(date);
        this.content = content;
        this.sender = sender ;


    }
    // Testing


    @Override
    public String toString() {
        return super.toString() + "TextMessage{" +
                "sender=" + sender.getUsername() +
                ", content='" + content + '\'' +
                '}';
    }
}

