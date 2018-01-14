package com.fogok.spaceshipserver.config;

import com.fogok.io.Fgkio;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public abstract class BaseConfigReader<T extends BaseConfigModel> {

    private T configModel;

    public BaseConfigReader(Class<T> configClass, String pathToConfig, String appName) throws IOException, IllegalAccessException, InstantiationException {
        final File file = Fgkio.files.crwFileInternal(pathToConfig, String.format("%s.%s", appName, "jsoncfg"));
        String fileContent = readFile(file);
        if (fileContent.equals("")) {
            configModel = configClass.newInstance();
            configModel.createDefaultConfigModel();
            writeConfigModelToFile(file);
        } else {
            configModel = new Gson().fromJson(fileContent, configClass);
        }
    }

    private String readFile(File file) throws IOException {
        String fileContent;
        try(FileInputStream inputStream = new FileInputStream(file)) {
            fileContent = IOUtils.toString(inputStream, "UTF-8");
        }
        return fileContent;
    }

    private void writeConfigModelToFile(File file) throws IOException {
        FileWriterWithEncoding writer = new FileWriterWithEncoding(file, "UTF-8", false);
        writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(configModel));
        writer.flush();
        writer.close();
    }

    public T getConfig(){
        return configModel;
    }
}
