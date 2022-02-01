import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class RandomAdvise {

    public static String getAdvise() throws IOException {
        URL url = new URL("https://api.adviceslip.com/advice");
        String line2 = "";

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
            for (String line; (line = reader.readLine()) != null; ) {
                line2 = line;
            }
        }

        String advise = line2.substring(line2.indexOf("advice") + 10, line2.lastIndexOf(".")) + ".";
        String advise2 = advise.replaceAll("\\\\","");
        if (advise.startsWith("Today, do not use")) {return "It's wrong to be right.";}

        else return advise2;
    }
}
