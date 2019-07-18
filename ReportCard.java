/******************************
Abdullah Arif
Program to create transcript using 4 input csv files.
******************************/
import java.util.*; //use for List and maps
import java.io.*; //handling file I/O 

//Report card class
public class ReportCard{
	//stores list of students using studentID as key ordered by student id
	private static Map<Integer ,Student> studentList;
	//stores list of courses using courseId
	private static Map<Integer ,Course> courseList;
	//stores which course each test belongs to using testID
	private static Map<Integer ,Integer> testCourseList;
	//stores respective weight of each test used with 
	private static Map<Integer,Integer> testWeightList;

	public static void main(String[]args){
		//All id are are assumed to be integers based on example input
		studentList = new TreeMap<Integer, Student>(); 
		courseList = new HashMap<Integer, Course>();
		testCourseList = new HashMap<Integer ,Integer>(); 
		testWeightList = new HashMap<Integer ,Integer>(); 

		//process student information
		processStudents();
		//process course information
		processCourses();
		//process  test file into related Maps
		processTests();
		//verify all test weight add up to a 100
		verifyCourses();
		//process from Marks file
		processMarks();
		//Create output *use print to make sure it works
		createOutput();
	}
	private static void processStudents(){
		try{
			BufferedReader br = new BufferedReader(new FileReader("inputs/students.csv")); 
			String line = br.readLine(); //throw away the description line
			int studentId=0;
			String studentName;
			while ( (line = br.readLine()) != null ) { //read till end of file
				String[] info = line.split(","); //splitting info from line
				//create instance of student with the given name and mapped on by their unique studentID
				studentId=Integer.parseInt(info[0]);
			    studentName=info[1];
				studentList.put(studentId,new Student(studentName)); 
			}
			br.close(); 
		} catch(FileNotFoundException e){
			System.out.println("File students.csv is not located in the 'inputs' folder\n");
		} catch(IOException e){
			System.out.println("Program does not have IO permission.\n");
		}    
	}

	private static void processCourses() {
		try{
			BufferedReader br = new BufferedReader(new FileReader("inputs/courses.csv")); 
			String line = br.readLine(); //throw away the description line
			int courseId=0;
			String courseName,teacherName;
			while ((line = br.readLine()) != null) { //read till end of file
				String[] info = line.split(","); //splitting info from line
				//Create instance of student with the courses name and the proffesor teaching it
				courseId    = Integer.parseInt(info[0]);
				courseName  = info[1];
				teacherName = info[2];
				courseList.put(courseId,new Course(courseName,teacherName));
			}
			br.close(); 
		} catch(FileNotFoundException e){
			System.out.println("File courses.csv is not located in the 'inputs' folder\n");
		} catch(IOException e){
			System.out.println("Program does not have IO permission.\n");
		} 
	}

	private static void processTests(){
		try{
			BufferedReader br = new BufferedReader(new FileReader("inputs/tests.csv"));
			String line = br.readLine(); //throw away the description line
			int testId, courseId, testWeight;
			while ((line = br.readLine()) != null) { //read till end of file
				String[] info = line.split(","); //splitting info from line
				testId     = Integer.parseInt(info[0]);
				courseId   = Integer.parseInt(info[1]);
				testWeight = Integer.parseInt(info[2]);
				//Creating a link beetween courses and tests
				testCourseList.put(testId,courseId);
				//holding the weight of each test specified by a unique test id
				testWeightList.put(testId,testWeight);
				courseList.get(courseId).addWeight(testWeight);
			}
			br.close();    
		} catch(FileNotFoundException e){
			System.out.println("File tests.csv is not located in the 'inputs' folder\n");
		} catch(IOException e){
			System.out.println("Program does not have IO permission.\n");
		} 
	}

