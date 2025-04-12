package com.alex.web.node.pdm.mapper.detail;

import com.alex.web.node.pdm.dto.detail.NewDetailDto;
import com.alex.web.node.pdm.exception.EntityNotFoundException;
import com.alex.web.node.pdm.model.Detail;
import com.alex.web.node.pdm.repository.SpecificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public abstract class DetailMapperDecorator implements DetailMapper {
    @Autowired
    private DetailMapper delegate;
    @Autowired
    private SpecificationRepository specificationRepository;

    public Detail toDetailFromNewDetailDto(NewDetailDto newDetailDto) {
        Detail detail = delegate.toDetailFromNewDetailDto(newDetailDto);
        Optional.ofNullable(newDetailDto.specificationId())
                .flatMap(specificationRepository::findById)
                .ifPresentOrElse(
                        detail::setSpecification,
                        () -> {
                            throw new EntityNotFoundException(
                                    "The specification with id %d is not found".formatted(newDetailDto.specificationId()));
                        });
        return detail;
    }


}
