package com.alex.web.node.pdm.mapper.specification;


import com.alex.web.node.pdm.dto.specification.NewSpecificationDto;
import com.alex.web.node.pdm.model.Specification;
import com.alex.web.node.pdm.repository.UserRepository;
import com.alex.web.node.pdm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public abstract class SpecificationMapperDecorator implements SpecificationMapper {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SpecificationMapper delegate;
    @Autowired
    private UserService userService;


    public Specification toSpecification(NewSpecificationDto newSpecificationDto) {
        Specification specification = delegate.toSpecification(newSpecificationDto);
      //  specification.setAmount(0);
        Optional<String> maybeName = userService.getCurrentName();
        maybeName.flatMap(userRepository::findByUsername)
                .ifPresentOrElse(specification::setUser,
                        () -> {
                            throw new UsernameNotFoundException("The username '%1$s' is not found".formatted(maybeName.get()));
                        });
        return specification;

    }

}
