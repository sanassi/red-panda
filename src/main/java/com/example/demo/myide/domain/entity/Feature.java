package com.example.demo.myide.domain.entity;

import com.example.demo.utils.Given;

import javax.validation.constraints.NotNull;

@Given()
public interface Feature {

    /**
     * @param project {@link Project} on which the feature is executed.
     * @param params  Parameters given to the features.
     * @return {@link ExecutionReport}
     */
    @NotNull ExecutionReport execute(final Project project, final Object... params);

    /**
     * @return The type of the Feature.
     */
    @NotNull Type type();

    @FunctionalInterface
    interface ExecutionReport {
        boolean isSuccess();
    }

    interface Type {
    }
}
