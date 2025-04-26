package com.alex.web.node.pdm.controller;

import com.alex.web.node.pdm.dto.detail.NewDetailDto;
import com.alex.web.node.pdm.dto.detail.UpdateDetailDto;
import com.alex.web.node.pdm.model.Detail;
import com.alex.web.node.pdm.service.DetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

/**
 * This class describes a controller layer for {@link Detail detail-entity}.
 * It contains endpoints for providing different views.
 */

@Slf4j
@RequestMapping("/details")
@RequiredArgsConstructor
@Controller
public class DetailController {
    private final DetailService detailService;

    /**
     * Returns path to detail.html for rendering and presentation for user.
     * This endpoint allows to find a specific detail by id.
     *
     * @param model   service param of spring for rendering.
     * @param id      id of detail.
     * @param referer header to go prev page.
     * @return detail.html.
     */

    @GetMapping("/{id}")
    public String findById(Model model,
                           @PathVariable Long id,
                           @RequestHeader String referer) {
        model.addAllAttributes(Map.of("detail", detailService.findById(id), "referer", referer));
        return "detail/detail";
    }

    /**
     * Returns url to {@link SpecificationController#findAllDetailsBySpecId(Model, String, Long) findAllBySpecId}.
     * If a request contains some errors then occurs setting errors in model.
     * This endpoint allows to create a  new detail.
     *
     * @param model              service param of spring for rendering.
     * @param newDetailDto       input-dto.
     * @param bindingResult      result of validation.
     * @param redirectAttributes service param to add attributes in model during redirect.
     * @return redirect url.
     */

    @PostMapping
    public String create(Model model,
                         @Validated NewDetailDto newDetailDto,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes
    ) {
        log.info("--start 'create a new detail' endpoint--");
        if (bindingResult.hasErrors())
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());

        else
            model.addAttribute("detail", detailService.create(newDetailDto));

        return "redirect:/specifications/" + newDetailDto.specificationId() + "/details";
    }

    /**
     * Returns url to {@link SpecificationController#findAllDetailsBySpecId(Model, String, Long) findAllBySpecId}.
     *This endpoint allows to remove a new specific detail by id.
     * @param id     id of detail.
     * @param specId id of specification.
     * @return redirect url.
     */

    @PostMapping("/{specId}/delete")
    public String delete(@RequestParam("detailId") Long id,
                         @PathVariable Long specId) {
        log.info("--start 'delete user by id' endpoint--");
        detailService.delete(id);
        return "redirect:/specifications/{specId}/details";
    }

    /**
     * Returns path to detail.html for rendering and presentation for user.
     * If a request contains some errors then occurs setting errors in model and returns redirect url
     * to {@link this#findById(Model, Long, String) findById}.
     * This endpoint allows to update a new specific detail.
     * @param model              service param of spring for rendering.
     * @param id                 id of detail.
     * @param updateDetailDto    input-dto.
     * @param bindingResult      result of validation.
     * @param redirectAttributes service param to add attributes in model during redirect.
     * @return
     */

    @PostMapping("/{id}/update")
    public String update(Model model,
                         @PathVariable Long id,
                         @Validated UpdateDetailDto updateDetailDto,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        log.info("--start 'update detail by id' endpoint--");
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/details/{id}";
        }
        model.addAttribute("detail", detailService.update(id, updateDetailDto));
        return "detail/detail";
    }

}
