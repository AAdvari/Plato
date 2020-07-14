package Plato.server;

import java.util.Date;

public class GameReportMessage extends Message {
   private String nameOfGame ;
   private String winnerUsername  ;
   private String looserUsername ;

    public GameReportMessage(Date date, String content, String nameOfGame, String winnerUsername , String looserUsername) {
        super(date, content);
        this.nameOfGame = nameOfGame;
        this.looserUsername = looserUsername ;
        this.winnerUsername = winnerUsername;
    }
}
