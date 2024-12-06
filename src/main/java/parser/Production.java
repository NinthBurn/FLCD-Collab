package parser;

import java.util.Objects;

public class Production {
    private final String leftHandSide;
    private final String rightHandSide;
    private Integer productionIndex;

    public Production(String leftHandSide, String rightHandSide, int productionIndex) {
        this.leftHandSide = leftHandSide.trim();
        this.rightHandSide = rightHandSide.trim();
        this.productionIndex = productionIndex;
    }

    public String getLeftHandSide() {
        return leftHandSide;
    }

    public String getRightHandSide() {
        return rightHandSide;
    }

    public Integer getProductionIndex() {
        return productionIndex;
    }

    public boolean compareAnalysisItem(AnalysisItem item) {
        return this.leftHandSide.equals(item.getLeftHandSide()) &&
                this.rightHandSide.equals(item.getRightHandSide());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Production that = (Production) o;
        return Objects.equals(leftHandSide, that.leftHandSide) && Objects.equals(rightHandSide, that.rightHandSide) && Objects.equals(productionIndex, that.productionIndex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftHandSide, rightHandSide, productionIndex);
    }

    @Override
    public String toString() {
        return "[" + productionIndex + "] " + leftHandSide + " -> " + rightHandSide;
    }
}
