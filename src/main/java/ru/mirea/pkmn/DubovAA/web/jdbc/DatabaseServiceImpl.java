package ru.mirea.pkmn.DubovAA.web.jdbc;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.mirea.pkmn.*;
import ru.mirea.pkmn.DubovAA.CardImport;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;

public class DatabaseServiceImpl implements DatabaseService {
    private final Connection connection;
    private final Properties databaseProperties;

    public DatabaseServiceImpl() throws SQLException, IOException {

        // Загружаем файл database.properties

        databaseProperties = new Properties();
        databaseProperties.load(new FileInputStream("src/main/resources/database.properties"));

        // Подключаемся к базе данных

        connection = DriverManager.getConnection(
                databaseProperties.getProperty("database.url"),
                databaseProperties.getProperty("database.user"),
                databaseProperties.getProperty("database.password")
        );
        System.out.println("Connection is "+(connection.isValid(0) ? "up" : "down"));
    }

    @Override
    public Card getCardFromDatabase(String cardName) {
        // Реализовать получение данных о карте из БД
        Card card = new Card();
        try {
            String query = String.format("SELECT * FROM card WHERE (id = '%s');", cardName);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                card.setName(resultSet.getString("name"));
                UUID evolves_from = (UUID) resultSet.getObject("evolves_from");
                card.setEvolvesFrom(evolves_from == null ? null : getCardFromDatabase(evolves_from));
                card.setNumber(Integer.parseInt(resultSet.getString("card_number")));
                card.setHp(resultSet.getInt("hp"));
                card.setPokemonOwner(getStudentFromDatabase((UUID) resultSet.getObject("pokemon_owner")));
                card.setRegulationMark(resultSet.getString("regulation_mark").charAt(0));
                card.setWeaknessType(EnergyType.valueOf(resultSet.getString("weakness_type")));
                card.setGameSet(resultSet.getString("game_set"));
                String resist = resultSet.getString("resistance_type");
                card.setResistanceType(Objects.equals(resist, "null") ? null : EnergyType.valueOf(resist));
                card.setPokemonStage(PokemonStage.valueOf(resultSet.getString("stage").toUpperCase()));
                card.setRetreatCost(resultSet.getString("retreat_cost"));
                card.setSkills(CardImport.parseAttackSkillsFromJson(resultSet.getString("attack_skills")));
            }
            else {
                throw new RuntimeException("Card not found");
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return card;
    }

    public Card getCardFromDatabase(UUID cardName) throws SQLException, JsonProcessingException {
        // Реализовать получение данных о карте из БД
        Card card = new Card();
        try {
            String query = String.format("SELECT * FROM card WHERE (id = '%s');", cardName);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                card.setName(resultSet.getString("name"));
                UUID evolves_from = (UUID) resultSet.getObject("evolves_from");
                card.setEvolvesFrom(evolves_from == null ? null : getCardFromDatabase(evolves_from));
                card.setNumber(Integer.parseInt(resultSet.getString("card_number")));
                card.setHp(resultSet.getInt("hp"));
                card.setPokemonOwner(getStudentFromDatabase((UUID) resultSet.getObject("pokemon_owner")));
                card.setRegulationMark(resultSet.getString("regulation_mark").charAt(0));
                card.setWeaknessType(EnergyType.valueOf(resultSet.getString("weakness_type")));
                card.setGameSet(resultSet.getString("game_set"));
                String resist = resultSet.getString("resistance_type");
                System.out.println(resist);
                card.setResistanceType(Objects.equals(resist, "null") ? null : EnergyType.valueOf(resist));
                card.setPokemonStage(PokemonStage.valueOf(resultSet.getString("stage").toUpperCase()));
                card.setRetreatCost(resultSet.getString("retreat_cost"));
                card.setSkills(CardImport.parseAttackSkillsFromJson(resultSet.getString("attack_skills")));
            }
            else {
                throw new RuntimeException("Card not found");
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return card;
    }

    @Override
    public Student getStudentFromDatabase(String studentName) throws SQLException {
        // Реализовать получение данных о студенте из БД
        Student result = new Student();
        String query = String.format("SELECT * FROM student WHERE (id = '%s');", studentName);
        ResultSet rs = connection.createStatement().executeQuery(query);
        if(rs.next()){
            result.setFirstName(rs.getString("firstName"));
            result.setFamilyName(rs.getString("familyName"));
            result.setSurName(rs.getString("patronicName"));
            result.setGroup(rs.getString("group"));
        }
        else {
            throw new RuntimeException("Empty result from database");
        }
        return result;
    }

    public Student getStudentFromDatabase(UUID studentName) throws SQLException {
        Student result = new Student();
        String query = String.format("SELECT * FROM student WHERE (id = '%s');", studentName);
        ResultSet rs = connection.createStatement().executeQuery(query);
        if(rs.next()){
            result.setFirstName(rs.getString("firstName"));
            result.setFamilyName(rs.getString("familyName"));
            result.setSurName(rs.getString("patronicName"));
            result.setGroup(rs.getString("group"));
        }
        else {
            throw new RuntimeException("Empty result from database");
        }
        return result;
    }

    @Override
    public void saveCardToDatabase(Card card) throws SQLException {
        // Реализовать отправку данных карты в БД
        StringBuilder queryBase = new StringBuilder("INSERT INTO card(");
        StringBuilder query = new StringBuilder("VALUES(");

        if (card.getEvolvesFrom() != null){
            queryBase.append("evolves_from, ");
            saveCardToDatabase(card.getEvolvesFrom());
            Statement stmt = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE
            );
            ResultSet rs = stmt.executeQuery(String.format("SELECT id FROM card WHERE (name = '%s');", card.getEvolvesFrom().getName()));
            rs.last();
            query.append("'").append(rs.getObject("id")).append("', ");
        }
        if (card.getPokemonOwner() != null) {
            queryBase.append(" pokemon_owner,");
            try{
                String tmp = String.format("SELECT id FROM student WHERE (\"familyName\" = '%s' AND \"firstName\" = '%s' AND \"patronicName\" = '%s');",
                        card.getPokemonOwner().getSurName(), card.getPokemonOwner().getFirstName(), card.getPokemonOwner().getFamilyName());
                ResultSet rs = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_UPDATABLE).executeQuery(tmp);
                rs.last();
                query.append(rs.getObject("id")).append("', ");
            }catch (Exception e){
                query.append("'").append(createPokemonOwner(card.getPokemonOwner())).append("', ");
            }

        }
        queryBase.append(" id, name, hp, game_set, stage, retreat_cost, weakness_type, resistance_type, attack_skills, pokemon_type, regulation_mark, card_number) ");
        query.append("'").append(UUID.randomUUID()).append("', '");
        query.append(card.getName()).append("', ");
        query.append(card.getHp()).append(", '");
        query.append(card.getGameSet()).append("', '");
        query.append(card.getPokemonStage()).append("', '");
        query.append(card.getRetreatCost()).append("', '");
        query.append(card.getWeaknessType()).append("', '");
        query.append(card.getResistanceType()).append("', '");
        query.append("[");
        for (AttackSkill as : card.getSkills()){
            query.append(as.toString().replace('\'', '`')).append(", ");
        }
        query.delete(query.length()-2, query.length()-1);
        query.append("]', '");
        query.append(card.getPokemonType()).append("', '");
        query.append(card.getRegulationMark()).append("', ");
        query.append(card.getNumber());
        query.append(");");

        System.out.println(queryBase.toString() + query.toString());
        try {
            connection.createStatement().executeUpdate(queryBase.toString() + query.toString());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UUID createPokemonOwner(Student owner) {
        // Реализовать добавление студента - владельца карты в БД
        UUID ownerID = UUID.randomUUID();

        String query = "insert into student(id, " +
                "\"familyName\", \"firstName\", \"patronicName\", \"group\") " +
                "values(?, ?, ?, ?, ?)";

        try(PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, ownerID);
            statement.setString(2, owner.getFamilyName());
            statement.setString(3, owner.getFirstName());
            statement.setString(4, owner.getSurName());
            statement.setString(5, owner.getGroup());

            statement.execute();
        }
        catch(SQLException e) {
            throw new RuntimeException(e);
        }
        return ownerID;
    }
}
