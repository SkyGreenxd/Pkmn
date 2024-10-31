package ru.mirea.pkmn.DubovAA.web.jdbc;

import ru.mirea.pkmn.Card;
import ru.mirea.pkmn.Student;
import java.sql.SQLException;
import java.util.UUID;

public interface DatabaseService {
    Card getCardFromDatabase(String cardName);

    Student getStudentFromDatabase(String studentName) throws SQLException;

    void saveCardToDatabase(Card card) throws SQLException;

    UUID createPokemonOwner(Student owner);
}
