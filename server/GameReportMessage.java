package Plato.server;

import java.util.Date;

public class GameReportMessage extends Message {
   private String nameOfGame ;
   private String winnerUsername  ;

    public GameReportMessage(Date date, String content, String nameOfGame, String winnerUsername) {
        super(date, content);
        this.nameOfGame = nameOfGame;
        this.winnerUsername = winnerUsername;
    }
}
