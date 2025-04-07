package com.alex.web.node.pdm.controller;

import com.alex.web.node.pdm.dto.specification.NewSpecificationDto;
import com.alex.web.node.pdm.dto.specification.UpdateSpecificationDto;
import com.alex.web.node.pdm.search.SpecificationSearchDto;
import com.alex.web.node.pdm.service.DetailService;
import com.alex.web.node.pdm.service.SpecificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

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
        model.addAllAttributes(Map.of(
                "details", detailService.findAllBySpecId(specId),
                "specId", specId,
                "referer", referer));
        return "detail/details";
    }


    @PostMapping
    /*@PreAuthorize("#authUser.id==#newSpecificationDto.userId()")*/
    public String create(Model model,
                         @Validated NewSpecificationDto newSpecificationDto,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes
    ) {
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
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/specifications/{id}";
        }
        model.addAttribute("spec", specificationService.update(id, updateSpecificationDto));
        return "specification/specification";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long specId) {
        specificationService.delete(specId);
        return "redirect:/specifications";
    }

}
