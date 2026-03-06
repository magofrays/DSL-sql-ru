
package by.magofrays;

import java.io.IOException;

import static java.lang.System.out;
import static com.github.demidko.aot.WordformMeaning.lookupForMeanings;

public class Main {
    public static void main(String[] args) throws IOException {

        var meanings = lookupForMeanings("люди");
        out.println(meanings.size());
        /* 1 */

        out.println(meanings.get(0).getMorphology());

        out.println(meanings.get(0).getLemma());

        for (var t : meanings.get(0).getTransformations()) {
            out.println(t.toString() + " " + t.getMorphology());
        }
    }
}
