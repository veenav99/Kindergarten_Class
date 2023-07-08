package kindergarten;
import javax.swing.ToolTipManager;

/**
 * This class represents a Classroom, with:
 * - an SNode instance variable for students in line,
 * - an SNode instance variable for musical chairs, pointing to the last student in the list,
 * - a boolean array for seating availability (eg. can a student sit in a given seat), and
 * - a Student array parallel to seatingAvailability to show students filed into seats 
 */

public class Classroom {
    private SNode studentsInLine;             // when students are in line: references the FIRST student in the LL
    private SNode musicalChairs;              // when students are in musical chairs: references the LAST student in the CLL
    private boolean[][] seatingAvailability;  // represents the classroom seats that are available to students
    private Student[][] studentsSitting;      // when students are sitting in the classroom: contains the students

    /**
     * Constructor for classrooms
     * @param l passes in students in line
     * @param m passes in musical chairs
     * @param a passes in availability
     * @param s passes in students sitting
     */
    
    public Classroom ( SNode l , SNode m , boolean[][] a , Student[][] s ) {
	    studentsInLine = l;
	    musicalChairs = m;
	    seatingAvailability = a;
	    studentsSitting = s;
	}
	
    //Default constructor starts an empty classroom. 
     
    public Classroom() {
        this( null , null , null , null );
    }
	

    private void insertStudent( Student st ){
        if ( studentsInLine == null ){
            SNode studentsInLine = new SNode( st , null );
            return;
        }
        if (st.compareNameTo( studentsInLine.getStudent() ) < 0 ){
            SNode temp = new SNode( st , studentsInLine );
            studentsInLine = temp;
            return;
        }
        SNode currentSNode = studentsInLine;
        while ( currentSNode != null ){
            SNode nextNode = currentSNode.getNext();

            if( nextNode == null ){
                currentSNode.setNext( new SNode( st , null ) ); 
                return;
            }

            if ( ( st.compareNameTo( currentSNode.getStudent() ) > 0 ) && ( st.compareNameTo( nextNode.getStudent() ) < 0 ) ) {
                SNode temp = new SNode( st , nextNode );
                currentSNode.setNext( temp );
                return;
            } else {
                currentSNode = nextNode;
                nextNode = currentSNode.getNext();
            }
        }
    }

    private void insertStudentByHeight( Student st ){
        if ( studentsInLine == null ){
            studentsInLine = new SNode( st , null );
            return;
        }
        SNode currentSNode = studentsInLine;

        if ( st.getHeight() < currentSNode.getStudent().getHeight() ) {

            SNode temp = new SNode( st , studentsInLine );
            studentsInLine = temp;
            return;

        }

        while ( currentSNode != null ) {
            SNode nextNode = currentSNode.getNext();

            if (nextNode == null){
                if ( ( st.getHeight() >= currentSNode.getStudent().getHeight() ) ){
                    currentSNode.setNext( new SNode( st , null ) );
                }
                return;
            }

            if ( ( st.getHeight() >= currentSNode.getStudent().getHeight() && ( st.getHeight() < ( nextNode.getStudent().getHeight() ) ) ) ) {
                SNode temp = new SNode( st , nextNode );
                currentSNode.setNext( temp );
                return;
            } else {
                currentSNode = nextNode;
                nextNode = nextNode.getNext();
            }
        }
    }


    /**
     * This method simulates students coming into the classroom and standing in line. 
     * @param filename the student information input file
     */

    public void makeClassroom ( String filename ) {

        String studentLine;

        StdIn.setFile( filename );
        int numLines = StdIn.readInt();
        StdIn.readLine();

        for (int i = 0; i < numLines; i++){
            studentLine = StdIn.readLine();

            String [] studentAr = studentLine.split(" "); 
            String firstName = studentAr[ 0 ];
            String lastName = studentAr[ 1 ];
            int height = Integer.parseInt(studentAr[ 2 ]);
            Student st = new Student( firstName , lastName , height );

            if ( studentsInLine == null ){
                studentsInLine = new SNode( st , null );
            } else {
                insertStudent( st );
            }
        }
    }


    /**
     * This method creates and initializes the seatingAvailability (2D array) of 
     * available seats inside the classroom. Imagine that unavailable seats are broken and cannot be used.
     *  
     * This method creates the studentsSitting array with the same number of rows and columns as the seatingAvailability array
     * 
     * This method does not seat students on the seats
     */

    public void setupSeats( String seatingChart ) {

        StdIn.setFile( seatingChart );

        int r = StdIn.readInt();
        int c = StdIn.readInt();
        int count = 0;

        studentsSitting = new Student[ r ][ c ];
        seatingAvailability = new boolean[ r ][ c ];

        for ( int i = 0; i < r; i++){
            for ( int j = 0; j < c; j++){
                boolean seats = StdIn.readBoolean();
                seatingAvailability[ i ][ j ] = seats;
                if ( seats ){
                    count++;
                }
            }
        }
    }


