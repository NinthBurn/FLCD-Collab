import java.util.ArrayList;
import java.util.List;

public class ProgramInternalForm {
    private final List<ScannerToken> tokens;

    public ProgramInternalForm() {
        this.tokens = new ArrayList<>();
    }

    public void add(ScannerToken token){
        this.tokens.add(token);
    }

    @Override
    public String toString(){
        StringBuilder computedString = new StringBuilder();
        int size = this.tokens.size();

        for(ScannerToken token : this.tokens) {
            computedString.append(token.getToken())
                    .append(" - ")
                    .append(token.getSymbolTablePosition())
                    .append(" -> ")
                    .append(token.getType())
                    .append("\n");
        }

        return computedString.toString();
    }
}