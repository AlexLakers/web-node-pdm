package com.alex.web.node.pdm.mapper.specification;

import com.alex.web.node.pdm.dto.specification.NewSpecificationDto;
import com.alex.web.node.pdm.dto.specification.SpecificationDto;
import com.alex.web.node.pdm.dto.specification.UpdateSpecificationDto;
import com.alex.web.node.pdm.model.Specification;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.mapstruct.NullValuePropertyMappingStrategy.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        builder = @Builder(disableBuilder = true),
        uses = /*DetailMapper.class}*/{SpecificationMapperUtil.class}
)
@DecoratedWith(SpecificationMapperDecorator.class)
@Component
public interface SpecificationMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "detailsIds", qualifiedByName = {"SpecificationMapperUtil", "detailsToIds"}, source = "details")
    SpecificationDto toSpecificationDto(Specification specification);

    List<SpecificationDto> toSpecificationDtoList(List<Specification> specifications);

    @Mapping(target = "amount", constant = "0")
    Specification toSpecification(NewSpecificationDto newSpecificationDto);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void updateSpecification(UpdateSpecificationDto updateSpecificationDto,
                             @MappingTarget Specification specification);
}
