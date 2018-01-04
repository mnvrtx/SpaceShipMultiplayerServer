package com.fogok.authentication.config;

import com.fogok.spaceshipserver.config.BaseConfigReader;
import com.fogok.spaceshipserver.utlis.CLIArgs;

import java.io.IOException;

public class AuthConfigReader extends BaseConfigReader<AuthConfig>{

    public AuthConfigReader(CLIArgs cliArgs) throws IOException, IllegalAccessException, InstantiationException {
        super(AuthConfig.class, cliArgs.configPath, cliArgs.serviceName);
    }

}
