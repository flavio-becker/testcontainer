package com.testcontainers.domain.entities;

import lombok.Builder;

@Builder
public record EmployeeEvent(Integer id, String firstName, String lastName) {
}
