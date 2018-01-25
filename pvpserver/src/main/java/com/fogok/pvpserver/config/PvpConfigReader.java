package com.fogok.pvpserver.config;

import com.fogok.spaceshipserver.config.BaseConfigReader;
import com.fogok.spaceshipserver.utlis.CLIArgs;

import java.io.IOException;

public class PvpConfigReader extends BaseConfigReader<PvpConfig> {

    public PvpConfigReader(CLIArgs cliArgs) throws IOException, IllegalAccessException, InstantiationException {
        super(PvpConfig.class, cliArgs.configPath, cliArgs.serviceName);
    }

}
