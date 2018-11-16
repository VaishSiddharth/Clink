package com.testlabic.datenearu.QuestionUtils;

public class ModelQuestion {
    String question;
    String optA;
    String optB;
    String optC;
    String optD;
    String correctOption;
    
    public String getSelectedOption() {
        return selectedOption;
    }
    
    public void setSelectedOption(String selectedOption) {
        this.selectedOption = selectedOption;
    }
    
    String selectedOption;
    
    public ModelQuestion() {
    }
    
    public ModelQuestion(String question, String optA, String optB, String optC, String optD, String correctOption) {
        this.question = question;
        this.optA = optA;
        this.optB = optB;
        this.optC = optC;
        this.optD = optD;
        this.correctOption = correctOption;
    }
    
    public String getCorrectOption() {
        return correctOption;
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
