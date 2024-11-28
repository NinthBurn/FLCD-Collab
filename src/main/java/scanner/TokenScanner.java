package scanner;

import adt.Pair;
import adt.SymbolTable;
import automata.FiniteAutomata;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class TokenScanner {
    int symbolTableSize = 23;
    SymbolTable symbolTable;
    ProgramInternalForm programInternalForm;
    FiniteAutomata integerFA;
    FiniteAutomata identifierFA;

    List<String> operators = List.of("+", "-", "*", "/", "%", "=>", ">=", "<=", "==", "!=", "=", ">", "<");
    List<String> objectOperators = List.of("#", "->");
    List<String> separators = List.of(" ", "\t", "\n", "[", "]", "{", "}", "(", ")", ",", ";", ":", "~", "\"", "#");
    List<String> reservedWords = List.of("var", "none", "int", "float", "string", "bool", "if", "else", "while", "for", "read", "print", "and", "or", "not", "meth", "true", "false", "break");

    public TokenScanner(){
        this.symbolTable = new SymbolTable(symbolTableSize);
        this.programInternalForm = new ProgramInternalForm();

        try {
            this.integerFA = new FiniteAutomata("integer.txt");
            this.identifierFA = new FiniteAutomata("identifier.txt");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String readFile(String filename) {
        BufferedReader reader;
        StringBuilder lines = new StringBuilder();

        try {
            reader = new BufferedReader(new FileReader(filename));
            String line = reader.readLine();

            while (line != null) {
                lines.append(line);
                lines.append("\n");
                line = reader.readLine();
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines.toString();
    }

    public List<String> extractTokens(String lines) {
        List<String> uncategorizedTokens = new ArrayList<>();

        String separatorsString = separators.stream().reduce("", (a, b) -> a + b);
        StringTokenizer tokenizer = new StringTokenizer(lines, separatorsString, true);

        while(tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            uncategorizedTokens.add(token);
        }

        return uncategorizedTokens;
    }

    public List<ScannerToken> tokenize(List<String> tokens) {
        // Retains the token, the line and column it was found at
        List<ScannerToken> uncategorizedTokens = new ArrayList<>();

        int line = 1, column = 1;
        boolean stringConstant = false, inComment = false;
        StringBuilder currentToken = new StringBuilder();

        for(String token : tokens) {
            switch(token) {
                case "~":
                    inComment = true;
                    break;
                case "\"":
                    if(inComment)
                        break;

                    currentToken.append(token);
                    if(stringConstant) {
                        uncategorizedTokens.add(
                                new ScannerToken(currentToken.toString(), new Pair<>(line, column)));
                        currentToken = new StringBuilder();
                    }

                    stringConstant = !stringConstant;
                    break;
                case "\n":
                    inComment = false;
                    line++; column = 1;
                    break;
                default:
                    if(inComment)
                        break;

                    if(stringConstant)
                        currentToken.append(token);

                    else if(!token.equals(" ")) {
                        uncategorizedTokens.add(
                                new ScannerToken(token, new Pair<>(line, column)));
                        column++;
                    }
                    break;
            }
        }

        return uncategorizedTokens;
    }

    public void scan(String filename) {
        String fileContent = readFile(filename);
        List<ScannerToken> uncategorizedTokens = tokenize(extractTokens(fileContent));

        boolean foundLexicalError = false;

        for(ScannerToken uncategorizedToken : uncategorizedTokens) {
            String token = uncategorizedToken.getToken();

            if(this.reservedWords.contains(token)) {
                uncategorizedToken.setType(TokenType.RESERVED_WORD);
                uncategorizedToken.setSymbolTablePosition(new Pair<>(-1, -1));
                this.programInternalForm.add(uncategorizedToken);

            } else if(this.operators.contains(token) || this.objectOperators.contains(token)){
                uncategorizedToken.setType(TokenType.OPERATOR);
                uncategorizedToken.setSymbolTablePosition(new Pair<>(-1, -1));
                this.programInternalForm.add(uncategorizedToken);

            } else if(this.separators.contains(token)){
                uncategorizedToken.setType(TokenType.SEPARATOR);
                uncategorizedToken.setSymbolTablePosition(new Pair<>(-1, -1));
                this.programInternalForm.add(uncategorizedToken);

//            } else if(Pattern.compile("^0$|^[-|+]?[1-9]([0-9])*(\\.[0-9]+)?$|\"([0-9]|[a-zA-Z _!?.$@])*\"$").matcher(token).matches()) {
            } else if(integerFA.acceptsSequence(token) || Pattern.compile("^[-|+]?[1-9]([0-9])*(\\.[0-9]+)$|\"([0-9]|[a-zA-Z _!?.$@])*\"$").matcher(token).matches()) {
                this.symbolTable.add(token);
                uncategorizedToken.setToken("CONSTANT");
                uncategorizedToken.setType(TokenType.CONSTANT);
                uncategorizedToken.setSymbolTablePosition(symbolTable.findPosition(token));
                this.programInternalForm.add(uncategorizedToken);

            }
//            else if(Pattern.compile("^([a-zA-Z]|_)[a-zA-Z_0-9]*$").matcher(token).matches()) {
            else if(identifierFA.acceptsSequence(token)) {
                this.symbolTable.add(token);
                uncategorizedToken.setToken("IDENTIFIER");
                uncategorizedToken.setType(TokenType.IDENTIFIER);
                uncategorizedToken.setSymbolTablePosition(symbolTable.findPosition(token));
                this.programInternalForm.add(uncategorizedToken);

            } else {
                Pair<Integer, Integer> pairLineColumn = uncategorizedToken.getSymbolTablePosition();
                System.out.println("Error found on line: " + pairLineColumn.first + ", column: " + pairLineColumn.second + " - invalid token \"" + token + "\"");
                foundLexicalError = true;
            }
        }

        if(!foundLexicalError){
            System.out.println("Program is lexically correct!");
        }

        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter("PIF.out", false));
            writer.write(programInternalForm.toString());
            writer.close();

            writer = new BufferedWriter(new FileWriter("ST.out", false));
            writer.write(symbolTable.toString());
            writer.close();

        }catch(Exception ignored){}
    }
}
