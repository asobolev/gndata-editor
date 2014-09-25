// Copyright (c) 2014, German Neuroinformatics Node (G-Node)
//
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted under the terms of the BSD License. See
// LICENSE file in the root of the Project.

package gndata.lib.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Global application configuration.
 */
public class GlobalConfig extends AbstractConfig {

    private List<ProjectItem> projects = new ArrayList<>();


    public List<ProjectItem> getProjects() {
        return projects;
    }

    public void setProjects(List<ProjectItem> projects) {
        this.projects = projects;
    }

    public void appendProject(String path, String name) {
        this.projects.add(new ProjectItem(path, name));
    }

    public Optional<ProjectItem> getProject(String path) {
        Path tmp = Paths.get(path).toAbsolutePath().normalize();
        return projects.stream()
                .filter(item -> {
                    Path other = Paths.get(item.path).toAbsolutePath().normalize();
                    return tmp.equals(other);
                })
                .findAny();
    }

    /**
     * Loads the project settings from a json file.
     * If the file does not exist, a default configuration is created.
     *
     * @param filePath      Path to the application config file.
     *
     * @return The loaded configuration.
     *
     * @throws java.io.IOException If the loading fails.
     */
    public static GlobalConfig load(String filePath) throws IOException {
        Path tmpPath = Paths.get(filePath)
                .toAbsolutePath()
                .normalize();

        if (Files.exists(tmpPath)) {
            return AbstractConfig.load(tmpPath.toString(), GlobalConfig.class);
        } else {
            GlobalConfig config = new GlobalConfig();
            // set defaults here if necessary
            config.setFilePath(tmpPath.toString());
            config.store();
            return config;
        }
    }

    /**
     * Returns a path to the default configuration file location.
     *
     * TODO Use user\AppData\Roaming\gndata\config.json on windows.
     *
     * @return The path to the configuration file.
     */
    public static String makeConfigPath() {
        return Paths.get(System.getProperty("user.home"), ".gndata", "config.json")
                .toAbsolutePath()
                .toString();
    }

    /**
     * Just a small container for known projects and their location.
     */
    public static class ProjectItem {
        public String path, name;

        public ProjectItem(String path, String name) {
            this.path = path;
            this.name = name;
        }
    }
}
