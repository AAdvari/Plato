package com.plato.server;
import java.io.Serializable;
import java.util.Date;

public class GameReportMessage extends Message implements Serializable {
   private String nameOfGame ;
   private String winnerUsername  ;
   private String looserUsername ;
   private boolean draw = false ;

    public GameReportMessage(Date date, String nameOfGame, String winnerUsername , String looserUsername , boolean draw) {
        super(date);
        this.nameOfGame = nameOfGame;
        this.looserUsername = looserUsername ;
        this.winnerUsername = winnerUsername;
        this.draw = draw ;
    }


    //test


    @Override
    public String toString() {
        return "GameReportMessage{" +
                "nameOfGame='" + nameOfGame + '\'' +
                ", winnerUsername='" + winnerUsername + '\'' +
                ", looserUsername='" + looserUsername + '\'' +
                ", draw=" + draw +
                '}';
    }
}
