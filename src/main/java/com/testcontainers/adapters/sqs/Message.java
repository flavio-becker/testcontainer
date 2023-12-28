package com.testcontainers.adapters.sqs;

import java.util.UUID;

public record Message(UUID uuid, String content) {}
