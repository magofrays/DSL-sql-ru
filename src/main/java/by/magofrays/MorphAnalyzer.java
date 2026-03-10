package by.magofrays;

import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.stachek66.nlp.mystem.holding.Factory;
import ru.stachek66.nlp.mystem.holding.MyStem;
import ru.stachek66.nlp.mystem.holding.MyStemApplicationException;
import ru.stachek66.nlp.mystem.holding.Request;
import ru.stachek66.nlp.mystem.model.Info;
import scala.Option;
import scala.collection.JavaConversions;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NoArgsConstructor
public class MorphAnalyzer {
    private final static MyStem mystemAnalyzer =
            new Factory("-igd --format json --weight")
                    .newMyStem("3.0", Option.empty()).get();

    @Getter
    private static MorphAnalyzer instance = new MorphAnalyzer();

    public Set<String> morph(String word) {
        Set<String> grammemes = new HashSet<>();
        try {
            final Iterable<Info> result = JavaConversions.asJavaIterable(
                    mystemAnalyzer.analyze(Request.apply(word))
                            .info()
                            .toIterable()
            );

            for (final Info info : result) {
                if (info.initial() != null && !info.initial().isEmpty()) {
                    grammemes.addAll(parseMyStemResponse(info.rawResponse()));
                }
            }
        } catch (MyStemApplicationException e) {
            System.err.println("Ошибка при анализе слова '" + word + "': " + e.getMessage());
        }

        return grammemes;
    }


    private Set<String> parseMyStemResponse(String rawResponse) {

        try {

            Pattern grPattern = Pattern.compile("\"gr\":\"([^\"]+)\"");
            Matcher grMatcher = grPattern.matcher(rawResponse);
            if (grMatcher.find()) {
                String gr = grMatcher.group(1);
                return parseGrammemsToSet(gr);
            }


        } catch (Exception e) {
            System.err.println("Ошибка парсинга ответа MyStem: " + e.getMessage());
            System.err.println("Ответ: " + rawResponse);
        }
        return Set.of();
    }


    public Set<String> parseGrammemsToSet(String gr) {
        Set<String> grammems = new HashSet<>();
        gr = gr.replace("[", "").replace("]", "").trim();
        List<String> tokens = smartSplit(gr);

        for (String token : tokens) {
            token = token.trim();
            if (token.isEmpty()) continue;

            if (token.startsWith("=(") && token.endsWith(")")) {
                String variants = token.substring(2, token.length() - 1);
                String[] variantParts = variants.split("\\|");

                for (String variant : variantParts) {
                    String[] subTokens = variant.split(",");
                    for (String subToken : subTokens) {
                        grammems.add(subToken.trim());
                    }
                }
            } else if (token.contains("=")) {
                String[] parts = token.split("=");
                grammems.add(parts[0].trim());
                if (parts.length > 1) {
                    String[] rightParts = parts[1].split(",");
                    for (String rightPart : rightParts) {
                        if(rightPart.contains("|")){
                            for(var elem : List.of(rightPart.split("\\|"))){
                                grammems.add(elem.trim());
                            }
                        }
                        else{
                            grammems.add(rightPart.trim());
                        }
                    }
                }
            } else {
                grammems.add(token);
            }
        }
        Set<String> result = new HashSet<>();
        for (var gram: grammems){
            gram = gram.replace("(", "").replace(")", "");
            result.add(gram);
        }
        return result;
    }

    private List<String> smartSplit(String gr) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int bracketLevel = 0;

        for (int i = 0; i < gr.length(); i++) {
            char c = gr.charAt(i);

            if (c == '(') {
                bracketLevel++;
                current.append(c);
            } else if (c == ')') {
                bracketLevel--;
                current.append(c);
            } else if (c == ',' && bracketLevel == 0) {
                if (!current.isEmpty()) {
                    tokens.add(current.toString().trim());
                    current = new StringBuilder();
                }
            } else {
                current.append(c);
            }
        }

        if (!current.isEmpty()) {
            tokens.add(current.toString().trim());
        }

        return tokens;
    }

    public static void main(String[] args) {
        MorphAnalyzer analyzer = new MorphAnalyzer();

        // Тестовые слова
        String[] testWords = {
                "нет", "апельсинов",      // глаголы в повелительном
                "больше", "батарейка", "мой",      // существительные
                "кринжует", "заебывал", "поооны", // сленг и опечатки
                "красная", "большой",              // прилагательные
                "пять", "десять"                    // числительные
        };
        for (var word : testWords){
            analyzer.morph(word);
        }


    }
}