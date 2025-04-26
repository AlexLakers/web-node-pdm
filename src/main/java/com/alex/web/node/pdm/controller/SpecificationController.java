package com.alex.web.node.pdm.controller;

import com.alex.web.node.pdm.dto.specification.NewSpecificationDto;
import com.alex.web.node.pdm.dto.specification.UpdateSpecificationDto;
import com.alex.web.node.pdm.model.Specification;
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

/**
 * This class describes a controller layer for {@link Specification  spec-entity}.
 * It contains endpoints for providing different views.
 */

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/specifications")
public class SpecificationController {

    private final SpecificationService specificationService;
    private final DetailService detailService;

    /**
     * Returns path to specifications.html for rendering and presentation for user.
     * This endpoint allows to find a specific specification by id.
     *
     * @param id      id of specification.
     * @param model   service param of spring for rendering.
     * @param referer header to go prev page.
     * @return user.html.
     */

    @GetMapping("/{id}")
    public String findById(@PathVariable Long id,
                           Model model,
                           @RequestHeader(required = false) String referer) {
        log.info("--start 'find specification by id' endpoint--");
        model.addAllAttributes(Map.of("spec", specificationService.findById(id), "referer", referer));
        return "specification/specification";
    }

    /**
     * Returns path to specifications.html for rendering and presentation for user.
     * If request contains errors then redirects to itself endpoint with errors.
     * This endpoint allows to find list of specification.
     *
     * @param model                  service param of spring for rendering.
     * @param referer                header to go prev page.
     * @param specificationSearchDto search input-dto.
     * @param bindingResult          result of validation.
     * @param redirectAttributes     service param to add attributes in model.
     * @return specifications.html
     */

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

    /**
     * Returns path to details.html for rendering and presentation for user.
     * This endpoint allows to find list of details by specification id.
     *
     * @param model   service param of spring for rendering.
     * @param referer header to go prev page.
     * @param specId  id of specification.
     * @return details.html.
     */

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

    /**
     * Redirects url to {@link this#findAll(Model, String, SpecificationSearchDto, BindingResult, RedirectAttributes) findAll}.
     * If request contains errors then occurs setting errors to model.
     * This endpoint allows to create a new specification.
     *
     * @param model               service param of spring for rendering.
     * @param newSpecificationDto input-dto.
     * @param bindingResult       result of validation.
     * @param redirectAttributes  service param to add attributes in model.
     * @return redirect url.
     */

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

    /**
     * Returns path to specification.html for rendering and presentation for user.
     * If request contains errors redirects to {@link this#findById(Long, Model, String) findById}.
     * This endpoint allows to update a new specific specification.
     *
     * @param id                     is of specification,
     * @param model                  specific param for rendering.
     * @param updateSpecificationDto input-dto.
     * @param bindingResult          result of validation.
     * @param redirectAttributes     service param to add attributes in model.
     * @return specification.html
     */

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

    /**
     * Returns url to {@link this#findAll(Model, String, SpecificationSearchDto, BindingResult, RedirectAttributes) findAll}.
     * This endpoint allows to delete a specific specification by id.
     *
     * @param specId if of specification.
     * @return redirect url.
     */

    @PostMapping("/delete")
    public String delete(@RequestParam Long specId) {
        log.info("--start 'delete specification by id' endpoint--");
        specificationService.delete(specId);
        return "redirect:/specifications";
    }

}
