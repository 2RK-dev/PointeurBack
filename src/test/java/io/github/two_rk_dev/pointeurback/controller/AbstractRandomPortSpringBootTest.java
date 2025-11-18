package io.github.two_rk_dev.pointeurback.controller;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractRandomPortSpringBootTest {

    @Contract(pure = true)
    @NotNull String url(String path) {
        return "http://localhost:" + this.getPort() + "/api/v1" + path;
    }

    protected abstract int getPort();
}
