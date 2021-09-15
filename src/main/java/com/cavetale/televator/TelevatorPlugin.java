package com.cavetale.televator;

import com.cavetale.core.event.player.PluginPlayerEvent.Detail;
import com.cavetale.core.event.player.PluginPlayerEvent;
import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class TelevatorPlugin extends JavaPlugin implements Listener {
    static final String PERM = "televator.televator";

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJump(PlayerJumpEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission(PERM)) return;
        if (!player.getPassengers().isEmpty()) return;
        Block fromBlock = event.getFrom().add(0, -0.1, 0).getBlock();
        if (fromBlock.getType() != Material.GOLD_BLOCK) return;
        final int top = fromBlock.getWorld()
            .getHighestBlockYAt(fromBlock.getX(), fromBlock.getZ());
        Block block = fromBlock.getRelative(0, 1, 0);
        while (block.getY() <= top) {
            if (!block.isPassable()) break;
            block = block.getRelative(0, 1, 0);
        }
        final int distance = block.getY() - fromBlock.getY();
        if (distance < 3) return;
        if (block.getType() != Material.GOLD_BLOCK) return;
        if (!block.getRelative(0, 1, 0).isPassable()) return;
        if (!block.getRelative(0, 2, 0).isPassable()) return;
        event.setCancelled(true);
        Location target = player.getLocation();
        target.setY((double) (block.getY() + 1));
        target.setX((double) block.getX() + 0.5);
        target.setZ((double) block.getZ() + 0.5);
        getServer().getScheduler().runTask(this, () -> {
                player.teleport(target, TeleportCause.PLUGIN);
                target.getWorld().playSound(target,
                                            Sound.ITEM_CHORUS_FRUIT_TELEPORT,
                                            SoundCategory.PLAYERS,
                                            0.25f, 2.0f);
                target.getWorld().spawnParticle(Particle.SPELL_INSTANT,
                                                target,
                                                4, 0.25, 0, 0.25, 1.0);
                player.sendActionBar(Component.text("Up " + distance + " blocks", NamedTextColor.GOLD));
                PluginPlayerEvent.Name.RIDE_TELEVATOR.ultimate(this, player)
                    .detail(Detail.BLOCK, fromBlock)
                    .detail(Detail.DIRECTION, BlockFace.UP)
                    .call();
            });
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) return;
        Player player = event.getPlayer();
        if (!player.hasPermission(PERM)) return;
        if (!player.getPassengers().isEmpty()) return;
        if (!player.isOnGround()) return;
        Location target = player.getLocation();
        Block fromBlock = target.add(0, -0.1, 0).getBlock();
        if (fromBlock.getType() != Material.GOLD_BLOCK) return;
        Block block = fromBlock.getRelative(0, -1, 0);
        while (block.getY() > 0) {
            if (!block.isPassable()) break;
            block = block.getRelative(0, -1, 0);
        }
        final int distance = fromBlock.getY() - block.getY();
        if (distance < 3) return;
        if (block.getType() != Material.GOLD_BLOCK) return;
        target.setY((double) (block.getY() + 1));
        target.setX((double) block.getX() + 0.5);
        target.setZ((double) block.getZ() + 0.5);
        getServer().getScheduler().runTask(this, () -> {
                player.teleport(target, TeleportCause.PLUGIN);
                target.getWorld().playSound(target,
                                            Sound.ITEM_CHORUS_FRUIT_TELEPORT,
                                            SoundCategory.PLAYERS,
                                            0.25f, 2.0f);
                target.getWorld().spawnParticle(Particle.SPELL_INSTANT,
                                                target,
                                                4, 0.25, 0, 0.25, 1.0);
                player.sendActionBar(Component.text("Down " + distance + " blocks", NamedTextColor.GOLD));
                PluginPlayerEvent.Name.RIDE_TELEVATOR.ultimate(this, player)
                    .detail(Detail.BLOCK, fromBlock)
                    .detail(Detail.DIRECTION, BlockFace.DOWN)
                    .call();
            });
    }
}
