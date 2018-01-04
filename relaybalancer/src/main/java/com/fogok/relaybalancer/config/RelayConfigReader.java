package com.fogok.relaybalancer.config;

import com.fogok.spaceshipserver.config.BaseConfigReader;
import com.fogok.spaceshipserver.utlis.CLIArgs;

import java.io.IOException;

public class RelayConfigReader extends BaseConfigReader<RelayConfig> {

    public RelayConfigReader(CLIArgs cliArgs) throws IOException, IllegalAccessException, InstantiationException {
        super(RelayConfig.class, cliArgs.configPath, cliArgs.serviceName);
    }

}