    // This method simulates students taking their seats in the classroom
     

    public void seatStudents () {

        int row = seatingAvailability.length;
        int column = seatingAvailability[ 0 ].length;

        for ( int i = 0; i < row; i++ ){
            for ( int j = 0; j < column; j++ ){

                if ( !seatingAvailability[ i ][ j ] ){

                    continue;
                
                } else {

                    if ( musicalChairs != null ){

                        studentsSitting[ i ][ j ] = musicalChairs.getStudent();
                        musicalChairs = null;

                        continue;
                    }

                    if ( studentsInLine != null ){

                        studentsSitting[ i ][ j ] = studentsInLine.getStudent();
                        studentsInLine = studentsInLine.getNext();
                    }
                }
            }
        }
    }


    // Traverses studentsSitting row-wise (starting at row 0) removing a seated student and adding that student to the end of the musicalChairs list

    public void insertMusicalChairs () {

        int row = seatingAvailability.length;
        int column = seatingAvailability[ 0 ].length;

        for ( int i = row - 1; i >= 0; i-- ){
            for ( int j = column - 1; j >= 0; j--){

                if ( studentsSitting[ i ][ j ] == null ){

                    continue;
                
                } else {

                    if ( musicalChairs == null ){

                        musicalChairs = new SNode( studentsSitting[ i ][ j ] , null );
                        studentsSitting[ i ][ j ] = null;
                        musicalChairs.setNext( musicalChairs );
                    
                        continue;
                    }

                    SNode temp = new SNode( studentsSitting[ i ][ j ] , musicalChairs.getNext() );
                    musicalChairs.setNext( temp );

                    musicalChairs = temp;
                    studentsSitting[ i ][ j ] = null;
                }
            }
        }
     }


    /**
     * This method repeatedly removes students from the musicalChairs until there is only one student (the winner)
     * Removes eliminated student from the list and inserts students back in studentsInLine in ascending height order (shortest to tallest)
     */

    public void playMusicalChairs() {
	    
        SNode front = musicalChairs.getNext();
        int count = 0;
        while ( front != musicalChairs ){

            front = front.getNext();
            count++;
        }

        Student [] studentList = new Student[count];
        int iCount = count;

        for ( int i = 0; i < iCount; i++ ) {

            int x = StdRandom.uniform(count);
            SNode prev = new SNode();
            SNode head = musicalChairs.getNext();

            for ( int j = 0; j <= x; j++ ){

                prev = head;
                head = head.getNext();
            }

            prev.setNext( head.getNext() );
            musicalChairs = prev;
            count--;
            studentList[ i ] = head.getStudent();
        }
        musicalChairs.getStudent().print();

        for ( int i = 0; i < iCount; i++ ){
            for ( int j = 0; j < iCount; j++ ){

                Student temp = null;

                if ( studentList[ i ].getHeight() > studentList[ j ].getHeight() ){

                    temp = studentList[ i ];
                    studentList[ i ] = studentList[ j ];
                    studentList[ j ] = temp;
                }  

                if ( studentList[ i ].getHeight() == studentList[ j ].getHeight() ){

                    temp = studentList[ i ];
                    studentList[ i ] = studentList[ j ];
                    studentList[ j ] = temp;
                }
            }
        }

        SNode Front = null;

        for ( int i = 0; i < studentList.length; i++ ){
            studentsInLine = new SNode( studentList[ i ] , Front);
            Front = studentsInLine;
        } 
        seatStudents();
    } 


    /**
     * Inserts a student to wherever the students are (whatever activity is not empty)
     * - adds to the end of either linked list or the next available empty seat
     */

    public void addLateStudent ( String firstName, String lastName, int height ) {

        Student lateStudent = new Student ( firstName , lastName , height );
        SNode tardyStudent = new SNode ( lateStudent , null );

        if ( studentsInLine != null ){
            SNode ptr = studentsInLine;

            while ( ( ptr.getNext() != null ) ){
                
                ptr = ptr.getNext();

            }
            ptr.setNext( tardyStudent );

        } else if ( musicalChairs != null ) {

            SNode ptr = new SNode( lateStudent , musicalChairs.getNext() );

            musicalChairs.setNext( ptr );
            musicalChairs = musicalChairs.getNext();

        } else if ( studentsSitting != null ){

            for ( int i = 0; i < studentsSitting.length; i++){
                boolean seatLateStudent = false;
                for ( int j = 0; j < studentsSitting[i].length; j++){

                    if ( seatingAvailability[i][j] && studentsSitting[i][j] == null ){
                        studentsSitting[i][j] = lateStudent;
                        seatLateStudent = true;
                        break;
                    }
                }
                if ( seatLateStudent ){
                    break;
                }
            }
        }
    }


    // This method deletes an early-leaving student from wherever the students are at (ie. whatever activity is not empty)

