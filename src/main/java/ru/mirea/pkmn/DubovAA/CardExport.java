package ru.mirea.pkmn.DubovAA;

import ru.mirea.pkmn.Card;

import java.io.*;

public class CardExport {
    public static final long serialVersionUID = 1L;
    public static void cardExport(Card myCard) throws IOException {
        File file = new File(myCard.getName() + ".crd");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(myCard);

    }

}
