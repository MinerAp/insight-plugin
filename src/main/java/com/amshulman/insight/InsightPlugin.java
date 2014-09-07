package com.amshulman.insight;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.World;

import com.amshulman.insight.backend.WriteBackend;
import com.amshulman.insight.command.CommandInsight;
import com.amshulman.insight.event.InternalEventHandler;
import com.amshulman.insight.event.InventoryInteractListener;
import com.amshulman.insight.event.RegistrationHandler;
import com.amshulman.insight.event.WandListener;
import com.amshulman.insight.event.block.BlockBreakListener;
import com.amshulman.insight.event.block.BlockBurnListener;
import com.amshulman.insight.event.block.BlockFadeListener;
import com.amshulman.insight.event.block.BlockFormListener;
import com.amshulman.insight.event.block.BlockFromToListener;
import com.amshulman.insight.event.block.BlockIgniteListener;
import com.amshulman.insight.event.block.BlockMultiPlaceListener;
import com.amshulman.insight.event.block.BlockPlaceListener;
import com.amshulman.insight.event.block.BlockSpreadListener;
import com.amshulman.insight.event.block.EntityBlockFormListener;
import com.amshulman.insight.event.block.EntityChangeBlockListener;
import com.amshulman.insight.event.block.EntityExplodeListener;
import com.amshulman.insight.event.block.LeavesDecayListener;
import com.amshulman.insight.event.block.PlayerBucketEmptyListener;
import com.amshulman.insight.event.block.PlayerBucketFillListener;
import com.amshulman.insight.event.block.StructureGrowListener;
import com.amshulman.insight.event.block.todo.BlockPistonExtendListener;
import com.amshulman.insight.event.block.todo.BlockPistonRetractListener;
import com.amshulman.insight.event.block.todo.SignChangeListener;
import com.amshulman.insight.event.entity.EntityDamageByEntityListener;
import com.amshulman.insight.event.entity.EntityDeathListener;
import com.amshulman.insight.event.entity.HangingBreakByEntityListener;
import com.amshulman.insight.event.entity.HangingBreakListener;
import com.amshulman.insight.event.entity.HangingPlaceListener;
import com.amshulman.insight.event.entity.PlayerExpChangeListener;
import com.amshulman.insight.event.entity.PlayerInteractEntityListener;
import com.amshulman.insight.event.entity.PlayerShearEntityListener;
import com.amshulman.insight.event.entity.VehicleEnterListener;
import com.amshulman.insight.event.entity.VehicleExitListener;
import com.amshulman.insight.event.entity.todo.EntityTargetListener;
import com.amshulman.insight.event.entity.todo.EntityUnleashListener;
import com.amshulman.insight.event.entity.todo.PlayerLeashEntityListener;
import com.amshulman.insight.event.entity.todo.PlayerUnleashEntityListener;
import com.amshulman.insight.event.item.FurnaceBurnListener;
import com.amshulman.insight.event.item.InventoryCloseListener;
import com.amshulman.insight.event.item.InventoryOpenListener;
import com.amshulman.insight.event.item.PlayerDropItemListener;
import com.amshulman.insight.event.item.PlayerPickupItemListener;
import com.amshulman.insight.event.tbd.CreatureSpawnListener;
import com.amshulman.insight.event.tbd.PlayerJoinListener;
import com.amshulman.insight.event.tbd.PlayerQuitListener;
import com.amshulman.insight.event.tbd.PotionSplashListener;
import com.amshulman.insight.parser.QueryParser;
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

        for (World world : Bukkit.getWorlds()) {
            writeBackend.registerWorld(world.getName());
        }

        // Give the query parser a list of known worlds to accept
        QueryParser.setWorlds(configurationContext.getRawReadBackend().getWorlds());

        registerCommandExecutor(new CommandInsight(configurationContext));
        registerEventHandler(new RegistrationHandler(configurationContext));
        registerEventHandler(new WandListener(configurationContext));

        registerEventHandler(new BlockPlaceListener(configurationContext));
        registerEventHandler(new BlockMultiPlaceListener());
        registerEventHandler(new BlockBreakListener());
        registerEventHandler(new BlockFromToListener());
        registerEventHandler(new EntityExplodeListener());

        boolean unused = true;
        if (!unused) {
            registerEventHandler(new CreatureSpawnListener()); // TODO
            registerEventHandler(new EntityTargetListener()); // TODO
            registerEventHandler(new PotionSplashListener()); // TODO
            registerEventHandler(new SignChangeListener()); // TODO
        }

        if (configurationContext.isLoggingInventory()) { // TODO
            registerEventHandler(new InventoryCloseListener(writeBackend));
            registerEventHandler(new InventoryOpenListener());
            registerEventHandler(new FurnaceBurnListener());
            registerEventHandler(new InventoryInteractListener(configurationContext));
        }

        if (configurationContext.isLoggingDrops()) {
            registerEventHandler(new PlayerDropItemListener());
            registerEventHandler(new PlayerPickupItemListener());
        }

        if (configurationContext.isLoggingLogins()) {
            registerEventHandler(new PlayerJoinListener()); // TODO
            registerEventHandler(new PlayerQuitListener()); // TODO
        }

        if (configurationContext.isLoggingVehicles()) {
            registerEventHandler(new VehicleEnterListener());
            registerEventHandler(new VehicleExitListener());
        }

        if (configurationContext.isLoggingLeashes()) {
            registerEventHandler(new EntityUnleashListener()); // TODO
            registerEventHandler(new PlayerLeashEntityListener()); // TODO
            registerEventHandler(new PlayerUnleashEntityListener()); // TODO
        }

        if (configurationContext.isLoggingDeaths() || configurationContext.isLoggingDrops()) {
            registerEventHandler(new EntityDeathListener(configurationContext));
        }

        if (configurationContext.isLoggingPistons()) {
            registerEventHandler(new BlockPistonExtendListener()); // TODO
            registerEventHandler(new BlockPistonRetractListener()); // TODO
        }

        if (configurationContext.isLoggingBuckets()) {
            registerEventHandler(new PlayerBucketEmptyListener());
            registerEventHandler(new PlayerBucketFillListener());
        }

        if (configurationContext.isLoggingHangings()) {
            registerEventHandler(new HangingBreakByEntityListener());
            registerEventHandler(new HangingBreakListener());
            registerEventHandler(new HangingPlaceListener());
            registerEventHandler(new EntityDamageByEntityListener());
        }

        if (configurationContext.isLoggingFire()) {
            registerEventHandler(new BlockBurnListener());
            registerEventHandler(new BlockIgniteListener());
        }

        if (configurationContext.isLoggingNaturalChanges()) { // TODO
            registerEventHandler(new BlockSpreadListener());
            registerEventHandler(new BlockFadeListener(configurationContext));
            registerEventHandler(new BlockFormListener());
            registerEventHandler(new EntityBlockFormListener());
            registerEventHandler(new StructureGrowListener());
            registerEventHandler(new LeavesDecayListener());
        }

        if (configurationContext.isLoggingExpChanges()) {
            registerEventHandler(new PlayerExpChangeListener());
        }

        if (configurationContext.isLoggingSheep()) {
            registerEventHandler(new PlayerShearEntityListener());
        }

        if (configurationContext.isLoggingSheep() || true) { // TODO
            registerEventHandler(new EntityChangeBlockListener(configurationContext));
        }

        if (configurationContext.isLoggingHangings() || configurationContext.isLoggingSheep()) {
            registerEventHandler(new PlayerInteractEntityListener(configurationContext));
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
