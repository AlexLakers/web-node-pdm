package com.alex.web.node.pdm.search;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

import java.util.*;
import java.util.function.BiFunction;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SortBuilder {
    private static final String DEFAULT_SORT_PROPERTY = "id";
    private static final Set<String> columns = Set.of("id", "code", "amount", "description", "user_id");

    private final List<Sort.Order> orders = new ArrayList<>();

    public static SortBuilder builder() {
        return new SortBuilder();
    }

    public SortBuilder add(String direction, String property, BiFunction<Sort.Direction, String, Sort.Order> func) {
        if (Objects.nonNull(property) && columns.contains(property))
            orders.add(func.apply(Sort.Direction.valueOf(direction), property));
        else
            orders.add(Sort.Order.asc(DEFAULT_SORT_PROPERTY));
        return this;
    }

    public Sort build() {
        return Sort.by(orders);
    }
}
