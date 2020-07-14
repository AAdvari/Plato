package Plato.server;
import java.util.Date;

public class TextMessage extends Message {
    private User sender ;

    public TextMessage(Date date, User sender , String content) {
        super(date, content);
        this.sender = sender ;


    }
    // Testing

    @Override
    public String toString() {
        return  super.toString() + " TextMessage{" +
                "sender=" + sender +
                '}' ;
    }
}

