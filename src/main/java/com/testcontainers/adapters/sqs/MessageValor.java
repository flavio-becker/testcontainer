package com.testcontainers.adapters.sqs;

import java.util.UUID;

public record MessageValor(UUID uuid, String content) {}
