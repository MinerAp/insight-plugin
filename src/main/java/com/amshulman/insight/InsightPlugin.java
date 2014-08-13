package com.amshulman.insight;

import javax.annotation.Nonnull;

import com.amshulman.insight.backend.WriteBackend;
import com.amshulman.insight.command.CommandInsight;
import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.event.InventoryInteractListener;
import com.amshulman.insight.event.PlayerRegistrationHandler;
import com.amshulman.insight.event.WandListener;
import com.amshulman.insight.event.block.BlockBreakListener;
import com.amshulman.insight.event.block.BlockBurnListener;
import com.amshulman.insight.event.block.BlockFadeListener;
import com.amshulman.insight.event.block.BlockFormListener;
import com.amshulman.insight.event.block.BlockFromToListener;
import com.amshulman.insight.event.block.BlockIgniteListener;
import com.amshulman.insight.event.block.BlockPlaceListener;
import com.amshulman.insight.event.block.BlockSpreadListener;
import com.amshulman.insight.event.block.EntityBlockFormListener;
import com.amshulman.insight.event.block.EntityChangeBlockListener;
import com.amshulman.insight.event.block.LeavesDecayListener;
import com.amshulman.insight.event.block.PlayerBucketEmptyListener;
import com.amshulman.insight.event.block.PlayerBucketFillListener;
import com.amshulman.insight.event.block.StructureGrowListener;
import com.amshulman.insight.event.block.todo.BlockPistonExtendListener;
import com.amshulman.insight.event.block.todo.BlockPistonRetractListener;
import com.amshulman.insight.event.block.todo.SignChangeListener;
import com.amshulman.insight.event.entity.EntityDeathListener;
import com.amshulman.insight.event.entity.todo.EntityTargetListener;
import com.amshulman.insight.event.entity.todo.EntityUnleashListener;
import com.amshulman.insight.event.entity.todo.HangingBreakByEntityListener;
import com.amshulman.insight.event.entity.todo.HangingBreakListener;
import com.amshulman.insight.event.entity.todo.HangingPlaceListener;
import com.amshulman.insight.event.entity.todo.PlayerInteractEntityListener;
import com.amshulman.insight.event.entity.todo.PlayerLeashEntityListener;
import com.amshulman.insight.event.entity.todo.PlayerShearEntityListener;
import com.amshulman.insight.event.entity.todo.PlayerUnleashEntityListener;
import com.amshulman.insight.event.entity.todo.VehicleCreateListener;
import com.amshulman.insight.event.entity.todo.VehicleDestroyListener;
import com.amshulman.insight.event.entity.todo.VehicleEnterListener;
import com.amshulman.insight.event.entity.todo.VehicleExitListener;
import com.amshulman.insight.event.intransitive.PlayerExpChangeListener;
import com.amshulman.insight.event.item.FurnaceBurnListener;
import com.amshulman.insight.event.item.InventoryCloseListener;
import com.amshulman.insight.event.item.InventoryOpenListener;
import com.amshulman.insight.event.item.PlayerDropItemListener;
import com.amshulman.insight.event.item.PlayerPickupItemListener;
import com.amshulman.insight.event.tbd.CreatureSpawnListener;
import com.amshulman.insight.event.tbd.EntityExplodeListener;
import com.amshulman.insight.event.tbd.PlayerJoinListener;
import com.amshulman.insight.event.tbd.PlayerQuitListener;
import com.amshulman.insight.event.tbd.PotionSplashListener;
import com.amshulman.insight.row.RowEntry;
import com.amshulman.insight.util.CraftBukkitUtil;
import com.amshulman.insight.util.InsightConfigurationContext;
import com.amshulman.mbapi.MbapiPlugin;

public class InsightPlugin extends MbapiPlugin implements com.amshulman.insight.util.InsightPlugin {

    private WriteBackend writeBackend;

    @Override
    public void onLoad() {
        super.onLoad();

        InternalEventHandler.setPlugin(this);
    }

