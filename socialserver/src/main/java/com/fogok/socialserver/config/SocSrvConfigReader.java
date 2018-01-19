package com.fogok.socialserver.config;

import com.fogok.spaceshipserver.config.BaseConfigReader;
import com.fogok.spaceshipserver.utlis.CLIArgs;

import java.io.IOException;

public class SocSrvConfigReader extends BaseConfigReader<SocSrvConfig> {

    public SocSrvConfigReader(CLIArgs cliArgs) throws IOException, IllegalAccessException, InstantiationException {
        super(SocSrvConfig.class, cliArgs.configPath, cliArgs.serviceName);
    }

}
