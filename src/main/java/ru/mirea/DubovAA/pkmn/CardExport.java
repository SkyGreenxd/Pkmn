package ru.mirea.DubovAA.pkmn;

import java.io.*;

public class CardExport {
    public static void cardExport(Card myCard) throws IOException {
        File file = new File(myCard.getName() + ".crd");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(myCard);

    }

}
