package com.amshulman.insight.util;

import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;

import lombok.EqualsAndHashCode;
import lombok.Value;

import com.amshulman.insight.backend.BackendType;
import com.amshulman.insight.backend.PlayerCallbackReadBackend;
import com.amshulman.insight.backend.SqlReadWriteBackend;
import com.amshulman.insight.backend.WriteBackend;
import com.amshulman.insight.management.PlayerInfoManager;
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

    Logger logger;
    PlayerCallbackReadBackend readBackend;
    WriteBackend writeBackend;
    PlayerInfoManager infoManager;

    boolean loggingInventory;

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
        
        /* tuning */
        cacheSize = configuration.getInt("database.tuning.cacheSize");

        /* logging choices */
        loggingInventory = configuration.getBoolean("logging.inventory");









        infoManager = new PlayerInfoManager();

        switch (getDatabaseType()) {
            case MYSQL:
            case POSTGRES:
                SqlReadWriteBackend sqlBackend = new SqlReadWriteBackend(this);
                readBackend = new PlayerCallbackReadBackend(sqlBackend, infoManager);
                writeBackend = sqlBackend;
                break;
            case REDIS:
            default:
                throw new UnsupportedOperationException("No redis support yet");
                // break;
        }
    }
}
