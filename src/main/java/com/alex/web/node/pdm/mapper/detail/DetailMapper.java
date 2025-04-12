package com.alex.web.node.pdm.mapper.detail;

import com.alex.web.node.pdm.dto.detail.DetailDto;
import com.alex.web.node.pdm.dto.detail.NewDetailDto;
import com.alex.web.node.pdm.dto.detail.UpdateDetailDto;
import com.alex.web.node.pdm.model.Detail;
import lombok.RequiredArgsConstructor;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.mapstruct.NullValuePropertyMappingStrategy.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
      //  uses = DetailMapperUtil.class,
       // uses = SpecificationRepository.class,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        builder = @Builder(disableBuilder = true))
@DecoratedWith(DetailMapperDecorator.class)
@Component
public interface DetailMapper {

    @Mapping(target = "specificationId",source = "specification.id")
     DetailDto toDetailDto(Detail detail);
     List<DetailDto> toDetailDtoList(List<Detail> details);
    //@Mapping(target = "specification",ignore = true)
     Detail toDetailFromNewDetailDto(NewDetailDto newDetailDto);
    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void updateDetail(UpdateDetailDto updateDetailDto, @MappingTarget Detail detail);

/*@AfterMapping
    public void setSpecification(NewDetailDto newDetailDto,@MappingTarget Detail detail) {
        Optional.ofNullable(newDetailDto.specificationId())
                .flatMap(specificationRepository::findById)
                .ifPresentOrElse(
        detail::setSpecification,
                        () -> {
        throw new SpecificationNotFoundException(
                                    "The specification with id=" + newDetailDto.specificationId() + "is not found");
        });


    }*/

}
