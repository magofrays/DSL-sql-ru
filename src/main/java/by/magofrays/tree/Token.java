package by.magofrays.tree;

import by.magofrays.MorphAnalyzer;
import lombok.Data;

import java.util.Set;

@Data
public class Token {
    private String value;

    private Set<String> grammemes;

    public Token(String value){
        this.value = value;
        this.grammemes = MorphAnalyzer.getInstance().morph(value);
    }
}
