package ru.mirea.DubovAA.pkmn;
import java.io.*;
import java.util.ArrayList;

public class CardImport {
    public static Card impFromTxt(String path) {

        Card cardPokemon = new Card();

        try(BufferedReader reader = new BufferedReader(new FileReader(path))) {
            for (int i = 0; i < 12; i++) {
                String line = reader.readLine();
                switch (i) {
                    case 0: cardPokemon.setPokemonStage(PokemonStage.valueOf(line)); break;
                    case 1: cardPokemon.setName(line); break;
                    case 2: cardPokemon.setHp(Integer.parseInt(line)); break;
                    case 3: cardPokemon.setPokemonType(EnergyType.valueOf(line)); break;
                    case 4: if ("NULL".equals(line)) {line = null;}
                    else {cardPokemon.setEvolvesForm(impFromTxt(line));} break;
                    case 5: cardPokemon.setSkills(getAttackSkill(line)); break;
                    case 6: cardPokemon.setWeaknessType(EnergyType.valueOf(line)); break;
                    case 7: if ("NULL".equals(line)) {line = null;}
                    else {cardPokemon.setResistanceType(EnergyType.valueOf(line));} break;
                    case 8: cardPokemon.setRetreatCost(line); break;
                    case 9: cardPokemon.setGameSet(line); break;
                    case 10: cardPokemon.setRegulationMark(line.charAt(0)); break;
                    case 11: cardPokemon.setPokemonOwner(getStudent(line)); break;
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

}
