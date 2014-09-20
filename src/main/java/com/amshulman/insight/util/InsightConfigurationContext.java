package com.amshulman.insight.util;

import java.util.Collection;
import java.util.logging.Logger;

import lombok.EqualsAndHashCode;
import lombok.Value;

import org.bukkit.configuration.file.FileConfiguration;

import com.amshulman.insight.backend.BackendType;
import com.amshulman.insight.backend.PlayerCallbackReadBackend;
import com.amshulman.insight.backend.ReadBackend;
import com.amshulman.insight.backend.SqlReadWriteBackend;
import com.amshulman.insight.backend.WriteBackend;
import com.amshulman.insight.management.PlayerInfoManager;
import com.amshulman.insight.worldedit.WorldEditBridge;
import com.amshulman.mbapi.MbapiPlugin;
import com.amshulman.mbapi.util.ConfigurationContext;

@Value
@EqualsAndHashCode(callSuper = false)
public class InsightConfigurationContext extends ConfigurationContext implements InsightDatabaseConfigurationInfo {

    int cacheSize;

    String databaseAddress;
    String databaseName;
    int databasePort;
    String databaseUsername;
    String databasePassword;
    BackendType databaseType;

    boolean worldEditEnabled;

    Logger logger;
    PlayerCallbackReadBackend readBackend;
    ReadBackend rawReadBackend;
    WriteBackend writeBackend;
    PlayerInfoManager infoManager;

    Collection<String> excludedWorlds;

    boolean loggingInventory;
    boolean loggingDrops;
    boolean loggingLogins;
    boolean loggingVehicles;
    boolean loggingLeashes;
    boolean loggingDeaths;
    boolean loggingPistons;
    boolean loggingBuckets;
    boolean loggingHangings;
    boolean loggingFire;
    boolean loggingNaturalChanges;
    boolean loggingExpChanges;
    boolean loggingSheep;
    boolean loggingFarmland;

    public InsightConfigurationContext(MbapiPlugin plugin) {
        super(plugin);
        logger = plugin.getLogger();

        plugin.saveDefaultConfig();
        FileConfiguration configuration = plugin.getConfig();

        try {
            databaseType = BackendType.valueOf(configuration.getString("database.connection.type", "").toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("\"" + configuration.getString("database.connection.type" + "\" is not a valid database type", ""));
        }

        databaseAddress = configuration.getString("database.connection.address");
        if (databaseAddress == null || databaseAddress.length() == 0) {
            throw new IllegalArgumentException("\"" + databaseAddress + "\" is not a valid address");
        }

        databasePort = configuration.getInt("database.connection.port");
        if (databasePort < 1 || databasePort > 65535) {
            throw new IllegalArgumentException("\"" + databasePort + "\" is not a valid port");
        }

        databaseName = configuration.getString("database.connection.databaseName");
        if (databaseName == null || databaseName.length() == 0) {
            throw new IllegalArgumentException("\"" + databaseName + "\" is not a valid database name");
        }

        databaseUsername = configuration.getString("database.connection.username");
        databasePassword = configuration.getString("database.connection.password");

        boolean worldEditEnabled = false;
        try {
            worldEditEnabled = WorldEditBridge.isWorldEditEnabled();
        } catch (NoClassDefFoundError e) {}
        this.worldEditEnabled = worldEditEnabled;

        excludedWorlds = configuration.getStringList("excludedWorlds");

        /* tuning */
        cacheSize = configuration.getInt("database.tuning.cacheSize");

        /* logging choices */
        loggingInventory = configuration.getBoolean("logging.inventory");
        loggingDrops = configuration.getBoolean("logging.drops");
        loggingLogins = configuration.getBoolean("logging.logins");
        loggingVehicles = configuration.getBoolean("logging.vehicles");
        loggingLeashes = configuration.getBoolean("logging.leashes");
        loggingDeaths = configuration.getBoolean("logging.deaths");
        loggingPistons = configuration.getBoolean("logging.pistons");
        loggingBuckets = configuration.getBoolean("logging.buckets");
        loggingHangings = configuration.getBoolean("logging.hangings");
        loggingFire = configuration.getBoolean("logging.fire");
        loggingNaturalChanges = configuration.getBoolean("logging.nature");
        loggingExpChanges = configuration.getBoolean("logging.xp");
        loggingSheep = configuration.getBoolean("logging.sheep");
        loggingFarmland = configuration.getBoolean("logging.farmland");

        infoManager = new PlayerInfoManager();

        switch (getDatabaseType()) {
            case MYSQL:
            case POSTGRES:
                SqlReadWriteBackend sqlBackend = new SqlReadWriteBackend(this);
                readBackend = new PlayerCallbackReadBackend(sqlBackend, infoManager);
                rawReadBackend = sqlBackend;
                writeBackend = sqlBackend;
                break;
            case REDIS:
            default:
                throw new UnsupportedOperationException("No redis support yet");
                // break;
        }
    }
}
