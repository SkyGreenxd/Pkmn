package ru.mirea.pkmn.DubovAA;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import ru.mirea.pkmn.*;
import ru.mirea.pkmn.DubovAA.web.http.PkmnHttpClient;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CardImport {
    public static final long serialVersionUID = 1L;
    public static Card impFromTxt(String path) {

        Card cardPokemon = new Card();

        try(BufferedReader reader = new BufferedReader(new FileReader(path))) {
            for (int i = 0; i < 13; i++) {
                String line = reader.readLine();
                switch (i) {
                    case 0: cardPokemon.setPokemonStage(PokemonStage.valueOf(line)); break;
                    case 1: cardPokemon.setName(line); break;
                    case 2: cardPokemon.setHp(Integer.parseInt(line)); break;
                    case 3: cardPokemon.setPokemonType(EnergyType.valueOf(line)); break;
                    case 4: if ("NULL".equals(line)) {line = null;}
                    else {cardPokemon.setEvolvesFrom(impFromTxt(line));} break;
                    case 5: cardPokemon.setSkills(getAttackSkill(line)); break;
                    case 6: cardPokemon.setWeaknessType(EnergyType.valueOf(line)); break;
                    case 7: if ("NULL".equals(line)) {line = null;}
                    else {cardPokemon.setResistanceType(EnergyType.valueOf(line));} break;
                    case 8: cardPokemon.setRetreatCost(line); break;
                    case 9: cardPokemon.setGameSet(line); break;
                    case 10: cardPokemon.setRegulationMark(line.charAt(0)); break;
                    case 11: cardPokemon.setPokemonOwner(getStudent(line)); break;
                    case 12: cardPokemon.setNumber(Integer.parseInt(line)); break;
                }
            }
            reader.close();
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException("Файл не найден");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        return cardPokemon;
    }

    private static ArrayList<AttackSkill> getAttackSkill(String s) {
        ArrayList<AttackSkill> array = new ArrayList<>();

        String[] skills = s.split(",");
        for (String skill : skills) {
            String[] str = skill.split("/");
            array.add(new AttackSkill(str[1],"", str[0], Integer.parseInt(str[2])));
        }

        return array;
    }

    private static Student getStudent(String s) {
        Student student = new Student();

        String[] str = s.split("/");

        student.setFirstName(str[1]);
        student.setSurName(str[2]);
        student.setFamilyName(str[0]);
        student.setGroup(str[3]);

        return student;
    }

    public static Card cardImport(String file) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(file);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        Card card = (Card) objectInputStream.readObject();
        return card;
    }

    public static void AttackSkillUpd(Card card, PkmnHttpClient httpClient) throws IOException {
        if(card.getEvolvesFrom() != null){
            AttackSkillUpd(card.getEvolvesFrom(), httpClient);
        }

        List<JsonNode> descriptions = httpClient.getPokemonCard(card.getName(), card.getNumber()).findValues("text");
        for (int i = 0; i < descriptions.size(); i++){
            card.getSkills().get(i).setDescription(descriptions.get(i).asText());
        }
    }

    public static ArrayList<AttackSkill> parseAttackSkillsFromJson(String json) throws JsonProcessingException {
        ArrayList<AttackSkill> result = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode tmp = (ArrayNode) objectMapper.readTree(json);
        for(int i = 0; i < tmp.size(); i++){
            JsonNode jn = tmp.get(i);
            AttackSkill as = new AttackSkill();
            as.setDescription(jn.findValue("description").toString());
            as.setCost(jn.findValue("cost").toString());
            as.setDamage((jn.get("damage").asInt()));
            as.setName(jn.findValue("name").toString());
            result.add(as);
        }
        return result;
    }

}