    @Override
    public void onEnable() {
        try {
            CraftBukkitUtil.initialize(this);
        } catch (ClassCastException | IllegalArgumentException | SecurityException | ReflectiveOperationException e) {
            this.getLogger().severe("Could not find support for this CraftBukkit version.");
            e.printStackTrace();
            this.setEnabled(false);
            return;
        }

        InsightConfigurationContext configurationContext;
        try {
            configurationContext = new InsightConfigurationContext(this);
        } catch (IllegalArgumentException e) {
            getLogger().severe(e.getMessage());
            setEnabled(false);
            return;
        }

        writeBackend = configurationContext.getWriteBackend();

        registerCommandExecutor(new CommandInsight(configurationContext));
        registerEventHandler(new PlayerRegistrationHandler(configurationContext));
        registerEventHandler(new WandListener(configurationContext));

        registerEventHandler(new BlockBreakListener());
        registerEventHandler(new BlockBurnListener());
        registerEventHandler(new BlockFadeListener());
        registerEventHandler(new BlockFormListener());
        registerEventHandler(new BlockFromToListener());
        registerEventHandler(new BlockIgniteListener()); // TODO
        registerEventHandler(new BlockPlaceListener());
        registerEventHandler(new BlockSpreadListener());
        registerEventHandler(new CreatureSpawnListener()); // TODO
        registerEventHandler(new EntityBlockFormListener());
        registerEventHandler(new EntityChangeBlockListener());
        registerEventHandler(new EntityExplodeListener()); // TODO
        registerEventHandler(new EntityTargetListener()); // TODO
        registerEventHandler(new HangingBreakByEntityListener()); // TODO
        registerEventHandler(new HangingBreakListener()); // TODO
        registerEventHandler(new HangingPlaceListener()); // TODO
        registerEventHandler(new LeavesDecayListener());
        registerEventHandler(new PlayerBucketEmptyListener());
        registerEventHandler(new PlayerBucketFillListener());
        registerEventHandler(new PlayerExpChangeListener()); // TODO
        registerEventHandler(new PlayerInteractEntityListener()); // TODO
        registerEventHandler(new PlayerShearEntityListener()); // TODO
        registerEventHandler(new PotionSplashListener()); // TODO
        registerEventHandler(new SignChangeListener());
        registerEventHandler(new StructureGrowListener()); // TODO

        if (configurationContext.isLoggingInventory()) {
            registerEventHandler(new InventoryCloseListener(writeBackend));
            registerEventHandler(new InventoryOpenListener());
            registerEventHandler(new FurnaceBurnListener());
            registerEventHandler(new InventoryInteractListener());
        }

        boolean dropLogging = true;
        if (dropLogging) {
            registerEventHandler(new PlayerDropItemListener());
            registerEventHandler(new PlayerPickupItemListener());
        }
        
        boolean loginLogging = false;
        if (loginLogging) {
            registerEventHandler(new PlayerJoinListener());
            registerEventHandler(new PlayerQuitListener());
        }
        
        boolean vehicleLogging = false;
        if (vehicleLogging) {
            registerEventHandler(new VehicleCreateListener()); // TODO
            registerEventHandler(new VehicleDestroyListener()); // TODO
            registerEventHandler(new VehicleEnterListener()); // TODO
            registerEventHandler(new VehicleExitListener()); // TODO
        }

        boolean leashLogging = false;
        if (leashLogging) {
            registerEventHandler(new EntityUnleashListener()); // TODO
            registerEventHandler(new PlayerLeashEntityListener()); // TODO
            registerEventHandler(new PlayerUnleashEntityListener()); // TODO            
        }
        
        boolean deathLogging = true;
        if (deathLogging) {
            registerEventHandler(new EntityDeathListener());
            
        }
        
        boolean pistonLogging = false;
        if (pistonLogging) {
            registerEventHandler(new BlockPistonExtendListener()); // TODO
            registerEventHandler(new BlockPistonRetractListener()); // TODO
        }
        
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        writeBackend.close();
    }

    @Override
    public void logEvent(@Nonnull RowEntry row) {
        writeBackend.submit(row);
    }
}
