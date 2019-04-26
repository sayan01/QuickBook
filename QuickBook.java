import java.util.*;
import java.io.*;
import java.awt.print.*;
import java.awt.*;
class QuickBook{
	static String[][] passenger_data;
	static String[] receipt;
	public static void cancel()throws IOException{
		File f = new File("data.txt");
		if(!f.exists()){
			System.out.print("We do not have any booking data. Sorry.");
			System.exit(0);
		}
		Scanner sc = new Scanner(System.in);
		System.out.println("Please enter Transaction number: ");
		String t = sc.nextLine();
		if(t.length()!=16){
			System.out.print("Transaction number is invalid!");
			System.exit(1);
		}
		System.out.println("Please enter PNR number: ");
		String p = sc.nextLine();
		if(p.length()!=10){
			System.out.print("PNR number is invalid!");
			System.exit(1);
		}
		RandomAccessFile raf = new RandomAccessFile(f,"rw");
		boolean found = false;
		long prevFID = 0;
		while(raf.getFilePointer() != raf.length()){
			String line = raf.readLine();
			String[] tokens = line.split("\t");
			if(tokens[tokens.length-1].equals("CANCELLED")) {prevFID = raf.getFilePointer();continue;}
			if(tokens[0].equals(t) && tokens[1].equals(p)){
				found = true;
				if(datePast(tokens[4])){
					System.out.print("The ticket is of a past booking. Ticket cannot be cancelled.");
					System.exit(0);
				}
				System.out.print("Record found, Are you sure you want to cancel booking? (Y/N) ");
				if(sc.nextLine().trim().equalsIgnoreCase("N"))System.exit(-1);
				String others = "";
				while(raf.getFilePointer() != raf.length()){
					others+= raf.readLine()+"\n";
				}
				raf.seek(prevFID);
				raf.writeBytes(line + "\t"+"CANCELLED"+"\n"+others);
				break;
			}
			prevFID = raf.getFilePointer();
		}
		if(!found){
			System.out.println("No such record of train ticket is present.");
		}
		else{
			System.out.println("Train Ticket cancelled successfully.");
		}
		System.exit(0);
	}
	public static boolean datePast(String cdf){
		Calendar curr = Calendar.getInstance();
		int yr = Integer.parseInt(cdf.substring(cdf.length()-4));
		int mon = "JanFebMarAprMayJunJulAugSepOctNovDec".indexOf(cdf.substring(4,7))  /  3;
		int date = Integer.parseInt(cdf.substring(8,10));
		int hr = Integer.parseInt(cdf.substring(11,13));
		int min = Integer.parseInt(cdf.substring(14,16));
		Calendar rec = Calendar.getInstance();
		rec.set(yr,mon,date,hr,min,0);
		return rec.before(curr);

	}
	public static void main(String[] args) throws IOException{
		Scanner sc = new Scanner(System.in);
		String[] cities = loadCities();
		System.out.println("\nQuickBook\n");
		Calendar currentDate = Calendar.getInstance();
		System.out.println("Current Time: "+ currentDate.getTime().toString());
		System.out.println("What do you want to do?\n1.Book Tickets\n2.Cancel Tickets");
		int book_or_can = Integer.parseInt(sc.nextLine());
		if(book_or_can==2){
			System.out.print("We are sorry to hear you have to cancel your booking.\n");
			cancel();
		}
		else if(book_or_can!=1){
			// datePast(Calendar.getInstance().getTime().toString());
			System.out.println("Invalid choice!\nTerminating Program.");
			System.exit(1);
		}
		System.out.print("Journey from (enter name of city): ");
		String city_from = sc.nextLine().trim().toUpperCase();
		System.out.print("Journey to (enter name of city): ");
		String city_to = sc.nextLine().trim().toUpperCase();
		if(city_to.equals(city_from)){
			System.out.print("Destination cannot be same as source!\nTerminating program.");
			System.exit(1);
		}
		int c = 0;
		for(String city:cities){
			if(city.equalsIgnoreCase(city_from) || city.equalsIgnoreCase(city_to))	c++;
			if(c==2) break;
		}
		if(c<2){
			System.out.println("City entered is not servicible.\nPossible Cities:");
			for(int i = 0; i < cities.length ; i++){
				System.out.print(cities[i] +( (i%6==5)?"\n":"    ")  );
			}
			System.exit(0);
		}
		System.out.print("Enter Date of Journey(DD MM YYYY): ");
		String date_journey = sc.nextLine().trim();
		Calendar journeyDate = Calendar.getInstance();

		String[] timings = {"07:00","12:30","02:45","06:15","08:20","11:15"};
		String[] tk = date_journey.split("[ ./]+");
		int time = (int)(Math.random()*6);
		int yr = Integer.parseInt(tk[2]);
		int mn = Integer.parseInt(tk[1])-1;
		int day = Integer.parseInt(tk[0]);
		int hr = Integer.parseInt(timings[time].substring(0,2));
		int min = Integer.parseInt(timings[time].substring(3,5));
		journeyDate.set(yr,mn,day,hr,min,0);

		if(journeyDate.before(currentDate)){
			System.out.print("Journey Date is in past.\nTerminating Program.");
			System.exit(1);
		}
		String Class = ""; byte class_index;

		String[] classes = {"Anubhuti Class (EA)","AC First Class (1A)","Exec. Chair Car (EC)","AC 2 Tier (2A)",
		"First Class (FC)","AC 3 Tier (3A)","AC 3 Economy (3E)","AC Chair Car (CC)","Sleeper (SL)","Second Sitting (2S)"};
		System.out.print("Choose Class: \n");
		for(int i = 0; i < classes.length;i++){
			System.out.println(i+1+". "+classes[i]);
		}
		class_index = sc.nextByte();
		if(class_index>classes.length) System.exit(1);
		Class = classes[class_index-1];

		System.out.print("Train found: \n");
		int train_number = (int)(Math.random()*89999)+10000;
		String train_name = city_from+"-"+city_to+" EXPRESS";
		int duration = ((int)(Math.random()*24))+1;
		int distance = (int)(Math.random()*1500) + 500;
		Calendar arrivalDate = Calendar.getInstance();
		arrivalDate.set(journeyDate.get(Calendar.YEAR),journeyDate.get(Calendar.MONTH),journeyDate.get(Calendar.DATE),journeyDate.get(Calendar.HOUR_OF_DAY),journeyDate.get(Calendar.MINUTE),0);
		arrivalDate.add(Calendar.HOUR_OF_DAY,duration);
		System.out.println("Train number: "+ train_number+"\nTrain Name: "+ train_name +"\nTime of Arrival at "+city_from+ ": "+
			journeyDate.getTime().toString() +"\nDuration: "+ duration + "hrs\nTime of arrival at "+city_to+": "+ arrivalDate.getTime().toString() + "\nDistance: "+ distance +"KM");
		System.out.print("\n\nBook Train? (Y/N) ");
		if(sc.next().equalsIgnoreCase("N")){
			System.exit(0);
		}
		String[] quotas = {"General","Tatkal","Tatkal Premium","Ladies","Yuva","Senior Citizen"};
		System.out.println("Enter Quota: ");
		for(int i = 0; i< quotas.length;i++){
			System.out.println(i+1+". "+quotas[i]);
		}
		byte quota_index = sc.nextByte();
		if(quota_index>quotas.length) System.exit(1);
		String quota = quotas[quota_index-1];

		System.out.print("Do you want e-ticket(1) or i-ticket(2)? ");
		int ticket_choice = sc.nextByte();
		if(ticket_choice>2 || ticket_choice<1)System.exit(1);
		System.out.print("How many tickets do you want to book? (max 10): ");
		int no_of_passengers = Integer.parseInt(sc.next());
		if(no_of_passengers<1 || no_of_passengers>10) System.exit(1);
		System.out.println("Enter passenger Details: ");
		passenger_data = new String[no_of_passengers][1+1+1+1+1];
		int adult = 0;
		sc.nextLine();
		for(int i = 0; i < no_of_passengers;i++){
			System.out.println("Enter passenger #"+(i+1) + " Details: ");
			System.out.print("Enter name: ");
			passenger_data[i][0] = sc.nextLine();
			System.out.print("Enter age: ");
			passenger_data[i][1] = sc.nextLine();
			if(Integer.parseInt(passenger_data[i][1]) < 1 || Integer.parseInt(passenger_data[i][1]) > 100) System.exit(1);
			if (Integer.parseInt(passenger_data[i][1]) >=18) adult++;
			System.out.print("Enter sex: (M/F)  ");
			passenger_data[i][2] = sc.nextLine().toUpperCase();
			if(!(passenger_data[i][2] .equals( "M" )|| passenger_data[i][2] .equals("F")) )System.exit(1);
			System.out.print("Enter food choice: (VEG/NON-VEG)  ");
			passenger_data[i][3] = sc.nextLine();
		}
		int child = no_of_passengers- adult;
		System.out.print("Enter additional information: \nPhone number: ");
		String phno = sc.nextLine();
		System.out.print("Enter address: ");
		String add = sc.nextLine();
		double fare;
		// fare calculation
		fare = (distance/100 + (classes.length - class_index) + adult + child/1.2 ) * 200;
		fare = (int)fare;
		fare += ((int)(Math.random() * 100))/100.0 ;
		System.out.println("Fare to be payed : \nTicket Fare = "+fare + "\nQuickBook service charge = 15.00\nTotal Fare = "+(fare+15));
		System.out.print("Proceed to payment? (Y/N) ");
		if(sc.next().toUpperCase().trim().equalsIgnoreCase("N")) System.exit(1);

		// tickets booked
		if(ticket_choice == 2){
		 	System.out.print("Your tickets are booked. \nYour tickets will be delivered to your address withing 3-5 days.\n"
		 			+"Thank You.");
		 	System.exit(0);
		}
		else{
			String transactionID = createUnique("TRANS");
			String PNR = createUnique("PNR");
			receipt = new String[17];
			receipt[0] = transactionID;
			receipt[1] = PNR;
			receipt[2] = train_name;
			receipt[3] = ""+train_number;
			receipt[4] = journeyDate.getTime().toString();
			receipt[5] = Class;
			receipt[6] = currentDate.getTime().toString();
			receipt[7] = "" + distance+" KM";
			receipt[8] = city_from;
			receipt[9] = city_to;
			receipt[10] = arrivalDate.getTime().toString();
			receipt[11] = ""+adult;
			receipt[12] = ""+child;
			receipt[13] = quota;
			receipt[14] = phno;
			receipt[15] = add;
			receipt[16] = "RS. "+fare;
			String coach = "";
			String[] ber = {"UPPER","MIDDLE","LOWER"};
			for(int i = 0; i< 2 ; i++){
				if(i==0)
					coach+= (char)(Math.random() * 26 + 65);
				else
					coach += (char)(Math.random() * 10 + 48);
			}
			for(int i = 0; i < no_of_passengers;i++){
				int seatNo = (int)(Math.random()* 100);
				String berth = ber[seatNo%3];
				passenger_data[i][4] = coach+ " "+ seatNo+ " " +berth;
			}
			writeData(receipt,passenger_data);
			System.out.print("Tickets have been booked.\nPrint Tickets? (Y/N) ");
			if(!sc.next().trim().toUpperCase().equalsIgnoreCase("N"))
				PrintTicket.print();
		}
	}
	private static String createUnique(String field) throws IOException{
		if(field.equalsIgnoreCase("TRANS")){
			String transactionID = "";
			for(int i = 0; i<16;i++)transactionID += ""+(int)(Math.random()*10);
			if(!unique(transactionID,"TRANS"))
				return createUnique("TRANS");
			else return transactionID;
		}
		else if(field.equalsIgnoreCase("PNR")){
			String PNR = "";
			for(int i = 0; i<10;i++)PNR += ""+(int)(Math.random()*10);
			if(unique(PNR,"PNR"))
				return PNR;
			else return createUnique("PNR");
		}
		else return null;
	}
	private static boolean unique(String data,String field) throws IOException{
		File f = new File("data.txt");
		if(!f.exists()) return true;
		RandomAccessFile raf = new RandomAccessFile(f,"r");
		raf.seek(0);
		boolean unique = true;
		while(raf.getFilePointer() != raf.length()){
			String line = raf.readLine();
			String[] tokens = line.split("\t");
			if(field.equalsIgnoreCase("TRANS")){
				if(tokens[0].equals(data))
					return false;
			}
			else if(field.equalsIgnoreCase("PNR")){
				if(tokens[1].equals(data))
					return false;
			}
		}
		return true;
	}
	private static void writeData(String[] receipt, String[][] pada) throws IOException{
		File f = new File("data.txt");
		if(!f.exists()){
			System.out.println("Database File is not present.\nMaking new file.");
			if(!f.createNewFile()){
				System.out.println("Could not create new file.\nTerminating Program.");
			}
			else{
				System.out.println("New File made.");
			}
		}
		RandomAccessFile raf = new RandomAccessFile(f,"rw");
		raf.seek(raf.length());
		for(String receipt_data:receipt){
			raf.writeBytes(receipt_data+"\t");
		}
		for(String[] row:pada){
			for(String data:row){
				raf.writeBytes(data+"\t");
			}
		}
		raf.writeBytes("\n");
	}
	private static String[] loadCities() throws IOException{
		File f = new File("city.txt");
		if(!f.exists()){
			System.out.print("City Database file is missing.\nProgram will terminate.");
			System.exit(1);
		}
		String[] rv = new String[countLines(f.getAbsolutePath())];
		RandomAccessFile raf = new RandomAccessFile(f,"r");
		int k = 0;
		while(raf.getFilePointer() != raf.length()){
			rv[k++] = raf.readLine();
		}
		return rv;
	}
	private static int countLines(String filename) throws IOException {
        try (InputStream is = new BufferedInputStream(new FileInputStream(filename))) { // tries to access given file
            byte[] c = new byte[1024];  // local buffer of 1KB
            int count = 0;
            int readChars;
            boolean endsWithoutNewLine = false;
            while ((readChars = is.read(c)) != -1) {    // while EOF is not encountered
                for (int i = 0; i < readChars; ++i) {   // read the characters stored in buffer
                    if (c[i] == '\n')                   // if '/n' is present, increase counter
                        ++count;
                }
                endsWithoutNewLine = (c[readChars - 1] != '\n'); // Stores weather the last character of current buffer
                                                                // was a newline or not.
            }
            if (endsWithoutNewLine) {               // if the last character of the file wasn't a newline, then increase
                                                    // counter by 1
                ++count;
            }
            return count;
        }
    }
}

