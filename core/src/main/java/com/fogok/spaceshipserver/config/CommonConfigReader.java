package com.fogok.spaceshipserver.config;

import com.fogok.spaceshipserver.utlis.CLIArgs;

import java.io.IOException;

public class CommonConfigReader extends BaseConfigReader<CommonConfig>{

    public CommonConfigReader(CLIArgs cliArgs) throws IOException, IllegalAccessException, InstantiationException {
        super(CommonConfig.class, cliArgs.configPath, CommonConfig.class.getSimpleName().toLowerCase());
    }

}
