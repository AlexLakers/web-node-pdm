package com.alex.web.node.pdm.search;

import java.util.List;

/**
 * It describes a dto-object for search a specific details by list of id.
 * @param ids list of detail id's.
 */

public record DetailSearchDto(List<Long> ids) {
}
