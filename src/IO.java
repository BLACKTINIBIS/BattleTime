import java.util.Scanner;

public class IO {
    private final Scanner input;
    public IO(){
        input = new Scanner(System.in);
    }
    public void log(String msg){
        System.out.println(msg);
    }
    public String nextLine(){
        return input.nextLine();
    }

    //complicated function to get valid menu inputs from user
    public int getOption(int max, String message){
        Integer response = null;
        String garbage;
        while(response == null){
            this.log(message);
            if(input.hasNextInt()){
                response = input.nextInt();
                if(response <= 0){
                    response = 0;
                } else {
                    if(response > max){
                        this.log("Please pick an option between 1 and " + max);
                        response = null;
                    }
                }
            } else {
                garbage = input.nextLine();
                this.log("Sorry, '" + garbage + "' is not a valid option.");
            }
        }
        return response;
    }
}