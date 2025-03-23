package com.alex.web.node.pdm.mapper.specification;


import com.alex.web.node.pdm.model.Detail;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Named("SpecificationMapperUtil")
public class SpecificationMapperUtil {
    @Named("detailsToIds")
    public List<Long> detailsToIds(List<Detail> details) {
        return details.stream()
                .map(Detail::getId)
                .collect(Collectors.toList());
    }

}
