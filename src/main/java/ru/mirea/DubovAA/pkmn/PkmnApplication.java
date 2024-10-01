package ru.mirea.DubovAA.pkmn;

import java.io.IOException;

public class PkmnApplication {
    public static void main(String[] args) throws ClassNotFoundException, IOException {
        Card myPokemonCard = CardImport.impFromTxt("src/main/resources/my_card.txt");
        System.out.println(myPokemonCard);
        CardExport.cardExport(myPokemonCard);

        Card import_some_card = CardImport.cardImport("src/main/resources/Durant.crd");
        System.out.println(import_some_card);

    }
}
