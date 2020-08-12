package model;

public class AttributeSummary {
    private String name;
    private String type;
    private Double nominalTypeProbability;
    private Double integerTypeProbability;
    private Double realNumTypeProbability;
    private long missingValuesCount;
    private long uniqueValuesCount;
    private long discreteValuesCount;
    private Double missingValuesProbability;
    private Double uniqueValuesProbability;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getNominalTypeProbability() {
        return nominalTypeProbability;
    }

    public void setNominalTypeProbability(Double nominalTypeProbability) {
        this.nominalTypeProbability = nominalTypeProbability;
    }

    public Double getIntegerTypeProbability() {
        return integerTypeProbability;
    }

    public void setIntegerTypeProbability(Double integerTypeProbability) {
        this.integerTypeProbability = integerTypeProbability;
    }

    public Double getRealNumTypeProbability() {
        return realNumTypeProbability;
    }

    public void setRealNumTypeProbability(Double realNumTypeProbability) {
        this.realNumTypeProbability = realNumTypeProbability;
    }

    public long getMissingValuesCount() {
        return missingValuesCount;
    }

    public void setMissingValuesCount(long missingValuesCount) {
        this.missingValuesCount = missingValuesCount;
    }

    public long getUniqueValuesCount() {
        return uniqueValuesCount;
    }

    public void setUniqueValuesCount(long uniqueValuesCount) {
        this.uniqueValuesCount = uniqueValuesCount;
    }

    public long getDiscreteValuesCount() {
        return discreteValuesCount;
    }

    public void setDiscreteValuesCount(long discreteValuesCount) {
        this.discreteValuesCount = discreteValuesCount;
    }

    public Double getMissingValuesProbability() {
        return missingValuesProbability;
    }

    public void setMissingValuesProbability(Double missingValuesProbability) {
        this.missingValuesProbability = missingValuesProbability;
    }

    public Double getUniqueValuesProbability() {
        return uniqueValuesProbability;
    }

    public void setUniqueValuesProbability(Double uniqueValuesProbability) {
        this.uniqueValuesProbability = uniqueValuesProbability;
    }
}
