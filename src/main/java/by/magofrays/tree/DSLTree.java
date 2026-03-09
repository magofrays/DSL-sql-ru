package by.magofrays.tree;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class DSLTree {
    @Getter
    private Node node;
    private final List<Token> tokens;
    private Integer index;

    private Set<String> failGrammemes;

    public DSLTree(List<Token> tokens, Integer index) {
        this.tokens = tokens;
        this.index = index;
        node = createStatement();
    }

    private void next(){
        index++;
    }
    private void before(){
        if(index < tokens.size()){
            index--;
        }
    }

    private Token current(){
        if(index < tokens.size()){
            return tokens.get(index);
        }
        throw new IllegalArgumentException("Tokens end bad request");
    }

    public Node createStatement(){
        Node root = Node.builder()
                .name("STATEMENT")
                .build();
        List<Node> children = new ArrayList<>();
        Node action = createAction(); // действие
        children.add(action);
        next();
        Node recipients = createRecipients(); // цели, для кого
        if(recipients != null){
            children.add(recipients);
            next();
        }
        Node quantifier = createQuantifier(); // количество
        if(quantifier != null){
            children.add(quantifier);
            next();
        }
        Node listSubjects = createListSubjects(); // что достать надо
        children.add(listSubjects);
        next();
        Node preposition = createPreposition();
        if(preposition == null){
            throwException();
        }
        next();
        Node listStorage = createListStorages(); // откуда достать
        children.add(listStorage);
        next();
        Node listFilter = createListFilter();
        if(listFilter != null){
            children.add(listFilter);
        }
        root.setChildren(children);
        return root;
    }

    private Node createAdvpro(){
        Token current = current();
        failGrammemes = Set.of("ADVPRO");
        if(current.getGrammemes().containsAll(failGrammemes)){
            return Node.builder()
                            .name("ADVPRO")
                            .value(current.getValue())
                            .grammemes(current.getGrammemes())
                            .build();
        }
        return null;
    }

    private Node createListFilter() {
        if(index >= tokens.size()){
            return null;
        }
        Node filter = createFilter();
        next();
        if(index < tokens.size()){
            return filter;
        }
        Node union = createUnion();
        if(union == null){
            return filter;
        }
        List<Node> children = new ArrayList<>();
        children.add(filter);
        children.add(union);
        Node listFilter = createListFilter();
        children.addAll(listFilter.getChildren() == null ? List.of(listFilter) : listFilter.getChildren());
        return Node.builder()
                .name("LIST FILTER")
                .children(children)
                .build();
    }

    private Node createFilter() {
        Node advpro = createAdvpro();
        if(advpro == null){
            throwException();
        }
        next(); //todo
        List<Node> children = new ArrayList<>();
        failGrammemes = Set.of("PART");
        if(advpro.getGrammemes().containsAll(failGrammemes)){
            children.add(advpro);
        }
    }

    private Node createListStorages() {
        Node storage = createStorage();
        next();
        Node preposition = createPreposition();
        if(preposition == null){
            before();
            return storage;
        }
        List<Node> children = new ArrayList<>();
        children.add(storage);
        next();
        var storages = createListStorages();
        children.addAll(storages.getChildren() == null ? List.of(storages) : storages.getChildren());
        return Node.builder().name("LIST_STORAGES").children(children).build();
    }

    private Node createStorage() {
        Token current = current();
        failGrammemes = Set.of("S", "мн", "род");
        if(current.getGrammemes().containsAll(failGrammemes)){
            return Node.builder()
                    .name("STORAGE")
                    .value(current.getValue())
                    .grammemes(current.getGrammemes())
                    .build();
        }
        failGrammemes = Set.of("S", "ед", "род");
        if(current.getGrammemes().containsAll(failGrammemes)){
            return Node.builder()
                    .name("STORAGE")
                    .value(current.getValue())
                    .grammemes(current.getGrammemes())
                    .build();
        }

        throwException();
        return null;
    }

    private void throwException(){
        throw new IllegalArgumentException(
                String.format(
                        "Error at token: %s, bad token at position: %d must be: %s",
                        current().getValue(),
                        index,
                        Arrays.toString(failGrammemes.toArray())
                ));
    }

    private Node createListSubjects() {
        Node subject = createSubject();
        if(subject == null){
            throwException();
        }
        next();
        Node union = createUnion();
        if(union == null){
            before();
            return subject;
        }
        next();
        List<Node> children = new ArrayList<>();
        children.add(subject);
        Node listSubjects = createListSubjects();
        children.addAll(listSubjects.getChildren() == null ? List.of(listSubjects) : listSubjects.getChildren());
        return Node.builder()
                .name("LIST SUBJECTS")
                .children(children)
                .build();
    }

    private Node createSubject(){
        Token current = current();
        failGrammemes = Set.of("S", "род", "мн");
        if(current().getGrammemes().containsAll(failGrammemes)){
            return Node.builder()
                    .name("SUBJECT")
                    .value(current.getValue())
                    .grammemes(current.getGrammemes())
                    .build();
        }
        failGrammemes = Set.of("S", "вин", "ед");
        if(current().getGrammemes().containsAll(failGrammemes)){
            return Node.builder()
                    .name("SUBJECT")
                    .value(current.getValue())
                    .grammemes(current.getGrammemes())
                    .build();
        }
        return null;
    }

    private Node createQuantifier() {
        var current = current();
        if(current.getGrammemes().containsAll(Set.of("им", "NUM")) ||
            current.getGrammemes().containsAll(Set.of("вин", "NUM"))){
            return Node.builder()
                    .name("QUANTIFIER")
                    .grammemes(current.getGrammemes())
                    .value(current.getValue())
                    .build();
        }
        if(StringUtils.isNumeric(current.getValue())){
            return Node.builder()
                    .name("QUANTIFIER")
                    .value(current.getValue())
                    .build();
        }
        if(
                current.getGrammemes().containsAll(Set.of("SPRO", "им", "мн")) ||
                        current.getGrammemes().containsAll(Set.of("APRO", "им", "ед"))

        ){
            return Node.builder()
                    .name("QUANTIFIER")
                    .value(current.getValue())
                    .grammemes(current.getGrammemes())
                    .build();
        }
        return null;
    }

    private Node createAction() {
        Set<String> inf = Set.of("V", "инф", "сов");
        Set<String> imper = Set.of("V", "пов", "2-л", "ед");
        failGrammemes = imper;
        Token current = current();
        if(current.getGrammemes().containsAll(imper) || current.getGrammemes().containsAll(inf)){
            return Node.builder()
                    .name("ACTION")
                    .value(current.getValue())
                    .grammemes(current.getGrammemes())
                    .build();
        }
        throwException();
        return null;
    }

    private Node createRecipients() {
        Node recipient = createRecipient();
        if(recipient == null) return null;
        List<Node> children = new ArrayList<>();
        children.add(recipient);
        next();
        Node union = createUnion();
        if (union != null){
            next();
            Node chRecipients = createRecipients();
            children.addAll(chRecipients.getChildren() == null ? List.of(chRecipients) : chRecipients.getChildren());
        }
        else {
            before();
        }
        return Node.builder()
                .name("RECIPIENTS")
                .children(children)
                .build();
    }


    private Node createRecipient() {
        Node preposition = createPreposition();
        if(preposition != null) {
            next();
            Token current = current();
            if(current.getGrammemes().containsAll(Set.of("S", "вин")) || current.getGrammemes().containsAll(Set.of("S", "им"))){
                return Node.builder()
                        .name("RECIPIENT")
                        .value(current.getValue())
                        .grammemes(current.getGrammemes())
                        .children(List.of(preposition))
                        .build();
            }
        }
        if(current().getGrammemes().containsAll(Set.of("S", "дат"))){
            return Node.builder()
                    .name("RECIPIENT")
                    .value(current().getValue())
                    .grammemes(current().getGrammemes())
                    .build();
        }
        return null;
    }

    private Node createPreposition() {
        Token current = current();
        if(current.getGrammemes().contains("PR")){
            return Node.builder()
                    .name("PREPOSITION")
                    .value(current.getValue())
                    .grammemes(current.getGrammemes())
                    .build();
        }
        return null;
    }

    private Node createUnion() {
        if(current().getGrammemes().contains("CONJ") || current().getValue().equals(",")){
            return Node.builder()
                    .name("UNION")
                    .value(current().getValue())
                    .grammemes(current().getGrammemes()).build();
        }
        return null;
    }
}
