package com.kc.questionsparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class App {

	private static List<Question> questions = new ArrayList<Question>();

	public static void main(String args[]) throws JsonGenerationException, JsonMappingException, IOException {
		
		Properties p = readProperties();
		String inputFilepath = p.getProperty("questionsFileName");
		String tableName = p.getProperty("tableName");

		File file = new File("conf/"+inputFilepath);

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			System.out.println("Input file not found");
		}

		String st;
		int count = 0;
		StringBuilder currentQuestion = new StringBuilder("");
		try {
			while ((st = br.readLine()) != null) {
				if (st.startsWith("#Q")) {
					if (count != 0) {
						addToQuestions(currentQuestion.toString());
					}
					currentQuestion = new StringBuilder();
					currentQuestion.append(st);
					count++;
				} else
					currentQuestion.append("\n" + st);
			}
		} catch (IOException e) {
			System.out.println("Error reading the input file");
		}
		addToQuestions(currentQuestion.toString());
		System.out.println("Read " + count + " questions!!");
	//	printJSON(questions);
		try {
			persistToDB(questions,tableName);
		} catch (SQLException e) {
			System.out.println("Got exception in persisting : "+e);
			return;
		}
		System.out.println("Completed Persisiting!!!");
	}

	private static Properties readProperties() throws IOException {
		FileReader reader=new FileReader("conf/application.properties");  
	    Properties p=new Properties();  
	    p.load(reader);
		return p;  
	}

	private static void persistToDB(List<Question> questions,String tableName) throws SQLException {
		String dbUrl ="jdbc:mysql://localhost:3306/kc?enabledTLSProtocols=TLSv1.2";
		Connection conn = DriverManager.getConnection(dbUrl,"kcUser","password");
		for(Question q: questions) {
			String sql = "INSERT INTO `kc`.`"+tableName+"`(`question`,`answer`,`options`,`options_count`)"
					+ " VALUES (?,?,?,?);";
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1,q.question );
			preparedStatement.setString(2,q.answer );
			preparedStatement.setString(3,q.options.toString() );
			preparedStatement.setLong(4,q.optionsCount );
			preparedStatement.execute();
			preparedStatement.close();
		}
		conn.close();
	}

	private static void printJSON(List<Question> questions)
			throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		StringWriter stringWriter = new StringWriter();
		objectMapper.writeValue(stringWriter, questions);
		System.out.println("Writing the json file");
		try {
			FileWriter fw = new FileWriter("C:\\Users\\sha\\Documents\\Learning\\animals.json");
			fw.write(stringWriter.toString());
			fw.close();
		} catch (Exception e) {
			System.out.println("Got exception in writing json file : "+e);
		}
		System.out.println("Completed wrting json...");

	}

	private static void addToQuestions(String currentQuestion){
		BufferedReader br = new BufferedReader(new StringReader(currentQuestion));
		int linecount = 0;
		String st = null;
		String question = null, answer = null;
		List<String> options = new ArrayList<String>();
		try {
			while ((st = br.readLine()) != null) {
				if (linecount == 0 && st.startsWith("#Q")) {
					question = st.substring(3);
				} else if (linecount == 1 && st.startsWith("^")) {
					answer = st.substring(2);
				} else if (st.length() > 0) {
					options.add(st.substring(2));
				}
				linecount++;
			}
		} catch (IOException e) {
			System.out.println("Got exception creating question object for: "+currentQuestion+"\nException:\n"+e);
		}
		questions.add(new Question(question, answer, options, options.size()));
	}
}