	private static boolean verifyCourses() {
		for (Course  c : courseList.values()){  
            if(!c.valid()){
            	System.out.println("ERROR: "+c + "is not a valid course\n");//throw error
            	return false;
            }   
    	} 
    	return true;
	}
	private static void processMarks() {
		try{
			BufferedReader br = new BufferedReader(new FileReader("inputs/marks.csv")); 
			String line = br.readLine(); //throw away the description line
			int testId, studentId, mark;
			double weightedMark;
			while ((line = br.readLine()) != null) { //read till end of file
				String[] info = line.split(","); //splitting info from line
				//Add in weighted marks to students
				testId=Integer.parseInt(info[0]);
				studentId=Integer.parseInt(info[1]);
				mark=Integer.parseInt(info[2]);
				weightedMark = testWeightList.get(testId)/100.0f * (double)mark ;
				studentList.get(studentId).addMark(testCourseList.get(testId),weightedMark);
			}
			br.close();
		} catch(FileNotFoundException e){
			System.out.println("File marks.csv is not located in the 'inputs' folder\n");
		} catch(IOException e){
			System.out.println("Program does not have IO permission.\n");
		}    
	}

	private static void createOutput(){
		try{
			FileWriter out=new FileWriter("output.txt");
			for(Map.Entry<Integer ,Student> entry : studentList.entrySet()){
				Student s = entry.getValue();
				out.write(String.format("Student Id:%2d, %s",entry.getKey(), s));
				TreeSet<Integer> courses = s.getCourseList();
				for(int courseId:courses){ //iterate through each course student took
					if(!s.completedCourse(courseId, courseList.get(courseId).getTotalTest())){
						//let user know a student has not completed a course
						System.out.println("ERROR:Student has not completed course with ID\n"+courseId);
					}
					out.write(courseList.get(courseId)+s.getGrade(courseId)+"\n");	
				}
				out.write("\n\n");
			}
			out.close();
		} catch(IOException e){
			System.out.println("Program does not have IO permission.\n");
		} 
	}
}

//Student class
class Student{
	private String name; //student name
	//average of student in given courseId
	private Map<Integer, Double> courseGradeAverage = new HashMap<Integer, Double>();
	//store how many test are done used for error detection
	private Map<Integer,Integer> courseProgress = new HashMap<Integer, Integer>();

	public Student(String n){ 
		this.name=n;
	}

	@Override
	public String toString(){ //return student info in proper format
		return "name: " + name +"\n"+ this.getTotalAverage() + "\n";
	}

	public void addMark(int courseId, double mark){ //add student marks to correct course
		if(courseGradeAverage.containsKey(courseId)){ //updating mark
			courseGradeAverage.replace(courseId,courseGradeAverage.get(courseId)+mark); 
			courseProgress.replace(courseId,courseProgress.get(courseId)+1);
		}
		else{ //adding in new course
			courseGradeAverage.put(courseId,mark);
			courseProgress.put(courseId,1);
		}
	}

	private String getTotalAverage(){  //get overall average for student
		double n=0, i=0;
		for (double mark : courseGradeAverage.values()){  
            n+= mark;
        	i+=1;
    	} 
    	//return in proper format
    	return String.format("%-20s%2.2f%s\n","Total Average:",n/i ,"%");
	}

	public boolean completedCourse(int courseId, int totalTest){  //check if student completed the course
		return courseProgress.get(courseId) == totalTest;
	}

	public TreeSet<Integer> getCourseList(){ //return courseList in sorted order
		return new TreeSet<Integer>(courseGradeAverage.keySet());
	}

	public String getGrade( int courseId){ //return grade in course
		return String.format("%19s%-7s%2.2f%s\n","Final Grade",":",courseGradeAverage.get(courseId),"%"); 
	}
}
//Course class
class Course{
	private String name, teacher; //name of course and name of teacher teaching the course
	private int totalWeight, testNum;

	public Course(String courseName, String teacherName){
		this.name= courseName;
		this.teacher=teacherName;
		totalWeight=0;
		testNum=0;
	}
	public void addWeight(int weight){ //store total weight of all test in course for error-checking 
		totalWeight+=weight;
		testNum+=1;
	}

	public boolean valid(){ //verify total wieght of all test is a 100
		return totalWeight==100;
	}

	public int getTotalTest(){ 
		return testNum;
	}

	@Override
	public String toString(){ //return course in proper format for output
		return String.format("%15s %s, Teacher: %s\n","Course:",name,teacher);
	}
}