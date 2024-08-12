public class Main {
    public static void main(String[] args) {
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
        enemy = new Entity(Game.getName(),Game.roll(6));

        io.log("Okay, the legend of " + player.name + " begins!");
        io.log("It is time to chose your stats!");
        Game.chooseStats(player, io);

        //loop until player dies!
        while(player.stats[1] > 0){
            io.log("\nYou are fighting the mighty level " + enemy.level + " " + enemy.name + "!");
            io.log("LET THE BATTLE BEGIN!!!\n");

            //do rounds until enemy dies
            while(enemy.stats[1] > 0 && player.stats[1] > 0){
                Game.doRound(io,player,enemy);
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
                xp += Game.roll(10 * enemy.level);
                io.log("You have " + victories + " victories, and " + xp + " experience points!\n");

                //give player food for winning!
                player.stats[5] += Game.roll(12);
                //food maxes out at 30
                if(player.stats[5] > 30) player.stats[5] = 30;

                //heal player before next battle!
                player.stats[1] = player.stats[0];
                player.stats[4] = player.stats[3];

                //create new enemy
                enemy = new Entity(Game.getName(), Game.roll(6 + victories));

                //level up!
                if(xp > 25 * player.level) {
                    player.level += 1;
                    xp = 0;
                    io.log("You level up! You are now level " + player.level);
                    //increase player stats!
                    for (int i = 0; i < player.stats.length; i++) {
                        int amt = Game.roll(6);
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


    //classes

    //technically you don't need this, but I like having a single place for input and output

}