package com.testlabic.datenearu.QuestionUtils;

public class ModelQuestion {
    String question;
    String optA;
    String optB;
    String optC;
    String optD;
    
    public ModelQuestion(String question, String optA, String optB, String optC, String optD) {
        this.question = question;
        this.optA = optA;
        this.optB = optB;
        this.optC = optC;
        this.optD = optD;
    }
    
    public String getQuestion() {
        return question;
    }
    
    public String getOptA() {
        return optA;
    }
    
    public String getOptB() {
        return optB;
    }
    
    public String getOptC() {
        return optC;
    }
    
    public String getOptD() {
        return optD;
    }
}
