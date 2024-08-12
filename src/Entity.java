public class Entity {
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
            stats[i] = Game.rollWithAdvantage(20) + Game.roll(level);
        }
        //make max health the highest of health and max health
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
        int attackAmount = (2 * Game.rollWithAdvantage(stats[2])) / 3;
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
        int amt = Game.roll(6);
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

        int amt = Game.roll(10);
        stats[stat] += amt;

        //if health or stamina are higher than max health or stamina, reset to max
        if(stats[stat] > stats[stat - 1]){
            stats[stat] = stats[stat - 1];
        }

        return amt;
    }
}
