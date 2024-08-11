import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        Random rng = new Random();
        //create custom input/output object
        IO io = new IO();
        //create custom entity objects for player and enemy
        Entity player;
        Entity enemy;
        //variables for victory and experience points for leveling up
        int victories = 0;
        int xp = 0;

        //intro
        io.log("\n\n\n()()()()()()()()()()()()()()()()()()()()()");
        io.log(")()()()(WELCOME TO BATTLE TIME 1.0)()()()(");
        io.log("()()()()()()()()()()()()()()()()()()()()()\n\n");
        io.log("What is your name, puny human?");
        //create player and enemy
        player = new Entity(io.nextLine(), 6);
        enemy = new Entity(getName(),roll(6));

        io.log("Okay, the legend of " + player.name + " begins!");
        io.log("It is time to chose your stats!");
        chooseStats(player, io);

        //loop until player dies!
        while(player.stats[1] > 0){
            io.log("\nYou are fighting the mighty level " + enemy.level + " " + enemy.name + "!");
            io.log("LET THE BATTLE BEGIN!!!\n");

            //do rounds until enemy dies
            while(enemy.stats[1] > 0 && player.stats[1] > 0){
                doRound(io,player,enemy);
            }

            //if both the player and the enemy die
            if(enemy.stats[1] <= 0 && player.stats[1] <= 0){
                io.log("You, the mighty " + player.name + ", and your foe, the feared " + enemy.name + " both fall to the ground, dead.");
                io.log("You were too evenly matched.");
                System.exit(0);
            }


            //if the enemy is defeated:
            if(enemy.stats[1] <= 0) {
                io.log(enemy.name + " falls to the ground, defeated!");
                io.log("\nYOU WIN, CONGRATULATIONS MORTAL!");

                //increment victories, add experience points, and tell user
                victories++;
                xp += roll(10 * enemy.level);
                io.log("You have " + victories + " victories, and " + xp + " experience points!\n");

                //give player food for winning!
                player.stats[5] += roll(12);
                //food maxes out at 30
                if(player.stats[5] > 30) player.stats[5] = 30;

                //heal player before next battle!
                player.stats[1] = player.stats[0];
                player.stats[4] = player.stats[3];

                //create new enemy
                enemy = new Entity(getName(), roll(6 + victories));

                //level up!
                if(xp > 25 * player.level) {
                    player.level += 1;
                    xp = 0;
                    io.log("You level up! You are now level " + player.level);
                    //increase player stats!
                    for (int i = 0; i < player.stats.length; i++) {
                        int amt = roll(6);
                        player.stats[i] += amt;
                    }
                }
            }
        }

        //if player health is 0 or below, they die and the program ends
        io.log("You, " + player.name + ", crumple to the floor, defeated like a baby!");
        io.log("YOU LOSE, PUNY HUMAN!");
        io.log("You defeated " + victories + " foes before you fell in battle!");
        System.exit(0);
    }

    //functions
    private static void chooseStats(Entity player, IO io){
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
                String rollChoices = "";
                for(int j = 0; j < tempStatRolls.size(); j++){
                    rollChoices += j + ". " + tempStatRolls.get(j) + "\n";
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
    private static void doRound(IO io, Entity player, Entity enemy){
        boolean done = false;
        int choice;

        //displays the stats and menu at the start of each round
        displayState(io,player,enemy);
        displayMenu(io);

        //get an option from the user and do the thing
        while(!done){
            choice = io.getOption(3, "What will you do, " + player.name + "?");
            io.log("\n\n"); //spacer to make actions stand out
            int amt = 0;
            switch(choice){
                case(1): //attack
                    amt = player.attack();
                    if(amt > 0){
                        io.log("You attack " + enemy.name + " for " + amt + " damage!");
                        enemy.hurt(amt);
                        done = true;
                    } else {
                        io.log("You cannot attack, your stamina is too low!");
                        done = false;
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
    private static void doEnemyRound(IO io, Entity player, Entity enemy){
        boolean done = false;
        while(!done){
            int choice = roll(3); //the enemy does things at random
            int amt = 0;
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

    private static void displayMenu(IO io){
        io.log("\n[OPTIONS]");
        io.log("1. Attack");
        io.log("2. Heal");
        io.log("3. Eat");
        io.log("4. Rest");
        io.log("0. Quit");
    }

    //displays the stats of the player and the enemy
    private static void displayState(IO io, Entity player, Entity enemy){
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
    private static String getName(){
        Random rng = new Random();
        String vow = "aeiouy";
        String con = "bcdfghjklmnpqrstvwxz";
        String name = "";
        String response;
        int length = rng.nextInt(4)+2;
        for(int i = 0; i < length; i++){
            name += con.charAt(rng.nextInt(vow.length()));
            name += vow.charAt(rng.nextInt(vow.length()));
        }
        //capitalize the first letter of name.
        response = name.substring(0,1).toUpperCase() + name.substring(1);
        return response;
    }

    private static boolean allStatsChosen(int[] arr){
        for(int n: arr){
            if(n==0){
                return false;
            }
        }
        return true;
    }

    //get random numbers up to a maximum
    private static int roll(int max){
        Random rng = new Random();
        return rng.nextInt(max) + 1;
    }

    //like in Dungeons and Dragons, rolls twice and takes the higher amt
    private static int rollWithAdvantage(int max){
        Random rng = new Random();
        int roll1, roll2;
        roll1 = rng.nextInt(max) + 1;
        roll2 = rng.nextInt(max) + 1;
        return Math.max(roll1, roll2);
    }

    //classes

    //these should be in their own files, but i've left them inside this one for ease of illustration
    public static class Entity {
        String name;
        int level;
        int[] stats = new int[6];
        /*
            0: max health
            1: health
            2: strength
            3: max stamina
            4: stamina
            5: food
         */

        //public method used to create a new Entity
        public Entity(String name, int level) {
            this.name = name;
            this.level = level;
            //set stats randomly
            for(int i = 0; i < stats.length; i++){
                stats[i] = rollWithAdvantage(20) + roll(level);
            }
            //make max health the higher of health and max health
            int maxHealth = Math.max(stats[0],stats[1]);
            stats[0] = maxHealth;
            stats[1] = maxHealth;

            //do the same for stamina and max stamina
            int maxStamina = Math.max(stats[3],stats[4]);
            stats[3] = maxStamina;
            stats[4] = maxStamina;
        }

        //attack method
        public int attack(){
            int attackAmount = (2 * rollWithAdvantage(stats[2])) / 3;
            //if you have stamina:
            if(stats[4] > (attackAmount / 2)){
                //lose some stamina
                stats[4] -= attackAmount / 2;
            } else {
                attackAmount = 0;
            }
            return attackAmount;
        }

        //hurt method
        public void hurt(int amount){
            stats[1] -= amount;
        }

        //rest
        public int rest(){
            int amt = roll(6);
            stats[4] += amt;
            stats[1] += amt;
            return amt;
        }

        //heal or eat
        public int raiseStat(int stat){
            //if raising stamina
            if(stat == 4){
                //if there's food, eat it
                if(stats[5] > 0){
                    stats[5] -= 5;
                    if(stats[5] < 0) stats[5] = 0;
                } else {
                    //if you can't eat, you can't raise stamina
                    return 0;
                }
            }

            //if health is full don't heal!
            if(stats[stat] >= stats[stat-1]){
                return 0;
            }

            int amt = roll(10);
            stats[stat] += amt;

            //if health or stamina are higher than max health or stamina, reset to max
            if(stats[stat] > stats[stat - 1]){
                stats[stat] = stats[stat - 1];
            }

            return amt;
        }
    }

    //technically you don't need this, but I like having a single place for input and output
    private static class IO {
        private Scanner input;
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
            String garbage = "";
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
}