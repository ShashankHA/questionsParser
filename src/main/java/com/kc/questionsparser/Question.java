package com.kc.questionsparser;
import java.util.List;

public class Question {
	
	String question;
	String answer ;
	List<String> options;
	int optionsCount ;
	
	public Question(String question, String answer, List<String> options, int optionsCount) {
		super();
		this.question = question;
		this.answer = answer;
		this.options = options;
		this.optionsCount = optionsCount;
	}

	public int getOptionsCount() {
		return optionsCount;
	}

	public void setOptionsCount(int optionsCount) {
		this.optionsCount = optionsCount;
	}

	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public List<String> getOptions() {
		return options;
	}
	public void setOptions(List<String> options) {
		this.options = options;
	}

}
