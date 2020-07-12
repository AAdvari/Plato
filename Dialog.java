package Plato;

import java.util.Objects;

public class Dialog {
    private User user1 ;
    private User user2 ;

    public Dialog(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
    }

    public User getUser1() {
        return user1;
    }

    public User getUser2() {
        return user2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dialog dialog = (Dialog) o;
        return (user1.equals(dialog.user1) && user2.equals(dialog.user2) ) ||
                (user1.equals(dialog.user2) && user2.equals(dialog.user1)) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user1, user2);
    }
}
