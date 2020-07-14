package anse.Numeaning;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityLevelChangeEvent;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.event.player.PlayerTeleportEvent.TeleportCause;
import cn.nukkit.level.Level;
import cn.nukkit.network.protocol.ChangeDimensionPacket;
import cn.nukkit.network.protocol.PlayStatusPacket;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.TextFormat;

public class Main extends PluginBase implements Listener {
    public Main() {
    }

    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getLogger().info("加载成功...");
    }

    @EventHandler
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        final Player player = event.getPlayer();
        if (!event.getFrom().level.getFolderName().equals(event.getTo().getLevel().getFolderName())) {
            Level nether = this.getServer().getLevelByName("nether");
            if (nether != null) {
                player.teleport(nether.getSpawnLocation(), (TeleportCause)null);
            }

            this.getServer().getScheduler().scheduleDelayedTask(new Task() {
                public void onRun(int i) {
                    player.teleport(event.getTo(), (TeleportCause)null);
                }
            }, 5);
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onPlayerLevelChange(EntityLevelChangeEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            final Player player = (Player)entity;
            Level nether = this.getServer().getLevelByName("nether");
            if (nether == null) {
                if (player.isOp()) {
                    player.sendMessage(TextFormat.RED + "[Numeaning] 必须有地狱地图作为中转.");
                }

                return;
            }

            Level targetLevel = event.getTarget();
            int targetDimension = targetLevel.getDimension();
            ChangeDimensionPacket changeDimensionPacket = new ChangeDimensionPacket();
            changeDimensionPacket.dimension = targetDimension;
            player.dataPacket(changeDimensionPacket);
            final PlayStatusPacket playStatusPacket = new PlayStatusPacket();
            playStatusPacket.status = 3;
            if (targetDimension != 0) {
                player.dataPacket(playStatusPacket);
            } else {
                this.getServer().getScheduler().scheduleDelayedTask(new Task() {
                    public void onRun(int i) {
                        player.dataPacket(playStatusPacket);
                    }
                }, 65);
            }
        }

    }
}
