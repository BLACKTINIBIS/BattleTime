import java.util.ArrayList;
import java.util.Random;

public class Game {
    public static void chooseStats(Entity player, IO io){
        //variables for player character creation
        int[] tempPlayerStats = new int[4];
        /*the temporary stat rolls are stored in an ArrayList because
          it was a lot nicer to be able to delete members of it instead of
          making a new array every time*/
        ArrayList<Integer> tempStatRolls = new ArrayList<>();
        //special array of strings for character creation
        String[] enumStats = {
                "Health",
                "Strength",
                "Stamina",
                "Food"
        };

        io.log("\nHere are your randomly chosen stats:\n");

        //roll 5 times to get 5 options for your 4 stats, so one can be discarded
        for(int i = 0; i < 5; i++){
            tempStatRolls.add(rollWithAdvantage(20) + roll(6));
        }

        //while the player hasn't chosen all stats, loop
        while(!allStatsChosen(tempPlayerStats)){
            //for each temporary player stat,
            for(int i = 0; i < tempPlayerStats.length; i++){
                //display the available rolls
                StringBuilder rollChoices = new StringBuilder();
                for(int j = 0; j < tempStatRolls.size(); j++){
                    rollChoices.append(j).append(". ").append(tempStatRolls.get(j)).append("\n");
                }
                //get an option from the player for which roll to use for the current stat
                int option = io.getOption(tempStatRolls.size(), "Which roll will you use for " + enumStats[i] + "?\n" + rollChoices);
                //assign the roll from the list to the current stat
                tempPlayerStats[i] = tempStatRolls.get(option);
                //delete that particular roll from the available options
                tempStatRolls.remove(option);
            }
        }

        io.log("You have chosen your stats wisely, " + player.name + ".");
        //assign stats to player!

        //health and max health
        player.stats[0] = tempPlayerStats[0];
        player.stats[1] = tempPlayerStats[0];
        //strength
        player.stats[2] = tempPlayerStats[1];
        //stamina
        player.stats[3] = tempPlayerStats[2];
        player.stats[4] = tempPlayerStats[2];
        //food
        player.stats[5] = tempPlayerStats[3];
    }

    //this is where the game logic is
    public static void doRound(IO io, Entity player, Entity enemy){
        boolean done = false;
        int choice;

        //displays the stats and menu at the start of each round
        displayState(io,player,enemy);
        displayMenu(io);

        //get an option from the user and do the thing
        while(!done){
            choice = io.getOption(3, "What will you do, " + player.name + "?");
            io.log("\n\n"); //spacer to make actions stand out
            int amt;
            switch(choice){
                case(1): //attack
                    amt = player.attack();
                    if(amt > 0){
                        io.log("You attack " + enemy.name + " for " + amt + " damage!");
                        enemy.hurt(amt);
                        done = true;
                    } else {
                        io.log("You cannot attack, your stamina is too low!");
                    }
                    break;
                case(2): //heal
                    amt = player.raiseStat(1);
                    if(amt > 0){
                        io.log("You heal yourself by " + amt + " for a new total of " + player.stats[1] + "!");
                        done = true;
                    } else {
                        io.log("You cannot heal, your health is full!");
                    }
                    break;
                case(3): //eat
                    amt = player.raiseStat(4);
                    if(amt > 0){
                        io.log("You eat and raise your stamina by " + amt + " for a total of " + player.stats[4] + "!");
                        done = true;
                    } else {
                        io.log("You cannot eat right now!");
                    }
                    break;
                case(4): //rest
                    amt = player.rest();
                    io.log("You rest and gain " + amt + " health and stamina!");
                    break;
                case(0): //quit
                    io.log("You have chosen to quit? COWARD!!!");
                    done = true;
                    System.exit(0);
                    break;
            }
        }
        //Also do a turn for the enemy!
        doEnemyRound(io,player,enemy);
        io.log("\n"); //another spacer
    }

    //The """AI""" behind the enemy!
    public static void doEnemyRound(IO io, Entity player, Entity enemy){
        boolean done = false;
        while(!done){
            int choice = roll(3); //the enemy does things at random
            int amt;
            switch(choice){
                case(1): //attack
                    amt = enemy.attack();
                    if(amt > 0){
                        io.log(enemy.name + " attacks you for " + amt + " damage!");
                        player.hurt(amt);
                        done = true;
                    }
                    break;
                case(2): //heal
                    amt = enemy.raiseStat(1);
                    if(amt > 0){
                        io.log(enemy.name + " heals by " + amt + "!");
                        done = true;
                    }
                    break;
                case(3): //eat
                    amt = enemy.raiseStat(4);
                    if(amt > 0){
                        io.log(enemy.name + " eats to restore " + amt + " stamina!");
                        done = true;
                    }
                    break;
                case(4):
                    amt = enemy.rest();
                    io.log(enemy.name + " rests and gains " + amt + " health and stamina!");
                    break;
            }
        }
    }

    public static void displayMenu(IO io){
        io.log("\n[OPTIONS]");
        io.log("1. Attack");
        io.log("2. Heal");
        io.log("3. Eat");
        io.log("4. Rest");
        io.log("0. Quit");
    }

    //displays the stats of the player and the enemy
    public static void displayState(IO io, Entity player, Entity enemy){
        String spacer = "\t\t\t\t";

        //stores the names of each stat as strings at the same index as entity.stats
        String[] enumStats = {
                " Max Health: ",
                "     Health: ",
                "   Strength: ",
                "Max Stamina: ",
                "    Stamina: ",
                "       Food: "};

        //print header
        io.log(player.name + spacer + "\t\t" + enemy.name);

        //for each stat, display it nice and formatted using enumStats array
        for(int i = 0; i < enumStats.length; i++){
            io.log(enumStats[i] + player.stats[i] + spacer + enumStats[i] + enemy.stats[i]);
        }
    }

    //randomly generates a name using consonant vowel consonant vowel etc
    public static String getName(){
        Random rng = new Random();
        String vow = "aeiouy";
        String con = "bcdfghjklmnpqrstvwxz";
        StringBuilder name = new StringBuilder();
        String response;
        int length = rng.nextInt(4)+2;
        for(int i = 0; i < length; i++){
            name.append(con.charAt(rng.nextInt(vow.length())));
            name.append(vow.charAt(rng.nextInt(vow.length())));
        }
        //capitalize the first letter of name.
        response = name.substring(0,1).toUpperCase() + name.substring(1);
        return response;
    }

    public static boolean allStatsChosen(int[] arr){
        for(int n: arr){
            if(n==0){
                return false;
            }
        }
        return true;
    }

    //get random numbers up to a maximum
    public static int roll(int max){
        Random rng = new Random();
        return rng.nextInt(max) + 1;
    }

    //like in Dungeons and Dragons, rolls twice and takes the higher amt
    public static int rollWithAdvantage(int max){
        Random rng = new Random();
        int roll1, roll2;
        roll1 = rng.nextInt(max) + 1;
        roll2 = rng.nextInt(max) + 1;
        return Math.max(roll1, roll2);
    }
}
