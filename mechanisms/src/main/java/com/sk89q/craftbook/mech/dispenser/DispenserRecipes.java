package com.sk89q.craftbook.mech.dispenser;

import java.util.ArrayList;

import org.bukkit.block.Dispenser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.sk89q.craftbook.bukkit.MechanismsPlugin;

public class DispenserRecipes implements Listener{

    MechanismsPlugin plugin;

    ArrayList<Recipe> recipes = new ArrayList<Recipe>();

    public DispenserRecipes(MechanismsPlugin plugin) {
        this.plugin = plugin;
        recipes.add(new XPShooter());
        recipes.add(new SnowShooter());
    }

    @EventHandler
    public void onBlockDispense(BlockDispenseEvent event) {
        if(event.getBlock().getState() instanceof Dispenser && plugin.getLocalConfiguration().dispenserSettings.enable) {
            Dispenser dis = (Dispenser)event.getBlock().getState();
            if(dispenseNew(dis,event.getItem(),event.getVelocity(), event)) {
                event.setCancelled(true);
            }
        }
    }

    public boolean dispenseNew(Dispenser dis, ItemStack item, Vector velocity, BlockDispenseEvent event) {
        ItemStack[] stacks = dis.getInventory().getContents();
        boolean toReturn = false;
        for(Recipe r : recipes) {
            current: {
            if((r.recipe[0] == 0 && stacks[0] == null) || (r.recipe[0] == stacks[0].getTypeId())) {
                for(int i = 1; i < stacks.length; i++)
                {
                    if((r.recipe[i] == 0 && stacks[i] == null) || (r.recipe[i] == stacks[i].getTypeId()))
                        continue;
                    else
                        break current; //This recipe is invalid.
                }
                toReturn = r.doAction(dis, item, velocity, event);
                for(int i = 1; i < stacks.length; i++)
                {
                    if((r.recipe[i] == 0 && stacks[i] == null) || (r.recipe[i] == stacks[i].getTypeId())) {
                        if(stacks[i] == null || stacks[i].getTypeId() == 0 || r.recipe[i] == 0) {

                        }
                        else if(stacks[i].getTypeId() == 326 || stacks[i].getTypeId() == 327 || stacks[i].getTypeId() == 335)
                            stacks[i].setTypeId(325); //Get your bucket back
                        else if(stacks[i].getAmount() == 1)
                            stacks[i].setTypeId(0);
                        else
                            stacks[i].setAmount(stacks[i].getAmount() - 1);
                    } else
                        return true; //Cancel the event, as obviously something went wrong.
                }
                dis.getInventory().setContents(stacks);
            }
            break current;
        }
        }
        return toReturn; //Leave it be.
    }
}