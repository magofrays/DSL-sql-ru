package by.magofrays.tree;

import lombok.Builder;
import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Builder
@Data
public class Node {
    String name;
    Set<String> grammemes;
    List<Node> children;
    String value;


    public String toString() {
        var stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("%s %s %s",
                name,
                value,
                grammemes == null ? "[]" : Arrays.toString(grammemes.toArray())
        ));

        if (children != null && !children.isEmpty()) {
            stringBuilder.append("\n");
            for (Node node : children) {
                stringBuilder.append("\t").append(node.toString().replace("\n", "\n\t"));
                stringBuilder.append("\n");
            }
        }

        return stringBuilder.toString();
    }
}
