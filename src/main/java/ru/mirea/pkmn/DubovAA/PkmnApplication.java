package ru.mirea.pkmn.DubovAA;

import com.fasterxml.jackson.databind.JsonNode;
import ru.mirea.pkmn.Card;
import ru.mirea.pkmn.DubovAA.web.http.PkmnHttpClient;
import ru.mirea.pkmn.DubovAA.web.jdbc.DatabaseServiceImpl;
import java.io.IOException;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class PkmnApplication {
    public static final long serialVersionUID = 1L;
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        PkmnHttpClient httpClient = new PkmnHttpClient();
//        DatabaseServiceImpl db = new DatabaseServiceImpl();

        Card myPokemonCard = CardImport.impFromTxt("src/main/resources/my_card.txt");
        CardImport.AttackSkillUpd(myPokemonCard, httpClient);

//        db.createPokemonOwner(myPokemonCard.getPokemonOwner());
//        db.saveCardToDatabase(myPokemonCard);


       System.out.println("Моя карточка: " + myPokemonCard);
        CardExport.cardExport(myPokemonCard);

        Card import_some_card = CardImport.cardImport("src/main/resources/Pyroar.crd");
        System.out.println("Импортированная карточка: " + import_some_card);


        PkmnHttpClient pkmnHttpClient = new PkmnHttpClient();

        JsonNode card = pkmnHttpClient.getPokemonCard("Durant", 13);
        System.out.println(card.toPrettyString());

        System.out.println(card.findValues("attacks")
                .stream()
                .map(JsonNode::toPrettyString)
                .collect(Collectors.toSet()));
    }
}
