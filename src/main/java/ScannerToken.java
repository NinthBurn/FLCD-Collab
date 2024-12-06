import adt.Pair;

public class ScannerToken {
    private String token;
    private TokenType type;
    private Pair<Integer, Integer> symbolTablePosition;

    public ScannerToken(String token, TokenType type, Pair<Integer, Integer> symbolTablePosition) {
        this.token = token;
        this.type = type;
        this.symbolTablePosition = symbolTablePosition;
    }

    public ScannerToken(String token, Pair<Integer, Integer> symbolTablePosition) {
        this.token = token;
        this.symbolTablePosition = symbolTablePosition;
        this.type = TokenType.NONE;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    public Pair<Integer, Integer> getSymbolTablePosition() {
        return symbolTablePosition;
    }

    public void setSymbolTablePosition(Pair<Integer, Integer> symbolTablePosition) {
        this.symbolTablePosition = symbolTablePosition;
    }

    @Override
    public String toString() {
        return "ScannerToken{" +
                "token='" + token + '\'' +
                ", type=" + type +
                ", symbolTablePosition=" + symbolTablePosition +
                '}';
    }
}