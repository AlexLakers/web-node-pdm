package com.alex.web.node.pdm.controller;

import com.alex.web.node.pdm.dto.specification.NewSpecificationDto;
import com.alex.web.node.pdm.dto.specification.UpdateSpecificationDto;
import com.alex.web.node.pdm.search.SpecificationSearchDto;
import com.alex.web.node.pdm.service.DetailService;
import com.alex.web.node.pdm.service.SpecificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/specifications")
public class SpecificationController {

    private final SpecificationService specificationService;
    private final DetailService detailService;

    @GetMapping("/{id}")
    public String findById(@PathVariable Long id,
                           Model model,
                           @RequestHeader(required = false) String referer) {
        log.info("--start 'find specification by id' endpoint--");
        model.addAllAttributes(Map.of("spec", specificationService.findById(id), "referer", referer));
        return "specification/specification";
    }

    @GetMapping
    public String findAll(Model model,
                          @ModelAttribute @RequestHeader(required = false) String referer,
                          @Validated SpecificationSearchDto specificationSearchDto,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes
    ) {
        log.info("--start 'find all specifications' endpoint--");
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorsSearch", bindingResult.getFieldErrors());
            return "redirect:/specifications";

        } else {
            model.addAllAttributes(Map.of(
                    "search", specificationSearchDto,
                    "page", specificationService.findAll(specificationSearchDto),
                    "referer", referer));
        }
        return "specification/specifications";
    }

    @GetMapping("/{specId}/details")
    public String findAllDetailsBySpecId(Model model,
                                         @RequestHeader(required = false, defaultValue = "/specifications") String referer,
                                         @PathVariable Long specId) {
        log.info("--start 'find all detail by specification id' endpoint--");
        model.addAllAttributes(Map.of(
                "details", detailService.findAllBySpecId(specId),
                "specId", specId,
                "referer", referer));
        return "detail/details";
    }


    @PostMapping
    public String create(Model model,
                         @Validated NewSpecificationDto newSpecificationDto,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes
    ) {
        log.info("--start 'create a new specification' endpoint--");
        if (bindingResult.hasErrors())
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
        else
            specificationService.create(newSpecificationDto);
        return "redirect:/specifications";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id,
                         Model model,
                         @Validated UpdateSpecificationDto updateSpecificationDto,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes
    ) {
        log.info("--start 'update specification by id' endpoint--");
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/specifications/{id}";
        }
        model.addAttribute("spec", specificationService.update(id, updateSpecificationDto));
        return "specification/specification";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long specId) {
        log.info("--start 'delete specification by id' endpoint--");
        specificationService.delete(specId);
        return "redirect:/specifications";
    }

}
