package by.magofrays.tree;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Builder
@Data
public class Node {
    String name;
    Set<String> grammemes;
    List<Node> children;
    String value;
}
