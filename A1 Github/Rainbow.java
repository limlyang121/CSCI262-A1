import java.io.FileWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Rainbow{
    public static int totalLine ;
    //Md5
    public static String getMd5(String input)
    {
        try {
 
            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
 
            // digest() method is called to calculate message digest
            // of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes());
 
            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);
 
            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
 
        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    //Main
    public static void main(String[] args){
        if (args.length == 0){
            System.out.println("Please include txt File");
            return ;
        }

        //get FileName
        String fileName = args[0];

        //Store totalLine to global var
        Rainbow.totalLine = getTotalLine(fileName);

        System.out.println("Total Line for Passowrd.txt is " + Rainbow.totalLine);

        //Store  the Hash table to memory
        ArrayList<String[]> myData = setData(fileName);

        //Create Rainbow.txt
        rainbowFile(myData);

        //Find Password
        findPassword(myData);

    }

    //Read and store all thing in txt to ArrayList
    public static ArrayList<String[]> setData(String fileName){
        ArrayList<String[]> myData = new ArrayList<String[]>();
        try{
            File fileRead = new File(fileName);
            Scanner sc = new Scanner(fileRead);

            while (fileRead.exists()){

                String temp;
                while (sc.hasNextLine() ){
                    temp = sc.nextLine();
                    
                   String data[] = new String[3];
                   data[0] = temp;
                   data[1] = getMd5(temp);
                   data[2] = getReductionSize(data[1]) ;
    
                   myData.add(data);
                    
                    
                } 
                sc.close();
            }
            
            
        }catch (Exception e){

        }
        return myData;
    }

    //Get totalLine of a txt File
    public static int getTotalLine(String fileName){
        int totalLine = 0;
        try{
            File file = new File(fileName);
            Scanner sc = new Scanner(file);
            
            System.out.println(fileName);
            
            while (sc.hasNextLine()){
                sc.nextLine();
                totalLine++;
            }
            
            sc.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        
        return totalLine;
    }

    //Get Reduction Size
    public static String getReductionSize(String hashValue ){
        BigInteger decimalValue = new BigInteger(hashValue, 16);
        int reductionTime = decimalValue.mod(BigInteger.valueOf(Rainbow.totalLine)).intValue();
        reductionTime += 1;

        return String.valueOf(reductionTime) ;
    }


    //Write to Rainbow file
    public static void rainbowFile(ArrayList<String[]> myData){
        // displayOption(myData);
        //createSampleToTest (myData);
        ArrayList<String[]> rainbowList = new ArrayList<>();
        ArrayList<Integer> marked = new ArrayList<>();

        //Loop through all Hash Table
        for (int i = 0 ; i < myData.size(); i++){
            int currentOne = i;
            //Skip if currentOne is already marked
            if (marked.contains(currentOne)){
                continue;
            }
            //Reduction Process
            for (int j = 0 ; j < 4 ; j++){
                
                int oldOne = currentOne;
                //Set the reduction to the next reduction
                currentOne = Integer.parseInt(myData.get(currentOne)[2]) -1 ;
                //Check if mark size is 0, if not check if int (oldOne) add to mark
                if (marked.size() != 0) {
                    if (!marked.contains(Integer.parseInt(myData.get(oldOne)[2])-1 )){
                        marked.add(Integer.parseInt(myData.get(oldOne)[2])-1 );
                    }
                }else
                    marked.add(Integer.parseInt(myData.get(oldOne)[2])-1 );
            }

            //Store the finalHash and the password and add to arrayList
            String[] tempData = {myData.get(i)[0], myData.get(currentOne)[1]};
            rainbowList.add(tempData);

        }

        //Sort the ArrayList by finalHash
        rainbowList.sort((o1,o2) -> o1[1].compareTo(o2[1]));

        System.out.println("======================================");
        System.out.println("Successfully Store to Rainbow.txt");
        System.out.println("======================================");
        //Write to Rainbow.txt
        writeFile(rainbowList);

    }

    //Write To Rainbow.txt
    public static void writeFile(ArrayList<String[]> myData){
        try{
            File refreshFile = new File("Rainbow.txt");
            if (refreshFile.exists())
                refreshFile.delete();

            FileWriter fw = new FileWriter("Rainbow.txt",true);

            for (int i = 0 ; i < myData.size();i++)
                fw.write(myData.get(i)[0] +","+ myData.get(i)[1]+"\n")  ;
            
            fw.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        System.out.println("Total Line for Rainbow is : "+ getTotalLine("Rainbow.txt"));
    }

//=============================================================================================================

    //From this onward, is the ask user for input (second part)
    //This part is when user type hash value to find password
    public static void findPassword(ArrayList<String[]> myData){
        Scanner sc = new Scanner(System.in);
        //Store the rainbow table to memory Object
        ArrayList<String[]> rainbowTable =  getRainBowTable();
        // displayTest(rainbowTable);

        boolean programEnd = false;
        while (!programEnd){
            System.out.println("Enter 'Exit' to Quit the Program");
            System.out.println("Please input Hash Value : ");
    
            String userInput = sc.nextLine();

            if (userInput.equalsIgnoreCase("Exit")){
                programEnd = true;
                continue;
            }
            //If the length is not 32, ask user to reinput by skip to beginning
            if (userInput.length() != 32){
                System.out.println("Please input 32 length of value");
                continue;
            }
            
            //Get Final store from getPassword(function)
            String finalPassword = getPassword(myData, rainbowTable, userInput);
            if (!finalPassword.isEmpty()){
                System.out.println("Password is " + finalPassword);
            }else  
                //If after reduction and still can't found password(The input hash not in password txt (after hash)))
                System.out.println("Password not found");

            

            System.out.println("Press Enter to Continue");
            sc.nextLine();
            System.out.println("===========================================================");
        }

        sc.close();

    }


    //Begin Find Password
    public static String getPassword(ArrayList<String[]> myData , ArrayList<String[]> rainbowTable , String userInput){
        //Get the user input hash and convert to big Integer and get it reduction number
        int myReduction = Integer.parseInt(getReductionSize(userInput) ) -1 ;
        int currentOne = myReduction;
        ArrayList<String> passwordCollisionStore= new ArrayList<>();
        String finalPassword = new String();
        String currentHash = userInput;

        
        //Do Reduction 100 times (can be lower, i just do 100 for more accurate)
        for (int i = 0 ; i < 100 ; i++){
            
            //Check if current user Input is on the rainbow table then
            //Get All Password that have same hash with currentHash in the rainbow table(Since there collision)
            passwordCollisionStore = hashExist(currentHash, rainbowTable);
            
            //Reduce to next Reduction (can also put below, the same)
            currentHash = myData.get(currentOne)[1];

            //if collisionStore(Array List that on line 256) size is not 0 (which mean currentHash is in rainbow )
            if (passwordCollisionStore.size() != 0 ){
                //Get the password with "CheckingWithHashTable" Function
                finalPassword = CheckingWithHashTable(userInput, passwordCollisionStore, myData);
                //If the password is not empty (which mean found the password), return it
                if (!finalPassword.isEmpty()){
                    return finalPassword;
                }

            }
            //else,Reduction to another hash table
            
            //Same with the one in line 244, but line 244 is the start point
            currentOne = Integer.parseInt(myData.get(currentOne)[2])-1;
        }


        //If no password found, return empty;
        finalPassword = "";
        return finalPassword;
    }

    //Read and store rainbow.txt and store to memory Object
    public static ArrayList<String[]> getRainBowTable(){
        ArrayList<String[]> rainbowTable = new ArrayList<>();
        try{
            File myFile = new File("Rainbow.txt");
            Scanner rc = new Scanner(myFile);
            while (rc.hasNextLine()){
                String[] tempData = rc.nextLine().split(",");
                rainbowTable.add(tempData);

            }
            rc.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
        return rainbowTable;
    }

    //Loop though all rainbow table and check if hash value is in the rainbow table
    public static ArrayList<String> hashExist(String hashValue, ArrayList<String[]> rainbowTable){
        ArrayList<String> collisionStore = new ArrayList<>();

        for (int i = 0 ; i < rainbowTable.size(); i++){

            if (hashValue.equalsIgnoreCase(rainbowTable.get(i)[1])){
                String stringExist = rainbowTable.get(i)[0];
                collisionStore.add(stringExist);
            }

            if ((hashValue.compareToIgnoreCase(rainbowTable.get(i)[1]) < 0))
                break;
        }
        return collisionStore;
    }


    //Loop though hash Table to see if userInput is in hashTable with reduction
    public static String CheckingWithHashTable(String userInput,ArrayList<String> passwordCollisionStore, ArrayList<String[]> myData){
        //if there is multiple collision, need to check them all
        for (int i = 0 ; i < passwordCollisionStore.size() ; i++){
            String passwordStore = passwordCollisionStore.get(i);
            String myHashPassword = getMd5(passwordCollisionStore.get(i));
            for (int j = 0 ; j < 10 ; j++){
                
                int currentOne = Integer.parseInt(getReductionSize(myHashPassword)) - 1 ;
                if (userInput.equalsIgnoreCase(myHashPassword)){
                    return passwordStore;
                }
                passwordStore = myData.get(currentOne)[0];
                myHashPassword =  myData.get(currentOne)[1];
            }
        }

        return "";
    }

//Mostly for display stuff and create sample
    public static void displayTest(ArrayList<String[]> myData){
        for (int i = 0 ; i < 100; i++){
            System.out.println(i+1 + " = " + myData.get(i)[0]);
        }
    }

    //To Display the Each Password it hash and reduction (not recommend on password.txt)
    public static void displayOption(ArrayList<String[]> myData){
        for (int i = 0 ; i < myData.size(); i++)
            System.out.printf("%-2d. %-15s %20s %4s \n", i+1, myData.get(i)[0], myData.get(i)[1], myData.get(i)[2]);
    }

    //To create sample so i can copy to test if it working
    public static void createSampleToTest(ArrayList<String[]> myData){
        try{
            FileWriter fw =  new FileWriter("sample.txt");
            for (int i = 0 ; i < myData.size(); i++)
                fw.write(myData.get(i)[0] + ", " + myData.get(i)[1] +", " + myData.get(i)[2] + "\n");
            fw.close();
        }catch (Exception e ){
            e.printStackTrace();
        }
    }
}