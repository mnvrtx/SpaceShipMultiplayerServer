package com.fogok.spaceshipserver.utlis;

import com.beust.jcommander.Parameter;

/**
 * -log
 LEVEL_NONE = 6;
 LEVEL_ERROR = 5;
 LEVEL_WARN = 4;
 LEVEL_INFO = 3;
 LEVEL_DEBUG = 2;
 LEVEL_TRACE = 1;

 -log 1 -debug port 15505
 */
public class CLIArgs {

    //region Common
    @Parameter(names = "-log", description = "LogLevel. ")
    public Integer logLevel = 1;

    @Parameter(names = "-debug", description = "Debug mode")
    public Boolean debug = false;

    @Parameter(names = "-port", description = "Server port")
    public Integer port = 15505;

    @Parameter(names = "-name", description = "Service name")
    public String serviceName = "unnamedService";
    //endregion

    //region Authentication
    //endregion

}