class PrintTicket
        implements Printable {
    public int print(Graphics g, PageFormat pf, int page) {
        if (page > 0) {
            return NO_SUCH_PAGE;
        }
        Graphics2D g2d = (Graphics2D)g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());
        String[] labels = {"Transaction ID: ","PNR No: ","Train Name: ","Train Number: ","Journey Date and Time: ","Class: ","Date of Booking: ","Distance: ","From: ","To: ","Arrival Time: ","Adult: ","Child: ","Quota: ","Phone Number: ","Address: ","Fare: "};
        String[] pada_labels = {"Name","Age","Gender","Food Choice","Coach/Seat/Berth"};
        int y = 150;
        g.setFont(new Font("Century Gothic",Font.PLAIN,25));
        g.drawString("QuickBook - Book Railway Tickets with Ease",50,100);
        String[][] pada = QuickBook.passenger_data;
        String[] receipt = QuickBook.receipt;

        for(int i = 0; i< 17;i++){
        	String label = labels[i];
        	String data = receipt[i];
        	g.setFont(new Font("Arial",Font.BOLD,14));
        	g.drawString(label, 20, y);
        	g.setFont(new Font("Consolas",Font.PLAIN,12));
        	g.drawString(data,200, y);
        	y+=15;
        }
        y+=25;
        	g.setFont(new Font("Arial",Font.BOLD,20));
        	g.drawString("Passenger Details:", 20, y);
        y+=25;
        	g.setFont(new Font("Arial",Font.BOLD,14));
        	g.drawString("Sn. No.", 30, y);
        for(int i = 0; i < 5;i++){
        	g.setFont(new Font("Arial",Font.BOLD,14));
        	g.drawString(pada_labels[i], (i+1)*90, y);
        }
        y+= 20;
        for(int i = 0;i<pada.length;i++){
        	g.setFont(new Font("Arial",Font.PLAIN,12));
        	g.drawString(""+(i+1), 30, y);
        	for(int j = 0; j<5;j++){
	        	g.setFont(new Font("Arial",Font.PLAIN,12));
	        	g.drawString(pada[i][j], (j+1)*90, y);
        	}
        	y+=20;
        }
        return PAGE_EXISTS;
    }
    static void print(){
        PrinterJob job = PrinterJob.getPrinterJob();
        PrintTicket pt = new PrintTicket();
        job.setPrintable(pt);
        if (job.printDialog()) {
            try {
                job.print();
            }
            catch (PrinterException pe) {
                System.out.print("Could not Print\n"+pe.toString());
            }
        }
    }
}