    public void deleteLeavingStudent ( String firstName, String lastName ) {

        if ( studentsInLine != null ){

            SNode ptr = studentsInLine;

            if ( ptr.getStudent().getFirstName().equalsIgnoreCase(firstName) && ptr.getStudent().getLastName().equalsIgnoreCase(lastName)){

                studentsInLine = studentsInLine.getNext(); 
                return;

            }

            while ( ( ptr.getNext() != null ) ){

                if ( ptr.getNext().getStudent().getFirstName().equalsIgnoreCase(firstName) && ptr.getNext().getStudent().getLastName().equalsIgnoreCase(lastName) ){

                    ptr.setNext( ptr.getNext().getNext() );
                    return;
                }

                ptr = ptr.getNext();
            }

        } else if ( musicalChairs != null ){

            SNode ptr = musicalChairs;

            if ( ptr.getStudent().getFirstName().equalsIgnoreCase( firstName ) && ptr.getStudent().getLastName().equalsIgnoreCase( lastName ) ){ 

                musicalChairs.setNext( musicalChairs.getNext().getNext() );
                return;
            } 

            while ( ptr.getNext() != musicalChairs ){

                if ( ptr.getNext().getStudent().getFirstName().equalsIgnoreCase(firstName) && ptr.getNext().getStudent().getLastName().
		    equalsIgnoreCase(lastName) ){

                    ptr.setNext( ptr.getNext().getNext() );
                    return;
                }

                ptr = ptr.getNext();
            }
            
        } else if ( studentsSitting != null ){

            for ( int i = 0; i < studentsSitting.length; i++ ){
		    
                for ( int j = 0; j < studentsSitting[ i ].length; j++ ){

                    if ( studentsSitting[ i ][ j ].getFirstName().equalsIgnoreCase( firstName ) && studentsSitting[ i ][ j ].getLastName().
			equalsIgnoreCase( lastName ) ){
			    
                        studentsSitting[i][j] = null;
                        return;
                    }
                } 
            }
        }   
    }
    



    //Used by driver to display students in line
	
    public void printStudentsInLine () {

        //Print studentsInLine
        StdOut.println ( "Students in Line:" );
        if ( studentsInLine == null ) { StdOut.println("EMPTY"); }

        for ( SNode ptr = studentsInLine; ptr != null; ptr = ptr.getNext() ) {
            StdOut.print ( ptr.getStudent().print() );
            if ( ptr.getNext() != null ) { StdOut.print ( " -> " ); }
        }
        StdOut.println();
        StdOut.println();
    }

    // Prints the seated students; can use this method to debug
	
    public void printSeatedStudents () {

        StdOut.println("Sitting Students:");

        if ( studentsSitting != null ) {
        
            for ( int i = 0; i < studentsSitting.length; i++ ) {
                for ( int j = 0; j < studentsSitting[i].length; j++ ) {

                    String stringToPrint = "";
                    if ( studentsSitting[i][j] == null ) {

                        if (seatingAvailability[i][j] == false) {stringToPrint = "X";}
                        else { stringToPrint = "EMPTY"; }

                    } else { stringToPrint = studentsSitting[i][j].print();}

                    StdOut.print ( stringToPrint );
                    
                    for ( int o = 0; o < (10 - stringToPrint.length()); o++ ) {
                        StdOut.print (" ");
                    }
                }
                StdOut.println();
            }
        } else {
            StdOut.println("EMPTY");
        }
        StdOut.println();
    }

    // Prints the musical chairs; can use this method to debug.
	
    public void printMusicalChairs () {
        StdOut.println ( "Students in Musical Chairs:" );

        if ( musicalChairs == null ) {
            StdOut.println("EMPTY");
            StdOut.println();
            return;
        }
        SNode ptr;
        for ( ptr = musicalChairs.getNext(); ptr != musicalChairs; ptr = ptr.getNext() ) {
            StdOut.print(ptr.getStudent().print() + " -> ");
        }
        if ( ptr == musicalChairs) {
            StdOut.print(musicalChairs.getStudent().print() + " - POINTS TO FRONT");
        }
        StdOut.println();
    }

    //Prints the state of the classroom; can use this method to debug.
	
    public void printClassroom() {
        printStudentsInLine();
        printSeatedStudents();
        printMusicalChairs();
    }

    //Used to get and set objects.

    public SNode getStudentsInLine() { return studentsInLine; }
    public void setStudentsInLine(SNode l) { studentsInLine = l; }

    public SNode getMusicalChairs() { return musicalChairs; }
    public void setMusicalChairs(SNode m) { musicalChairs = m; }

    public boolean[][] getSeatingAvailability() { return seatingAvailability; }
    public void setSeatingAvailability(boolean[][] a) { seatingAvailability = a; }

    public Student[][] getStudentsSitting() { return studentsSitting; }
    public void setStudentsSitting(Student[][] s) { studentsSitting = s; }
}
