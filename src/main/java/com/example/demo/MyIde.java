package com.example.demo;

import com.example.demo.myide.domain.entity.Project;
import com.example.demo.myide.domain.entity.ProjectClass;
import com.example.demo.myide.domain.service.ProjectService;
import com.example.demo.myide.domain.service.ProjectServiceInstance;
import com.example.demo.utils.Given;
import net.bytebuddy.utility.dispatcher.JavaDispatcher;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Starter class, we will use this class and the init method to get a
 * configured instance of {@link ProjectService}.
 */
@Given(overwritten = false)
public class MyIde {

    /**
     * Init method. It must return a fully functional implementation of {@link ProjectService}.
     *
     * @return An implementation of {@link ProjectService}.
     */
    public static ProjectService init(final Configuration configuration) {
        configuration_ = configuration;
        return ProjectServiceInstance.INSTANCE;
    }

    /**
     * Record to specify where the configuration of your IDE
     * must be stored. Might be useful for the search feature.
     */
    public record Configuration(Path indexFile,
                                Path tempFolder) {
    }
    public static Configuration configuration_;
}
