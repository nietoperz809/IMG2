package common;

import java.util.Random;

public class RandomWord {
    static Random random = new Random(System.nanoTime());
    private static final String[] SYLLABLES = {"ba", "be", "bi", "bo", "bu", "ca", "ce", "ci", "co", "cu",
            "da", "de", "di", "do", "du", "fa", "fe", "fi", "fo", "fu",
            "ley", "end", "fux", "dir", "puf", "ass", "for", "vox", "lax", "don", "ald", "sex", "bag",
            "duck", "loot", "edge", "fuck", "smut", "dirt", "kilo", "face",
            "cock", "cunt", "arse", "tits", "noob", "mega", "dick", "dead", "pipi"
    };

    public static String generateWord(int syllableCount) {
        StringBuilder word = new StringBuilder();

        if (syllableCount <=0) {
            syllableCount = random.nextInt(3)+2;
        }

        for (int i = 0; i < syllableCount; i++) {
            word.append(SYLLABLES[random.nextInt(SYLLABLES.length)]);
        }

        return word.toString();
    }

    public static void main(String[] args) {
        System.out.println(".."+SYLLABLES.length);
        for (int i = 1; i < 100; i++) {
//            System.out.print (generateWord(0)+" -- ");
//            if (i%10 == 0)
//                System.out.println();
            System.out.println(random.nextInt(SYLLABLES.length));
        }
    }
}