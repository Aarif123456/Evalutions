How to run program
Go to your terminal that runs java and type in the following 2 commands:
javac ReportCard.java
java ReportCard

To ensure the program runs properly make sure you have the following 4 input file in the input folder:
students.csv -holds infromation about student in the format student_id, student_name
courses.csv  -holds infromation about courses in the format course_id, course_name, teacher
tests.csv    -holds infromation about tests in courses in the format test-id,course_id,test_weight
marks.csv    -holds infromation about marks of each student on the tests they took in the format test_id, student_id,mark

------------------------------------------------------------------------------------------------------
Brief description
My program takes information from four files; that store data about the performance of students in their respective courses and information about the courses. Then taking that data to create a transcript which holds the final grade of every course each student took. 
