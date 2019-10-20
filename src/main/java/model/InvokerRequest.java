package model;

public class InvokerRequest {
    private String customerId;
    private int questionaireIndex;

    public InvokerRequest() {
    }

    public InvokerRequest(String customerId, int questionaireIndex) {
        this.customerId = customerId;
        this.questionaireIndex = questionaireIndex;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public int getQuestionaireIndex() {
        return questionaireIndex;
    }

    public void setQuestionaireIndex(int questionaireIndex) {
        this.questionaireIndex = questionaireIndex;
    }
}
