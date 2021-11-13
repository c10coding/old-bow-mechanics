package net.nthbyte.bowmechanicsfix;

import net.dohaw.corelib.CoreLib;
import net.dohaw.corelib.JPUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.Collection;

public final class BowMechanicsFix extends JavaPlugin implements Listener, CommandExecutor {

    private BaseConfig baseConfig;

    @Override
    public void onEnable() {
        // Plugin startup logic
        CoreLib.setInstance(this);
        JPUtils.registerEvents(this);
        JPUtils.validateFiles("config.yml");
        this.baseConfig = new BaseConfig();
        JPUtils.registerCommand("bowmechanicsfix", this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onProjectileFire(EntityShootBowEvent e){
        System.out.println("FIRE");
        Entity entity = e.getEntity();
        Entity projectile = e.getProjectile();
        if(projectile instanceof SpectralArrow){
            SpectralArrow arrow = (SpectralArrow) projectile;
            if(!(arrow.getShooter() instanceof Player)){
                return;
            }
            Player shooter = (Player) arrow.getShooter();
            arrow.teleport(getLocationInBack(entity.getLocation(), 1.5).add(0, 1.5, 0));
            Bukkit.getScheduler().runTaskLater(this, () -> {
                final Vector arrowVelocity_ = arrow.getVelocity().setY(0);
                Collection<Entity> nearbyEntities = arrow.getNearbyEntities(0.2, 0.2, 0.2);
                for(Entity en : nearbyEntities){
                    if(en.getUniqueId().equals(shooter.getUniqueId())){
                        shooter.setVelocity(new Vector(0, 0, 0));
                        if (arrow.getKnockbackStrength() > 0) {
                            Vector vec_ = arrowVelocity_.normalize().multiply(arrow.getKnockbackStrength() * 0.6D);
                            if (vec_.length() > 0.0D) {
                                addVelocity(shooter, vec_.getX(), 0.45D, vec_.getZ());
                            }
                        } else {
                            Vector vec_ = arrowVelocity_.normalize().multiply(baseConfig.getVelocityScale());
                            if (vec_.length() > 0.0D) {
                                addVelocity(shooter, vec_.getX(), 0.45D, vec_.getZ());
                            }
                        }
                        shooter.playEffect(EntityEffect.HURT);
                        shooter.damage(1);
                        arrow.remove();
                        return;
                    }
                }
            }, 5L);
        }
    }

    @EventHandler
    public void onEntityTakeDamage(ProjectileHitEvent e){
        Projectile projectile = e.getEntity();
        if(projectile instanceof SpectralArrow && e.getHitEntity() != null && e.getHitEntity() instanceof LivingEntity){
            LivingEntity hitEntity = (LivingEntity) e.getHitEntity();
            if(projectile.getShooter() instanceof Player){
                if(((Player) projectile.getShooter()).getUniqueId().equals(hitEntity.getUniqueId()) && projectile.getTicksLived() > 6){
                    hitEntity.playEffect(EntityEffect.HURT);
                    hitEntity.damage(0.3);
                    projectile.remove();
                }
            }
        }
    }

    public static Location getLocationInBack(Location location, double numBlocksInBack) {
        Location clone = location.clone();
        return getLocationInFront(clone, numBlocksInBack * -1);
    }

    public static Location getLocationInFront(Location location, double numBlocksInFront){
        return location.clone().add(location.clone().getDirection().multiply(numBlocksInFront));
    }

    public void addVelocity(Player player, double d0, double d1, double d2) {
        player.setVelocity(player.getVelocity().add(new Vector(d0, d1, d2)));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length != 0 && args[0].equalsIgnoreCase("reload")){
            baseConfig.reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "The plugin has been reloaded!");
            return true;
        }
        return false;
    }

}
