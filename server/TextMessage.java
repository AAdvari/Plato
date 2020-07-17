package Plato.server;
import java.util.Date;

public class TextMessage extends Message {
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
                "sender=" + sender +
                ", content='" + content + '\'' +
                '}';
    }
}

