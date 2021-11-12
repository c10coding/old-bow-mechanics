package net.nthbyte.bowmechanicsfix;

import net.dohaw.corelib.CoreLib;
import net.dohaw.corelib.JPUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.Collection;

public final class BowMechanicsFix extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic
        CoreLib.setInstance(this);
        JPUtils.registerEvents(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onProjectileFire(EntityShootBowEvent e){
        System.out.println("LAUNCHED");
        Entity entity = e.getEntity();
        Entity projectile = e.getProjectile();
        if(projectile instanceof Arrow){
            Arrow arrow = (Arrow) projectile;
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
                            Vector vec_ = arrowVelocity_.normalize().multiply(0.4D);
                            if (vec_.length() > 0.0D) {
                                addVelocity(shooter, vec_.getX(), 0.45D, vec_.getZ());
                            }
                        }
                    }
                    arrow.remove();
                }
            }, 5L);
        }
    }

    @EventHandler
    public void onEntityTakeDamage(ProjectileHitEvent e){
        Projectile projectile = e.getEntity();
        if(projectile instanceof Arrow && e.getHitEntity() != null){
            if(projectile.getShooter() instanceof Player){
                if(((Player) projectile.getShooter()).getUniqueId().equals(e.getHitEntity().getUniqueId()) && projectile.getTicksLived() > 6){
                    System.out.println("They have hit themseleves");
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

}
