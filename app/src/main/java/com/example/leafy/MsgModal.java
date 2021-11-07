package com.example.leafy;

public class MsgModal {
    private String answer; // json 답변 key랑 똑같이!
    public MsgModal(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
